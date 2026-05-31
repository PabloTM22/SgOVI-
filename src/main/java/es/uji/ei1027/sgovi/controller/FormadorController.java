package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.model.Formador;
import es.uji.ei1027.sgovi.service.FormadorService;
import es.uji.ei1027.sgovi.validator.FormadorValidator;
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
@RequestMapping("/formadores")
public class FormadorController {

    private final FormadorDao formadorDao;
    private final FormadorValidator formadorValidator;
    private final FormadorService formadorService;

    @Autowired
    public FormadorController(FormadorDao formadorDao, FormadorValidator formadorValidator, FormadorService formadorService) {
        this.formadorDao = formadorDao;
        this.formadorValidator = formadorValidator;
        this.formadorService = formadorService;
    }

    @InitBinder("formador")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(formadorValidator);
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("formadores", formadorDao.getFormadores());
        return "formador/lista";
    }

    @GetMapping("/alta")
    public String altaForm(Model model) {
        model.addAttribute("formador", new Formador());
        return "formador/alta";
    }

    @PostMapping("/alta")
    public String altaSubmit(@ModelAttribute("formador") @Valid Formador formador,
                             BindingResult bindingResult,
                             @RequestParam("contrasena") String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            bindingResult.rejectValue("dni", "required.contrasena",
                    "La contraseña es obligatoria.");
        }
        if (bindingResult.hasErrors()) {
            return "formador/alta";
        }
        formadorService.registrarFormador(formador, contrasena);
        return "redirect:/formadores";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable String id, Model model) {
        model.addAttribute("formador", formadorDao.getFormador(id));
        return "formador/editar";
    }

    @PostMapping("/editar/{id}")
    public String editarSubmit(@PathVariable String id,
                               @ModelAttribute("formador") @Valid Formador formador,
                               BindingResult bindingResult,
                               @RequestParam("contrasena") String contrasena) {
        if (bindingResult.hasErrors()) {
            return "formador/editar";
        }
        formador.setIdFormador(id);
        formadorService.actualizarFormador(formador, contrasena);
        return "redirect:/formadores";
    }

    @PostMapping("/borrar/{id}")
    public String borrar(@PathVariable String id,
                         RedirectAttributes redirectAttributes) {
        try {
            formadorDao.deleteFormador(id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("message",
                    "No se puede eliminar el formador porque tiene actividades asociadas.");
        }
        return "redirect:/formadores";
    }
}