/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AccountDAO;
import dao.DressPhotoComboDAO;
import dao.LocationDAO;
import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.PhotoScheduleDAO;
import dao.PhotographyStudiosDAO;
import dao.RentalProductDAO;
import dao.RoleDAO;
import dao.studioStaffDAO;
import dto.DressPhotoCombo;
import dto.Location;
import dto.Order;
import dto.OrderDetail;
import dto.PhotoSchedule;
import dto.OrderItem;
import dto.PhotographyStudio;
import dto.Profile;
import dto.RentalProduct;
import dto.Role;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ptd
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    public final String HOME_PAGE = "DispatcherServlet?btAction=Home";
    public final String ADMIN_PAGE = "admin.jsp";
    public final String PHOTO_PAGE = "photoHome.jsp";
    public final String RENTAL_STAFF_PAGE = "rentalPage.jsp";
    public final String ERROR_PAGE = "error.jsp";

    public final String ADMIN_ROLE = "admin";
    public final String USER_ROLE = "user";
    public final String STAFF_ROLE = "staff";
    public final String RENTAL_STAFF_ROLE = "rental_staff";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String url = ERROR_PAGE;
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");

        AccountDAO dao = new AccountDAO();
        OrderDAO orderDAO = new OrderDAO();
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        PhotoScheduleDAO scheduleDAO = new PhotoScheduleDAO();
        LocationDAO locationDAO = new LocationDAO();
        PhotographyStudiosDAO studioDAO = new PhotographyStudiosDAO();
        RoleDAO roleDAO = new RoleDAO();
