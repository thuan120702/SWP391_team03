/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AccountDAO;
import dto.Profile;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
@WebServlet(name = "AddAccountServlet", urlPatterns = {"/AddAccountServlet"})
public class AddAccountServlet extends HttpServlet {

    public final String ERROR_PAGE = "error.jsp";
    public final String ADMIN_PAGE = "admin.jsp";

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

        String userName = request.getParameter("txtUserName");
        String firstName = request.getParameter("txtFistName");
        String lastName = request.getParameter("txtLastName");
        String email = request.getParameter("txtEmail");
        String phone = request.getParameter("txtPhone");
        String address = request.getParameter("txtAddress");
        String roleId = request.getParameter("roleId");

        String passwordDefault = "123";
        AccountDAO dao = new AccountDAO();
        HttpSession session = request.getSession();

        try {
            boolean checkUserName = dao.checkValidUsername(userName);
            if (!checkUserName) {
                Profile profile = new Profile(firstName, lastName, email, phone, address, userName, Integer.parseInt(roleId), passwordDefault);
                boolean result = dao.insertProfifle(profile);
                if (result) {
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

                    url = ADMIN_PAGE;
                }
            }else{
                url = ADMIN_PAGE;
                request.setAttribute("ERROR_USER_NAME", "error user name");
            }

        } catch (NamingException ex) {
            log("DispatcherServlet_NamingException: " + ex.getMessage());
        } catch (SQLException ex) {
            log("DispatcherServlet_SQLException " + ex.getMessage());
        } finally {
            RequestDispatcher dispatcher = request.getRequestDispatcher(url);
            dispatcher.forward(request, response);
//            response.sendRedirect(url);
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
