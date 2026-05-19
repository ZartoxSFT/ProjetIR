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
import modele.Evenement;
import modele.Fanfaron;
import modele.InscriptionDetail;
import modele.Instrument;

/**
 * SERVLET EVENEMENT - Gestion des evenements et des inscriptions
 *
 * Responsabilites :
 * - Afficher la liste des evenements disponibles
 * - Autoriser les membres habilites a creer, modifier ou supprimer un evenement
 * - Permettre a un fanfaron de s'inscrire avec un instrument qu'il joue
 * - Afficher les participants inscrits a un evenement selectionne
 * - Gerer l'annulation d'une inscription
 *
 * Securite :
 * - Verifie que l'utilisateur est connecte avant chaque action
 * - Controle les droits admin ou commission prestation pour les actions sensibles
 */
@WebServlet("/evenement")
public class EvenementServlet extends HttpServlet {

    // Liste blanche des statuts acceptes pour eviter les valeurs incoherentes en base
    private static final Set<String> STATUTS_VALIDES = new HashSet<>(
            Arrays.asList("present", "absent", "incertain"));

    // Liste blanche des titres/types proposes dans les formulaires
    private static final Set<String> NOMS_EVENEMENT_VALIDES = new HashSet<>(
            Arrays.asList("atelier", "repetition", "prestation"));

    /**
     * Traitement des requetes GET.
     * Charge les evenements, les droits de l'utilisateur et les details demandes.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");
        request.setAttribute("fanfaron", fanfaron);

        // Droits evenement : administrateur ou membre de la commission prestation
        FanfaronDAO fanfaronDao = DAOFactory.getFanfaronDAO();
        boolean peutProposer = fanfaron.getAdmin()
                || fanfaronDao.isMemberOfCommissionPrestation(fanfaron.getId());
        request.setAttribute("peutProposer", peutProposer);
        request.setAttribute("peutModifierEvenement", peutProposer);

        EvenementDAO dao = DAOFactory.getEvenementDAO();
        List<Evenement> evenements = dao.getAllEvenements();
        request.setAttribute("evenements", evenements);

        Integer evenementId = lireIdDepuisAttributOuParametre(request, "evenementId");
        if (evenementId != null) {
            chargerDetailsEvenement(request, fanfaron, dao, evenementId);
        }

        Integer editionId = lireIdDepuisAttributOuParametre(request, "editionId");
        if (editionId != null) {
            chargerEvenementAEditer(request, dao, editionId, peutProposer);
        }

        request.getRequestDispatcher("/vue/evenement.jsp").forward(request, response);
    }

    /**
     * Traitement des requetes POST.
     * Oriente chaque formulaire vers le bon traitement selon le parametre action.
     */
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

        if ("update-evenement".equals(action)) {
            handleModifierEvenement(request, response, fanfaron);
            return;
        }

        if ("delete-evenement".equals(action)) {
            handleSupprimerEvenement(request, response, fanfaron);
            return;
        }

