package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.model.Candidato;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CandidatoService {

    private final CandidatoDao candidatoDao;

    @Autowired
    public CandidatoService(CandidatoDao candidatoDao) {
        this.candidatoDao = candidatoDao;
    }

    public void registrarCandidato(Candidato candidato) {
        String idAp = "AP-" + UUID.randomUUID().toString().substring(0, 8);
        candidato.setIdAp(idAp);
        candidato.setEstadoAceptado(false);
        candidato.setActivo(true);

        BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
        candidato.setContrasena(encryptor.encryptPassword(candidato.getContrasena()));

        candidatoDao.addCandidato(candidato);
    }
}
