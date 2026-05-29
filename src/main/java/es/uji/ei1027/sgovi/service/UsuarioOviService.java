package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.UsuarioDao;
import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.Usuario;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioOviService {

    private final UsuarioOviDao usuarioOviDao;
    private final UsuarioDao usuarioDao;

    @Autowired
    public UsuarioOviService(UsuarioOviDao usuarioOviDao, UsuarioDao usuarioDao) {
        this.usuarioOviDao = usuarioOviDao;
        this.usuarioDao = usuarioDao;
    }

    public void registrarUsuario(UsuarioOvi usuarioOvi) {
        String idUsuario = "usr_" + UUID.randomUUID().toString().substring(0, 8);
        usuarioOvi.setIdUsuario(idUsuario);
        usuarioOvi.setAceptadoTecnico(false);

        BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
        String hash = encryptor.encryptPassword(usuarioOvi.getContrasena());

        Usuario credenciales = new Usuario();
        credenciales.setUsername(idUsuario);
        credenciales.setPassword(hash);
        credenciales.setRol("USUARIO_OVI");
        credenciales.setActivo(false);
        usuarioDao.addUsuario(credenciales);

        usuarioOvi.setContrasena(hash);
        usuarioOviDao.addUsuario(usuarioOvi);
    }

    public void actualizarUsuario(UsuarioOvi usuarioOvi) {
        UsuarioOvi actual = usuarioOviDao.getUsuario(usuarioOvi.getIdUsuario());
        if (usuarioOvi.getContrasena() == null || usuarioOvi.getContrasena().isBlank()) {
            usuarioOvi.setContrasena(actual.getContrasena());
        } else {
            BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
            String hash = encryptor.encryptPassword(usuarioOvi.getContrasena());
            usuarioOvi.setContrasena(hash);
            usuarioDao.updatePassword(usuarioOvi.getIdUsuario(), hash);
        }
        usuarioOviDao.updateUsuario(usuarioOvi);
    }
}
