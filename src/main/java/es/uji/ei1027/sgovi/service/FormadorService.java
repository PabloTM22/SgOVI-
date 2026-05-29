package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.model.Formador;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormadorService {

    private final FormadorDao formadorDao;

    @Autowired
    public FormadorService(FormadorDao formadorDao) {
        this.formadorDao = formadorDao;
    }

    public void actualizarFormador(Formador formador) {
        Formador actual = formadorDao.getFormador(formador.getIdFormador());
        if (formador.getContrasena() == null || formador.getContrasena().isBlank()) {
            formador.setContrasena(actual.getContrasena());
        } else {
            BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
            formador.setContrasena(encryptor.encryptPassword(formador.getContrasena()));
        }
        formadorDao.updateFormador(formador);
    }
}