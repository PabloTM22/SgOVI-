// Ruta destino: src/main/java/es/uji/ei1027/sgovi/controller/AsistenteController.java

package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controlador para que las personas usuarias de la OVI exploren la
 * bolsa de asistentes personales aceptados, con filtros y vista en tarjetas.
 *
 * Pensado como alternativa a la vista tabular previa: oculta los identificadores
 * internos y presenta la información de forma más humana y filtrable.
 */
@Controller
@RequestMapping("/asistentes")
public class BusquedaAsistenteController {

    private final CandidatoDao candidatoDao;

    // Coordenadas de referencia para el orden por proximidad cuando todavía
    // no hay perfil con dirección del usuario. Centro de Castelló de la Plana.
    private static final double LAT_REF_DEFECTO = 39.9864;
    private static final double LNG_REF_DEFECTO = -0.0513;

    @Autowired
    public BusquedaAsistenteController(CandidatoDao candidatoDao) {
        this.candidatoDao = candidatoDao;
    }

    /**
     * Solo dejamos buscar a personas autenticadas que sean usuarias de la OVI
     * o personal técnico (que también puede necesitar consultar la bolsa).
     */
    private boolean puedeBuscar(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return false;
        String rol = user.getRol();
        return "USUARIO".equals(rol) || "TECNICO".equals(rol);
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false, defaultValue = "TODOS") String tipo,
                         @RequestParam(required = false) String texto,
                         @RequestParam(required = false) String disponibilidad,
                         @RequestParam(required = false, defaultValue = "RELEVANCIA") String orden,
                         HttpSession session,
                         Model model) {
        if (!puedeBuscar(session)) {
            session.setAttribute("nextUrl", "/asistentes/buscar");
            return "redirect:/login";
        }

        List<Candidato> resultados = candidatoDao.buscar(
                tipo, texto, disponibilidad, orden,
                LAT_REF_DEFECTO, LNG_REF_DEFECTO
        );

        // Devolvemos los valores de los filtros para que el formulario los recuerde
        // tras la recarga (Thymeleaf los repuebla con th:value y th:selected).
        model.addAttribute("resultados", resultados);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroTexto", texto);
        model.addAttribute("filtroDisponibilidad", disponibilidad);
        model.addAttribute("filtroOrden", orden);
        return "asistente/buscar";
    }

    /**
     * Página de detalle del asistente. Solo accesible para asistentes
     * aceptados y activos en la bolsa.
     */
    @GetMapping("/{idAp}")
    public String perfil(@PathVariable String idAp,
                         HttpSession session,
                         Model model) {
        if (!puedeBuscar(session)) {
            session.setAttribute("nextUrl", "/asistentes/" + idAp);
            return "redirect:/login";
        }

        Candidato a = candidatoDao.getCandidato(idAp);
        if (a == null || !a.isEstadoAceptado() || !a.isActivo()) {
            return "redirect:/asistentes/buscar";
        }

        model.addAttribute("asistente", a);
        return "asistente/perfil";
    }
}