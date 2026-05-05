package controleur;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import dao.ConnexionJDBCDAO;
import modele.Fanfaron;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/vue/connexion.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nomFanfaron = request.getParameter("nomFanfaron");
        String password = request.getParameter("motDePasse");

        nomFanfaron = nomFanfaron == null ? "" : nomFanfaron.trim();
        password = password == null ? "" : password;

        if (nomFanfaron.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Tous les champs sont obligatoires.");
            request.getRequestDispatcher("/vue/connexion.jsp").forward(request, response);
            return;
        }

        String motDePasseHash = hashPassword(password);

        ConnexionJDBCDAO dao = new ConnexionJDBCDAO();
        Fanfaron fanfaron = dao.authenticate(nomFanfaron, motDePasseHash);

        if (fanfaron != null) {
            HttpSession session = request.getSession();
            session.setAttribute("fanfaron", fanfaron);
            session.setAttribute("utilisateur", fanfaron);

            response.sendRedirect(request.getContextPath() + "/accueil");
        } else {
            request.setAttribute("error", "Nom de fanfaron ou mot de passe incorrect.");
            request.getRequestDispatcher("/vue/connexion.jsp").forward(request, response);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Impossible de hacher le mot de passe.", e);
        }
    }
}