        request.setAttribute("erreur", "Action inconnue.");
        doGet(request, response);
    }

    /**
     * Lit un identifiant depuis un attribut interne ou depuis un parametre de requete.
     */
    private Integer lireIdDepuisAttributOuParametre(HttpServletRequest request, String nom) {
        Integer id = (Integer) request.getAttribute(nom);
        if (id != null) {
            return id;
        }

        String parametre = request.getParameter(nom);
        if (parametre == null || parametre.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(parametre.trim());
        } catch (NumberFormatException ex) {
            request.setAttribute("erreur", "Identifiant d'evenement invalide.");
            return null;
        }
    }

    /**
     * Charge les informations d'un evenement selectionne et ses inscriptions.
     */
    private void chargerDetailsEvenement(HttpServletRequest request, Fanfaron fanfaron,
            EvenementDAO dao, int evenementId) {
        try {
            Evenement evenementSelectionne = dao.getById(evenementId);
            if (evenementSelectionne == null) {
                request.setAttribute("erreur", "Evenement introuvable.");
                return;
            }

            request.setAttribute("evenementSelectionne", evenementSelectionne);

            EvenementInscriptionDAO inscriptionDao = DAOFactory.getEvenementInscriptionDAO();
            List<InscriptionDetail> inscriptions = inscriptionDao.getInscriptionsByEvenement(evenementId);
            request.setAttribute("inscriptions", inscriptions);

            InstrumentDAO instrumentDao = DAOFactory.getInstrumentDAO();
            List<Instrument> instruments = instrumentDao.findInstrumentsByFanfaron(fanfaron.getId());
            request.setAttribute("instruments", instruments);
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur lors du chargement de l'evenement.");
        }
    }

    /**
     * Charge l'evenement a modifier si l'utilisateur a les droits necessaires.
     */
    private void chargerEvenementAEditer(HttpServletRequest request, EvenementDAO dao,
            int editionId, boolean peutModifier) {
        if (!peutModifier) {
            request.setAttribute("erreur", "Vous n'etes pas autorise a modifier un evenement.");
            return;
        }

        try {
            Evenement evenementAEditer = dao.getById(editionId);
            if (evenementAEditer != null) {
                request.setAttribute("evenementAEditer", evenementAEditer);
            } else {
                request.setAttribute("erreur", "Evenement a modifier introuvable.");
            }
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur lors du chargement de l'evenement a modifier.");
        }
    }

    /**
     * Ajoute un evenement apres verification des droits et validation du formulaire.
     */
    private void handleAjouterEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        if (!peutGererEvenement(fanfaron)) {
            request.setAttribute("erreur", "Vous n'etes pas autorise a proposer un evenement.");
            doGet(request, response);
            return;
        }

        Evenement evenement = construireEvenementDepuisRequete(request);
        if (evenement == null) {
            doGet(request, response);
            return;
        }

        EvenementDAO dao = DAOFactory.getEvenementDAO();
        if (dao.insertAvecOrganisateur(evenement, fanfaron.getId())) {
            request.setAttribute("succes", "Evenement ajoute avec succes.");
        } else {
            request.setAttribute("erreur", "Erreur lors de l'ajout de l'evenement.");
        }

        doGet(request, response);
    }

    /**
     * Modifie les informations d'un evenement existant.
     */
    private void handleModifierEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        if (!peutGererEvenement(fanfaron)) {
            request.setAttribute("erreur", "Vous n'etes pas autorise a modifier un evenement.");
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

        Evenement evenement = construireEvenementDepuisRequete(request);
        if (evenement == null) {
            request.setAttribute("editionId", evenementId);
            doGet(request, response);
            return;
        }
        evenement.setId(evenementId);

        EvenementDAO dao = DAOFactory.getEvenementDAO();
        if (dao.updateEvenement(evenement)) {
            request.setAttribute("succes", "Evenement modifie avec succes.");
        } else {
            request.setAttribute("erreur", "Erreur lors de la modification de l'evenement.");
            request.setAttribute("editionId", evenementId);
        }

        doGet(request, response);
    }

    /**
     * Construit un objet Evenement a partir des champs communs aux formulaires.
     */
    private Evenement construireEvenementDepuisRequete(HttpServletRequest request) {
        String nom = nettoyer(request.getParameter("nom"));
        String horodatage = nettoyer(request.getParameter("horodatage"));
        String dureeStr = nettoyer(request.getParameter("duree"));
        String lieu = nettoyer(request.getParameter("lieu"));
        String description = nettoyer(request.getParameter("description"));

        if (nom.isEmpty() || horodatage.isEmpty()
                || dureeStr.isEmpty() || lieu.isEmpty()) {
            request.setAttribute("erreur", "Tous les champs obligatoires doivent etre remplis.");
            return null;
        }

        if (!NOMS_EVENEMENT_VALIDES.contains(nom)) {
            request.setAttribute("erreur", "Titre d'evenement invalide.");
            return null;
        }

        int duree;
        try {
            duree = Integer.parseInt(dureeStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("erreur", "La duree doit etre un nombre entier.");
            return null;
        }

        Timestamp horodatageTs;
        try {
            horodatageTs = Timestamp.valueOf(LocalDateTime.parse(horodatage));
        } catch (DateTimeParseException ex) {
            request.setAttribute("erreur", "Format de date/heure invalide.");
            return null;
        }

        if (description.isEmpty()) {
            description = null;
        }

        return new Evenement(nom, horodatageTs, duree, lieu, description);
    }

    /**
     * Inscrit ou met a jour l'inscription du fanfaron pour un evenement.
     */
    private void handleInscriptionEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        String evenementIdStr = nettoyer(request.getParameter("evenementId"));
        String instrumentIdStr = nettoyer(request.getParameter("instrumentId"));
        String statut = nettoyer(request.getParameter("statut"));

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

        if (!fanfaronJoueInstrument(fanfaron, instrumentId)) {
            request.setAttribute("erreur", "Vous ne pouvez vous inscrire qu'avec un instrument que vous jouez.");
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

    /**
     * Supprime une inscription existante.
     * Un administrateur peut annuler pour tous, un fanfaron seulement pour lui-meme.
     */
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

    /**
     * Supprime un evenement et laisse la base supprimer les inscriptions liees via les contraintes.
     */
    private void handleSupprimerEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        if (!peutGererEvenement(fanfaron)) {
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

    /**
     * Verifie si le fanfaron a le droit de gerer les evenements.
     */
    private boolean peutGererEvenement(Fanfaron fanfaron) {
        FanfaronDAO fanfaronDao = DAOFactory.getFanfaronDAO();
        return fanfaron.getAdmin()
                || fanfaronDao.isMemberOfCommissionPrestation(fanfaron.getId());
    }

    /**
     * Verifie que l'instrument choisi fait partie du profil du fanfaron.
     */
    private boolean fanfaronJoueInstrument(Fanfaron fanfaron, int instrumentId) {
        InstrumentDAO instrumentDao = DAOFactory.getInstrumentDAO();
        for (Instrument instrument : instrumentDao.findInstrumentsByFanfaron(fanfaron.getId())) {
            if (instrument.getId() != null && instrument.getId().longValue() == instrumentId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Nettoie les parametres texte recus depuis les formulaires.
     */
    private String nettoyer(String valeur) {
        return valeur == null ? "" : valeur.trim();
    }
}
