package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.model.Formador;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.service.FormadorService;
import es.uji.ei1027.sgovi.validator.FormadorValidator;
import jakarta.servlet.http.HttpSession;
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

    private boolean esTecnico(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        return user != null && "TECNICO".equals(user.getRol());
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/formadores");
            return "redirect:/login";
        }
        model.addAttribute("formadores", formadorDao.getFormadores());
        return "formador/lista";
    }

    @GetMapping("/alta")
    public String altaForm(HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/formadores/alta");
            return "redirect:/login";
        }
        model.addAttribute("formador", new Formador());
        return "formador/alta";
    }

    @PostMapping("/alta")
    public String altaSubmit(@ModelAttribute("formador") Formador formador,
                             BindingResult bindingResult,
                             HttpSession session) {
        if (!esTecnico(session)) return "redirect:/login";
        if (bindingResult.hasErrors()) {
            return "formador/alta";
        }
        formadorDao.addFormador(formador);
        return "redirect:/formadores";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/formadores/editar/" + id);
            return "redirect:/login";
        }
        model.addAttribute("formador", formadorDao.getFormador(id));
        return "formador/editar";
    }

    @PostMapping("/editar/{id}")
    public String editarSubmit(@PathVariable int id,
                               @ModelAttribute("formador") Formador formador,
                               BindingResult bindingResult,
                               HttpSession session) {
        if (!esTecnico(session)) return "redirect:/login";
        if (bindingResult.hasErrors()) {
            return "formador/editar";
        }
        formador.setIdFormador(id);
        formadorService.actualizarFormador(formador);
        return "redirect:/formadores";
    }

    @GetMapping("/borrar/{id}")
    public String borrar(@PathVariable int id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!esTecnico(session)) return "redirect:/login";
        try {
            formadorDao.deleteFormador(id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorBorrar",
                    "No se puede eliminar el formador porque tiene actividades asociadas.");
        }
        return "redirect:/formadores";
    }
}