package mn.edu.num.tms.adapter.primary.web;

import mn.edu.num.tms.core.application.ThesisDTO;
import mn.edu.num.tms.core.application.ThesisService;
import mn.edu.num.tms.infrastructure.persistence.RepositoryFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/theses")
public class ThesisServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ThesisService thesisService;

    @Override
    public void init() throws ServletException {
        this.thesisService = new ThesisService(RepositoryFactory.create());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ThesisDTO> thesisList = thesisService.getAllTheses();
        request.setAttribute("thesisList", thesisList);
        request.getRequestDispatcher("/WEB-INF/views/theses.jsp")
               .forward(request, response);
    }
}