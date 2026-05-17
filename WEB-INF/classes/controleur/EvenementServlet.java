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
 *
 * URL de routage : /evenement
 */
@WebServlet("/evenement")
public class EvenementServlet extends HttpServlet {

    // Liste blanche des statuts acceptes pour eviter les valeurs incoherentes en base
    private static final Set<String> STATUTS_VALIDES = new HashSet<>(
            Arrays.asList("present", "absent", "incertain"));

    /**
     * Traitement des requetes GET
     * Charge les evenements, les droits de l'utilisateur et les details demandes.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Recuperation de la session existante pour verifier l'authentification
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");
        request.setAttribute("fanfaron", fanfaron);

        // Calcul des droits : admin ou membre de la commission prestation
        FanfaronDAO fanfaronDao = DAOFactory.getFanfaronDAO();
        boolean peutProposer = fanfaron.getAdmin()
                || fanfaronDao.isMemberOfCommissionPrestation(fanfaron.getId());
        request.setAttribute("peutProposer", peutProposer);
        request.setAttribute("peutModifierEvenement", peutProposer);

        EvenementDAO dao = DAOFactory.getEvenementDAO();
        List<Evenement> evenements = dao.getAllEvenements();
        request.setAttribute("evenements", evenements);

        // Lecture de l'evenement a afficher : attribut interne ou parametre URL
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

        // Si un evenement est selectionne, charger ses details et ses inscriptions
        if (evenementId != null) {
            try {
                Evenement evenementSelectionne = dao.getById(evenementId);
                if (evenementSelectionne != null) {
                    request.setAttribute("evenementSelectionne", evenementSelectionne);

                    EvenementInscriptionDAO inscriptionDao = DAOFactory.getEvenementInscriptionDAO();
                    List<InscriptionDetail> inscriptions = inscriptionDao.getInscriptionsByEvenement(evenementId);
                    request.setAttribute("inscriptions", inscriptions);

                    InstrumentDAO instrumentDao = DAOFactory.getInstrumentDAO();
                    List<Instrument> instruments = instrumentDao.findInstrumentsByFanfaron(fanfaron.getId());
                    request.setAttribute("instruments", instruments);
                } else {
                    request.setAttribute("erreur", "Evenement introuvable.");
                }
            } catch (Exception e) {
                request.setAttribute("erreur", "Erreur lors du chargement de l'evenement.");
            }
        }

        // Lecture de l'evenement a modifier, separee de la selection d'affichage
        Integer editionId = (Integer) request.getAttribute("editionId");
        if (editionId == null) {
            String editionParam = request.getParameter("editionId");
            if (editionParam != null && !editionParam.trim().isEmpty()) {
                try {
                    editionId = Integer.parseInt(editionParam.trim());
                } catch (NumberFormatException ex) {
                    request.setAttribute("erreur", "Identifiant d'evenement a modifier invalide.");
                }
            }
        }

        // Charge le formulaire d'edition seulement si l'utilisateur a le droit de modifier
        if (editionId != null) {
            boolean peutModifier = fanfaron.getAdmin() || peutProposer;
            if (!peutModifier) {
                request.setAttribute("erreur", "Vous n'etes pas autorise a modifier un evenement.");
            } else {
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
        }

        request.getRequestDispatcher("/vue/evenement.jsp").forward(request, response);
    }

    /**
     * Traitement des requetes POST
     * Oriente chaque formulaire vers le bon traitement selon le parametre action.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Toutes les actions POST necessitent un utilisateur connecte
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");

        // Action par defaut : ajout d'evenement, pour compatibilite avec le formulaire
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
     * Ajoute un evenement apres verification des droits et validation du formulaire.
     */
    private void handleAjouterEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        // Seuls les admins et la commission prestation peuvent proposer un evenement
        FanfaronDAO fanfaronDao = DAOFactory.getFanfaronDAO();
        boolean peutProposer = fanfaron.getAdmin()
                || fanfaronDao.isMemberOfCommissionPrestation(fanfaron.getId());
        if (!peutProposer) {
            request.setAttribute("erreur", "Vous n'etes pas autorise a proposer un evenement.");
            doGet(request, response);
            return;
        }

        String nom = request.getParameter("nom");
        String horodatage = request.getParameter("horodatage");
        String dureeStr = request.getParameter("duree");
        String lieu = request.getParameter("lieu");
        String description = request.getParameter("description");

