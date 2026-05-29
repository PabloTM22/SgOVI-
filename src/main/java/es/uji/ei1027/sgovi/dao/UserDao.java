package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.model.Usuario;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    private final UsuarioDao usuarioDao;

    @Autowired
    public UserDao(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public UserDetails loadUserByUsername(String username, String password) {
        Usuario usuario = usuarioDao.getUsuario(username);
        if (usuario == null || !usuario.isActivo()) {
            return null;
        }

        BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
        if (!encryptor.checkPassword(password, usuario.getPassword())) {
            return null;
        }

        UserDetails user = new UserDetails();
        user.setUsername(usuario.getUsername());
        user.setRol(usuario.getRol());
        return user;
    }
}