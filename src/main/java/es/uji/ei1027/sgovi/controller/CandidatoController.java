package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.service.CandidatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/candidatos")
public class CandidatoController {

    private final CandidatoService candidatoService;
    private final CandidatoDao candidatoDao;

    @Autowired
    public CandidatoController(CandidatoService candidatoService, CandidatoDao candidatoDao) {
        this.candidatoService = candidatoService;
        this.candidatoDao = candidatoDao;
    }


    @GetMapping("/registro")
    public String registrarCandidatoForm(Model model) {
        model.addAttribute("candidato", new Candidato());
        return "candidato/registro";
    }

    @PostMapping("/registro")
    public String registrarCandidatoSubmit(@ModelAttribute Candidato candidato) {
        candidatoService.registrarCandidato(candidato);
        return "redirect:/";
    }


    // Listado de candidatos con estado_aceptado = false
    @GetMapping("/revision")
    public String listarPendientes(Model model) {
        model.addAttribute("candidatos", candidatoDao.findByStatus(false));
        return "candidato/revision";
    }

    // Acción para aceptar al candidato (cambia estado a true)
    @GetMapping("/aceptar/{idAp}")
    public String aceptarCandidato(@PathVariable String idAp) {
        candidatoDao.updateStatus(idAp, true);
        return "redirect:/candidatos/revision";
    }

    // Acción para rechazar al candidato (lo borra de la BD)
    @GetMapping("/rechazar/{idAp}")
    public String rechazarCandidato(@PathVariable String idAp) {
        candidatoDao.deleteCandidato(idAp);
        return "redirect:/candidatos/revision";
    }
}