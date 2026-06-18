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

    private static final int PAGE_LENGTH = 10;

    @GetMapping
    public String listar(@RequestParam(value = "buscar", required = false) String buscar,
                         @RequestParam(value = "orden", required = false, defaultValue = "apellidos") String orden,
                         @RequestParam(value = "page") java.util.Optional<Integer> page,
                         Model model) {

        java.util.List<Formador> formadores = formadorDao.getFormadores();

        // Búsqueda de texto libre (nombre, apellidos, dni, email)
        if (buscar != null && !buscar.isBlank()) {
            String q = buscar.toLowerCase();
            formadores = formadores.stream()
                    .filter(f -> (f.getNombre() + " " + f.getApellidos()).toLowerCase().contains(q)
                            || (f.getDni() != null && f.getDni().toLowerCase().contains(q))
                            || (f.getEmail() != null && f.getEmail().toLowerCase().contains(q)))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Ordenación
        java.util.Comparator<Formador> comparador;
        switch (orden) {
            case "nombre":
                comparador = java.util.Comparator.comparing(Formador::getNombre, String.CASE_INSENSITIVE_ORDER);
                break;
            case "email":
                comparador = java.util.Comparator.comparing(Formador::getEmail, String.CASE_INSENSITIVE_ORDER);
                break;
            default:
                comparador = java.util.Comparator.comparing(Formador::getApellidos, String.CASE_INSENSITIVE_ORDER);
        }
        formadores.sort(comparador);

        // Paginación (troceado en memoria, patrón del boletín)
        java.util.List<java.util.List<Formador>> paginas = new java.util.ArrayList<>();
        for (int i = 0; i < formadores.size(); i += PAGE_LENGTH) {
            paginas.add(formadores.subList(i, Math.min(i + PAGE_LENGTH, formadores.size())));
        }
        int totalPaginas = paginas.size();
        int paginaActual = page.orElse(1);
        if (paginaActual < 1) paginaActual = 1;
        if (paginaActual > totalPaginas) paginaActual = totalPaginas;

        java.util.List<Formador> pagina = totalPaginas == 0
                ? new java.util.ArrayList<>()
                : paginas.get(paginaActual - 1);

        java.util.List<Integer> numerosPagina = java.util.stream.IntStream.rangeClosed(1, totalPaginas)
                .boxed().collect(java.util.stream.Collectors.toList());

        model.addAttribute("formadores", pagina);
        model.addAttribute("numerosPagina", numerosPagina);
        model.addAttribute("paginaActual", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalRegistros", formadores.size());
        model.addAttribute("buscar", buscar);
        model.addAttribute("filtroOrden", orden);
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