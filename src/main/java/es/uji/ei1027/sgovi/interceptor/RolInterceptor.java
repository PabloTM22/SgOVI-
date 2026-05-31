package es.uji.ei1027.sgovi.interceptor;

import es.uji.ei1027.sgovi.model.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class RolInterceptor implements HandlerInterceptor {

    private final String rolRequerido;

    public RolInterceptor(String rolRequerido) {
        this.rolRequerido = rolRequerido;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession();
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user != null && rolRequerido.equals(user.getRol())) {
            return true;
        }
        session.setAttribute("nextUrl", urlSolicitada(request));
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private String urlSolicitada(HttpServletRequest request) {
        String uri = request.getRequestURI().substring(request.getContextPath().length());
        String query = request.getQueryString();
        return query == null ? uri : uri + "?" + query;
    }
}