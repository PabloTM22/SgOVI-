package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.*;
import es.uji.ei1027.sgovi.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {
    private final SolicitudServicioAPDao solicitudDao;
    private final CandidatoDao candidatoDao;
    private final SeleccionDao seleccionDao;
    private final RegistroContratoDao contratoDao;
    private final UsuarioOviDao usuarioDao;

    @Autowired
    public TecnicoController(SolicitudServicioAPDao solicitudDao,
                             CandidatoDao candidatoDao,
                             SeleccionDao seleccionDao,
                             RegistroContratoDao contratoDao,
                             UsuarioOviDao usuarioDao) {
        this.solicitudDao = solicitudDao;
        this.candidatoDao = candidatoDao;
        this.seleccionDao = seleccionDao;
        this.contratoDao = contratoDao;
        this.usuarioDao = usuarioDao;
    }

    @GetMapping("/solicitudes")
    public String listaSolicitudesEnRevision(Model model) {
        List<SolicitudServicioAP> solicitudes = solicitudDao.getSolicitudes();
        Map<Integer, Integer> contratoPorSolicitud = new java.util.LinkedHashMap<>();
        for (SolicitudServicioAP s : solicitudes) {
            if ("cerrada con contrato".equals(s.getEstado())
                    || "cerrada con contrato finalizado".equals(s.getEstado())) {
                for (Seleccion sel : seleccionDao.findBySolicitud(s.getIdSolicitud())) {
                    if ("aceptada".equals(sel.getEstado())) {
                        RegistroContrato c = contratoDao.findBySeleccion(sel.getIdSeleccion());
                        if (c != null) {
                            contratoPorSolicitud.put(s.getIdSolicitud(), c.getIdContrato());
                        }
                    }
                }
            }
        }
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("contratoPorSolicitud", contratoPorSolicitud);
        return "tecnico/revisionSolicitudes";
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public String aprobar(@PathVariable int id) {
        solicitudDao.updateEstado(id, "aprobada");
        return "redirect:/tecnico/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazar(@PathVariable int id) {
        solicitudDao.updateEstado(id, "rechazada");
        return "redirect:/tecnico/solicitudes";
    }

    @GetMapping("/solicitudes/{id}/candidatos")
    public String candidatosParaSolicitud(@PathVariable int id, Model model) {
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
        Map<Integer, Candidato> candidatosSeleccion = new java.util.LinkedHashMap<>();
        for (Seleccion s : seleccionesExistentes) {
            candidatosSeleccion.put(s.getIdSeleccion(), candidatoDao.getCandidato(s.getIdAp()));
        }
        model.addAttribute("selecciones", seleccionesExistentes);
        model.addAttribute("candidatosSeleccion", candidatosSeleccion);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idsYaSeleccionados", idsYaSeleccionados);

        UsuarioOvi usuarioSolicitante = usuarioDao.getUsuario(solicitud.getIdUsuario());
        model.addAttribute("nombreUsuario",
                usuarioSolicitante != null
                        ? usuarioSolicitante.getNombre() + " " + usuarioSolicitante.getApellidos()
                        : null);
        return "tecnico/candidatosParaSolicitud";
    }

    @PostMapping("/solicitudes/{idSolicitud}/seleccionar/{idAp}")
    public String seleccionarCandidato(@PathVariable int idSolicitud,
                                       @PathVariable String idAp,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        List<Seleccion> existentes = seleccionDao.findBySolicitud(idSolicitud);
        boolean yaSeleccionado = existentes.stream()
                .anyMatch(s -> idAp.equals(s.getIdAp()));
        if (yaSeleccionado) {
            redirectAttributes.addFlashAttribute("message",
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
            redirectAttributes.addFlashAttribute("message", "Candidato seleccionado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al seleccionar: " + e.getMessage());
        }
        return "redirect:/tecnico/solicitudes/" + idSolicitud + "/candidatos";
    }

    @PostMapping("/solicitudes/{idSolicitud}/retirar/{idSeleccion}")
    public String retirarSeleccion(@PathVariable int idSolicitud,
                                   @PathVariable int idSeleccion,
                                   RedirectAttributes redirectAttributes) {
        Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null || seleccion.getIdSolicitud() != idSolicitud) {
            redirectAttributes.addFlashAttribute("message", "Selección no encontrada.");
            return "redirect:/tecnico/solicitudes/" + idSolicitud + "/candidatos";
        }
        if (!("propuesta".equals(seleccion.getEstado()) || "contactada".equals(seleccion.getEstado()))) {
            redirectAttributes.addFlashAttribute("message",
                    "Solo se puede retirar una propuesta que no haya sido aceptada ni descartada.");
            return "redirect:/tecnico/solicitudes/" + idSolicitud + "/candidatos";
        }
        seleccionDao.deleteSeleccion(idSeleccion);
        redirectAttributes.addFlashAttribute("message", "Propuesta retirada correctamente.");
        return "redirect:/tecnico/solicitudes/" + idSolicitud + "/candidatos";
    }
}