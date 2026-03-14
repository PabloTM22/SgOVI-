package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.SolicitudServicioAPDao;
import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {
    private SolicitudServicioAPDao solicitudDao;

    @Autowired
    public void setSolicitudDao(SolicitudServicioAPDao solicitudDao) {
        this.solicitudDao = solicitudDao;
    }

    // TODO: sustituir por usuario de sesión cuando se implemente el login
    private static final String USUARIO_ACTUAL = "usr_nyom";

    // Lista las solicitudes del usuario actual
    @GetMapping("/lista")
    public String lista(Model model) {
        model.addAttribute("solicitudes", solicitudDao.findByUsuario(USUARIO_ACTUAL));
        model.addAttribute("idUsuario", USUARIO_ACTUAL);
        return "solicitud/lista";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("solicitud", new SolicitudServicioAP());
        return "solicitud/nueva";
    }

    // Procesa el formulario y guarda la solicitud
    @PostMapping("/nueva")
    public String nuevaSubmit(@ModelAttribute SolicitudServicioAP solicitud) {
        solicitud.setIdUsuario(USUARIO_ACTUAL);
        solicitud.setEstado("en revisión");
        solicitudDao.addSolicitud(solicitud);
        return "redirect:/solicitudes/lista";
    }
}
