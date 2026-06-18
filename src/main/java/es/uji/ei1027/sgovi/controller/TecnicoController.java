package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.dao.RegistroContratoDao;
import es.uji.ei1027.sgovi.dao.SeleccionDao;
import es.uji.ei1027.sgovi.dao.SolicitudServicioAPDao;
import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.model.RegistroContrato;
import es.uji.ei1027.sgovi.model.Seleccion;
import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
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

    private static final int PAGE_LENGTH = 10;

    @GetMapping("/solicitudes")
    public String listaSolicitudesEnRevision(@RequestParam(value = "buscar", required = false) String buscar,
                                             @RequestParam(value = "tipo", required = false, defaultValue = "TODOS") String tipo,
                                             @RequestParam(value = "estado", required = false, defaultValue = "TODOS") String estado,
                                             @RequestParam(value = "orden", required = false, defaultValue = "fecha") String orden,
                                             @RequestParam(value = "page") java.util.Optional<Integer> page,
                                             Model model) {

        List<SolicitudServicioAP> solicitudes = solicitudDao.getSolicitudes();

        // Resolver nombre del solicitante por solicitud (para búsqueda y para mostrar)
        Map<Integer, String> nombrePorSolicitud = new java.util.LinkedHashMap<>();
        for (SolicitudServicioAP s : solicitudes) {
            UsuarioOvi u = usuarioDao.getUsuario(s.getIdUsuario());
            nombrePorSolicitud.put(s.getIdSolicitud(),
                    u != null ? u.getNombre() + " " + u.getApellidos() : "Usuario no disponible");
        }

        // Búsqueda por nombre del solicitante
        if (buscar != null && !buscar.isBlank()) {
            String q = buscar.toLowerCase();
            solicitudes = solicitudes.stream()
                    .filter(s -> nombrePorSolicitud.get(s.getIdSolicitud()).toLowerCase().contains(q))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filtro por tipo (PAP / PATI)
        if (tipo != null && !"TODOS".equalsIgnoreCase(tipo)) {
            solicitudes = solicitudes.stream()
                    .filter(s -> tipo.equals(s.getTipoAsistencia()))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filtro por estado
        if (estado != null && !"TODOS".equalsIgnoreCase(estado)) {
            solicitudes = solicitudes.stream()
                    .filter(s -> estado.equals(s.getEstado()))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Ordenación
        java.util.Comparator<SolicitudServicioAP> comparador;
        switch (orden) {
            case "estado":
                comparador = java.util.Comparator.comparing(SolicitudServicioAP::getEstado, String.CASE_INSENSITIVE_ORDER);
                break;
            case "tipo":
                comparador = java.util.Comparator.comparing(SolicitudServicioAP::getTipoAsistencia, String.CASE_INSENSITIVE_ORDER);
                break;
            default: // fecha (más reciente primero)
                comparador = java.util.Comparator.comparing(SolicitudServicioAP::getFechaSolicitud,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder()));
        }
        solicitudes.sort(comparador);

        // Mapa de contratos (solo para las cerradas con contrato)
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

        // Paginación (troceado en memoria)
        java.util.List<List<SolicitudServicioAP>> paginas = new java.util.ArrayList<>();
        for (int i = 0; i < solicitudes.size(); i += PAGE_LENGTH) {
            paginas.add(solicitudes.subList(i, Math.min(i + PAGE_LENGTH, solicitudes.size())));
        }
        int totalPaginas = paginas.size();
        int paginaActual = page.orElse(1);
        if (paginaActual < 1) paginaActual = 1;
        if (paginaActual > totalPaginas) paginaActual = totalPaginas;

        List<SolicitudServicioAP> pagina = totalPaginas == 0
                ? new java.util.ArrayList<>()
                : paginas.get(paginaActual - 1);

        List<Integer> numerosPagina = java.util.stream.IntStream.rangeClosed(1, totalPaginas)
                .boxed().collect(java.util.stream.Collectors.toList());

        model.addAttribute("solicitudes", pagina);
        model.addAttribute("nombrePorSolicitud", nombrePorSolicitud);
        model.addAttribute("contratoPorSolicitud", contratoPorSolicitud);
        model.addAttribute("numerosPagina", numerosPagina);
        model.addAttribute("paginaActual", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalRegistros", solicitudes.size());
        model.addAttribute("buscar", buscar);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroOrden", orden);
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

        UsuarioOvi usuarioSolicitante = usuarioDao.getUsuario(solicitud.getIdUsuario());
        model.addAttribute("nombreUsuario",
                usuarioSolicitante != null
                        ? usuarioSolicitante.getNombre() + " " + usuarioSolicitante.getApellidos()
                        : null);

        model.addAttribute("selecciones", seleccionesExistentes);
        model.addAttribute("candidatosSeleccion", candidatosSeleccion);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idsYaSeleccionados", idsYaSeleccionados);
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

    @GetMapping("/solicitudes/{id}")
    public String detalleSolicitud(@PathVariable int id, Model model) {
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(id);
        if (solicitud == null) {
            return "redirect:/tecnico/solicitudes";
        }
        UsuarioOvi usuario = usuarioDao.getUsuario(solicitud.getIdUsuario());
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("usuario", usuario);
        return "tecnico/detalleSolicitud";
    }
}