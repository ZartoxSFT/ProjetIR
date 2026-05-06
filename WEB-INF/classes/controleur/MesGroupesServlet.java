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

        String[] instrumentIds = request.getParameterValues("instruments");
        String[] groupeIds = request.getParameterValues("groupes");

        InstrumentDAO dao = new InstrumentDAO();

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