package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.model.Formador;
import es.uji.ei1027.sgovi.validator.FormadorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/formadores")
public class FormadorController {

    private final FormadorDao formadorDao;
    private final FormadorValidator formadorValidator;

    @Autowired
    public FormadorController(FormadorDao formadorDao,
                              FormadorValidator formadorValidator) {
        this.formadorDao = formadorDao;
        this.formadorValidator = formadorValidator;
    }


    @InitBinder("formador")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(formadorValidator);
    }

    //  Formulario de alta de formador

    @GetMapping("/alta")
    public String altaFormadorForm(Model model) {
        model.addAttribute("formador", new Formador());
        return "formador/alta";
    }

    @PostMapping("/alta")
    public String altaFormadorSubmit(@ModelAttribute("formador") Formador formador,
                                     BindingResult bindingResult) {


        formadorValidator.validate(formador, bindingResult);

        // Si hay errores de validación, volvemos al formulario
        if (bindingResult.hasErrors()) {
            return "formador/alta";
        }

        // Sin errores: damos de alta al formador y redirigimos
        formadorDao.addFormador(formador);
        return "redirect:/";
    }
}