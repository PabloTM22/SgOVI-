package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.dao.UsuarioDao;
import es.uji.ei1027.sgovi.model.Formador;
import es.uji.ei1027.sgovi.model.Usuario;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FormadorService {

    private final FormadorDao formadorDao;
    private final UsuarioDao usuarioDao;

    @Autowired
    public FormadorService(FormadorDao formadorDao, UsuarioDao usuarioDao) {
        this.formadorDao = formadorDao;
        this.usuarioDao = usuarioDao;
    }

    public void registrarFormador(Formador formador, String contrasenaClaro) {
        String idFormador = "frm_" + UUID.randomUUID().toString().substring(0, 8);
        formador.setIdFormador(idFormador);

        BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
        String hash = encryptor.encryptPassword(contrasenaClaro);

        Usuario credenciales = new Usuario();
        credenciales.setUsername(idFormador);
        credenciales.setPassword(hash);
        credenciales.setRol("FORMADOR");
        credenciales.setActivo(true);
        usuarioDao.addUsuario(credenciales);

        formadorDao.addFormador(formador);
    }

    public void actualizarFormador(Formador formador, String nuevaContrasena) {
        if (nuevaContrasena != null && !nuevaContrasena.isBlank()) {
            BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
            String hash = encryptor.encryptPassword(nuevaContrasena);
            usuarioDao.updatePassword(formador.getIdFormador(), hash);
        }
        formadorDao.updateFormador(formador);
    }
}