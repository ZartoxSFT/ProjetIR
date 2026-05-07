package controleur;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.DAOFactory;
import dao.EvenementDAO;
import dao.EvenementInscriptionDAO;
import dao.FanfaronDAO;
import dao.InstrumentDAO;
import modele.InscriptionDetail;
import modele.Instrument;
import modele.Evenement;
import modele.Fanfaron;

@WebServlet("/evenement")
public class EvenementServlet extends HttpServlet {

    private static final Set<String> STATUTS_VALIDES = new HashSet<>(
            Arrays.asList("present", "absent", "incertain"));

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");
        request.setAttribute("fanfaron", fanfaron);

        FanfaronDAO fanfaronDao = DAOFactory.getFanfaronDAO();
        boolean peutProposer = fanfaronDao.isMemberOfCommissionPrestation(fanfaron.getId());
        request.setAttribute("peutProposer", peutProposer);

        EvenementDAO dao = DAOFactory.getEvenementDAO();
        List<Evenement> evenements = dao.getAllEvenements();
        request.setAttribute("evenements", evenements);

        Integer evenementId = (Integer) request.getAttribute("evenementId");
        if (evenementId == null) {
            String evenementParam = request.getParameter("evenementId");
            if (evenementParam != null && !evenementParam.trim().isEmpty()) {
                try {
                    evenementId = Integer.parseInt(evenementParam.trim());
                } catch (NumberFormatException ex) {
                    request.setAttribute("erreur", "Identifiant d'evenement invalide.");
                }
            }
        }

        if (evenementId != null) {
            try {
                Evenement evenementSelectionne = dao.getById(evenementId);
                if (evenementSelectionne != null) {
                    request.setAttribute("evenementSelectionne", evenementSelectionne);

                    EvenementInscriptionDAO inscriptionDao = DAOFactory.getEvenementInscriptionDAO();
                    List<InscriptionDetail> inscriptions = inscriptionDao.getInscriptionsByEvenement(evenementId);
                    request.setAttribute("inscriptions", inscriptions);

                    InstrumentDAO instrumentDao = DAOFactory.getInstrumentDAO();
                    List<Instrument> instruments = instrumentDao.findAllInstruments();
                    request.setAttribute("instruments", instruments);
                } else {
                    request.setAttribute("erreur", "Evenement introuvable.");
                }
            } catch (Exception e) {
                request.setAttribute("erreur", "Erreur lors du chargement de l'evenement.");
            }
        }

        request.getRequestDispatcher("/vue/evenement.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "add-evenement";
        }

        if ("add-evenement".equals(action)) {
            handleAjouterEvenement(request, response, fanfaron);
            return;
        }

        if ("inscription".equals(action)) {
            handleInscriptionEvenement(request, response, fanfaron);
            return;
        }

        if ("delete-inscription".equals(action)) {
            handleSupprimerInscription(request, response, fanfaron);
            return;
        }

        if ("delete-evenement".equals(action)) {
            handleSupprimerEvenement(request, response, fanfaron);
            return;
        }

