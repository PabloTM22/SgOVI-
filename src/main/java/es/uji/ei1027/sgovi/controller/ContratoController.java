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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Controller
@RequestMapping("/contratos")
public class ContratoController {

    @Value("${upload.file.directory}")
    private String uploadDirectory;

    private final RegistroContratoDao contratoDao;
    private final SeleccionDao seleccionDao;
    private final SolicitudServicioAPDao solicitudDao;
    private final CandidatoDao candidatoDao;
    private final UsuarioOviDao usuarioDao;

    @Autowired
    public ContratoController(RegistroContratoDao contratoDao,
                              SeleccionDao seleccionDao,
                              SolicitudServicioAPDao solicitudDao,
                              CandidatoDao candidatoDao,
                              UsuarioOviDao usuarioDao) {
        this.contratoDao = contratoDao;
        this.seleccionDao = seleccionDao;
        this.solicitudDao = solicitudDao;
        this.candidatoDao = candidatoDao;
        this.usuarioDao = usuarioDao;
    }

    private boolean esTecnico(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        return user != null && "TECNICO".equals(user.getRol());
    }

    private void poblarDatos(Model model, Seleccion seleccion, SolicitudServicioAP solicitud) {
        model.addAttribute("seleccion", seleccion);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("candidato", candidatoDao.getCandidato(seleccion.getIdAp()));
        model.addAttribute("usuario", usuarioDao.getUsuario(solicitud.getIdUsuario()));
    }

