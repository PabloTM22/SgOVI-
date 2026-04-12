package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.service.CandidatoService;
import es.uji.ei1027.sgovi.validator.CandidatoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/candidatos")
public class CandidatoController {

    private final CandidatoService candidatoService;
    private final CandidatoDao candidatoDao;
    private final CandidatoValidator candidatoValidator;

    @Autowired
    public CandidatoController(CandidatoService candidatoService,
                               CandidatoDao candidatoDao,
                               CandidatoValidator candidatoValidator) {
        this.candidatoService = candidatoService;
        this.candidatoDao = candidatoDao;
        this.candidatoValidator = candidatoValidator;
    }

    /**
     * Vincula el validador al modelo Candidato para que Spring lo aplique
     * automáticamente cuando se procese el formulario de este controlador.
     */
    @InitBinder("candidato")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(candidatoValidator);
    }

    // ---------------------------------------------------------------
    //  Formulario público de registro
    // ---------------------------------------------------------------

    @GetMapping("/registro")
    public String registrarCandidatoForm(Model model) {
        model.addAttribute("candidato", new Candidato());
        return "candidato/registro";
    }

    /**
     * Procesa el envío del formulario de registro.
     *
     * Si la validación detecta errores (BindingResult.hasErrors()),
     * se devuelve el formulario con los mensajes de error sin persistir
     * nada en la base de datos.
     * Si todo es correcto, se delega en el servicio y se redirige al inicio.
     */
    @PostMapping("/registro")
    public String registrarCandidatoSubmit(@ModelAttribute("candidato") Candidato candidato,
                                           BindingResult bindingResult) {

        // El validador se ejecuta automáticamente gracias a @InitBinder,
        // pero también se puede invocar explícitamente:
        candidatoValidator.validate(candidato, bindingResult);

        // Si hay errores de validación, volvemos al formulario
        if (bindingResult.hasErrors()) {
            return "candidato/registro";
        }

        // Sin errores: registramos el candidato y redirigimos
        candidatoService.registrarCandidato(candidato);
        return "redirect:/";
    }

    // ---------------------------------------------------------------
    //  Panel del técnico (revisión de candidatos)
    // ---------------------------------------------------------------

    /** Lista los candidatos pendientes de aceptación (estadoAceptado = false). */
    @GetMapping("/revision")
    public String listarPendientes(Model model) {
        model.addAttribute("candidatos", candidatoDao.findByStatus(false));
        return "candidato/revision";
    }

    /** Acepta al candidato cambiando su estado a true. */
    @GetMapping("/aceptar/{idAp}")
    public String aceptarCandidato(@PathVariable String idAp) {
        candidatoDao.updateStatus(idAp, true);
        return "redirect:/candidatos/revision";
    }

    /** Rechaza al candidato borrándolo de la base de datos. */
    @GetMapping("/rechazar/{idAp}")
    public String rechazarCandidato(@PathVariable String idAp) {
        candidatoDao.deleteCandidato(idAp);
        return "redirect:/candidatos/revision";
    }
}