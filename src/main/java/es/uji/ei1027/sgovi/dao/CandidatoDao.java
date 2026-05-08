package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Candidato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository // Importante para que Spring sepa que esto maneja datos
public class CandidatoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // C - CREATE (Añadir un candidato)
    public void addCandidato(Candidato candidato) {
        jdbcTemplate.update(
                "INSERT INTO AsistentePersonal (id_ap, dni, nombre, apellidos, email, telefono, tipo_ap, formacion, experiencia, disponibilidad, latitud, longitud, estado_aceptado, contrasena, activo) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                candidato.getIdAp(), candidato.getDni(), candidato.getNombre(), candidato.getApellidos(),
                candidato.getEmail(), candidato.getTelefono(), candidato.getTipoAp(), candidato.getFormacion(),
                candidato.getExperiencia(), candidato.getDisponibilidad(), candidato.getLatitud(),
                candidato.getLongitud(), candidato.isEstadoAceptado(), candidato.getContrasena(), candidato.isActivo()
        );
    }

    // R - READ (Obtener un candidato por su ID)
    public Candidato getCandidato(String idAp) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM AsistentePersonal WHERE id_ap = ?",
                    new CandidatoRowMapper(),
                    idAp
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // R - READ ALL (Obtener todos los candidatos)
    public List<Candidato> getCandidatos() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM AsistentePersonal",
                    new CandidatoRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // U - UPDATE (Actualizar todos los datos de un candidato)
    public void updateCandidato(Candidato candidato) {
        jdbcTemplate.update(
                "UPDATE AsistentePersonal SET dni=?, nombre=?, apellidos=?, email=?, telefono=?, tipo_ap=?, formacion=?, experiencia=?, disponibilidad=?, latitud=?, longitud=?, estado_aceptado=?, contrasena=?, activo=? " +
                        "WHERE id_ap=?",
                candidato.getDni(), candidato.getNombre(), candidato.getApellidos(),
                candidato.getEmail(), candidato.getTelefono(), candidato.getTipoAp(), candidato.getFormacion(),
                candidato.getExperiencia(), candidato.getDisponibilidad(), candidato.getLatitud(),
                candidato.getLongitud(), candidato.isEstadoAceptado(), candidato.getContrasena(), candidato.isActivo(),
                candidato.getIdAp() // El ID va al final para el WHERE
        );
    }

    // D - DELETE (Borrar un candidato)
    public void deleteCandidato(String idAp) {
        jdbcTemplate.update(
                "DELETE FROM AsistentePersonal WHERE id_ap = ?",
                idAp
        );
    }



    // updateStatus: Actualiza SOLO si ha sido aceptado por el técnico
    public void updateStatus(String idAp, boolean estadoAceptado) {
        jdbcTemplate.update(
                "UPDATE AsistentePersonal SET estado_aceptado = ? WHERE id_ap = ?",
                estadoAceptado,
                idAp
        );
    }

    // findByStatus: Busca candidatos filtrando por si están aceptados o no
    public List<Candidato> findByStatus(boolean estadoAceptado) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM AsistentePersonal WHERE estado_aceptado = ?",
                    new CandidatoRowMapper(),
                    estadoAceptado
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // Devuelve candidatos aceptados del tipo indicado, ordenados por proximidad
    // al punto de referencia (latRef, lngRef) usando distancia euclidiana
    public List<Candidato> findAceptadosPorTipo(String tipoAp, double latRef, double lngRef) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM AsistentePersonal " +
                            "WHERE estado_aceptado = TRUE AND tipo_ap = ? AND latitud IS NOT NULL AND longitud IS NOT NULL " +
                            "ORDER BY (latitud - ?)^2 + (longitud - ?)^2",
                    new CandidatoRowMapper(),
                    tipoAp, latRef, lngRef
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}