package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.CandidatoDao;
import es.uji.ei1027.sgovi.model.Candidato;
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

    // Método que llamaremos cuando alguien rellene el formulario web
    public void registrarCandidato(Candidato candidato) {

        // 1. Generamos un ID único para el candidato (ej: AP-a1b2c3d4)
        String idAp = "AP-" + UUID.randomUUID().toString().substring(0, 8);
        candidato.setIdAp(idAp);

        // 2. Lógica de negocio: Un candidato nuevo NO está aceptado por defecto
        candidato.setEstadoAceptado(false);

        // 3. Lógica de negocio: El perfil nace activo en el sistema
        candidato.setActivo(true);

        // 4. Por último, lo guardamos en la base de datos usando el DAO que hiciste antes
        candidatoDao.addCandidato(candidato);
    }
}
