package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
import es.uji.ei1027.sgovi.service.UsuarioOviService;
import es.uji.ei1027.sgovi.validator.UsuarioOviValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    // Listado de todos los usuarios OVI
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioOviDao.getUsuarios());
        return "usuario/lista";
    }

    // Formulario de alta de nuevo usuario
    @GetMapping("/alta")
    public String altaForm(Model model) {
        model.addAttribute("usuario", new UsuarioOvi());
        return "usuario/alta";
    }

    // Procesa el formulario de alta
    @PostMapping("/alta")
    public String altaSubmit(@ModelAttribute("usuario") UsuarioOvi usuario,
                             BindingResult bindingResult) {
        new UsuarioOviValidator().validate(usuario, bindingResult);
        if (bindingResult.hasErrors()) {
            return "usuario/alta";
        }
        usuarioOviService.registrarUsuario(usuario);
        return "redirect:/usuarios";
    }

    // Formulario de edición
    @GetMapping("/editar/{idUsuario}")
    public String editarForm(@PathVariable String idUsuario, Model model) {
        UsuarioOvi usuario = usuarioOviDao.getUsuario(idUsuario);
        model.addAttribute("usuario", usuario);
        return "usuario/editar";
    }

    // Procesa la edición
    @PostMapping("/editar/{idUsuario}")
    public String editarSubmit(@PathVariable String idUsuario,
                               @ModelAttribute("usuario") UsuarioOvi usuario,
                               BindingResult bindingResult) {
        new UsuarioOviValidator().validate(usuario, bindingResult);
        if (bindingResult.hasErrors()) {
            return "usuario/editar";
        }
        usuario.setIdUsuario(idUsuario);
        usuarioOviDao.updateUsuario(usuario);
        return "redirect:/usuarios";
    }

    // Borrar un usuario
    @GetMapping("/borrar/{idUsuario}")
    public String borrar(@PathVariable String idUsuario) {
        usuarioOviDao.deleteUsuario(idUsuario);
        return "redirect:/usuarios";
    }
}
