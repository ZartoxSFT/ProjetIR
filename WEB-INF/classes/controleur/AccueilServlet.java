package controleur;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.DAOFactory;
import dao.EvenementDAO;
import dao.InstrumentDAO;
import modele.EvenementInscrit;
import modele.Fanfaron;
import modele.GroupeFanfare;
import modele.Instrument;

/**
 * SERVLET ACCUEIL - Gestion de la page d'accueil
 * 
 * Responsabilités :
 * - Vérifier que l'utilisateur est connecté (authentification)
 * - Charger les données personnelles du fanfaron
 * - Récupérer les instruments joués et les groupes d'appartenance
 * - Récupérer la liste des événements auxquels il est inscrit
 * - Transmettre les données à la vue JSP pour affichage
 * 
 * URL de routage : /accueil
 */
@WebServlet("/accueil")
public class AccueilServlet extends HttpServlet {
    
    /**
     * Traitement des requêtes GET
     * Affiche la page d'accueil avec les informations du fanfaron
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupère la session existante (sans créer de nouvelle si elle n'existe pas)
        HttpSession session = request.getSession(false);

        // Vérification d'authentification : si pas de session ou pas de fanfaron en session
        // L'utilisateur est redirigé vers la page de connexion
        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        // Récupération de l'objet Fanfaron stocké en session après la connexion
        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");
        
        // Initialisation des DAO (Data Access Objects) via la factory
        // Pattern Factory : centralise la création des DAO
        InstrumentDAO instrumentDao = DAOFactory.getInstrumentDAO();
        EvenementDAO evenementDao = DAOFactory.getEvenementDAO();

        // Récupération des instruments joués par le fanfaron via leur ID
        List<Instrument> instrumentsJoues = instrumentDao.findInstrumentsByFanfaron(fanfaron.getId());
        
        // Récupération des groupes de fanfare auxquels appartient le fanfaron
        List<GroupeFanfare> groupes = instrumentDao.findGroupesByFanfaron(fanfaron.getId());
        
        // Récupération des événements dans lesquels le fanfaron est inscrit
        List<EvenementInscrit> evenementsInscrits = evenementDao.getEvenementsInscritsByFanfaron(fanfaron.getId());

        // Transmission des données à la vue JSP via les attributs de requête
        request.setAttribute("fanfaron", fanfaron);
        request.setAttribute("instrumentsJoues", instrumentsJoues);
        request.setAttribute("groupes", groupes);
        request.setAttribute("evenementsInscrits", evenementsInscrits);

        // Forward vers la page JSP pour affichage (redirection interne)
        request.getRequestDispatcher("/vue/accueil.jsp").forward(request, response);
    }
}
