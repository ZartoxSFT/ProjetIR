package controleur;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.InstrumentDAO;
import modele.Fanfaron;
import modele.Instrument;
import modele.GroupeFanfare;

@WebServlet("/mes-groupes")
public class MesGroupesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");

        InstrumentDAO dao = new InstrumentDAO();

        List<Instrument> instruments = dao.findAllInstruments();
        List<GroupeFanfare> groupes = dao.findAllGroupes();

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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");

        String action = request.getParameter("action");
        String[] instrumentIds = request.getParameterValues("instruments");
        String[] groupeIds = request.getParameterValues("groupes");

        InstrumentDAO dao = new InstrumentDAO();

        if (action != null && !action.isBlank()) {
            if (!fanfaron.getAdmin()) {
                response.sendRedirect(request.getContextPath() + "/mes-groupes?error=forbidden");
                return;
            }

            boolean success = handleAdminAction(request, dao, action);
            response.sendRedirect(request.getContextPath() + "/mes-groupes?" + (success ? "success=admin" : "error=admin"));
            return;
        }

        boolean success = dao.updateInstrumentsFanfaron(fanfaron.getId(), instrumentIds);
        success &= dao.updateGroupesFanfaron(fanfaron.getId(), groupeIds);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/mes-groupes?success=1");
        } else {
            request.setAttribute("error", "Une erreur est survenue lors de la mise a jour de vos groupes et instruments. Reessayez plus tard.");
            doGet(request, response);
        }
    }

    private boolean handleAdminAction(HttpServletRequest request, InstrumentDAO dao, String action) {
        try {
            String nom = request.getParameter("nom");
            String idParam = request.getParameter("id");

            if ("addInstrument".equals(action)) {
                return nom != null && !nom.trim().isEmpty()
                        && dao.insertInstrument(new Instrument(null, nom.trim()));
            }

            if ("updateInstrument".equals(action)) {
                return nom != null && !nom.trim().isEmpty() && idParam != null
                        && dao.updateInstrument(new Instrument(Long.parseLong(idParam), nom.trim()));
            }

            if ("deleteInstrument".equals(action)) {
                return idParam != null && dao.deleteInstrument(Long.parseLong(idParam));
            }

            if ("addGroupe".equals(action)) {
                return nom != null && !nom.trim().isEmpty()
                        && dao.insertGroupe(new GroupeFanfare(null, nom.trim()));
            }

            if ("updateGroupe".equals(action)) {
                return nom != null && !nom.trim().isEmpty() && idParam != null
                        && dao.updateGroupe(new GroupeFanfare(Long.parseLong(idParam), nom.trim()));
            }

            if ("deleteGroupe".equals(action)) {
                return idParam != null && dao.deleteGroupe(Long.parseLong(idParam));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
