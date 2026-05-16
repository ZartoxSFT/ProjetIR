package controleur;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * SERVLET DECONNEXION - Gestion de la déconnexion utilisateur
 * 
 * Responsabilités :
 * - Invalider la session de l'utilisateur
 * - Supprimer les données stockées en session
 * - Rediriger vers la page de connexion
 * 
 * URL de routage : /deconnexion
 */
@WebServlet("/deconnexion")
public class DeconnexionServlet extends HttpServlet {

    /**
     * Traitement des requêtes GET
     * Déconnecte l'utilisateur en invalidant sa session
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupère la session existante (false = ne pas créer de nouvelle session)
        HttpSession session = request.getSession(false);

        // Si la session existe, l'invalider complètement
        // Cela supprime tous les attributs (fanfaron, utilisateur, etc.)
        if (session != null) {
            session.invalidate();
        }

        // Redirige l'utilisateur vers la page de connexion après déconnexion
        response.sendRedirect(request.getContextPath() + "/connexion");
    }
}