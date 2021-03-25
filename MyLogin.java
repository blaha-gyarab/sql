package main.Java.classes;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/login")
public class MyLogin extends HttpServlet {
    private long lastLoginATime = (System.currentTimeMillis() - 15100);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        String name = request.getParameter("name");
        String pass = request.getParameter("passwd");
		long currentTime = System.currentTimeMillis();

        if ((lastLoginATime + 15000) < currentTime) {
            if (name == null || pass == null) {
                out.println("Chyba: chybi parametr 'name' nebo 'passwd'");
            } else {
                String jdbcURL = System.getenv("JDBC_DATABASE_URL");

                try (Connection conn = DriverManager.getConnection(jdbcURL)) {
                    {
                        String sql = "SELECT * FROM users WHERE name = ? AND passwd = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, name);
                        stmt.setString(2, pass);
                        ResultSet rs = stmt.executeQuery(sql);

                        if (rs.next()) {
                            out.println("OK");
                        } else {
                            out.println("spatne jmeno nebo heslo");
                            lastLoginATime = System.currentTimeMillis();
                            
                        }
                    }
                } catch (SQLException | InterruptedException ex) {
                    out.println("Chyba: chyba pri praci s databazi:\n\n" + ex);
                }
            }
        } else {
			long timeoutRemain = (System.currentTimeMillis() - lastLoginATime);
            out.println("timeout " + timeoutRemain +  "  zbyva milisekund");
        }
        out.println("</body></html>");
    }
}