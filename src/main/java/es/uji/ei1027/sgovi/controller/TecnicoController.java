package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.SolicitudServicioAPDao;
import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import es.uji.ei1027.sgovi.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {
    private SolicitudServicioAPDao solicitudDao;

    @Autowired
    public void setSolicitudDao(SolicitudServicioAPDao solicitudDao) {
        this.solicitudDao = solicitudDao;
    }

    private boolean esTecnico(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        return user != null && "TECNICO".equals(user.getRol());
    }

    @GetMapping("/solicitudes")
    public String listaSolicitudesEnRevision(HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/tecnico/solicitudes");
            return "redirect:/login";
        }
        model.addAttribute("solicitudes", solicitudDao.findByEstado("en revision"));
        return "tecnico/revisionSolicitudes";
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public String aprobar(@PathVariable int id, HttpSession session) {
        if (!esTecnico(session)) return "redirect:/login";
        solicitudDao.updateEstado(id, "aprobada");
        return "redirect:/tecnico/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazar(@PathVariable int id, HttpSession session) {
        if (!esTecnico(session)) return "redirect:/login";
        solicitudDao.updateEstado(id, "rechazada");
        return "redirect:/tecnico/solicitudes";
    }
}