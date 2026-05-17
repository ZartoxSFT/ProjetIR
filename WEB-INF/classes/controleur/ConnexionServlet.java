package controleur;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import dao.DAOFactory;
import dao.FanfaronDAO;
import modele.Fanfaron;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * SERVLET CONNEXION - Gestion de l'authentification des fanfarons
 * 
 * Responsabilités :
 * - Afficher le formulaire de connexion (doGet)
 * - Vérifier les identifiants saisis (doPost)
 * - Hasher le mot de passe pour comparaison sécurisée
 * - Créer une session utilisateur après authentification réussie
 * 
 * Sécurité :
 * - Les mots de passe sont hashés avec SHA-256
 * - Le hash est encodé en Base64 pour stockage
 * 
 * URL de routage : /connexion
 */
@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {

    /**
     * Traitement des requêtes GET
     * Affiche le formulaire de connexion
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward vers la page de connexion JSP
        request.getRequestDispatcher("/vue/connexion.jsp").forward(request, response);
    }

    /**
     * Traitement des requêtes POST
     * Authentifie l'utilisateur en validant ses identifiants
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupération des paramètres du formulaire
        String nomFanfaron = request.getParameter("nomFanfaron");
        String password = request.getParameter("motDePasse");

        // Nettoyage des données : suppression des espaces et gestion des null
        nomFanfaron = nomFanfaron == null ? "" : nomFanfaron.trim();
        password = password == null ? "" : password;

        // Validation : vérifier que les champs ne sont pas vides
        if (nomFanfaron.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Tous les champs sont obligatoires.");
            request.getRequestDispatcher("/vue/connexion.jsp").forward(request, response);
            return;
        }

        // Hachage du mot de passe saisi pour le comparer avec celui en base de données
        String motDePasseHash = hashPassword(password);

        // Récupération du DAO via la factory (patron de conception Factory)
        FanfaronDAO dao = DAOFactory.getFanfaronDAO();
        Fanfaron fanfaron;

        try {
            // Appel à la méthode authenticate() du DAO
            // Cette méthode retourne un Fanfaron si les identifiants sont corrects, null sinon
            fanfaron = dao.authenticate(nomFanfaron, motDePasseHash);
        } catch (Exception e) {
            // Gestion des erreurs de base de données
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la connexion. Reessayez plus tard.");
            request.getRequestDispatcher("/vue/connexion.jsp").forward(request, response);
            return;
        }

        // Si un fanfaron a été trouvé avec ces identifiants
        if (fanfaron != null) {
            // Création ou récupération de la session
            HttpSession session = request.getSession();
            
            // Stockage du fanfaron en session sous deux noms différents
            // "fanfaron" et "utilisateur" pour compatibilité avec différents servlets
            session.setAttribute("fanfaron", fanfaron);
            session.setAttribute("utilisateur", fanfaron);

            // Redirection vers la page d'accueil après connexion réussie
            response.sendRedirect(request.getContextPath() + "/accueil");
        } else {
            // Identifiants incorrects : afficher un message d'erreur
            request.setAttribute("error", "Nom de fanfaron ou mot de passe incorrect.");
            request.getRequestDispatcher("/vue/connexion.jsp").forward(request, response);
        }
    }

    /**
     * Méthode utilitaire pour hasher un mot de passe
     * 
     * Algorithme : SHA-256 (Secure Hash Algorithm 256-bit)
     * Encodage final : Base64
     * 
     * @param password Le mot de passe en clair à hasher
     * @return Le mot de passe hashé et encodé en Base64
     * @throws IllegalStateException Si l'algorithme SHA-256 n'est pas disponible
     */
    private String hashPassword(String password) {
        try {
            // Création d'un instance de MessageDigest avec l'algorithme SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Hachage du mot de passe converti en bytes avec charset UTF-8
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Encodage du hash en Base64 pour faciliter le stockage/transmission
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 doit toujours être disponible en Java
            throw new IllegalStateException("Impossible de hacher le mot de passe.", e);
        }
    }
}
