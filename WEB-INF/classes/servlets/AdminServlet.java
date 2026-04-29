package servlets;

import dao.FanfaronDAO;
import modele.Fanfaron;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        try {
            FanfaronDAO dao = new FanfaronDAO();

            switch (action) {
                case "list":
                    afficherListe(request, response, dao);
                    break;
                case "edit":
                    afficherFormulaireModification(request, response, dao);
                    break;
                case "delete":
                    supprimerFanfaron(request, response, dao);
                    break;
                default:
                    afficherListe(request, response, dao);
            }
        } catch (SQLException e) {
            request.setAttribute("erreur", "Erreur base de données: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/admin.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            FanfaronDAO dao = new FanfaronDAO();

            if ("add".equals(action)) {
                ajouterFanfaron(request, response, dao);
            } else if ("update".equals(action)) {
                mettreAJourFanfaron(request, response, dao);
            } else if ("changeRole".equals(action)) {
                changerRole(request, response, dao);
            }
        } catch (SQLException e) {
            request.setAttribute("erreur", "Erreur base de données: " + e.getMessage());
            try {
                afficherListe(request, response, new FanfaronDAO());
            } catch (SQLException ignored) {
            }
        }
    }

    private void afficherListe(HttpServletRequest request, HttpServletResponse response, FanfaronDAO dao)
            throws SQLException, ServletException, IOException {
        List<Fanfaron> fanfarons = dao.getAll();
        request.setAttribute("fanfarons", fanfarons);
        request.getRequestDispatcher("/WEB-INF/jsp/admin.jsp").forward(request, response);
    }

    private void afficherFormulaireModification(HttpServletRequest request, HttpServletResponse response,
            FanfaronDAO dao)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Fanfaron fanfaron = dao.getById(id);
        request.setAttribute("fanfaron", fanfaron);
        request.setAttribute("mode", "edit");
        List<Fanfaron> fanfarons = dao.getAll();
        request.setAttribute("fanfarons", fanfarons);
        request.getRequestDispatcher("/WEB-INF/jsp/admin.jsp").forward(request, response);
    }

    private void supprimerFanfaron(HttpServletRequest request, HttpServletResponse response, FanfaronDAO dao)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        dao.delete(id);
        response.sendRedirect("admin");
    }

    private void ajouterFanfaron(HttpServletRequest request, HttpServletResponse response, FanfaronDAO dao)
            throws SQLException, ServletException, IOException {
        String nomFanfaron = request.getParameter("nomFanfaron");
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String genre = request.getParameter("genre");
        String contraintesAlimentaires = request.getParameter("contraintesAlimentaires");
        String role = request.getParameter("role");

        // Vérifications basiques
        if (nomFanfaron == null || nomFanfaron.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                motDePasse == null || motDePasse.trim().isEmpty()) {
            request.setAttribute("erreur", "Veuillez remplir tous les champs obligatoires");
            afficherListe(request, response, dao);
            return;
        }

        // Vérifier l'unicité
        if (dao.getByNomFanfaron(nomFanfaron) != null) {
            request.setAttribute("erreur", "Ce nom d'utilisateur existe déjà");
            afficherListe(request, response, dao);
            return;
        }

        if (dao.getByEmail(email) != null) {
            request.setAttribute("erreur", "Cet email est déjà utilisé");
            afficherListe(request, response, dao);
            return;
        }

        Fanfaron fanfaron = new Fanfaron();
        fanfaron.setNomFanfaron(nomFanfaron);
        fanfaron.setEmail(email);
        fanfaron.setMotDePasse(motDePasse); // TODO: Hasher avec BCrypt
        fanfaron.setPrenom(prenom);
        fanfaron.setNom(nom);
        fanfaron.setGenre(genre);
        fanfaron.setContraintesAlimentaires(contraintesAlimentaires);
        fanfaron.setRole(role != null && !role.isEmpty() ? role : "utilisateur");

        dao.create(fanfaron);
        request.setAttribute("succes", "Fanfaron créé avec succès");
        afficherListe(request, response, dao);
    }

    private void mettreAJourFanfaron(HttpServletRequest request, HttpServletResponse response, FanfaronDAO dao)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String nomFanfaron = request.getParameter("nomFanfaron");
        String email = request.getParameter("email");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String genre = request.getParameter("genre");
        String contraintesAlimentaires = request.getParameter("contraintesAlimentaires");
        String role = request.getParameter("role");

        Fanfaron fanfaron = dao.getById(id);
        fanfaron.setNomFanfaron(nomFanfaron);
        fanfaron.setEmail(email);
        fanfaron.setPrenom(prenom);
        fanfaron.setNom(nom);
        fanfaron.setGenre(genre);
        fanfaron.setContraintesAlimentaires(contraintesAlimentaires);
        fanfaron.setRole(role);

        dao.update(fanfaron);
        request.setAttribute("succes", "Fanfaron mis à jour avec succès");
        afficherListe(request, response, dao);
    }

    private void changerRole(HttpServletRequest request, HttpServletResponse response, FanfaronDAO dao)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String nouveauRole = request.getParameter("role");
        dao.updateRole(id, nouveauRole);
        response.sendRedirect("admin");
    }
}
