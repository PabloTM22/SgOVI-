package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.service.CandidatoService;
import es.uji.ei1027.sgovi.validator.CandidatoValidator;
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

    @InitBinder("candidato")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(candidatoValidator);
    }

    @GetMapping("/registro")
    public String registrarCandidatoForm(Model model) {
        model.addAttribute("candidato", new Candidato());
        return "candidato/registro";
    }

    @PostMapping("/registro")
    public String registrarCandidatoSubmit(@ModelAttribute("candidato") @Valid Candidato candidato,
                                           BindingResult bindingResult,
                                           @RequestParam("contrasena") String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            bindingResult.rejectValue("dni", "required.contrasena",
                    "La contraseña es obligatoria.");
        }
        if (bindingResult.hasErrors()) {
            return "candidato/registro";
        }
        candidatoService.registrarCandidato(candidato, contrasena);
        return "redirect:/";
    }

    @GetMapping("/revision")
    public String listarPendientes(Model model) {
        model.addAttribute("candidatos", candidatoDao.findByEstado("pendiente"));
        return "candidato/revision";
    }

    @PostMapping("/aceptar/{idAp}")
    public String aceptarCandidato(@PathVariable String idAp,
                                   RedirectAttributes redirectAttributes) {
        candidatoService.aceptarCandidato(idAp);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Candidato aceptado correctamente.");
        return "redirect:/candidatos/revision";
    }


    @PostMapping("/rechazar/{idAp}")
    public String rechazarCandidato(@PathVariable String idAp,
                                    RedirectAttributes redirectAttributes) {
        candidatoService.rechazarCandidato(idAp);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Candidato rechazado.");
        return "redirect:/candidatos/revision";
    }

    @GetMapping("/detalle/{idAp}")
    public String detalle(@PathVariable String idAp, Model model) {
        Candidato candidato = candidatoDao.getCandidato(idAp);
        if (candidato == null) {
            return "redirect:/candidatos/revision";
        }
        model.addAttribute("candidato", candidato);
        return "candidato/detalle";
    }
}