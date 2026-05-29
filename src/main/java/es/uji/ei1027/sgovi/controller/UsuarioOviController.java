package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
import es.uji.ei1027.sgovi.service.UsuarioOviService;
import es.uji.ei1027.sgovi.validator.UsuarioOviValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioOviController {
    private final UsuarioOviService usuarioOviService;
    private final UsuarioOviDao usuarioOviDao;
    private final UsuarioOviValidator usuarioOviValidator;

    @Autowired
    public UsuarioOviController(UsuarioOviService usuarioOviService, UsuarioOviDao usuarioOviDao, UsuarioOviValidator usuarioOviValidator) {
        this.usuarioOviService = usuarioOviService;
        this.usuarioOviDao = usuarioOviDao;
        this.usuarioOviValidator = usuarioOviValidator;
    }

    @InitBinder("usuario")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(usuarioOviValidator);
    }

    private boolean esTecnico(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        return user != null && "TECNICO".equals(user.getRol());
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/usuarios");
            return "redirect:/login";
        }
        model.addAttribute("usuarios", usuarioOviDao.getUsuarios());
        return "usuario/lista";
    }

    @GetMapping("/alta")
    public String altaForm(HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/usuarios/alta");
            return "redirect:/login";
        }
        model.addAttribute("usuario", new UsuarioOvi());
        return "usuario/alta";
    }

    @PostMapping("/alta")
    public String altaSubmit(@ModelAttribute("usuario") @Valid UsuarioOvi usuario,
                             BindingResult bindingResult,
                             @RequestParam("contrasena") String contrasena,
                             HttpSession session) {
        if (!esTecnico(session)) return "redirect:/login";
        if (contrasena == null || contrasena.isBlank()) {
            bindingResult.rejectValue("dni", "required.contrasena",
                    "La contraseña es obligatoria.");
        }
        if (bindingResult.hasErrors()) {
            return "usuario/alta";
        }
        usuarioOviService.registrarUsuario(usuario, contrasena);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{idUsuario}")
    public String editarForm(@PathVariable String idUsuario, HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/usuarios/editar/" + idUsuario);
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioOviDao.getUsuario(idUsuario));
        return "usuario/editar";
    }

    @PostMapping("/editar/{idUsuario}")
    public String editarSubmit(@PathVariable String idUsuario,
                               @ModelAttribute("usuario") @Valid UsuarioOvi usuario,
                               BindingResult bindingResult,
                               @RequestParam("contrasena") String contrasena,
                               HttpSession session) {
        if (!esTecnico(session)) return "redirect:/login";
        if (bindingResult.hasErrors()) {
            return "usuario/editar";
        }
        usuario.setIdUsuario(idUsuario);
        usuarioOviService.actualizarUsuario(usuario, contrasena);
        return "redirect:/usuarios";
    }

    @GetMapping("/revision")
    public String revision(HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/usuarios/revision");
            return "redirect:/login";
        }
        model.addAttribute("usuarios", usuarioOviDao.findByEstado("pendiente"));
        return "usuario/revision";
    }

    @GetMapping("/detalle/{idUsuario}")
    public String detalle(@PathVariable String idUsuario, HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/usuarios/detalle/" + idUsuario);
            return "redirect:/login";
        }
        UsuarioOvi usuario = usuarioOviDao.getUsuario(idUsuario);
        if (usuario == null) {
            return "redirect:/usuarios/revision";
        }
        model.addAttribute("usuario", usuario);
        return "usuario/detalle";
    }

    @PostMapping("/aceptar/{idUsuario}")
    public String aceptar(@PathVariable String idUsuario,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!esTecnico(session)) return "redirect:/login";
        usuarioOviService.aceptarUsuario(idUsuario);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario aceptado correctamente.");
        return "redirect:/usuarios/revision";
    }

    @PostMapping("/rechazar/{idUsuario}")
    public String rechazar(@PathVariable String idUsuario,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (!esTecnico(session)) return "redirect:/login";
        usuarioOviService.rechazarUsuario(idUsuario);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario rechazado.");
        return "redirect:/usuarios/revision";
    }

    @PostMapping("/borrar/{idUsuario}")
    public String borrar(@PathVariable String idUsuario,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!esTecnico(session)) return "redirect:/login";
        try {
            usuarioOviDao.deleteUsuario(idUsuario);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorBorrar",
                    "No se puede eliminar el usuario porque tiene solicitudes asociadas.");
        }
        return "redirect:/usuarios";
    }
}