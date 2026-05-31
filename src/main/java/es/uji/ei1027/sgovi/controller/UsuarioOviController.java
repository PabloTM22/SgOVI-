package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
import es.uji.ei1027.sgovi.service.UsuarioOviService;
import es.uji.ei1027.sgovi.validator.UsuarioOviValidator;
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

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioOviDao.getUsuarios());
        return "usuario/lista";
    }

    @GetMapping("/alta")
    public String altaForm(Model model) {
        model.addAttribute("usuario", new UsuarioOvi());
        return "usuario/alta";
    }

    @PostMapping("/alta")
    public String altaSubmit(@ModelAttribute("usuario") @Valid UsuarioOvi usuario,
                             BindingResult bindingResult,
                             @RequestParam("contrasena") String contrasena) {
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
    public String editarForm(@PathVariable String idUsuario, Model model) {
        model.addAttribute("usuario", usuarioOviDao.getUsuario(idUsuario));
        return "usuario/editar";
    }

    @PostMapping("/editar/{idUsuario}")
    public String editarSubmit(@PathVariable String idUsuario,
                               @ModelAttribute("usuario") @Valid UsuarioOvi usuario,
                               BindingResult bindingResult,
                               @RequestParam("contrasena") String contrasena) {
        if (bindingResult.hasErrors()) {
            return "usuario/editar";
        }
        usuario.setIdUsuario(idUsuario);
        usuarioOviService.actualizarUsuario(usuario, contrasena);
        return "redirect:/usuarios";
    }

    @GetMapping("/revision")
    public String revision(Model model) {
        model.addAttribute("usuarios", usuarioOviDao.findByEstado("pendiente"));
        return "usuario/revision";
    }

    @GetMapping("/detalle/{idUsuario}")
    public String detalle(@PathVariable String idUsuario, Model model) {
        UsuarioOvi usuario = usuarioOviDao.getUsuario(idUsuario);
        if (usuario == null) {
            return "redirect:/usuarios/revision";
        }
        model.addAttribute("usuario", usuario);
        return "usuario/detalle";
    }

    @PostMapping("/aceptar/{idUsuario}")
    public String aceptar(@PathVariable String idUsuario,
                          RedirectAttributes redirectAttributes) {
        usuarioOviService.aceptarUsuario(idUsuario);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario aceptado correctamente.");
        return "redirect:/usuarios/revision";
    }

    @PostMapping("/rechazar/{idUsuario}")
    public String rechazar(@PathVariable String idUsuario,
                           RedirectAttributes redirectAttributes) {
        usuarioOviService.rechazarUsuario(idUsuario);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario rechazado.");
        return "redirect:/usuarios/revision";
    }

    @PostMapping("/borrar/{idUsuario}")
    public String borrar(@PathVariable String idUsuario,
                         RedirectAttributes redirectAttributes) {
        try {
            usuarioOviDao.deleteUsuario(idUsuario);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorBorrar",
                    "No se puede eliminar el usuario porque tiene solicitudes asociadas.");
        }
        return "redirect:/usuarios";
    }
}