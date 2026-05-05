package controleur;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import dao.AdminJDBCDAO;
import modele.Fanfaron;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier que l'utilisateur est admin
        HttpSession session = request.getSession();
        Fanfaron utilisateur = (Fanfaron) session.getAttribute("utilisateur");

        if (utilisateur == null || !utilisateur.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        String action = request.getParameter("action");
        AdminJDBCDAO dao = new AdminJDBCDAO();

        if ("delete".equals(action)) {
            long id = Long.parseLong(request.getParameter("id"));
            if (dao.deleteFanfaron(id)) {
                request.setAttribute("succes", "Fanfaron supprimé avec succès.");
            } else {
                request.setAttribute("erreur", "Erreur lors de la suppression.");
            }
        } else if ("edit".equals(action)) {
            long id = Long.parseLong(request.getParameter("id"));
            Fanfaron fanfaron = dao.getFanfaronById(id);
            request.setAttribute("fanfaron", fanfaron);
        }

        // Charger la liste des fanfarons
        List<Fanfaron> fanfarons = dao.getAllFanfarons();
        request.setAttribute("fanfarons", fanfarons);

        request.getRequestDispatcher("/vue/admin.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier que l'utilisateur est admin
        HttpSession session = request.getSession();
        Fanfaron utilisateur = (Fanfaron) session.getAttribute("utilisateur");

        if (utilisateur == null || !utilisateur.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        String action = request.getParameter("action");
        AdminJDBCDAO dao = new AdminJDBCDAO();

        if ("add".equals(action)) {
            handleAddFanfaron(request, dao);
        } else if ("update".equals(action)) {
            handleUpdateFanfaron(request, dao);
        }

        // Charger la liste des fanfarons
        java.util.List<Fanfaron> fanfarons = dao.getAllFanfarons();
        request.setAttribute("fanfarons", fanfarons);

        // Forward vers la page admin
        request.getRequestDispatcher("/vue/admin.jsp").forward(request, response);
    }

    private void handleAddFanfaron(HttpServletRequest request, AdminJDBCDAO dao) {
        String nomFanfaron = request.getParameter("nomFanfaron");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String password = request.getParameter("motDePasse");
        String genre = request.getParameter("genre");
        String contraintes = request.getParameter("contraintesAlimentaires");
        boolean isAdmin = request.getParameter("admin") != null;

        if (nomFanfaron == null || nomFanfaron.trim().isEmpty() ||
                prenom == null || prenom.trim().isEmpty() ||
                nom == null || nom.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            request.setAttribute("erreur", "Tous les champs sont obligatoires.");
            return;
        }

        String motDePasseHash = hashPassword(password);

        Fanfaron fanfaron = new Fanfaron();
        fanfaron.setNomFanfaron(nomFanfaron);
        fanfaron.setPrenom(prenom);
        fanfaron.setNom(nom);
        fanfaron.setEmail(email);
        fanfaron.setMotDePasse(motDePasseHash);
        fanfaron.setGenre(genre);
        fanfaron.setContraintesAlimentaires(contraintes);
        fanfaron.setIsAdmin(isAdmin);

        if (dao.addFanfaron(fanfaron)) {
            request.setAttribute("succes", "Fanfaron ajouté avec succès.");
        } else {
            request.setAttribute("erreur", "Erreur lors de l'ajout du fanfaron.");
        }
    }

    private void handleUpdateFanfaron(HttpServletRequest request, AdminJDBCDAO dao) {
        long id = Long.parseLong(request.getParameter("id"));
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String genre = request.getParameter("genre");
        String contraintes = request.getParameter("contraintesAlimentaires");
        boolean isAdmin = request.getParameter("isAdmin") != null;

        Fanfaron fanfaron = new Fanfaron();
        fanfaron.setId(id);
        fanfaron.setPrenom(prenom);
        fanfaron.setNom(nom);
        fanfaron.setEmail(email);
        fanfaron.setGenre(genre);
        fanfaron.setContraintesAlimentaires(contraintes);
        fanfaron.setIsAdmin(isAdmin);

        if (dao.updateFanfaron(fanfaron)) {
            request.setAttribute("succes", "Fanfaron modifié avec succès.");
        } else {
            request.setAttribute("erreur", "Erreur lors de la modification.");
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