        request.setAttribute("erreur", "Action inconnue.");
        doGet(request, response);
    }

    private void handleAjouterEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        FanfaronDAO fanfaronDao = DAOFactory.getFanfaronDAO();
        if (!fanfaronDao.isMemberOfCommissionPrestation(fanfaron.getId())) {
            request.setAttribute("erreur", "Vous n'etes pas autorise a proposer un evenement.");
            doGet(request, response);
            return;
        }

        String nom = request.getParameter("nom");
        String horodatage = request.getParameter("horodatage");
        String dureeStr = request.getParameter("duree");
        String lieu = request.getParameter("lieu");
        String description = request.getParameter("description");

        nom = nom == null ? "" : nom.trim();
        horodatage = horodatage == null ? "" : horodatage.trim();
        dureeStr = dureeStr == null ? "" : dureeStr.trim();
        lieu = lieu == null ? "" : lieu.trim();
        description = description == null ? "" : description.trim();

        if (nom.isEmpty() || horodatage.isEmpty() || dureeStr.isEmpty()
                || lieu.isEmpty()) {
            request.setAttribute("erreur", "Tous les champs obligatoires doivent etre remplis.");
            doGet(request, response);
            return;
        }

        int duree;
        try {
            duree = Integer.parseInt(dureeStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("erreur", "La duree doit etre un nombre entier.");
            doGet(request, response);
            return;
        }

        Timestamp horodatageTs;
        try {
            LocalDateTime dateTime = LocalDateTime.parse(horodatage);
            horodatageTs = Timestamp.valueOf(dateTime);
        } catch (DateTimeParseException ex) {
            request.setAttribute("erreur", "Format de date/heure invalide.");
            doGet(request, response);
            return;
        }

        if (description != null && description.isEmpty()) {
            description = null;
        }

        Evenement evenement = new Evenement(nom, horodatageTs, duree, lieu, description);
        EvenementDAO dao = DAOFactory.getEvenementDAO();

        if (dao.insertAvecOrganisateur(evenement, fanfaron.getId())) {
            request.setAttribute("succes", "Evenement ajoute avec succes.");
        } else {
            request.setAttribute("erreur", "Erreur lors de l'ajout de l'evenement.");
        }

        doGet(request, response);
    }

    private void handleInscriptionEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        String evenementIdStr = request.getParameter("evenementId");
        String instrumentIdStr = request.getParameter("instrumentId");
        String statut = request.getParameter("statut");

        evenementIdStr = evenementIdStr == null ? "" : evenementIdStr.trim();
        instrumentIdStr = instrumentIdStr == null ? "" : instrumentIdStr.trim();
        statut = statut == null ? "" : statut.trim();

        int evenementId;
        int instrumentId;
        try {
            evenementId = Integer.parseInt(evenementIdStr);
            instrumentId = Integer.parseInt(instrumentIdStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("erreur", "Donnees d'inscription invalides.");
            doGet(request, response);
            return;
        }

        if (!STATUTS_VALIDES.contains(statut)) {
            request.setAttribute("erreur", "Statut invalide.");
            request.setAttribute("evenementId", evenementId);
            doGet(request, response);
            return;
        }

        EvenementInscriptionDAO inscriptionDao = DAOFactory.getEvenementInscriptionDAO();
        if (inscriptionDao.upsertInscription(fanfaron.getId(), evenementId, instrumentId, statut)) {
            request.setAttribute("succes", "Inscription enregistree.");
        } else {
            request.setAttribute("erreur", "Erreur lors de l'inscription.");
        }

        request.setAttribute("evenementId", evenementId);
        doGet(request, response);
    }

    private void handleSupprimerInscription(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        String evenementIdStr = request.getParameter("evenementId");
        String fanfaronIdStr = request.getParameter("fanfaronId");

        int evenementId;
        long fanfaronId;
        try {
            evenementId = Integer.parseInt(evenementIdStr);
            fanfaronId = Long.parseLong(fanfaronIdStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("erreur", "Donnees d'annulation invalides.");
            doGet(request, response);
            return;
        }

        if (fanfaronId != fanfaron.getId() && !fanfaron.getAdmin()) {
            request.setAttribute("erreur", "Vous n'etes pas autorise a annuler cette inscription.");
            request.setAttribute("evenementId", evenementId);
            doGet(request, response);
            return;
        }

        EvenementInscriptionDAO inscriptionDao = DAOFactory.getEvenementInscriptionDAO();
        if (inscriptionDao.deleteInscription(fanfaronId, evenementId)) {
            request.setAttribute("succes", "Inscription annulee.");
        } else {
            request.setAttribute("erreur", "Erreur lors de l'annulation de l'inscription.");
        }

        request.setAttribute("evenementId", evenementId);
        doGet(request, response);
    }

    private void handleSupprimerEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        FanfaronDAO fanfaronDao = DAOFactory.getFanfaronDAO();
        if (!fanfaronDao.isMemberOfCommissionPrestation(fanfaron.getId())) {
            request.setAttribute("erreur", "Vous n'etes pas autorise a supprimer un evenement.");
            doGet(request, response);
            return;
        }

        String evenementIdStr = request.getParameter("evenementId");
        int evenementId;
        try {
            evenementId = Integer.parseInt(evenementIdStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("erreur", "Identifiant d'evenement invalide.");
            doGet(request, response);
            return;
        }

        EvenementDAO dao = DAOFactory.getEvenementDAO();
        if (dao.deleteEvenement(evenementId)) {
            request.setAttribute("succes", "Evenement supprime avec succes.");
        } else {
            request.setAttribute("erreur", "Erreur lors de la suppression de l'evenement.");
        }

        doGet(request, response);
    }
}
