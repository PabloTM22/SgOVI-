package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.RegistroContratoDao;
import es.uji.ei1027.sgovi.dao.SolicitudServicioAPDao;
import es.uji.ei1027.sgovi.model.*;
import es.uji.ei1027.sgovi.validator.SolicitudValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import es.uji.ei1027.sgovi.dao.SeleccionDao;
import es.uji.ei1027.sgovi.dao.CandidatoDao;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {
    private final SolicitudServicioAPDao solicitudDao;
    private final SeleccionDao seleccionDao;
    private final CandidatoDao candidatoDao;
    private final RegistroContratoDao contratoDao;
    private final SolicitudValidator solicitudValidator;

    @Autowired
    public SolicitudController(SolicitudServicioAPDao solicitudDao,
                               SeleccionDao seleccionDao,
                               CandidatoDao candidatoDao,
                               RegistroContratoDao contratoDao,
                               SolicitudValidator solicitudValidator) {
        this.solicitudDao = solicitudDao;
        this.seleccionDao = seleccionDao;
        this.candidatoDao = candidatoDao;
        this.contratoDao = contratoDao;
        this.solicitudValidator = solicitudValidator;
    }

    @InitBinder("solicitud")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(solicitudValidator);
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
    public String nuevaSubmit(@ModelAttribute("solicitud") @Valid SolicitudServicioAP solicitud,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            return "solicitud/nueva";
        }
        UserDetails user = (UserDetails) session.getAttribute("user");
        solicitud.setIdUsuario(user.getUsername());
        solicitud.setEstado("en revision");
        solicitudDao.addSolicitud(solicitud);
        return "redirect:/solicitudes/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            session.setAttribute("nextUrl", "/solicitudes/" + id);
            return "redirect:/login";
        }
        UserDetails user = (UserDetails) session.getAttribute("user");
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(id);
        if (solicitud == null || !solicitud.getIdUsuario().equals(user.getUsername())) {
            return "redirect:/solicitudes/lista";
        }

        if ("cerrada con contrato".equals(solicitud.getEstado())
                || "cerrada con contrato finalizado".equals(solicitud.getEstado())) {
            for (Seleccion sel : seleccionDao.findBySolicitud(id)) {
                if ("aceptada".equals(sel.getEstado())) {
                    RegistroContrato c = contratoDao.findBySeleccion(sel.getIdSeleccion());
                    if (c != null) {
                        model.addAttribute("idContrato", c.getIdContrato());
                    }
                }
            }
        }

        model.addAttribute("solicitud", solicitud);
        return "solicitud/detalle";
    }
    @GetMapping("/{id}/propuestas")
    public String propuestas(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            session.setAttribute("nextUrl", "/solicitudes/" + id + "/propuestas");
            return "redirect:/login";
        }
        UserDetails user = (UserDetails) session.getAttribute("user");
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(id);
        if (solicitud == null || !solicitud.getIdUsuario().equals(user.getUsername())) {
            return "redirect:/solicitudes/lista";
        }
        List<Seleccion> selecciones = seleccionDao.findBySolicitud(id);
        Map<Integer, Candidato> candidatos = new LinkedHashMap<>();
        for (Seleccion s : selecciones) {
            candidatos.put(s.getIdSeleccion(), candidatoDao.getCandidato(s.getIdAp()));
        }
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("selecciones", selecciones);
        model.addAttribute("candidatos", candidatos);
        return "solicitud/propuestas";
    }

    @PostMapping("/{id}/propuestas/{idSeleccion}/aceptar")
    public String aceptarPropuesta(@PathVariable int id,
                                   @PathVariable int idSeleccion,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        UserDetails user = (UserDetails) session.getAttribute("user");
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(id);
        if (solicitud == null || !solicitud.getIdUsuario().equals(user.getUsername())) {
            return "redirect:/solicitudes/lista";
        }
        Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null || seleccion.getIdSolicitud() != id
                || !("propuesta".equals(seleccion.getEstado()) || "contactada".equals(seleccion.getEstado()))) {
            redirectAttributes.addFlashAttribute("mensajeError", "Esta propuesta ya no se puede aceptar.");
            return "redirect:/solicitudes/" + id + "/propuestas";
        }
        seleccionDao.updateEstado(idSeleccion, "aceptada");
        for (Seleccion otra : seleccionDao.findBySolicitud(id)) {
            if (otra.getIdSeleccion() != idSeleccion
                    && ("propuesta".equals(otra.getEstado()) || "contactada".equals(otra.getEstado()))) {
                seleccionDao.updateEstado(otra.getIdSeleccion(), "descartada");
            }
        }
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Ha aceptado al asistente personal propuesto. El resto de propuestas se han descartado.");
        return "redirect:/solicitudes/" + id + "/propuestas";
    }

    @PostMapping("/{id}/propuestas/{idSeleccion}/descartar")
    public String descartarPropuesta(@PathVariable int id,
                                     @PathVariable int idSeleccion,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        UserDetails user = (UserDetails) session.getAttribute("user");
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(id);
        if (solicitud == null || !solicitud.getIdUsuario().equals(user.getUsername())) {
            return "redirect:/solicitudes/lista";
        }
        Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null || seleccion.getIdSolicitud() != id
                || !("propuesta".equals(seleccion.getEstado()) || "contactada".equals(seleccion.getEstado()))) {
            redirectAttributes.addFlashAttribute("mensajeError", "Esta propuesta ya no se puede descartar.");
            return "redirect:/solicitudes/" + id + "/propuestas";
        }
        seleccionDao.updateEstado(idSeleccion, "descartada");
        redirectAttributes.addFlashAttribute("mensajeExito", "Propuesta descartada.");
        return "redirect:/solicitudes/" + id + "/propuestas";
    }
}