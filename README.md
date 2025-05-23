SmartShop is a web application where you can:
Create personal shopping lists
Add, delete, and check off items
Share your list via a unique link
Grant edit permissions to specific users by their username

Features
Create and manage shopping lists
Add and remove items from your list
Mark items as bought
Share lists with others via unique UUID links
Permission-based editing: only users granted permission by the list owner can modify a shared list
Invalid links automatically redirect to the homepage
Secure session management ensures only logged-in users can access the lists
Handy alerts for invalid login attempts

Installation & Setup
Prerequisites
IntelliJ IDEA (or another Java IDE)
XAMPP (Apache + MySQL)
Apache Tomcat server

Steps
1)Clone the repository:
git clone https://github.com/your-username/smartshop.git
cd smartshop
2)Open the project in IntelliJ IDEA (or your preferred IDE).
3)Configure Tomcat Server:
HTTP Port: 8080
JMX Port: 1099
4)Set Deployment artifact: SmartShop:war
5)Set up Database:
Launch XAMPP, start Apache and MySQL.
Open phpMyAdmin at http://localhost/phpmyadmin.
Create a new database called:
smartshop
Import the provided SQL dump to create the necessary tables and sample data:

Database SQL Dump:

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";
CREATE TABLE `items` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT 'item_default_name',
  `quantity` int(11) NOT NULL DEFAULT 1,
  `added_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `done` tinyint(1) NOT NULL DEFAULT 0,
  `list_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
CREATE TABLE `lists` (
  `id` int(11) NOT NULL,
  `uuid` varchar(36) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
CREATE TABLE `list_permissions` (
  `id` int(11) NOT NULL,
  `list_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `can_edit` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
ALTER TABLE `items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `list_id` (`list_id`);
ALTER TABLE `lists`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uuid` (`uuid`),
  ADD KEY `owner_id` (`owner_id`);
ALTER TABLE `list_permissions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `list_id` (`list_id`),
  ADD KEY `user_id` (`user_id`);
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `unique_username` (`username`);
ALTER TABLE `items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
ALTER TABLE `lists`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
ALTER TABLE `list_permissions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
ALTER TABLE `items`
  ADD CONSTRAINT `items_ibfk_1` FOREIGN KEY (`list_id`) REFERENCES `lists` (`id`);
ALTER TABLE `lists`
  ADD CONSTRAINT `lists_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`);
ALTER TABLE `list_permissions`
  ADD CONSTRAINT `list_permissions_ibfk_1` FOREIGN KEY (`list_id`) REFERENCES `lists` (`id`),
  ADD CONSTRAINT `list_permissions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

6)Run the project!

Account Requirements
You must register an account via the registration page before using the app.
After logging in, you can create lists and manage them.
Shared links do not give edit permission by default. Only users granted explicit permission by the owner can edit; others will view the list as guests.

Notes
If you manually change the UUID in the URL and it doesn't exist, you will be redirected to the homepage.
Sessions are used to restrict access to authenticated users.
Incorrect login attempts will trigger a friendly alert indicating a wrong username/password.

The development of SmartShop followed several key stages:
1)Planning:
Defining the core functionalities: creating shopping lists, sharing lists via links, and managing user permissions.
Designing the basic database schema and entity relationships.
2)Database Design:
Created tables users, lists, items, and list_permissions to manage authentication and access control.
Used foreign keys to ensure data integrity.
3)Backend Implementation:
Developed servlets for login, registration, list creation/editing, and handling shared UUID links.
4)Frontend Development:
Built simple and clean web forms without modern JavaScript frameworks, focusing on server-side data processing.
5)Testing and Debugging:
Tested various user scenarios, including invalid links, incorrect login attempts, and session management.

Unique Approaches and Methodologies
1)UUID-based Sharing:
Instead of using simple numeric IDs, the app uses UUIDs to securely and uniquely identify shared lists.
2)Role-based Access Control:
Only the list owner can grant editing permissions to other users.
3)Session-based Security:
Every request checks for an authenticated session, protecting user data.
4)Fail-safe Redirects:
Attempts to access non-existent lists automatically redirect users to the homepage.

Known Bugs and Limitations
1)No password recovery feature; if a user forgets their password, it can only be reset manually via the database.
2)Potential performance issues with very large lists (pagination is not implemented yet).

Why This Tech Stack Was Chosen
1)Java EE + Servlets:
Focused on understanding the fundamentals of web application architecture by working directly with HTTP requests and sessions.
2)MySQL:
A stable, widely-used relational database that's easy to integrate and manage.
3)Apache Tomcat:
A reliable and lightweight servlet container that's perfect for Java web apps in early development stages.
4)XAMPP:
Simplifies local server and database management, allowing faster development and testing.

I genuinely enjoy building web development projects using Java EE and MySQL.
These technologies are easy to work with, highly reliable, and form the main tech stack that I focus on mastering.

Contact
If you have questions or find any issues, feel free to open an issue or contact me at sherkhan.kozhakhmetov@nu.edu.kz.
