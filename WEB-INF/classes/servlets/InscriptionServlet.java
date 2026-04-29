package servlets;

import dao.FanfaronDAO;
import modele.Fanfaron;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/inscription")
public class InscriptionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/inscription.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Récupération des paramètres
        String nomFanfaron = request.getParameter("nomFanfaron");
        String email = request.getParameter("email");
        String emailConfirm = request.getParameter("emailConfirm");
        String motDePasse = request.getParameter("motDePasse");
        String motDePasseConfirm = request.getParameter("motDePasseConfirm");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String genre = request.getParameter("genre");
        String contraintesAlimentaires = request.getParameter("contraintesAlimentaires");

        // Validation
        Map<String, String> erreurs = validerFormulaire(nomFanfaron, email, emailConfirm,
                motDePasse, motDePasseConfirm, prenom, nom, genre);

        if (!erreurs.isEmpty()) {
            request.setAttribute("erreurs", erreurs);
            request.setAttribute("nomFanfaron", nomFanfaron);
            request.setAttribute("email", email);
            request.setAttribute("prenom", prenom);
            request.setAttribute("nom", nom);
            request.setAttribute("genre", genre);
            request.setAttribute("contraintesAlimentaires", contraintesAlimentaires);
            request.getRequestDispatcher("/WEB-INF/jsp/inscription.jsp").forward(request, response);
            return;
        }

        try {
            FanfaronDAO dao = new FanfaronDAO();

            // Vérifier l'unicité du nom et email
            if (dao.getByNomFanfaron(nomFanfaron) != null) {
                erreurs.put("nomFanfaron", "Ce nom d'utilisateur est déjà utilisé");
            }
            if (dao.getByEmail(email) != null) {
                erreurs.put("email", "Cet email est déjà utilisé");
            }

            if (!erreurs.isEmpty()) {
                request.setAttribute("erreurs", erreurs);
                request.setAttribute("nomFanfaron", nomFanfaron);
                request.setAttribute("email", email);
                request.setAttribute("prenom", prenom);
                request.setAttribute("nom", nom);
                request.setAttribute("genre", genre);
                request.setAttribute("contraintesAlimentaires", contraintesAlimentaires);
                request.getRequestDispatcher("/WEB-INF/jsp/inscription.jsp").forward(request, response);
                return;
            }

            // Créer le fanfaron
            Fanfaron fanfaron = new Fanfaron();
            fanfaron.setNomFanfaron(nomFanfaron);
            fanfaron.setEmail(email);
            fanfaron.setMotDePasse(hasherMotDePasse(motDePasse)); // TODO: utiliser BCrypt
            fanfaron.setPrenom(prenom);
            fanfaron.setNom(nom);
            fanfaron.setGenre(genre);
            fanfaron.setContraintesAlimentaires(contraintesAlimentaires);
            fanfaron.setRole("utilisateur");

            dao.create(fanfaron);

            // Connexion automatique après inscription
            request.getSession().setAttribute("utilisateur", fanfaron);
            response.sendRedirect("dashboard");

        } catch (SQLException e) {
            erreurs.put("general", "Erreur base de données: " + e.getMessage());
            request.setAttribute("erreurs", erreurs);
            request.getRequestDispatcher("/WEB-INF/jsp/inscription.jsp").forward(request, response);
        }
    }

    private Map<String, String> validerFormulaire(String nomFanfaron, String email, String emailConfirm,
            String motDePasse, String motDePasseConfirm,
            String prenom, String nom, String genre) {
        Map<String, String> erreurs = new HashMap<>();

        if (nomFanfaron == null || nomFanfaron.trim().isEmpty()) {
            erreurs.put("nomFanfaron", "Le nom d'utilisateur est obligatoire");
        } else if (nomFanfaron.length() < 3 || nomFanfaron.length() > 50) {
            erreurs.put("nomFanfaron", "Le nom doit contenir entre 3 et 50 caractères");
        }

        if (email == null || email.trim().isEmpty()) {
            erreurs.put("email", "L'email est obligatoire");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            erreurs.put("email", "L'email n'est pas valide");
        }

        if (emailConfirm == null || !emailConfirm.equals(email)) {
            erreurs.put("emailConfirm", "Les emails ne correspondent pas");
        }

        if (motDePasse == null || motDePasse.isEmpty()) {
            erreurs.put("motDePasse", "Le mot de passe est obligatoire");
        } else if (motDePasse.length() < 6) {
            erreurs.put("motDePasse", "Le mot de passe doit avoir au minimum 6 caractères");
        }

        if (motDePasseConfirm == null || !motDePasseConfirm.equals(motDePasse)) {
            erreurs.put("motDePasseConfirm", "Les mots de passe ne correspondent pas");
        }

        if (prenom == null || prenom.trim().isEmpty()) {
            erreurs.put("prenom", "Le prénom est obligatoire");
        }

        if (nom == null || nom.trim().isEmpty()) {
            erreurs.put("nom", "Le nom est obligatoire");
        }

        if (genre == null || genre.isEmpty()) {
            erreurs.put("genre", "Veuillez sélectionner un genre");
        }

        return erreurs;
    }

    // Méthode simple de hachage (à remplacer par BCrypt)
    private String hasherMotDePasse(String motDePasse) {
        // TODO: Utiliser BCrypt
        return motDePasse;
    }
}
