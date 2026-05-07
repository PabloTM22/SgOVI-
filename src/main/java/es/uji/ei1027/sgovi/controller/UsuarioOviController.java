package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
import es.uji.ei1027.sgovi.service.UsuarioOviService;
import es.uji.ei1027.sgovi.validator.UsuarioOviValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioOviController {
    private final UsuarioOviService usuarioOviService;
    private final UsuarioOviDao usuarioOviDao;

    @Autowired
    public UsuarioOviController(UsuarioOviService usuarioOviService, UsuarioOviDao usuarioOviDao) {
        this.usuarioOviService = usuarioOviService;
        this.usuarioOviDao = usuarioOviDao;
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
    public String altaSubmit(@ModelAttribute("usuario") UsuarioOvi usuario,
                             BindingResult bindingResult,
                             HttpSession session) {
        if (!esTecnico(session)) return "redirect:/login";
        new UsuarioOviValidator().validate(usuario, bindingResult);
        if (bindingResult.hasErrors()) {
            return "usuario/alta";
        }
        usuarioOviService.registrarUsuario(usuario);
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
                               @ModelAttribute("usuario") UsuarioOvi usuario,
                               BindingResult bindingResult,
                               HttpSession session) {
        if (!esTecnico(session)) return "redirect:/login";
        new UsuarioOviValidator().validate(usuario, bindingResult);
        if (bindingResult.hasErrors()) {
            return "usuario/editar";
        }
        usuario.setIdUsuario(idUsuario);
        usuarioOviDao.updateUsuario(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/borrar/{idUsuario}")
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