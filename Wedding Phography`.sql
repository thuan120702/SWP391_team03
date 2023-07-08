-- create database
CREATE DATABASE wedding_photography
go

USE wedding_photography
GO

-- Create the role table
CREATE TABLE roles (
    role_id INT IDENTITY(1,1) PRIMARY KEY,
		role_name VARCHAR(50) NOT NULL
);

-- Insert role data
INSERT INTO roles (role_name)
VALUES ('admin');
INSERT INTO roles (role_name)
VALUES ('user');
INSERT INTO roles (role_name)
VALUES ('staff');
INSERT INTO roles (role_name)
VALUES ('rental_staff');

-- Create the locations table
CREATE TABLE locations (
    location_id INT IDENTITY(1,1) PRIMARY KEY,
    location_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price float,
	image varchar(50)
);

-- Create the photography_studios table
CREATE TABLE photography_studios (
    studio_id INT IDENTITY(1,1)  PRIMARY KEY,
    studio_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price float,
	image varchar(50)
);

-- Create the profiles table
CREATE TABLE profiles (
    profile_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
	user_name VARCHAR(50) NOT NULL,
	password varchar(50),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(200),
    FOREIGN KEY (user_id) REFERENCES roles(role_id)
);



-- Create the orders table
CREATE TABLE orders (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    profile_id INT NOT NULL,
    order_date varchar(50),
	status varchar(10),
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id),
);

-- Create the rental_products table
CREATE TABLE rental_products (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price float,
	image varchar(50)
);

-- Create the rental_orders table
CREATE TABLE order_detail (
    order_detail_id INT IDENTITY(1,1) PRIMARY KEY,
	order_id int foreign key references orders(order_id),
	item_id int not null,
	item_type varchar(50),
    order_date varchar(50),
	name varchar(100),
	description varchar(500),
	price float,
	is_active bit,
);



-- Create the rental_schedules table
CREATE TABLE rental_schedules (
    schedule_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT,
    schedule_date  varchar(20),
	status varchar(50),
    FOREIGN KEY (user_id) REFERENCES profiles(profile_id),
    FOREIGN KEY (product_id) REFERENCES rental_products(product_id)
);

-- Create the feedback table
/*CREATE TABLE feedback (
    feedback_id INT PRIMARY KEY,
    user_id INT NOT NULL,
    message VARCHAR(500) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES roles(role_id)
);*/



-- use master
-- go
--drop database wedding_photography

-- Insert mock data into rental_products table
INSERT INTO rental_products (product_name, description, price,image)
VALUES
    ('Wedding Dress', 'Elegant white wedding dress', 500.00,'https://i.ibb.co/DgVYGGB/wedding-dress.jpg'),
    ('Tuxedo', 'Classic black tuxedo for groom', 300.00,'https://i.ibb.co/hZMzznQ/tuxedo.jpg'),
    ('Bridesmaid Dress', 'Stylish bridesmaid dress', 200.00,'https://i.ibb.co/SR5Bw26/Bridesmaid-Dress.jpg');

-- Insert mock data into photography_studios table
INSERT INTO photography_studios (studio_name, description, price,image)
VALUES
    ('Capture Moments Studio', 'Professional studio for wedding photography', 1000.00,'https://i.ibb.co/ZJNqbYc/capture-moment.jpg'),
    ('Dreamy Wedding Photography', 'Artistic photography studio for weddings', 1200.00,'https://i.ibb.co/Ytkx3tN/dreamy.jpg'),
    ('Love Story Studio', 'Romantic studio for capturing love stories', 900.00,'https://i.ibb.co/9qnGwGQ/love-story.jpg');

-- Insert mock data into locations table
INSERT INTO locations (location_name, description, price, image)
VALUES
    ('Beachside Resort', 'Beautiful beachside resort with stunning views', 2000.00, 'https://i.ibb.co/3vL6yg0/resort.jpg'),
    ('Enchanted Garden', 'Magical garden with lush greenery and vibrant flowers', 1500.00, 'https://i.ibb.co/whZTHDY/garden.jpg'),
    ('Historic Mansion', 'Grand mansion with rich history and elegant architecture', 2500.00, 'https://i.ibb.co/FwqG4fG/historic.jpg');

CREATE TABLE dress_and_photo_combo (
  id INT IDENTITY(1,1) PRIMARY KEY,
  combo_name VARCHAR(100),
  combo_description VARCHAR(500),
  dress_id INT,
  photo_studio_id INT,
  price FLOAT,
  image varchar(100),
  CONSTRAINT fk_combo_dress FOREIGN KEY (dress_id) REFERENCES rental_products(product_id),
  CONSTRAINT fk_combo_photo_studio FOREIGN KEY (photo_studio_id) REFERENCES photography_studios(studio_id)
);


-- Insert dummy data into DressAndPhotoCombo table
INSERT INTO dress_and_photo_combo (combo_name, combo_description, dress_id, photo_studio_id, price,image)
VALUES
  ('Combo 1', 'Rent a beautiful dress and have a photoshoot in Studio A', 1, 1, 299.99,'https://i.ibb.co/nsLgXGc/combo1.webp'),
  ('Combo 2', 'Elegant dress rental with on-location photoshoot at Garden B', 2, 2, 399.99,'https://i.ibb.co/vXw0w3s/combo2.jpg'),
  ('Combo 3', 'Get a stylish dress and capture memories at Beach C', 3, 3, 499.99,'https://i.ibb.co/4YLsGRd/combo3.jpg');


-- Add foreign key constraint to Location table

ALTER TABLE dress_and_photo_combo
ADD is_active bit default 1

ALTER TABLE locations
ADD is_active bit default 1


ALTER TABLE profiles
ADD is_active bit default 1


ALTER TABLE photography_studios
ADD is_active bit default 1


ALTER TABLE rental_products
ADD is_active bit default 1


/*
ALTER TABLE profiles
ADD password varchar(50);

ALTER TABLE locations
ADD image varchar(50);

ALTER TABLE rental_products
ADD image varchar(50);


ALTER TABLE photography_studios
ADD image varchar(50);


select p.first_name,p.last_name,p.email,p.phone_number,p.address,p.user_name,r.role_name
from profiles p
inner join roles r on p.user_id = r.role_id
where user_name = 'admin' and password = 'admin'

select location_id,location_name,description,price,image
from locations


select product_id, product_name,description,price,image
from rental_products


select studio_id,studio_name,description,price,image
from photography_studios

select location_id,location_name,description,price,image
from locations
where location_name like '%z%'

select id, combo_name, combo_description,dress_id,photo_studio_id,price,image
from dress_and_photo_combo

UPDATE locations
SET location_name = ?, description = ?, price = ?, image = ?
WHERE location_id = ?;

select location_id,location_name,description,price,image
from locations
where price > 1000 and price < 2000 and is_active = 1


UPDATE profiles
SET first_name = ?, last_name = ?, phone_number = ?, address = ?, email = ?
where profile_id = ?

insert dbo.photo_schedules(user_id,location_id,studio_id,schedule_date,is_active)
values (0,0,0,'',0)

insert dbo.photo_schedules(user_id,location_id,studio_id,schedule_date,is_active)
values (1,1,1,'abc',1)


select order_id,user_id,order_date,is_active
from orders
where user_id = ? and is_active = 1

select order_detail_id,order_id,name,description,price,order_date,is_active
from order_detail
where is_active = 1 and order_id = ?

select schedule_id, user_id, location_id, studio_id,schedule_date,is_active
from photo_schedules
where user_id = ? and is_active = 1


select profile_id,user_id,first_name,last_name,email,phone_number,address
from profiles 
where is_active = 1 and user_id != 1


*/

create table studio_staff(
	studio_staff_id INT IDENTITY(1,1) PRIMARY KEY,
	profile_id INT NOT NULL foreign key references profiles(profile_id),
	studio_id INT NOT NULL foreign key references photography_studios(studio_id),
	is_active bit
)


-- Create the cart table
CREATE TABLE cart (
    cart_id INT IDENTITY(1,1) PRIMARY KEY,
    profile_id INT NOT NULL,
	item_id int not null,
	item_type varchar(50),
	is_active bit,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id),
);

-- Create the photo_schedules table
CREATE TABLE photo_schedules (
    schedule_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    location_id INT,
    studio_id INT,
    schedule_date varchar(20),
	status varchar(10),
    FOREIGN KEY (user_id) REFERENCES profiles(profile_id),
    FOREIGN KEY (location_id) REFERENCES locations(location_id),
    FOREIGN KEY (studio_id) REFERENCES photography_studios(studio_id)
);

Alter table rental_products
add stock bigint 

Alter table dress_and_photo_combo
add stock bigint 

 Alter table orders
add amount float

-- Create Feedback table
CREATE TABLE Feedback (
    feedback_id INT IDENTITY(1,1) PRIMARY KEY,
    profile_id INT,
    TimeFeedBack VARCHAR(20),
    Content NVARCHAR(255),
    Rating VARCHAR(10),
	active bit,
	FOREIGN KEY (profile_id) REFERENCES profiles(profile_id),
);


create table ReplyFeedback(
	reply_id int IDENTITY(1,1) PRIMARY KEY,
	feedback_id int not null foreign key references Feedback(feedback_id),
	profile_id int not null foreign key references profiles(profile_id),
	ReplyContent varchar(255)
)

Alter table profiles
ADD CONSTRAINT UC_PROFILES_USER_NAME UNIQUE (user_name);


