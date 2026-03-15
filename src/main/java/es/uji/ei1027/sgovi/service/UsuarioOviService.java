package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.UsuarioOviDao;
import es.uji.ei1027.sgovi.model.UsuarioOvi;
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

    // Método que se llama cuando un usuario OVI se registra
    public void registrarUsuario(UsuarioOvi usuario) {

        // 1. Generamos un ID único para el usuario (ej: usr_a1b2c3d4)
        String idUsuario = "usr_" + UUID.randomUUID().toString().substring(0, 8);
        usuario.setIdUsuario(idUsuario);

        // 2. Lógica de negocio: un usuario nuevo no está aceptado por defecto
        usuario.setAceptadoTecnico(false);

        // 3. Guardamos en la base de datos usando el DAO
        usuarioOviDao.addUsuario(usuario);
    }
}