    @GetMapping("/nuevo/{idSeleccion}")
    public String nuevoForm(@PathVariable int idSeleccion, HttpSession session, Model model,
                            RedirectAttributes redirectAttributes) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/contratos/nuevo/" + idSeleccion);
            return "redirect:/login";
        }
        Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null || !"aceptada".equals(seleccion.getEstado())) {
            redirectAttributes.addFlashAttribute("message",
                    "Solo se puede formalizar un contrato sobre una propuesta aceptada.");
            return "redirect:/tecnico/solicitudes";
        }
        if (contratoDao.findBySeleccion(idSeleccion) != null) {
            redirectAttributes.addFlashAttribute("message",
                    "Esta propuesta ya tiene un contrato registrado.");
            return "redirect:/tecnico/solicitudes/" + seleccion.getIdSolicitud() + "/candidatos";
        }
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(seleccion.getIdSolicitud());
        RegistroContrato contrato = new RegistroContrato();
        contrato.setIdSeleccion(idSeleccion);
        contrato.setFechaInicio(LocalDate.now());
        model.addAttribute("contrato", contrato);
        poblarDatos(model, seleccion, solicitud);
        return "contrato/nuevo";
    }

    @PostMapping("/nuevo/{idSeleccion}")
    public String nuevoSubmit(@PathVariable int idSeleccion,
                              @ModelAttribute("contrato") RegistroContrato contrato,
                              @RequestParam("file") MultipartFile file,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (!esTecnico(session)) return "redirect:/login";
        Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null || !"aceptada".equals(seleccion.getEstado())) {
            redirectAttributes.addFlashAttribute("message",
                    "Solo se puede formalizar un contrato sobre una propuesta aceptada.");
            return "redirect:/tecnico/solicitudes";
        }
        if (contratoDao.findBySeleccion(idSeleccion) != null) {
            redirectAttributes.addFlashAttribute("message",
                    "Esta propuesta ya tiene un contrato registrado.");
            return "redirect:/tecnico/solicitudes/" + seleccion.getIdSolicitud() + "/candidatos";
        }
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(seleccion.getIdSolicitud());

        if (contrato.getFechaInicio() == null) {
            model.addAttribute("message", "La fecha de inicio es obligatoria.");
            poblarDatos(model, seleccion, solicitud);
            return "contrato/nuevo";
        }
        if (contrato.getFechaFin() != null && contrato.getFechaFin().isBefore(contrato.getFechaInicio())) {
            model.addAttribute("message", "La fecha de fin no puede ser anterior a la de inicio.");
            poblarDatos(model, seleccion, solicitud);
            return "contrato/nuevo";
        }

        String pdfRuta = null;
        if (file != null && !file.isEmpty()) {
            String nombreOriginal = file.getOriginalFilename();
            if (nombreOriginal == null || !nombreOriginal.toLowerCase().endsWith(".pdf")) {
                model.addAttribute("message", "El documento del contrato debe ser un PDF.");
                poblarDatos(model, seleccion, solicitud);
                return "contrato/nuevo";
            }
            String nombreFichero = "contrato_" + idSeleccion + "_" + System.currentTimeMillis() + ".pdf";
            try {
                Path destino = Paths.get(uploadDirectory + "pdfs/contratos/" + nombreFichero);
                Files.createDirectories(destino.getParent());
                Files.write(destino, file.getBytes());
                pdfRuta = "/pdfs/contratos/" + nombreFichero;
            } catch (IOException e) {
                model.addAttribute("message", "No se pudo guardar el documento: " + e.getMessage());
                poblarDatos(model, seleccion, solicitud);
                return "contrato/nuevo";
            }
        }

        UserDetails user = (UserDetails) session.getAttribute("user");
        contrato.setIdSeleccion(idSeleccion);
        contrato.setIdTecnico(user.getUsername());
        contrato.setPdfRuta(pdfRuta);
        contratoDao.addContrato(contrato);

        solicitudDao.updateEstado(seleccion.getIdSolicitud(), "cerrada con contrato");

        redirectAttributes.addFlashAttribute("message", "Contrato registrado correctamente.");
        RegistroContrato creado = contratoDao.findBySeleccion(idSeleccion);
        return "redirect:/contratos/" + creado.getIdContrato();
    }

    @GetMapping("/{idContrato}")
    public String detalle(@PathVariable int idContrato, HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/contratos/" + idContrato);
            return "redirect:/login";
        }
        RegistroContrato contrato = contratoDao.getContrato(idContrato);
        if (contrato == null) return "redirect:/";
        Seleccion seleccion = seleccionDao.getSeleccion(contrato.getIdSeleccion());
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(seleccion.getIdSolicitud());

        boolean autorizado = "TECNICO".equals(user.getRol())
                || user.getUsername().equals(solicitud.getIdUsuario())
                || user.getUsername().equals(seleccion.getIdAp());
        if (!autorizado) return "redirect:/";

        if (contrato.getFechaFin() != null
                && contrato.getFechaFin().isBefore(LocalDate.now())
                && "cerrada con contrato".equals(solicitud.getEstado())) {
            solicitudDao.updateEstado(solicitud.getIdSolicitud(), "cerrada con contrato finalizado");
            solicitud = solicitudDao.getSolicitud(solicitud.getIdSolicitud());
        }

        model.addAttribute("contrato", contrato);
        poblarDatos(model, seleccion, solicitud);
        model.addAttribute("esTecnico", "TECNICO".equals(user.getRol()));
        return "contrato/detalle";
    }

    @GetMapping("/{idContrato}/editar")
    public String editarForm(@PathVariable int idContrato, HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/contratos/" + idContrato + "/editar");
            return "redirect:/login";
        }
        RegistroContrato contrato = contratoDao.getContrato(idContrato);
        if (contrato == null) return "redirect:/";
        Seleccion seleccion = seleccionDao.getSeleccion(contrato.getIdSeleccion());
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(seleccion.getIdSolicitud());
        model.addAttribute("contrato", contrato);
        poblarDatos(model, seleccion, solicitud);
        return "contrato/editar";
    }

    @PostMapping("/{idContrato}/editar")
    public String editarSubmit(@PathVariable int idContrato,
                               @RequestParam(value = "fechaFin", required = false)
                               @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!esTecnico(session)) return "redirect:/login";
        RegistroContrato contrato = contratoDao.getContrato(idContrato);
        if (contrato == null) return "redirect:/";
        if (fechaFin != null && fechaFin.isBefore(contrato.getFechaInicio())) {
            Seleccion seleccion = seleccionDao.getSeleccion(contrato.getIdSeleccion());
            SolicitudServicioAP solicitud = solicitudDao.getSolicitud(seleccion.getIdSolicitud());
            model.addAttribute("contrato", contrato);
            poblarDatos(model, seleccion, solicitud);
            model.addAttribute("message", "La fecha de fin no puede ser anterior a la de inicio.");
            return "contrato/editar";
        }
        contrato.setFechaFin(fechaFin);
        contratoDao.updateContrato(contrato);
        redirectAttributes.addFlashAttribute("message", "Datos del contrato actualizados.");
        return "redirect:/contratos/" + idContrato;
    }
}