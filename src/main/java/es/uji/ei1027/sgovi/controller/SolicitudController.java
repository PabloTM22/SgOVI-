package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.SolicitudServicioAPDao;
import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.validator.SolicitudValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {
    private SolicitudServicioAPDao solicitudDao;

    @Autowired
    public void setSolicitudDao(SolicitudServicioAPDao solicitudDao) {
        this.solicitudDao = solicitudDao;
    }

    @GetMapping("/lista")
    public String lista(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            session.setAttribute("nextUrl", "/solicitudes/lista");
            return "redirect:/login";
        }
        UserDetails user = (UserDetails) session.getAttribute("user");
        model.addAttribute("solicitudes", solicitudDao.findByUsuario(user.getUsername()));
        return "solicitud/lista";
    }

    @GetMapping("/nueva")
    public String nuevaForm(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            session.setAttribute("nextUrl", "/solicitudes/nueva");
            return "redirect:/login";
        }
        model.addAttribute("solicitud", new SolicitudServicioAP());
        return "solicitud/nueva";
    }

    @PostMapping("/nueva")
    public String nuevaSubmit(@ModelAttribute("solicitud") SolicitudServicioAP solicitud,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        new SolicitudValidator().validate(solicitud, bindingResult);
        if (bindingResult.hasErrors()) {
            return "solicitud/nueva";
        }
        UserDetails user = (UserDetails) session.getAttribute("user");
        solicitud.setIdUsuario(user.getUsername());
        solicitud.setEstado("en revision");
        solicitudDao.addSolicitud(solicitud);
        return "redirect:/solicitudes/lista";
    }
}