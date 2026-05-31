package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.dao.ComunicacionUsuarioDao;
import es.uji.ei1027.sgovi.dao.SeleccionDao;
import es.uji.ei1027.sgovi.dao.SolicitudServicioAPDao;
import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.model.ComunicacionUsuario;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/comunicaciones")
public class ComunicacionController {

    private static final int MAX_MENSAJE = 2000;

    private final ComunicacionUsuarioDao comunicacionDao;
    private final SeleccionDao seleccionDao;
    private final SolicitudServicioAPDao solicitudDao;
    private final CandidatoDao candidatoDao;
    private final UsuarioOviDao usuarioDao;

    @Autowired
    public ComunicacionController(ComunicacionUsuarioDao comunicacionDao,
                                  SeleccionDao seleccionDao,
                                  SolicitudServicioAPDao solicitudDao,
                                  CandidatoDao candidatoDao,
                                  UsuarioOviDao usuarioDao) {
        this.comunicacionDao = comunicacionDao;
        this.seleccionDao = seleccionDao;
        this.solicitudDao = solicitudDao;
        this.candidatoDao = candidatoDao;
        this.usuarioDao = usuarioDao;
    }

    private boolean autorizado(UserDetails user, Seleccion seleccion, SolicitudServicioAP solicitud) {
        if (user == null) return false;
        String rol = user.getRol();
        if ("TECNICO".equals(rol)) return true;
        if ("USUARIO_OVI".equals(rol)) return user.getUsername().equals(solicitud.getIdUsuario());
        if ("CANDIDATO".equals(rol)) return user.getUsername().equals(seleccion.getIdAp());
        return false;
    }

    private String nombreAutor(ComunicacionUsuario m) {
        if (m.getIdTecnico() != null) {
            return "Equipo técnico de la OVI";
        }
        if (m.getIdUsuario() != null) {
            UsuarioOvi u = usuarioDao.getUsuario(m.getIdUsuario());
            return u != null ? u.getNombre() + " " + u.getApellidos() + " (persona usuaria)" : "Persona usuaria";
        }
        if (m.getIdAp() != null) {
            Candidato c = candidatoDao.getCandidato(m.getIdAp());
            return c != null ? c.getNombre() + " " + c.getApellidos() + " (asistente)" : "Asistente";
        }
        return "Desconocido";
    }

    private void poblarHilo(Model model, int idSeleccion, Seleccion seleccion, SolicitudServicioAP solicitud) {
        List<ComunicacionUsuario> mensajes = comunicacionDao.findBySeleccion(idSeleccion);
        Map<Integer, String> autores = new LinkedHashMap<>();
        for (ComunicacionUsuario m : mensajes) {
            autores.put(m.getIdComunicacion(), nombreAutor(m));
        }
        Candidato candidato = candidatoDao.getCandidato(seleccion.getIdAp());
        model.addAttribute("seleccion", seleccion);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("candidato", candidato);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("autores", autores);
        model.addAttribute("puedeEscribir", !"descartada".equals(seleccion.getEstado()));
    }

    @GetMapping("/seleccion/{idSeleccion}")
    public String hilo(@PathVariable int idSeleccion, HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null) return "redirect:/";
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(seleccion.getIdSolicitud());
        if (solicitud == null || !autorizado(user, seleccion, solicitud)) return "redirect:/";

        poblarHilo(model, idSeleccion, seleccion, solicitud);
        return "comunicacion/hilo";
    }

    @PostMapping("/seleccion/{idSeleccion}")
    public String enviar(@PathVariable int idSeleccion,
                         @RequestParam("mensaje") String mensaje,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        Seleccion seleccion = seleccionDao.getSeleccion(idSeleccion);
        if (seleccion == null) return "redirect:/";
        SolicitudServicioAP solicitud = solicitudDao.getSolicitud(seleccion.getIdSolicitud());
        if (solicitud == null || !autorizado(user, seleccion, solicitud)) return "redirect:/";

        if ("descartada".equals(seleccion.getEstado())) {
            redirectAttributes.addFlashAttribute("errorMensaje",
                    "No se pueden enviar mensajes en una propuesta descartada.");
            return "redirect:/comunicaciones/seleccion/" + idSeleccion;
        }
        if (mensaje == null || mensaje.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMensaje", "El mensaje no puede estar vacío.");
            return "redirect:/comunicaciones/seleccion/" + idSeleccion;
        }
        if (mensaje.length() > MAX_MENSAJE) {
            redirectAttributes.addFlashAttribute("errorMensaje",
                    "El mensaje es demasiado largo (máximo " + MAX_MENSAJE + " caracteres).");
            return "redirect:/comunicaciones/seleccion/" + idSeleccion;
        }

        ComunicacionUsuario com = new ComunicacionUsuario();
        com.setIdSeleccion(idSeleccion);
        com.setMensaje(mensaje.trim());
        switch (user.getRol()) {
            case "USUARIO_OVI": com.setIdUsuario(user.getUsername()); break;
            case "CANDIDATO":   com.setIdAp(user.getUsername());      break;
            case "TECNICO":     com.setIdTecnico(user.getUsername()); break;
            default: return "redirect:/";
        }
        comunicacionDao.addComunicacion(com);
        redirectAttributes.addFlashAttribute("mensajeExito", "Mensaje enviado.");
        return "redirect:/comunicaciones/seleccion/" + idSeleccion;
    }
}