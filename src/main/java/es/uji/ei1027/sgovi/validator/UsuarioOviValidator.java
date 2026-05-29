package es.uji.ei1027.sgovi.validator;

import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UsuarioOviValidator implements Validator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private UsuarioOviDao usuarioOviDao;

    @Autowired
    public void setUsuarioOviDao(UsuarioOviDao usuarioOviDao) {
        this.usuarioOviDao = usuarioOviDao;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UsuarioOvi.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UsuarioOvi usuario = (UsuarioOvi) target;

        if (usuario.getDni() == null || usuario.getDni().trim().isEmpty()) {
            errors.rejectValue("dni", "required.usuario.dni", "El DNI/NIE es obligatorio.");
        } else {
            UsuarioOvi existente = usuarioOviDao.getUsuarioByDni(usuario.getDni());
            if (existente != null && !existente.getIdUsuario().equals(usuario.getIdUsuario())) {
                errors.rejectValue("dni", "duplicate.usuario.dni",
                        "Ya existe un usuario con este DNI/NIE.");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "nombre", "required.usuario.nombre", "El nombre es obligatorio.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "apellidos", "required.usuario.apellidos", "Los apellidos son obligatorios.");

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "required.usuario.email",
                    "El correo electrónico es obligatorio.");
        } else if (!usuario.getEmail().matches(EMAIL_REGEX)) {
            errors.rejectValue("email", "invalid.usuario.email",
                    "El formato del correo electrónico no es válido.");
        }


    }
}