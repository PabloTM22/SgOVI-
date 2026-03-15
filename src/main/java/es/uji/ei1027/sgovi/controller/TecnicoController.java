package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.SolicitudServicioAPDao;
import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {
    private SolicitudServicioAPDao solicitudDao;

    @Autowired
    public void setSolicitudDao(SolicitudServicioAPDao solicitudDao) {
        this.solicitudDao = solicitudDao;
    }

    // Lista las solicitudes en estado "en revisión"
    @GetMapping("/solicitudes")
    public String listaSolicitudesEnRevision(Model model) {
        model.addAttribute("solicitudes", solicitudDao.findByEstado("en revisión"));
        return "tecnico/revisionSolicitudes";
    }

    // Aprueba una solicitud
    @PostMapping("/solicitudes/{id}/aprobar")
    public String aprobar(@PathVariable int id) {
        solicitudDao.updateEstado(id, "aprobada");
        return "redirect:/tecnico/solicitudes";
    }

    // Rechaza una solicitud
    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazar(@PathVariable int id) {
        solicitudDao.updateEstado(id, "rechazada");
        return "redirect:/tecnico/solicitudes";
    }
}
