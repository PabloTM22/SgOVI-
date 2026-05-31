package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.dao.UsuarioDao;
import es.uji.ei1027.sgovi.model.Candidato;
import es.uji.ei1027.sgovi.model.Usuario;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CandidatoService {

    private final CandidatoDao candidatoDao;
    private final UsuarioDao usuarioDao;

    @Autowired
    public CandidatoService(CandidatoDao candidatoDao, UsuarioDao usuarioDao) {
        this.candidatoDao = candidatoDao;
        this.usuarioDao = usuarioDao;
    }

    public void registrarCandidato(Candidato candidato, String contrasenaClaro) {
        String idAp = "ap_" + UUID.randomUUID().toString().substring(0, 8);
        candidato.setIdAp(idAp);
        candidato.setEstado("pendiente");

        BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
        String hash = encryptor.encryptPassword(contrasenaClaro);

        Usuario credenciales = new Usuario();
        credenciales.setUsername(idAp);
        credenciales.setPassword(hash);
        credenciales.setRol("CANDIDATO");
        credenciales.setActivo(false);
        usuarioDao.addUsuario(credenciales);

        candidatoDao.addCandidato(candidato);
    }

    public void aceptarCandidato(String idAp) {
        candidatoDao.updateEstado(idAp, "aceptado");
        usuarioDao.updateActivo(idAp, true);
    }

    public void rechazarCandidato(String idAp) {
        candidatoDao.updateEstado(idAp, "rechazado");
        usuarioDao.updateActivo(idAp, false);
    }
}