//        studioStaffDAO studioStaffDAO = new studioStaffDAO();
        RentalProductDAO rentalProductDAO = new RentalProductDAO();
        DressPhotoComboDAO comboDAO = new DressPhotoComboDAO();

        HttpSession session = request.getSession();

        try {
            Profile result = dao.checkLogin(userName, password);
            if (result != null) {
                if (ADMIN_ROLE.equals(result.getRoleName())) {
                    url = ADMIN_PAGE;

                    //get list staff role
                    List<Role> listRoleStaff = roleDAO.getStaffRole();

                    // manage user
                    List<Profile> listUser = dao.getAllProfile();

                    List<Profile> users = new ArrayList<>();
                    List<Profile> staff = new ArrayList<>();

                    for (Profile user : listUser) {
                        if (user.getRoleName().equals("user")) {
                            users.add(user);
                        } else {
                            staff.add(user);
                        }
                    }
                    session.setAttribute("LIST_USER", users);
                    session.setAttribute("LIST_STAFF", staff);

                    session.setAttribute("LIST_STAFF_ROLE", listRoleStaff);

                    // manage order
                    List<Order> listOrder = orderDAO.getAllOrder();
                    List<OrderItem> orderItem = new ArrayList<>();

                    if (listOrder.size() > 0) {
                        for (Order order : listOrder) {
                            List<OrderDetail> listOrderDetail = orderDetailDAO.getOrderDetailByOrderId(order.getOrderId());
                            if (listOrderDetail.size() > 0) {
                                OrderItem orderItemDetail = new OrderItem();
                                orderItemDetail.setList(listOrderDetail);
                                orderItem.add(orderItemDetail);
                            }
                        }
                        session.setAttribute("LIST_ORDER_ADMIN", orderItem);
                    }

                } else if (STAFF_ROLE.equals(result.getRoleName())) {
                    url = PHOTO_PAGE;

                    List<OrderItem> listPhotoScheItem = new ArrayList<>();

                    // flow: get order detail with type is rental_schedule and order is pending
                    List<OrderDetail> listSchedulePending = orderDetailDAO.getOrderDetailByItemType("photo_schedule");
                    // from order_detail we get location and studio 
                    for (OrderDetail orderDetail : listSchedulePending) {
                        PhotoSchedule photoSchedule = scheduleDAO.getPhotoScheduleById(orderDetail.getItemId());
                        Location location = locationDAO.getLocationById(photoSchedule.getLocationId());
                        PhotographyStudio studio = studioDAO.getStudioById(photoSchedule.getStudioId());

                        OrderDetail detailLocation = new OrderDetail(photoSchedule.getScheduleId(), location.getName(), location.getDescription(), location.getPrice(), photoSchedule.getScheduleDate(), orderDetail.getOrderId(), location.getId(), "location");
                        OrderDetail detailStudio = new OrderDetail(photoSchedule.getScheduleId(), studio.getName(), studio.getDescription(), studio.getPrice(), photoSchedule.getScheduleDate(), orderDetail.getOrderId(), studio.getId(), "studio");

                        OrderItem photoScheItem = new OrderItem();
                        photoScheItem.getList().add(detailLocation);
                        photoScheItem.getList().add(detailStudio);
                        listPhotoScheItem.add(photoScheItem);

                    }
                    session.setAttribute("LIST_PHOTO_SCHEDULE_STAFF", listPhotoScheItem);

                    List<Location> listLocation = locationDAO.getAllLocation();
                    session.setAttribute("LOCATIONS", listLocation);

                } else if (USER_ROLE.equals(result.getRoleName())) {
                    url = HOME_PAGE;
                    // get orderbyProfileId
                    Order orderExist = orderDAO.getOrderByProfileId(result.getProfileId());
                    int orderId = 0;
                    if (orderExist != null) {
                        orderId = orderExist.getOrderId();

                        if (orderId > 0) {
                            List<OrderDetail> listOrderDetailByOrder = orderDetailDAO.getOrderDetailByOrderId(orderId);

                            List<OrderItem> listPhotoScheduleItem = new ArrayList<>();

                            for (OrderDetail detail : listOrderDetailByOrder) {
                                //item_id and item_type --> add schedule photo
                                if (detail.getItemType().equals("photo_schedule")) {
                                    PhotoSchedule photoSchedule = scheduleDAO.getPhotoScheduleById(detail.getItemId());

                                    // get item
                                    Location location = locationDAO.getLocationById(photoSchedule.getLocationId());
                                    PhotographyStudio studio = studioDAO.getStudioById(photoSchedule.getStudioId());

                                    // init photo schedule
                                    OrderItem photoScheduleItem = new OrderItem();
                                    List<OrderDetail> listScheduleOrderDetail = new ArrayList<>();

                                    // add item into list
                                    listScheduleOrderDetail.add(new OrderDetail(detail.getOrderDetailId(), location.getName(), location.getDescription(), location.getPrice(), photoSchedule.getScheduleDate(), orderId, photoSchedule.getScheduleId(), "photo_schedule"));
                                    listScheduleOrderDetail.add(new OrderDetail(detail.getOrderDetailId(), studio.getName(), studio.getDescription(), studio.getPrice(), photoSchedule.getScheduleDate(),orderId, photoSchedule.getScheduleId(), "photo_schedule"));

                                    // add list into item photo schedule    
                                    photoScheduleItem.setList(listScheduleOrderDetail);
                                    listPhotoScheduleItem.add(photoScheduleItem);
                                } else {
                                    OrderItem photoScheduleItem = new OrderItem();
                                    List<OrderDetail> listScheduleOrderDetail = new ArrayList<>();
                                    listScheduleOrderDetail.add(detail);
                                    // add list into item photo schedule    
                                    photoScheduleItem.setList(listScheduleOrderDetail);
                                    listPhotoScheduleItem.add(photoScheduleItem);
                                }
                            }
                            
                           
                            session.setAttribute("LIST_CARR_ITEM", listPhotoScheduleItem);
                            session.setAttribute("CART_ITEM", listPhotoScheduleItem.size());
                        }
                    }

                    List<Location> listLocation = locationDAO.getAllLocation();
                    session.setAttribute("LIST_LOCATION", listLocation);

                    List<PhotographyStudio> listStudio = studioDAO.getAllPhotographyStudio();
                    session.setAttribute("LIST_STUDIO", listStudio);
                    
                    List<RentalProduct> rentalProducts = rentalProductDAO.getAllRentalProduct();
                    session.setAttribute("LIST_PRODUCT", rentalProducts);
                    
                    List<DressPhotoCombo> combos = comboDAO.getAllDressPhotoCombo();
                    session.setAttribute("LIST_COMBO", combos);

                } else if (RENTAL_STAFF_ROLE.equals(result.getRoleName())) {
                    url = RENTAL_STAFF_PAGE;
                    // get orderdetail with itemtype = rental product
                    List<OrderDetail> listOrderRental = orderDetailDAO.getOrderDetailByItemType("rental_product");
                    session.setAttribute("LIST_RENTAL_STAFF", listOrderRental);

                    // get all rental to manage
                    List<RentalProduct> listProduct = rentalProductDAO.getAllRentalProduct();
                    session.setAttribute("PRODUCTS", listProduct);
                }

                session.setAttribute("USER", result);
            }
        } catch (NamingException ex) {
            log("LoginServlet_NamingException: " + ex.getMessage());
        } catch (SQLException ex) {
            log("LoginServlet_SQLException " + ex.getMessage());
        } finally {
            RequestDispatcher dispatcher = request.getRequestDispatcher(url);
            dispatcher.forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
