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
import java.util.List;

/**
 * SERVLET ADMINISTRATION - Gestion administrative des fanfarons
 * 
 * Responsabilités :
 * - Accès réservé aux administrateurs
 * - Affichage de la liste des fanfarons
 * - Création de nouveaux fanfarons
 * - Modification des informations d'un fanfaron
 * - Suppression de fanfarons
 * - Gestion du statut administrateur
 * 
 * Sécurité :
 * - Vérification du statut admin à chaque requête
 * - Redirection vers la connexion si accès non autorisé
 * 
 * URL de routage : /admin
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    /**
     * Traitement des requêtes GET
     * Affiche la page admin avec la liste des fanfarons
     * Permet les actions : delete, edit
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupération de la session (sans création de nouvelle)
        HttpSession session = request.getSession();
        
        // Récupération de l'utilisateur connecté
        Fanfaron utilisateur = (Fanfaron) session.getAttribute("utilisateur");

        // SÉCURITÉ : Vérification du statut administrateur
        // Si l'utilisateur n'est pas connecté ou n'est pas admin, redirection vers la connexion
        if (utilisateur == null || !utilisateur.getAdmin()) {
            response.sendRedirect(request.getContextPath() + "/accueil");
            return;
        }

        // Récupération de l'action demandée via paramètre GET
        String action = request.getParameter("action");
        
        // Initialisation du DAO pour accès à la base de données
        FanfaronDAO dao = DAOFactory.getFanfaronDAO();

        // Traitement des actions possibles en GET
        if ("delete".equals(action)) {
            // ACTION DELETE : Suppression d'un fanfaron par son ID
            long id = Long.parseLong(request.getParameter("id"));
            if (dao.deleteFanfaron(id)) {
                // Message de succès si la suppression a réussi
                request.setAttribute("succes", "Fanfaron supprimé avec succès.");
            } else {
                // Message d'erreur si la suppression a échoué
                request.setAttribute("erreur", "Erreur lors de la suppression.");
            }
        } else if ("edit".equals(action)) {
            // ACTION EDIT : Affichage du formulaire d'édition
            // Récupération des données du fanfaron à modifier
            long id = Long.parseLong(request.getParameter("id"));
            Fanfaron fanfaron = dao.getFanfaronById(id);
            request.setAttribute("fanfaron", fanfaron);
        }

        // Chargement de la liste complète des fanfarons pour affichage dans le tableau
        List<Fanfaron> fanfarons = dao.getAllFanfarons();
        request.setAttribute("fanfarons", fanfarons);

        // Forward vers la page JSP d'administration
        request.getRequestDispatcher("/vue/admin.jsp").forward(request, response);
    }

    /**
     * Traitement des requêtes POST
     * Crée ou modifie les fanfarons selon l'action
     * Actions possibles : add, update
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupération de la session
        HttpSession session = request.getSession();
        
        // Récupération de l'utilisateur connecté
        Fanfaron utilisateur = (Fanfaron) session.getAttribute("utilisateur");

        // SÉCURITÉ : Vérification du statut administrateur
        if (utilisateur == null || !utilisateur.getAdmin()) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        // Récupération de l'action demandée via paramètre POST
        String action = request.getParameter("action");
        
        // Initialisation du DAO
        FanfaronDAO dao = DAOFactory.getFanfaronDAO();

        // Traitement des actions POST
        if ("add".equals(action)) {
            // ACTION ADD : Création d'un nouveau fanfaron
            handleAddFanfaron(request, dao);
        } else if ("update".equals(action)) {
            // ACTION UPDATE : Modification d'un fanfaron existant
            handleUpdateFanfaron(request, dao);
        }

        // Chargement de la liste des fanfarons après l'opération
        java.util.List<Fanfaron> fanfarons = dao.getAllFanfarons();
        request.setAttribute("fanfarons", fanfarons);

        // Forward vers la page admin pour affichage
        request.getRequestDispatcher("/vue/admin.jsp").forward(request, response);
    }

    /**
     * Méthode helper pour ajouter un nouveau fanfaron
     * Validation et création d'un nouveau fanfaron en base de données
     * 
     * @param request La requête contenant les paramètres du formulaire
     * @param dao Le DAO pour les opérations en base de données
     */
    private void handleAddFanfaron(HttpServletRequest request, FanfaronDAO dao) {
        // Récupération de tous les paramètres du formulaire
        String nomFanfaron = request.getParameter("nomFanfaron");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String password = request.getParameter("motDePasse");
        String genre = request.getParameter("genre");
        String contraintes = request.getParameter("contraintesAlimentaires");
        
        // Vérification du statut admin (true si la checkbox est cochée, false sinon)
        boolean isAdmin = request.getParameter("isAdmin") != null;

        // Validation des champs obligatoires
        if (nomFanfaron == null || nomFanfaron.trim().isEmpty() ||
                prenom == null || prenom.trim().isEmpty() ||
                nom == null || nom.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            request.setAttribute("erreur", "Tous les champs sont obligatoires.");
            return;
        }

        // Hachage du mot de passe
        String motDePasseHash = hashPassword(password);

        // Création d'une nouvelle instance Fanfaron
        Fanfaron fanfaron = new Fanfaron();
        fanfaron.setNomFanfaron(nomFanfaron);
        fanfaron.setPrenom(prenom);
        fanfaron.setNom(nom);
        fanfaron.setEmail(email);
        fanfaron.setMotDePasse(motDePasseHash);
        fanfaron.setGenre(genre);
        fanfaron.setContraintesAlimentaires(contraintes);
        fanfaron.setAdmin(isAdmin);

        // Insertion en base de données
        if (dao.addFanfaron(fanfaron)) {
            request.setAttribute("succes", "Fanfaron ajouté avec succès.");
        } else {
            request.setAttribute("erreur", "Erreur lors de l'ajout du fanfaron.");
        }
    }

    /**
     * Méthode helper pour modifier un fanfaron existant
     * Mise à jour des données d'un fanfaron dans la base de données
     * 
     * @param request La requête contenant les paramètres du formulaire
     * @param dao Le DAO pour les opérations en base de données
     */
    private void handleUpdateFanfaron(HttpServletRequest request, FanfaronDAO dao) {
        // Récupération de l'ID du fanfaron à modifier
        long id = Long.parseLong(request.getParameter("id"));
        
        // Récupération des paramètres du formulaire (à l'exception du mot de passe)
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String genre = request.getParameter("genre");
        String contraintes = request.getParameter("contraintesAlimentaires");
        
        // Statut admin basé sur la présence de la checkbox
        boolean isAdmin = request.getParameter("isAdmin") != null;

        // Création de l'objet Fanfaron avec les données modifiées
        Fanfaron fanfaron = new Fanfaron();
        fanfaron.setId(id);
        fanfaron.setPrenom(prenom);
        fanfaron.setNom(nom);
        fanfaron.setEmail(email);
        fanfaron.setGenre(genre);
        fanfaron.setContraintesAlimentaires(contraintes);
        fanfaron.setAdmin(isAdmin);

        // Mise à jour en base de données
        if (dao.updateFanfaron(fanfaron)) {
            request.setAttribute("succes", "Fanfaron modifié avec succès.");
        } else {
            request.setAttribute("erreur", "Erreur lors de la modification.");
        }
    }

    /**
     * Méthode utilitaire pour hasher un mot de passe
     * Utilise l'algorithme SHA-256 avec encodage Base64
     * 
     * @param password Le mot de passe en clair à hasher
     * @return Le mot de passe hashé et encodé en Base64
     */
    private String hashPassword(String password) {
        try {
            // Création de l'instance MessageDigest avec SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Hachage du mot de passe en UTF-8
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Encodage en Base64
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 est toujours disponible en Java
            throw new IllegalStateException("Impossible de hacher le mot de passe.", e);
        }
    }
}
