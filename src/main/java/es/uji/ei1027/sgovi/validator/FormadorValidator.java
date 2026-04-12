package es.uji.ei1027.sgovi.validator;

import es.uji.ei1027.sgovi.model.Formador;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class FormadorValidator implements Validator {

    // Expresión regular básica para validar formato de email
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // DNI (8 dígitos + letra) o NIE (X/Y/Z + 7 dígitos + letra)
    private static final String DNI_NIE_REGEX = "^([0-9]{8}[A-Za-z]|[XYZxyz][0-9]{7}[A-Za-z])$";

    // Teléfono español: 9 dígitos, opcionalmente con prefijo +34
    private static final String TELEFONO_REGEX = "^(\\+34)?[6-9][0-9]{8}$";

    /**
     * Indica que este validador sólo soporta la clase Formador.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Formador.class.equals(clazz);
    }

    /**
     * Lógica de validación del formulario de alta de formador.
     * Se validan los campos obligatorios y los formatos de DNI/NIE, email y teléfono.
     */
    @Override
    public void validate(Object target, Errors errors) {
        Formador formador = (Formador) target;

        // DNI/NIE: obligatorio y con formato válido
        if (formador.getDni() == null || formador.getDni().trim().isEmpty()) {
            errors.rejectValue("dni", "required.formador.dni",
                    "El DNI/NIE es obligatorio.");
        } else if (!formador.getDni().matches(DNI_NIE_REGEX)) {
            errors.rejectValue("dni", "invalid.formador.dni",
                    "El formato del DNI/NIE no es válido.");
        }

        // Nombre: obligatorio
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "nombre", "required.formador.nombre", "El nombre es obligatorio.");

        // Apellidos: obligatorio
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "apellidos", "required.formador.apellidos", "Los apellidos son obligatorios.");

        // Email: obligatorio y con formato válido
        if (formador.getEmail() == null || formador.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "required.formador.email",
                    "El correo electrónico es obligatorio.");
        } else if (!formador.getEmail().matches(EMAIL_REGEX)) {
            errors.rejectValue("email", "invalid.formador.email",
                    "El formato del correo electrónico no es válido.");
        }

        // Teléfono: opcional, pero si se introduce debe tener formato válido
        if (formador.getTelefono() != null && !formador.getTelefono().trim().isEmpty()
                && !formador.getTelefono().matches(TELEFONO_REGEX)) {
            errors.rejectValue("telefono", "invalid.formador.telefono",
                    "El formato del teléfono no es válido (9 dígitos, opcional prefijo +34).");
        }
    }
}