package es.uji.ei1027.sgovi.validator;

import es.uji.ei1027.sgovi.model.Candidato;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class CandidatoValidator implements Validator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Override
    public boolean supports(Class<?> clazz) {
        return Candidato.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Candidato candidato = (Candidato) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "dni", "required.candidato.dni", "El DNI/NIE es obligatorio.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "nombre", "required.candidato.nombre", "El nombre es obligatorio.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "apellidos", "required.candidato.apellidos", "Los apellidos son obligatorios.");

        if (candidato.getEmail() == null || candidato.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "required.candidato.email",
                    "El correo electrónico es obligatorio.");
        } else if (!candidato.getEmail().matches(EMAIL_REGEX)) {
            errors.rejectValue("email", "invalid.candidato.email",
                    "El formato del correo electrónico no es válido.");
        }
    }
}