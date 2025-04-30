package biblionerds;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class SellBookServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/biblio_nerds?useSSL=false&serverTimezone=UTC";
    private static final String DB_USERNAME = "root";  // Replace with your DB username
    private static final String DB_PASSWORD = "b8c8zj6hmf";  // Replace with your DB password

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form data from the request
        String bookName = request.getParameter("book-name");
        double price = Double.parseDouble(request.getParameter("price"));
        String contact = request.getParameter("contact");
        String email = request.getParameter("email");
        String genre = request.getParameter("genre");
        String condition = request.getParameter("book-condition");
        String sellerName = request.getParameter("your-name");
        String sellerAddress = request.getParameter("your-address");

        // Prepare the SQL query to insert the book details into the database
        String insertQuery = "INSERT INTO books (book_name, price, contact, email, genre, book_condition, your_name, your_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Set the response content type
        response.setContentType("text/html");

        try {
            // Load MySQL JDBC driver (optional step)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish a connection to the database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                // Set the values for the prepared statement
                stmt.setString(1, bookName);
                stmt.setDouble(2, price);
                stmt.setString(3, contact);
                stmt.setString(4, email);
                stmt.setString(5, genre);
                stmt.setString(6, condition);
                stmt.setString(7, sellerName);
                stmt.setString(8, sellerAddress);  // This corresponds to the 'your_address' column in the DB

                // Execute the query
                int rowsAffected = stmt.executeUpdate();

                // Check if the insert was successful
                if (rowsAffected > 0) {
                    // Redirect to the success page with a success message
                    response.sendRedirect("index.html?message=Book%20successfully%20listed%20for%20sale!");
                } else {
                    // Display an error message
                    response.sendRedirect("index.html?message=Error%20listing%20book.%20Please%20try%20again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();  // Print the detailed error
                response.sendRedirect("index.html?message=Database%20connection%20error.%20" + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  // Print the detailed error if the driver is not found
            response.sendRedirect("index.html?message=JDBC%20Driver%20not%20found.%20" + e.getMessage());
        }
    }
}
