package es.uji.ei1027.sgovi.validator;

import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class SolicitudValidator implements Validator {

    private static final List<String> TIPOS_VALIDOS = Arrays.asList("PAP", "PATI");

    @Override
    public boolean supports(Class<?> clazz) {
        return SolicitudServicioAP.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SolicitudServicioAP solicitud = (SolicitudServicioAP) target;

        // Tipo de asistencia: obligatorio y debe ser PAP o PATI
        if (solicitud.getTipoAsistencia() == null || solicitud.getTipoAsistencia().trim().isEmpty()) {
            errors.rejectValue("tipoAsistencia", "required.solicitud.tipoAsistencia",
                    "Debe seleccionar un tipo de asistencia.");
        } else if (!TIPOS_VALIDOS.contains(solicitud.getTipoAsistencia())) {
            errors.rejectValue("tipoAsistencia", "invalid.solicitud.tipoAsistencia",
                    "El tipo de asistencia debe ser PAP o PATI.");
        }
    }
}
