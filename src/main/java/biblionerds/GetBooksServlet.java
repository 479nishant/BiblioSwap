package biblionerds;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetBooksServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/biblio_nerds?useSSL=false&serverTimezone=UTC";
    private static final String DB_USERNAME = "root";  // Your DB username
    private static final String DB_PASSWORD = "b8c8zj6hmf";  // Your DB password

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchQuery = request.getParameter("search");
        String sql = "SELECT * FROM books";

        // Apply search filter if search term is provided
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql += " WHERE book_name LIKE ? OR genre LIKE ?";
        }

        response.setContentType("application/json");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set search query parameters if provided
            if (searchQuery != null && !searchQuery.isEmpty()) {
                stmt.setString(1, "%" + searchQuery + "%");
                stmt.setString(2, "%" + searchQuery + "%");
            }

            ResultSet rs = stmt.executeQuery();
            List<Book> books = new ArrayList<>();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookName(rs.getString("book_name"));
                book.setPrice(rs.getDouble("price"));
                book.setContact(rs.getString("contact"));
                book.setEmail(rs.getString("email"));
                book.setGenre(rs.getString("genre"));
                book.setBookCondition(rs.getString("book_condition"));
                book.setSellerName(rs.getString("your_name"));
                book.setSellerAddress(rs.getString("your_address"));
                books.add(book);
            }

            // Manually create JSON response
            StringBuilder jsonResponse = new StringBuilder("[");
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                jsonResponse.append("{")
                        .append("\"id\":").append(book.getId()).append(",")
                        .append("\"bookName\":\"").append(book.getBookName()).append("\",")
                        .append("\"price\":").append(book.getPrice()).append(",")
                        .append("\"contact\":\"").append(book.getContact()).append("\",")
                        .append("\"email\":\"").append(book.getEmail()).append("\",")
                        .append("\"genre\":\"").append(book.getGenre()).append("\",")
                        .append("\"bookCondition\":\"").append(book.getBookCondition()).append("\",")
                        .append("\"sellerName\":\"").append(book.getSellerName()).append("\",")
                        .append("\"sellerAddress\":\"").append(book.getSellerAddress()).append("\"")
                        .append("}");

                // Add a comma between book entries, but not after the last one
                if (i < books.size() - 1) {
                    jsonResponse.append(",");
                }
            }
            jsonResponse.append("]");

            // Write the JSON response to the client
            response.getWriter().write(jsonResponse.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"Database connection error.\"}");
        }
    }
}
