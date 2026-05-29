package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioOviService {

    private final UsuarioOviDao usuarioOviDao;

    @Autowired
    public UsuarioOviService(UsuarioOviDao usuarioOviDao) {
        this.usuarioOviDao = usuarioOviDao;
    }

    public void registrarUsuario(UsuarioOvi usuario) {
        String idUsuario = "usr_" + UUID.randomUUID().toString().substring(0, 8);
        usuario.setIdUsuario(idUsuario);
        usuario.setAceptadoTecnico(false);

        BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
        usuario.setContrasena(encryptor.encryptPassword(usuario.getContrasena()));

        usuarioOviDao.addUsuario(usuario);
    }

    public void actualizarUsuario(UsuarioOvi usuario) {
        UsuarioOvi actual = usuarioOviDao.getUsuario(usuario.getIdUsuario());
        if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
            usuario.setContrasena(actual.getContrasena());
        } else {
            BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
            usuario.setContrasena(encryptor.encryptPassword(usuario.getContrasena()));
        }
        usuarioOviDao.updateUsuario(usuario);
    }
}
