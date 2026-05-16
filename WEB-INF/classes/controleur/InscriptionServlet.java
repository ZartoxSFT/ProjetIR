package controleur;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import dao.DAOFactory;
import dao.FanfaronDAO;
import modele.Fanfaron;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * SERVLET INSCRIPTION - Gestion de l'enregistrement de nouveaux fanfarons
 * 
 * Responsabilités :
 * - Afficher le formulaire d'inscription (doGet)
 * - Valider les données saisies (côté serveur)
 * - Vérifier les doublons (nom d'utilisateur et email)
 * - Créer un nouveau fanfaron dans la base de données
 * - Hasher le mot de passe pour sécurité
 * 
 * Validations effectuées :
 * - Aucun champ ne doit être vide
 * - Les emails doivent correspondre (confirmation)
 * - Les mots de passe doivent correspondre (confirmation)
 * - Le nom d'utilisateur doit être unique
 * - L'email doit être unique
 * 
 * URL de routage : /inscription
 */
@WebServlet("/inscription")
public class InscriptionServlet extends HttpServlet {

    /**
     * Traitement des requêtes GET
     * Affiche le formulaire d'inscription
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward vers la page d'inscription JSP
        request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
    }

    /**
     * Traitement des requêtes POST
     * Crée un nouveau fanfaron après validation des données
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ÉTAPE 1 : Récupération de tous les paramètres du formulaire
        String nomFanfaron = request.getParameter("nomFanfaron");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String emailConfirm = request.getParameter("emailConfirm");
        String password = request.getParameter("motDePasse");
        String passwordConfirm = request.getParameter("motDePasseConfirm");
        String genre = request.getParameter("genre");
        String contraintes = request.getParameter("contraintesAlimentaires");

        // Nettoyage des données : suppression des espaces et gestion des null
        nomFanfaron = nomFanfaron == null ? "" : nomFanfaron.trim();
        prenom = prenom == null ? "" : prenom.trim();
        nom = nom == null ? "" : nom.trim();
        email = email == null ? "" : email.trim();
        emailConfirm = emailConfirm == null ? "" : emailConfirm.trim();
        genre = genre == null ? "" : genre.trim();
        contraintes = contraintes == null ? "" : contraintes.trim();

        // ÉTAPE 2 : Validation des champs obligatoires
        // Utilisation d'une HashMap pour stocker les erreurs avec la clé du champ
        java.util.Map<String, String> erreurs = new java.util.HashMap<>();

        // Vérifications de non-nullité et de non-vide
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

        // Si des champs obligatoires sont vides, retourner au formulaire avec erreurs
        if (!erreurs.isEmpty()) {
            // Réaffectation des valeurs pour réaffichage du formulaire rempli
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

        // ÉTAPE 3 : Validation de la correspondance des emails et mots de passe
        // Comparaison insensible à la casse pour les emails (exemple.fr == Exemple.FR)
        if (!email.equalsIgnoreCase(emailConfirm)) {
            erreurs.put("emailConfirm", "Les adresses email ne correspondent pas.");
        }

        // Comparaison sensible à la casse pour les mots de passe
        if (!password.equals(passwordConfirm)) {
            erreurs.put("motDePasseConfirm", "Les mots de passe ne correspondent pas.");
        }

        // Récupération du DAO via la factory
        FanfaronDAO dao = DAOFactory.getFanfaronDAO();
        
        try {
            // ÉTAPE 4 : Vérification des doublons en base de données
            // Vérifier que le nom d'utilisateur n'existe pas déjà
            if (dao.existsByNomFanfaron(nomFanfaron)) {
                erreurs.put("nomFanfaron", "Ce nom de fanfaron est déjà pris.");
            }

            // Vérifier que l'email n'existe pas déjà
            if (dao.existsByEmail(email)) {
                erreurs.put("email", "Cette adresse email est déjà utilisée.");
            }
        } catch (Exception e) {
            // Gestion des erreurs de base de données
            e.printStackTrace();
            erreurs.put("global", "Erreur lors de la verification des informations. Reessayez plus tard.");
        }

        // Si des erreurs ont été détectées, retourner au formulaire
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

        // ÉTAPE 5 : Hachage du mot de passe
        // Le mot de passe n'est jamais stocké en clair pour des raisons de sécurité
        String motDePasseHash = hashPassword(password);

        // ÉTAPE 6 : Création de l'objet Fanfaron
        // Constructeur qui initialise un nouveau fanfaron avec les données saisies
        Fanfaron fanfaron = new Fanfaron(
                nomFanfaron,
                prenom,
                nom,
                email,
                motDePasseHash,      // On passe le hash, pas le mot de passe en clair
                genre,
                contraintes);

        // ÉTAPE 7 : Insertion dans la base de données
        boolean ok = dao.addFanfaron(fanfaron);

        // ÉTAPE 8 : Traitement du résultat
        if (ok) {
            // Inscription réussie : afficher un message de succès
            request.setAttribute("success", "Inscription réussie. Vous pouvez vous connecter.");
            request.getRequestDispatcher("/vue/inscription.jsp").forward(request, response);
        } else {
            // Erreur lors de l'insertion : afficher le formulaire avec message d'erreur
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

    /**
     * Méthode utilitaire pour hasher un mot de passe
     * 
     * Algorithme : SHA-256
     * Encodage : Base64
     * Charset : UTF-8
     * 
     * @param password Le mot de passe en clair
     * @return Le mot de passe hashé et encodé en Base64
     */
    private String hashPassword(String password) {
        try {
            // Création de l'instance MessageDigest avec SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Hachage du mot de passe
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Encodage en Base64 pour manipulation textuelle
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 est toujours disponible en Java standard
            throw new IllegalStateException("Impossible de hacher le mot de passe.", e);
        }
    }
}
