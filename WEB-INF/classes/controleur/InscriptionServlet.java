package controleur;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import dao.InscriptionJDBCDAO;
import modele.Fanfaron;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@WebServlet("/inscription")
public class InscriptionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nomFanfaron = request.getParameter("nom_fanfaron");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String emailConfirm = request.getParameter("emailConfirm");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("passwordConfirm");
        String genre = request.getParameter("genre");
        String contraintes = request.getParameter("contraintes_alimentaires");

        nomFanfaron = nomFanfaron == null ? "" : nomFanfaron.trim();
        prenom = prenom == null ? "" : prenom.trim();
        nom = nom == null ? "" : nom.trim();
        email = email == null ? "" : email.trim();
        emailConfirm = emailConfirm == null ? "" : emailConfirm.trim();
        genre = genre == null ? "" : genre.trim();
        contraintes = contraintes == null ? "" : contraintes.trim();

        if (nomFanfaron.isEmpty() || prenom.isEmpty() || nom.isEmpty()
                || email.isEmpty() || emailConfirm.isEmpty()
                || password == null || password.isEmpty()
                || passwordConfirm == null || passwordConfirm.isEmpty()
                || genre.isEmpty() || contraintes.isEmpty()) {

            request.setAttribute("error", "Tous les champs sont obligatoires.");
            request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
            return;
        }

        if (!email.equalsIgnoreCase(emailConfirm)) {
            request.setAttribute("error", "Les adresses email ne correspondent pas.");
            request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
            return;
        }

        if (!password.equals(passwordConfirm)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas.");
            request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
            return;
        }

        InscriptionJDBCDAO dao = new InscriptionJDBCDAO();
        if (dao.existsByNomFanfaron(nomFanfaron)) {
            request.setAttribute("error", "Ce nom de fanfaron est déjà pris.");
            request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
            return;
        }

        if (dao.existsByEmail(email)) {
            request.setAttribute("error", "Cette adresse email est déjà utilisée.");
            request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
            return;
        }

        String motDePasseHash = hashPassword(password);

        Fanfaron fanfaron = new Fanfaron(
            nomFanfaron,
            prenom,
            nom,
            email,
            motDePasseHash,
            genre,
            contraintes
        );

        boolean ok = dao.insert(fanfaron);

        if (ok) {
            response.sendRedirect(request.getContextPath() + "/connexion");
        } else {
            request.setAttribute("error", "Erreur lors de l'inscription.");
            request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
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
