package controleur;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import dao.FanfaronDAO;
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

        String nomFanfaron = request.getParameter("nomFanfaron");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String emailConfirm = request.getParameter("emailConfirm");
        String password = request.getParameter("motDePasse");
        String passwordConfirm = request.getParameter("motDePasseConfirm");
        String genre = request.getParameter("genre");
        String contraintes = request.getParameter("contraintesAlimentaires");

        nomFanfaron = nomFanfaron == null ? "" : nomFanfaron.trim();
        prenom = prenom == null ? "" : prenom.trim();
        nom = nom == null ? "" : nom.trim();
        email = email == null ? "" : email.trim();
        emailConfirm = emailConfirm == null ? "" : emailConfirm.trim();
        genre = genre == null ? "" : genre.trim();
        contraintes = contraintes == null ? "" : contraintes.trim();

        java.util.Map<String, String> erreurs = new java.util.HashMap<>();

        if (nomFanfaron == null || nomFanfaron.trim().isEmpty()) {
            erreurs.put("nomFanfaron", "Le nom d'utilisateur est obligatoire.");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            erreurs.put("prenom", "Le prénom est obligatoire.");
        }
        if (nom == null || nom.trim().isEmpty()) {
            erreurs.put("nom", "Le nom est obligatoire.");
        }
        if (email == null || email.trim().isEmpty()) {
            erreurs.put("email", "L'email est obligatoire.");
        }
        if (emailConfirm == null || emailConfirm.trim().isEmpty()) {
            erreurs.put("emailConfirm", "La confirmation de l'email est obligatoire.");
        }
        if (password == null || password.isEmpty()) {
            erreurs.put("motDePasse", "Le mot de passe est obligatoire.");
        }
        if (passwordConfirm == null || passwordConfirm.isEmpty()) {
            erreurs.put("motDePasseConfirm", "La confirmation du mot de passe est obligatoire.");
        }
        if (genre == null || genre.trim().isEmpty()) {
            erreurs.put("genre", "Le genre est obligatoire.");
        }

        if (!erreurs.isEmpty()) {
            // réaffecte les valeurs pour réaffichage
            request.setAttribute("erreurs", erreurs);
            request.setAttribute("nomFanfaron", nomFanfaron);
            request.setAttribute("prenom", prenom);
            request.setAttribute("nom", nom);
            request.setAttribute("email", email);
            request.setAttribute("genre", genre);
            request.setAttribute("contraintesAlimentaires", contraintes);
            request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
            return;
        }

        if (!email.equalsIgnoreCase(emailConfirm)) {
            erreurs.put("emailConfirm", "Les adresses email ne correspondent pas.");
        }

        if (!password.equals(passwordConfirm)) {
            erreurs.put("motDePasseConfirm", "Les mots de passe ne correspondent pas.");
        }

        FanfaronDAO dao = new FanfaronDAO();
        try {
        if (dao.existsByNomFanfaron(nomFanfaron)) {
            erreurs.put("nomFanfaron", "Ce nom de fanfaron est déjà pris.");
        }

        if (dao.existsByEmail(email)) {
            erreurs.put("email", "Cette adresse email est déjà utilisée.");
        }
        } catch (Exception e) {
            e.printStackTrace();
            erreurs.put("global", "Erreur lors de la verification des informations. Reessayez plus tard.");
        }

        if (!erreurs.isEmpty()) {
            request.setAttribute("erreurs", erreurs);
            request.setAttribute("nomFanfaron", nomFanfaron);
            request.setAttribute("prenom", prenom);
            request.setAttribute("nom", nom);
            request.setAttribute("email", email);
            request.setAttribute("genre", genre);
            request.setAttribute("contraintesAlimentaires", contraintes);
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
                contraintes);

        boolean ok = dao.addFanfaron(fanfaron);

        if (ok) {
            request.setAttribute("success", "Inscription réussie. Vous pouvez vous connecter.");
            request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
        } else {
            erreurs.put("global", "Erreur lors de l'inscription. Réessayez plus tard.");
            request.setAttribute("erreurs", erreurs);
            request.setAttribute("nomFanfaron", nomFanfaron);
            request.setAttribute("prenom", prenom);
            request.setAttribute("nom", nom);
            request.setAttribute("email", email);
            request.setAttribute("genre", genre);
            request.setAttribute("contraintesAlimentaires", contraintes);
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
