package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.service.CandidatoService;
import es.uji.ei1027.sgovi.validator.CandidatoValidator;
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

    private boolean esTecnico(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        return user != null && "TECNICO".equals(user.getRol());
    }

    @GetMapping("/registro")
    public String registrarCandidatoForm(Model model) {
        model.addAttribute("candidato", new Candidato());
        return "candidato/registro";
    }

    @PostMapping("/registro")
    public String registrarCandidatoSubmit(@ModelAttribute("candidato") Candidato candidato,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "candidato/registro";
        }
        candidatoService.registrarCandidato(candidato);
        return "redirect:/";
    }

    @GetMapping("/revision")
    public String listarPendientes(HttpSession session, Model model) {
        if (!esTecnico(session)) {
            session.setAttribute("nextUrl", "/candidatos/revision");
            return "redirect:/login";
        }
        model.addAttribute("candidatos", candidatoDao.findByStatus(false));
        return "candidato/revision";
    }

    @PostMapping("/aceptar/{idAp}")
    public String aceptarCandidato(@PathVariable String idAp, HttpSession session) {
        if (!esTecnico(session)) return "redirect:/login";
        candidatoDao.updateStatus(idAp, true);
        return "redirect:/candidatos/revision";
    }

    @PostMapping("/rechazar/{idAp}")
    public String rechazarCandidato(@PathVariable String idAp,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!esTecnico(session)) return "redirect:/login";
        try {
            candidatoDao.deleteCandidato(idAp);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorBorrar",
                    "No se puede eliminar el candidato porque tiene selecciones asociadas.");
        }
        return "redirect:/candidatos/revision";
    }
}