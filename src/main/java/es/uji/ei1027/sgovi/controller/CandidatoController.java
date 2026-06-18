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

    private static final int PAGE_LENGTH = 10;

    @GetMapping("/revision")
    public String listarPendientes(@RequestParam(value = "buscar", required = false) String buscar,
                                   @RequestParam(value = "tipo", required = false, defaultValue = "TODOS") String tipo,
                                   @RequestParam(value = "orden", required = false, defaultValue = "apellidos") String orden,
                                   @RequestParam(value = "page") java.util.Optional<Integer> page,
                                   Model model) {

        java.util.List<Candidato> candidatos = candidatoDao.findByEstado("pendiente");

        // Búsqueda de texto libre (nombre, apellidos, dni)
        if (buscar != null && !buscar.isBlank()) {
            String q = buscar.toLowerCase();
            candidatos = candidatos.stream()
                    .filter(c -> (c.getNombre() + " " + c.getApellidos()).toLowerCase().contains(q)
                            || (c.getDni() != null && c.getDni().toLowerCase().contains(q)))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filtro por tipo (PAP / PATI)
        if (tipo != null && !"TODOS".equalsIgnoreCase(tipo)) {
            candidatos = candidatos.stream()
                    .filter(c -> tipo.equals(c.getTipoAp()))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Ordenación
        java.util.Comparator<Candidato> comparador;
        switch (orden) {
            case "nombre":
                comparador = java.util.Comparator.comparing(Candidato::getNombre, String.CASE_INSENSITIVE_ORDER);
                break;
            case "tipo":
                comparador = java.util.Comparator.comparing(Candidato::getTipoAp, String.CASE_INSENSITIVE_ORDER);
                break;
            default:
                comparador = java.util.Comparator.comparing(Candidato::getApellidos, String.CASE_INSENSITIVE_ORDER);
        }
        candidatos.sort(comparador);

        // Paginación (troceado en memoria, patrón del boletín)
        java.util.List<java.util.List<Candidato>> paginas = new java.util.ArrayList<>();
        for (int i = 0; i < candidatos.size(); i += PAGE_LENGTH) {
            paginas.add(candidatos.subList(i, Math.min(i + PAGE_LENGTH, candidatos.size())));
        }
        int totalPaginas = paginas.size();
        int paginaActual = page.orElse(1);
        if (paginaActual < 1) paginaActual = 1;
        if (paginaActual > totalPaginas) paginaActual = totalPaginas;

        java.util.List<Candidato> pagina = totalPaginas == 0
                ? new java.util.ArrayList<>()
                : paginas.get(paginaActual - 1);

        java.util.List<Integer> numerosPagina = java.util.stream.IntStream.rangeClosed(1, totalPaginas)
                .boxed().collect(java.util.stream.Collectors.toList());

        model.addAttribute("candidatos", pagina);
        model.addAttribute("numerosPagina", numerosPagina);
        model.addAttribute("paginaActual", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalRegistros", candidatos.size());
        model.addAttribute("buscar", buscar);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroOrden", orden);
        return "candidato/revision";
    }

    @PostMapping("/aceptar/{idAp}")
    public String aceptarCandidato(@PathVariable String idAp,
                                   RedirectAttributes redirectAttributes) {
        candidatoService.aceptarCandidato(idAp);
        redirectAttributes.addFlashAttribute("message",
                "Candidato aceptado correctamente.");
        return "redirect:/candidatos/revision";
    }

    @PostMapping("/rechazar/{idAp}")
    public String rechazarCandidato(@PathVariable String idAp,
                                    RedirectAttributes redirectAttributes) {
        candidatoService.rechazarCandidato(idAp);
        redirectAttributes.addFlashAttribute("message",
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