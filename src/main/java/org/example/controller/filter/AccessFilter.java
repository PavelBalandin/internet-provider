package org.example.controller.filter;

import org.apache.log4j.Logger;
import org.example.controller.command.user.DeleteTariffFromOrderCommand;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class AccessFilter implements Filter {
    private static final String URL_PATTERN = ".*/InternetProvider/";
    private static final List<String> common = new ArrayList<>();
    private static final List<String> guest = new ArrayList<>();
    private static final Map<String, List<String>> accessMap = new HashMap<>();
    private static final Logger logger = Logger.getLogger(AccessFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Filter initialization starts");
        common.addAll(
                Arrays.asList("/", "/user_registration.jsp", "/css/main.css", "getServiceList",
                        "getTariffListByService", "changeLocale", "downloadTariffList"));
        guest.addAll(
                Arrays.asList("/login.jsp", "login"));
        accessMap.put("ADMIN",
                Arrays.asList("/admin_page.jsp", "logout", "getUser", "createUser", "updateUser",
                        "getTariffList", "createTariff", "updateTariff", "deleteTariff"));
        accessMap.put("USER",
                Arrays.asList("logout", "getUserOrderPage", "getAddFundsPage", "getUserPaymentListPage",
                        "addTariffToOrder", "deleteTariffFromOrder", "makeOrder", "createPayment", "getUserTariffListPage"));
        logger.debug("Filter initialization finished");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("Filter starts");
        if (accessAllowed(request)) {
            logger.debug("Filter finished");
            chain.doFilter(request, response);
        } else {
            String errorMessage = "You don't have permission to access the requested resource";
            request.setAttribute("errorMessage", errorMessage);
            logger.trace("User doesn't have permission to access the requested resource");
            request.getRequestDispatcher("/not_found.jsp")
                    .forward(request, response);
        }

    }

    private boolean accessAllowed(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;

        String uri = req.getRequestURI();
        logger.trace("Uri :" + uri);

        String commandName = uri.replaceAll(URL_PATTERN, "");

        if (commandName == null || commandName.isEmpty()) {
            return false;
        }

        if (common.contains(commandName)) {
            return true;
        }

        HttpSession session = req.getSession(false);
        if (session == null)
            return false;

        String userRole = (String) session.getAttribute("role");
        if (userRole == null) {
            return guest.contains(commandName);
        }

        return accessMap.get(userRole).contains(commandName);

    }

    @Override
    public void destroy() {
        logger.debug("Filter destruction starts");

        logger.debug("Filter destruction finished");
    }
}
