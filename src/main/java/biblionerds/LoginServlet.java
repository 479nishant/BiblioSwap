package biblionerds;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get user input from the request
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Input validation
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h3>Email and password must not be empty.</h3>");
            out.println("<a href='login.html'>Try again</a>");
            out.println("</body></html>");
            return;  // Exit early
        }

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio_nerds?useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "b8c8zj6hmf");

            // SQL query to check email and password
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            System.out.println("Executing SQL query: " + sql); // Log the SQL query for debugging
            pst = conn.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, password);

            // Execute the query
            rs = pst.executeQuery();

            // Check if user credentials are valid
            if (rs.next()) {
                // Login successful, create a session
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", email);  // Store user info in session

                // Redirect to the home page
                response.sendRedirect("index.html");
            } else {
                // Login failed, show error message
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h3>Invalid email or password. Please try again.</h3>");
                out.println("<a href='login.html'>Try again</a>");
                out.println("</body></html>");
            }
        } catch (SQLException | ClassNotFoundException e) {
            // Log the error for server-side debugging and show a user-friendly message
            e.printStackTrace();  // Log the exception for debugging
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h3>Something went wrong. Please try again later.</h3>");
            out.println("<p>Error details: " + e.getMessage() + "</p>");  // Print the exception message
            out.println("</body></html>");
        } finally {
            // Close database resources to prevent memory leaks
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();  // Log any errors during cleanup
            }
        }
    }
}
