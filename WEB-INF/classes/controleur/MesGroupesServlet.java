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
import dao.InstrumentDAO;
import modele.Fanfaron;
import modele.Instrument;
import modele.GroupeFanfare;

/**
 * SERVLET MES GROUPES - Gestion du profil musical du fanfaron
 *
 * Responsabilites :
 * - Afficher les instruments et groupes disponibles
 * - Recuperer les choix deja associes au fanfaron connecte
 * - Enregistrer les instruments joues et les groupes d'appartenance
 *
 * URL de routage : /mes-groupes
 */
@WebServlet("/mes-groupes")
public class MesGroupesServlet extends HttpServlet {

    /**
     * Traitement des requetes GET
     * Prepare toutes les donnees necessaires a l'affichage du formulaire.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Recuperation de la session existante pour verifier l'utilisateur connecte
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");

        // DAO unique pour les instruments, les groupes et leurs associations
        InstrumentDAO dao = DAOFactory.getInstrumentDAO();

        // Listes de reference affichees dans les cases a cocher
        List<Instrument> instruments = dao.findAllInstruments();
        List<GroupeFanfare> groupes = dao.findAllGroupes();

        // Identifiants deja choisis par l'utilisateur, utilises pour cocher le formulaire
        List<Long> instrumentIdsChoisis =
                dao.findInstrumentIdsByFanfaron(fanfaron.getId());

        List<Long> groupeIdsChoisis =
                dao.findGroupeIdsByFanfaron(fanfaron.getId());

        request.setAttribute("fanfaron", fanfaron);
        request.setAttribute("instruments", instruments);
        request.setAttribute("groupes", groupes);
        request.setAttribute("instrumentIdsChoisis", instrumentIdsChoisis);
        request.setAttribute("groupeIdsChoisis", groupeIdsChoisis);

        request.getRequestDispatcher("/vue/mes-groupes.jsp").forward(request, response);
    }

    /**
     * Traitement des requetes POST
     * Enregistre les choix personnels.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Toutes les modifications necessitent une session active
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");

        String action = request.getParameter("action");
        if (action != null && !action.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin");
            return;
        }

        String[] instrumentIds = request.getParameterValues("instruments");
        String[] groupeIds = request.getParameterValues("groupes");

        InstrumentDAO dao = DAOFactory.getInstrumentDAO();

        // Mise a jour complete : suppression des anciennes associations puis insertion des nouvelles
        boolean success = dao.updateInstrumentsFanfaron(fanfaron.getId(), instrumentIds);
        success &= dao.updateGroupesFanfaron(fanfaron.getId(), groupeIds);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/mes-groupes?success=1");
        } else {
            request.setAttribute("error", "Une erreur est survenue lors de la mise a jour de vos groupes et instruments. Reessayez plus tard.");
            doGet(request, response);
        }
    }
}
