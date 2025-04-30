package biblionerds;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;

@WebServlet("/Signup")
public class SignupServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement pst = null;

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to MySQL
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/biblio_nerds", "root", "b8c8zj6hmf");

            // SQL query to insert user
            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, password);

            int rowAffected = pst.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("User registered: " + email);
                response.sendRedirect("index.html");
            } else {
                System.out.println("Insert failed for: " + email);
                response.sendRedirect("Signup.html");
            }

        } catch (SQLIntegrityConstraintViolationException dupEx) {
            // Handle duplicate email (UNIQUE constraint violation)
            System.out.println("Duplicate email: " + email);
            response.setContentType("text/html");
            response.getWriter().write("<script>alert('Email already registered.'); window.location='Signup.html';</script>");
        } catch (Exception e) {
            // Handle other errors
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().write("<script>alert('An error occurred. Please try again.'); window.location='Signup.html';</script>");
        } finally {
            try {
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Handle GET request to redirect to Signup page
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("Signup.html");
    }
}
