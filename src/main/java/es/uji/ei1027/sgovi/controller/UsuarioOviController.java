package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
import es.uji.ei1027.sgovi.service.UsuarioOviService;
import es.uji.ei1027.sgovi.validator.UsuarioOviValidator;
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
@RequestMapping("/usuarios")
public class UsuarioOviController {
    private final UsuarioOviService usuarioOviService;
    private final UsuarioOviDao usuarioOviDao;
    private final UsuarioOviValidator usuarioOviValidator;

    @Autowired
    public UsuarioOviController(UsuarioOviService usuarioOviService, UsuarioOviDao usuarioOviDao, UsuarioOviValidator usuarioOviValidator) {
        this.usuarioOviService = usuarioOviService;
        this.usuarioOviDao = usuarioOviDao;
        this.usuarioOviValidator = usuarioOviValidator;
    }

    @InitBinder("usuario")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(usuarioOviValidator);
    }


    private static final int PAGE_LENGTH = 10;

    @GetMapping
    public String listar(@RequestParam(value = "buscar", required = false) String buscar,
                         @RequestParam(value = "estado", required = false, defaultValue = "TODOS") String estado,
                         @RequestParam(value = "orden", required = false, defaultValue = "apellidos") String orden,
                         @RequestParam(value = "page") java.util.Optional<Integer> page,
                         Model model) {

        java.util.List<UsuarioOvi> usuarios = usuarioOviDao.getUsuarios();

        // Búsqueda de texto libre (nombre, apellidos, dni, email)
        if (buscar != null && !buscar.isBlank()) {
            String q = buscar.toLowerCase();
            usuarios = usuarios.stream()
                    .filter(u -> (u.getNombre() + " " + u.getApellidos()).toLowerCase().contains(q)
                            || (u.getDni() != null && u.getDni().toLowerCase().contains(q))
                            || (u.getEmail() != null && u.getEmail().toLowerCase().contains(q)))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filtro por estado
        if (estado != null && !"TODOS".equalsIgnoreCase(estado)) {
            usuarios = usuarios.stream()
                    .filter(u -> estado.equals(u.getEstado()))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Ordenación
        java.util.Comparator<UsuarioOvi> comparador;
        switch (orden) {
            case "nombre":
                comparador = java.util.Comparator.comparing(UsuarioOvi::getNombre, String.CASE_INSENSITIVE_ORDER);
                break;
            case "estado":
                comparador = java.util.Comparator.comparing(UsuarioOvi::getEstado, String.CASE_INSENSITIVE_ORDER);
                break;
            default:
                comparador = java.util.Comparator.comparing(UsuarioOvi::getApellidos, String.CASE_INSENSITIVE_ORDER);
        }
        usuarios.sort(comparador);

        // Paginación (patrón del boletín: troceado en memoria)
        java.util.List<java.util.List<UsuarioOvi>> paginas = new java.util.ArrayList<>();
        for (int i = 0; i < usuarios.size(); i += PAGE_LENGTH) {
            paginas.add(usuarios.subList(i, Math.min(i + PAGE_LENGTH, usuarios.size())));
        }
        int totalPaginas = paginas.size();
        int paginaActual = page.orElse(1);
        if (paginaActual < 1) paginaActual = 1;
        if (paginaActual > totalPaginas) paginaActual = totalPaginas;

        java.util.List<UsuarioOvi> pagina = totalPaginas == 0
                ? new java.util.ArrayList<>()
                : paginas.get(paginaActual - 1);

        java.util.List<Integer> numerosPagina = java.util.stream.IntStream.rangeClosed(1, totalPaginas)
                .boxed().collect(java.util.stream.Collectors.toList());

        model.addAttribute("usuarios", pagina);
        model.addAttribute("numerosPagina", numerosPagina);
        model.addAttribute("paginaActual", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalRegistros", usuarios.size());
        model.addAttribute("buscar", buscar);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroOrden", orden);
        return "usuario/lista";
    }

    @GetMapping("/alta")
    public String altaForm(Model model) {
        model.addAttribute("usuario", new UsuarioOvi());
        return "usuario/alta";
    }

    @PostMapping("/alta")
    public String altaSubmit(@ModelAttribute("usuario") @Valid UsuarioOvi usuario,
                             BindingResult bindingResult,
                             @RequestParam("contrasena") String contrasena,
                             Model model) {
        if (contrasena == null || contrasena.isBlank()) {
            bindingResult.rejectValue("dni", "required.contrasena",
                    "La contraseña es obligatoria.");
        }
        if (bindingResult.hasErrors()) {
            return "usuario/alta";
        }
        try {
            usuarioOviService.registrarUsuario(usuario, contrasena);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("message", "Ya existe un usuario con ese DNI/NIE.");
            return "usuario/alta";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{idUsuario}")
    public String editarForm(@PathVariable String idUsuario, Model model) {
        model.addAttribute("usuario", usuarioOviDao.getUsuario(idUsuario));
        return "usuario/editar";
    }

    @PostMapping("/editar/{idUsuario}")
    public String editarSubmit(@PathVariable String idUsuario,
                               @ModelAttribute("usuario") @Valid UsuarioOvi usuario,
                               BindingResult bindingResult,
                               @RequestParam("contrasena") String contrasena) {
        if (bindingResult.hasErrors()) {
            return "usuario/editar";
        }
        usuario.setIdUsuario(idUsuario);
        usuarioOviService.actualizarUsuario(usuario, contrasena);
        return "redirect:/usuarios";
    }

    @GetMapping("/revision")
    public String revision(Model model) {
        model.addAttribute("usuarios", usuarioOviDao.findByEstado("pendiente"));
        return "usuario/revision";
    }

    @GetMapping("/detalle/{idUsuario}")
    public String detalle(@PathVariable String idUsuario, Model model) {
        UsuarioOvi usuario = usuarioOviDao.getUsuario(idUsuario);
        if (usuario == null) {
            return "redirect:/usuarios/revision";
        }
        model.addAttribute("usuario", usuario);
        return "usuario/detalle";
    }

    @PostMapping("/aceptar/{idUsuario}")
    public String aceptar(@PathVariable String idUsuario,
                          RedirectAttributes redirectAttributes) {
        usuarioOviService.aceptarUsuario(idUsuario);
        redirectAttributes.addFlashAttribute("message",
                "Usuario aceptado correctamente.");
        return "redirect:/usuarios/revision";
    }

    @PostMapping("/rechazar/{idUsuario}")
    public String rechazar(@PathVariable String idUsuario,
                           RedirectAttributes redirectAttributes) {
        usuarioOviService.rechazarUsuario(idUsuario);
        redirectAttributes.addFlashAttribute("message",
                "Usuario rechazado.");
        return "redirect:/usuarios/revision";
    }

    @PostMapping("/borrar/{idUsuario}")
    public String borrar(@PathVariable String idUsuario,
                         RedirectAttributes redirectAttributes) {
        try {
            usuarioOviDao.deleteUsuario(idUsuario);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("message",
                    "No se puede eliminar el usuario porque tiene solicitudes asociadas.");
        }
        return "redirect:/usuarios";
    }
}