        // Normalisation des champs texte pour simplifier les validations suivantes
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

    /**
     * Inscrit ou met a jour l'inscription du fanfaron pour un evenement.
     */
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

        // Validation du statut pour rester coherent avec les valeurs attendues
        if (!STATUTS_VALIDES.contains(statut)) {
            request.setAttribute("erreur", "Statut invalide.");
            request.setAttribute("evenementId", evenementId);
            doGet(request, response);
            return;
        }

        InstrumentDAO instrumentDao = DAOFactory.getInstrumentDAO();
        boolean instrumentAutorise = false;
        // Verification metier : un fanfaron ne peut s'inscrire qu'avec un instrument lie a son profil
        for (Instrument instrument : instrumentDao.findInstrumentsByFanfaron(fanfaron.getId())) {
            if (instrument.getId() != null && instrument.getId().longValue() == instrumentId) {
                instrumentAutorise = true;
                break;
            }
        }

        if (!instrumentAutorise) {
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

        // Les identifiants arrivent sous forme de chaine depuis le formulaire
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

        // Controle d'autorisation avant suppression
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
     * Modifie les informations d'un evenement existant.
     */
    private void handleModifierEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        // Droits identiques a la creation : admin ou commission prestation
        FanfaronDAO fanfaronDao = DAOFactory.getFanfaronDAO();
        boolean peutModifier = fanfaron.getAdmin()
                || fanfaronDao.isMemberOfCommissionPrestation(fanfaron.getId());
        if (!peutModifier) {
            request.setAttribute("erreur", "Vous n'etes pas autorise a modifier un evenement.");
            doGet(request, response);
            return;
        }

        // Recuperation et validation des champs du formulaire d'edition
        String evenementIdStr = request.getParameter("evenementId");
        String nom = request.getParameter("nom");
        String horodatage = request.getParameter("horodatage");
        String dureeStr = request.getParameter("duree");
        String lieu = request.getParameter("lieu");
        String description = request.getParameter("description");

        int evenementId;
        try {
            evenementId = Integer.parseInt(evenementIdStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("erreur", "Identifiant d'evenement invalide.");
            doGet(request, response);
            return;
        }

        nom = nom == null ? "" : nom.trim();
        horodatage = horodatage == null ? "" : horodatage.trim();
        dureeStr = dureeStr == null ? "" : dureeStr.trim();
        lieu = lieu == null ? "" : lieu.trim();
        description = description == null ? "" : description.trim();

        if (nom.isEmpty() || horodatage.isEmpty() || dureeStr.isEmpty() || lieu.isEmpty()) {
            request.setAttribute("erreur", "Tous les champs obligatoires doivent etre remplis.");
            request.setAttribute("editionId", evenementId);
            doGet(request, response);
            return;
        }

        // Conversion de la duree saisie en entier
        int duree;
        try {
            duree = Integer.parseInt(dureeStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("erreur", "La duree doit etre un nombre entier.");
            request.setAttribute("editionId", evenementId);
            doGet(request, response);
            return;
        }

        // Conversion du champ datetime-local HTML vers un Timestamp SQL
        Timestamp horodatageTs;
        try {
            LocalDateTime dateTime = LocalDateTime.parse(horodatage);
            horodatageTs = Timestamp.valueOf(dateTime);
        } catch (DateTimeParseException ex) {
            request.setAttribute("erreur", "Format de date/heure invalide.");
            request.setAttribute("editionId", evenementId);
            doGet(request, response);
            return;
        }

        if (description.isEmpty()) {
            description = null;
        }

        // Creation du modele puis delegation de l'insertion au DAO
        // L'objet modele porte l'ID pour que le DAO fasse un UPDATE et non un INSERT
        Evenement evenement = new Evenement(nom, horodatageTs, duree, lieu, description);
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
     * Supprime un evenement et laisse la base supprimer les inscriptions liees via les contraintes.
     */
    private void handleSupprimerEvenement(HttpServletRequest request, HttpServletResponse response, Fanfaron fanfaron)
            throws ServletException, IOException {
        // Suppression reservee aux administrateurs ou a la commission prestation
        FanfaronDAO fanfaronDao = DAOFactory.getFanfaronDAO();
        boolean peutSupprimer = fanfaron.getAdmin()
                || fanfaronDao.isMemberOfCommissionPrestation(fanfaron.getId());
        if (!peutSupprimer) {
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
