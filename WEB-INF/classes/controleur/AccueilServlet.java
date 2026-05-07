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

@WebServlet("/accueil")
public class AccueilServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fanfaron") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Fanfaron fanfaron = (Fanfaron) session.getAttribute("fanfaron");
        InstrumentDAO instrumentDao = DAOFactory.getInstrumentDAO();
        EvenementDAO evenementDao = DAOFactory.getEvenementDAO();

        List<Instrument> instrumentsJoues = instrumentDao.findInstrumentsByFanfaron(fanfaron.getId());
        List<GroupeFanfare> groupes = instrumentDao.findGroupesByFanfaron(fanfaron.getId());
        List<EvenementInscrit> evenementsInscrits = evenementDao.getEvenementsInscritsByFanfaron(fanfaron.getId());

        request.setAttribute("fanfaron", fanfaron);
        request.setAttribute("instrumentsJoues", instrumentsJoues);
        request.setAttribute("groupes", groupes);
        request.setAttribute("evenementsInscrits", evenementsInscrits);

        request.getRequestDispatcher("/vue/accueil.jsp").forward(request, response);
    }
}
