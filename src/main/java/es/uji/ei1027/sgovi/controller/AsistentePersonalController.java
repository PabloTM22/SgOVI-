package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.dao.RegistroContratoDao;
import es.uji.ei1027.sgovi.dao.SeleccionDao;
import es.uji.ei1027.sgovi.dao.SolicitudServicioAPDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.model.RegistroContrato;
import es.uji.ei1027.sgovi.model.Seleccion;
import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import es.uji.ei1027.sgovi.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mi-perfil")
public class AsistentePersonalController {

    private final CandidatoDao candidatoDao;
    private final SeleccionDao seleccionDao;
    private final SolicitudServicioAPDao solicitudDao;
    private final RegistroContratoDao contratoDao;

    @Autowired
    public AsistentePersonalController(CandidatoDao candidatoDao,
                                       SeleccionDao seleccionDao,
                                       SolicitudServicioAPDao solicitudDao,
                                       RegistroContratoDao contratoDao) {
        this.candidatoDao = candidatoDao;
        this.seleccionDao = seleccionDao;
        this.solicitudDao = solicitudDao;
        this.contratoDao = contratoDao;
    }

    @GetMapping
    public String panel(HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        Candidato asistente = candidatoDao.getCandidato(user.getUsername());
        if (asistente == null) return "redirect:/";

        List<Seleccion> propuestas = seleccionDao.findByAp(user.getUsername());
        Map<Integer, SolicitudServicioAP> solicitudes = new LinkedHashMap<>();
        Map<Integer, Integer> contratoPorSeleccion = new LinkedHashMap<>();
        for (Seleccion s : propuestas) {
            solicitudes.put(s.getIdSeleccion(), solicitudDao.getSolicitud(s.getIdSolicitud()));
            if ("aceptada".equals(s.getEstado())) {
                RegistroContrato c = contratoDao.findBySeleccion(s.getIdSeleccion());
                if (c != null) {
                    contratoPorSeleccion.put(s.getIdSeleccion(), c.getIdContrato());
                }
            }
        }
        model.addAttribute("asistente", asistente);
        model.addAttribute("propuestas", propuestas);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("contratoPorSeleccion", contratoPorSeleccion);
        return "asistente/panel";
    }

    @GetMapping("/editar")
    public String editarForm(HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        Candidato asistente = candidatoDao.getCandidato(user.getUsername());
        if (asistente == null) return "redirect:/";
        model.addAttribute("asistente", asistente);
        return "asistente/editarPerfil";
    }

    @PostMapping("/editar")
    public String editarSubmit(@ModelAttribute("asistente") Candidato form,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        Candidato actual = candidatoDao.getCandidato(user.getUsername());
        if (actual == null) return "redirect:/";

        if (form.getEmail() == null || form.getEmail().trim().isEmpty()) {
            model.addAttribute("asistente", form);
            model.addAttribute("message", "El correo electrónico es obligatorio.");
            return "asistente/editarPerfil";
        }

        actual.setEmail(form.getEmail().trim());
        actual.setTelefono(form.getTelefono());
        actual.setFormacion(form.getFormacion());
        actual.setExperiencia(form.getExperiencia());
        actual.setDisponibilidad(form.getDisponibilidad());

        candidatoDao.updateCandidato(actual);
        redirectAttributes.addFlashAttribute("message", "Sus datos se han actualizado correctamente.");
        return "redirect:/mi-perfil";
    }
}