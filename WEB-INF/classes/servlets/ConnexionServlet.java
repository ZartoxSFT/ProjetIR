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

@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nomFanfaron = request.getParameter("nomFanfaron");
        String motDePasse = request.getParameter("motDePasse");

        if (nomFanfaron == null || nomFanfaron.trim().isEmpty() ||
                motDePasse == null || motDePasse.trim().isEmpty()) {
            request.setAttribute("erreur", "Tous les champs sont obligatoires");
            request.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(request, response);
            return;
        }

        try {
            FanfaronDAO dao = new FanfaronDAO();
            Fanfaron fanfaron = dao.getByNomFanfaron(nomFanfaron);

            if (fanfaron != null && verifierMotDePasse(motDePasse, fanfaron.getMotDePasse())) {
                // Mise à jour de la dernière connexion
                dao.updateDerniereConnexion(fanfaron.getId());

                // Stockage en session
                request.getSession().setAttribute("utilisateur", fanfaron);
                response.sendRedirect("dashboard");
            } else {
                request.setAttribute("erreur", "Identifiants incorrects");
                request.setAttribute("nomFanfaron", nomFanfaron);
                request.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("erreur", "Erreur base de données: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(request, response);
        }
    }

    // Méthode simple de vérification (à remplacer par BCrypt en production)
    private boolean verifierMotDePasse(String motDePasseSaisi, String motDePasseHashe) {
        // TODO: Utiliser BCrypt pour la vérification
        return motDePasseSaisi.equals(motDePasseHashe);
    }
}
