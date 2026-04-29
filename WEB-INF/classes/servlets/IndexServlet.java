package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class IndexServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si l'utilisateur est connecté, rediriger vers le dashboard
        if (request.getSession(false) != null && request.getSession().getAttribute("utilisateur") != null) {
            response.sendRedirect("dashboard");
        } else {
            // Sinon, rediriger vers la connexion
            response.sendRedirect("connexion");
        }
    }
}
