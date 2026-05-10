package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.dao.SeleccionDao;
import es.uji.ei1027.sgovi.dao.SolicitudServicioAPDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.model.Seleccion;
import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import es.uji.ei1027.sgovi.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {
    private SolicitudServicioAPDao solicitudDao;
    private CandidatoDao candidatoDao;
    private SeleccionDao seleccionDao;

    @Autowired
    public void setSolicitudDao(SolicitudServicioAPDao solicitudDao) {
        this.solicitudDao = solicitudDao;
    }

    @Autowired
    public void setCandidatoDao(CandidatoDao candidatoDao) {
        this.candidatoDao = candidatoDao;
    }

    @Autowired
    public void setSeleccionDao(SeleccionDao seleccionDao) {
        this.seleccionDao = seleccionDao;
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
        model.addAttribute("solicitudes", solicitudDao.getSolicitudes());
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

    @GetMapping("/solicitudes/{id}/candidatos")
    public String candidatosParaSolicitud(@PathVariable int id,
                                          HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/tecnico/solicitudes/" + id + "/candidatos");
            return "redirect:/login";
        }
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(id);
        if (solicitud == null || !"aprobada".equals(solicitud.getEstado())) {
            return "redirect:/tecnico/solicitudes";
        }
        List<Candidato> candidatos = candidatoDao.findAceptadosPorTipo(
                solicitud.getTipoAsistencia(), 39.9864, -0.0513
        );
        List<Seleccion> seleccionesExistentes = seleccionDao.findBySolicitud(id);
        List<String> idsYaSeleccionados = seleccionesExistentes.stream()
                .map(Seleccion::getIdAp)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idsYaSeleccionados", idsYaSeleccionados);
        return "tecnico/candidatosParaSolicitud";
    }

    @PostMapping("/solicitudes/{idSolicitud}/seleccionar/{idAp}")
    public String seleccionarCandidato(@PathVariable int idSolicitud,
                                       @PathVariable String idAp,
                                       HttpSession session,
                                       org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (!esTecnico(session)) return "redirect:/login";
        List<Seleccion> existentes = seleccionDao.findBySolicitud(idSolicitud);
        boolean yaSeleccionado = existentes.stream()
                .anyMatch(s -> idAp.equals(s.getIdAp()));
        if (yaSeleccionado) {
            redirectAttributes.addFlashAttribute("mensajeInfo",
                    "Este candidato ya ha sido propuesto para esta solicitud.");
            return "redirect:/tecnico/solicitudes/" + idSolicitud + "/candidatos";
        }
        UserDetails user = (UserDetails) session.getAttribute("user");
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSolicitud(idSolicitud);
        seleccion.setIdAp(idAp);
        seleccion.setIdTecnico(user.getUsername());
        seleccion.setEstado("propuesta");
        try {
            seleccionDao.addSeleccion(seleccion);
            redirectAttributes.addFlashAttribute("mensajeExito", "Candidato seleccionado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorSeleccion", "Error al seleccionar: " + e.getMessage());
        }
        return "redirect:/tecnico/solicitudes/" + idSolicitud + "/candidatos";
    }
}