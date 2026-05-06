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

import dao.EvenementInscriptionJDBCDAO;
import dao.EvenementJDBCDAO;
import dao.GroupeJDBCDAO;
import dao.InstrumentJDBCDAO;
import modele.InscriptionDetail;
import modele.Instrument;
import modele.Evenement;
import modele.Fanfaron;

@WebServlet("/evenement")
public class EvenementServlet extends HttpServlet {

    private static final Set<String> STATUTS_VALIDES = new HashSet<>(
            Arrays.asList("present", "absent", "incertain"));

    private static final Set<String> TYPES_VALIDES = new HashSet<>(
            Arrays.asList("atelier", "repetition", "prestation"));

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");
        request.setAttribute("fanfaron", fanfaron);

        GroupeJDBCDAO groupeDao = new GroupeJDBCDAO();
        boolean peutProposer = groupeDao.isMemberOfCommissionPrestation(fanfaron.getId());
        request.setAttribute("peutProposer", peutProposer);

        EvenementJDBCDAO dao = new EvenementJDBCDAO();
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

                    EvenementInscriptionJDBCDAO inscriptionDao = new EvenementInscriptionJDBCDAO();
                    List<InscriptionDetail> inscriptions = inscriptionDao.getInscriptionsByEvenement(evenementId);
                    request.setAttribute("inscriptions", inscriptions);

                    InstrumentJDBCDAO instrumentDao = new InstrumentJDBCDAO();
                    List<Instrument> instruments = instrumentDao.getAllInstruments();
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

        if ("delete-evenement".equals(action)) {
            handleSupprimerEvenement(request, response, fanfaron);
            return;
        }

        request.setAttribute("erreur", "Action inconnue.");
        doGet(request, response);
    }

    private void handleAjouterEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        GroupeJDBCDAO groupeDao = new GroupeJDBCDAO();
        if (!groupeDao.isMemberOfCommissionPrestation(fanfaron.getId())) {
            request.setAttribute("erreur", "Vous n'etes pas autorise a proposer un evenement.");
            doGet(request, response);
            return;
        }

        String typeEvenement = request.getParameter("typeEvenement");
        String nom = request.getParameter("nom");
        String horodatage = request.getParameter("horodatage");
        String dureeStr = request.getParameter("duree");
        String lieu = request.getParameter("lieu");
        String description = request.getParameter("description");

        typeEvenement = typeEvenement == null ? "" : typeEvenement.trim();
        nom = nom == null ? "" : nom.trim();
        horodatage = horodatage == null ? "" : horodatage.trim();
        dureeStr = dureeStr == null ? "" : dureeStr.trim();
        lieu = lieu == null ? "" : lieu.trim();
        description = description == null ? "" : description.trim();

        if (typeEvenement.isEmpty() || nom.isEmpty() || horodatage.isEmpty() || dureeStr.isEmpty()
                || lieu.isEmpty()) {
            request.setAttribute("erreur", "Tous les champs obligatoires doivent etre remplis.");
            doGet(request, response);
            return;
        }

        if (!TYPES_VALIDES.contains(typeEvenement)) {
            request.setAttribute("erreur", "Type d'evenement invalide.");
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

        Evenement evenement = new Evenement(typeEvenement, nom, horodatageTs, duree, lieu, description);
        EvenementJDBCDAO dao = new EvenementJDBCDAO();

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

        EvenementInscriptionJDBCDAO inscriptionDao = new EvenementInscriptionJDBCDAO();
        if (inscriptionDao.upsertInscription(fanfaron.getId(), evenementId, instrumentId, statut)) {
            request.setAttribute("succes", "Inscription enregistree.");
        } else {
            request.setAttribute("erreur", "Erreur lors de l'inscription.");
        }

        request.setAttribute("evenementId", evenementId);
        doGet(request, response);
    }

    private void handleSupprimerEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        GroupeJDBCDAO groupeDao = new GroupeJDBCDAO();
        if (!groupeDao.isMemberOfCommissionPrestation(fanfaron.getId())) {
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

        EvenementJDBCDAO dao = new EvenementJDBCDAO();
        if (dao.deleteEvenement(evenementId)) {
            request.setAttribute("succes", "Evenement supprime avec succes.");
        } else {
            request.setAttribute("erreur", "Erreur lors de la suppression de l'evenement.");
        }

        doGet(request, response);
    }
}
