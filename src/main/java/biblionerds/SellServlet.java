package biblionerds;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class SellServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get parameters from the form
        String bookName = request.getParameter("book-name");
        String price = request.getParameter("price");
        String contact = request.getParameter("contact");
        String email = request.getParameter("email");
        String genre = request.getParameter("genre");

        // Validate parameters
        if (bookName == null || bookName.isEmpty() || price == null || price.isEmpty() ||
            contact == null || contact.isEmpty() || email == null || email.isEmpty() || genre == null || genre.isEmpty()) {
            response.sendRedirect("error.jsp");  // Redirect to error page if validation fails
            return;
        }

        // Database connection setup
        Connection conn = null;
        PreparedStatement pst = null;
        String message = "";

        try {
            // Establish DB connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio_nerds", "root", "password");  // Update password if necessary

            // Insert book into the database
            String sql = "INSERT INTO books (book_name, price, contact, email, genre) VALUES (?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, bookName);
            pst.setString(2, price);
            pst.setString(3, contact);
            pst.setString(4, email);
            pst.setString(5, genre);

            int rowAffected = pst.executeUpdate();

            if (rowAffected > 0) {
                message = "Book uploaded successfully!";
            } else {
                message = "Failed to upload book.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "Error: " + e.getMessage();
        } finally {
            try {
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Set message as request attribute and forward to the result page
        request.setAttribute("message", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("result.jsp");  // Ensure result.jsp exists
        dispatcher.forward(request, response);
    }
}
