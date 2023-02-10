// Jin Young Park
// CS6650 Assignment 1: Task 1

import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static java.util.Arrays.asList;

@WebServlet(name = "TwinderServlet", value = "/TwinderServlet")
public class TwinderServlet extends HttpServlet {
    protected static final int SWIPERIDLIMIT = 5000;
    protected static final int SWIPEEIDLIMIT = 1000000;
    protected static final int COMMENTLIMIT = 256;
    protected static final int SUCCESSFULRESCODE = 201;
    protected static final int UNSUCCESSFULRESCODE = 404;
    protected static final int INVALIDRESCODE = 400;



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String msg = "Hello World";

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        PrintWriter out = response.getWriter();
        out.println("<h1>" + msg + "</h1>");
    }

    private boolean isUrlValid(String[] urlPath) {
        if (urlPath.length >= 2 && (urlPath[2].equalsIgnoreCase("left") || urlPath[2].equalsIgnoreCase("right"))) {
            return true;
        }
        return false;
    }

    private boolean isSwiperValid(String swiperId) {
        try {
            if (Integer.parseInt(swiperId) < 1 || Integer.parseInt(swiperId) > SWIPERIDLIMIT) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSwipeeValid(String swipeeId) {
        try {
            if (Integer.parseInt(swipeeId) < 1 || Integer.parseInt(swipeeId) > SWIPEEIDLIMIT) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCommentValid(String comment) {
        try {
            if (comment.length() < 0 || comment.length() > COMMENTLIMIT) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        String urlPath = request.getPathInfo();

        try {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = request.getReader().readLine()) != null) {
                sb.append(s);
            }
            String[] urlParts = urlPath.split("/");
            State state = (State) gson.fromJson(sb.toString(), State.class);

            if (isUrlValid(urlParts)) {
                if (isSwiperValid(state.getSwiper()) && isSwipeeValid(state.getSwipee()) && isCommentValid(state.getComment())) {
                    response.setStatus(SUCCESSFULRESCODE);
                    response.getOutputStream().print("Write Successful!");
                } else {
                    response.setStatus(UNSUCCESSFULRESCODE);
                    response.getOutputStream().print(gson.toJson("Invalid user!"));
                }
            } else {
                response.setStatus(INVALIDRESCODE);
                response.getOutputStream().print(gson.toJson("Wrong input!"));
            }
            response.getOutputStream().flush();
        } catch (Exception e) {
            response.getOutputStream().print(gson.toJson("Wrong input!"));
            response.getOutputStream().flush();
        }
    }
}
