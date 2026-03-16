package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.AsistenciaFormacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AsistenciaFormacionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAsistencia(AsistenciaFormacion asistencia) {
        Integer nextId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id_asistencia), 0) + 1 FROM asistenciaformacion",
                Integer.class
        );
        jdbcTemplate.update(
                "INSERT INTO asistenciaformacion (id_asistencia, id_actividad, id_usuario, id_ap, asistio, certificado_ruta) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                nextId, asistencia.getIdActividad(), asistencia.getIdUsuario(),
                asistencia.getIdAp(), asistencia.isAsistio(), asistencia.getCertificadoRuta()
        );
    }

    public AsistenciaFormacion getAsistencia(int idAsistencia) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM asistenciaformacion WHERE id_asistencia = ?",
                    new AsistenciaFormacionRowMapper(), idAsistencia
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AsistenciaFormacion> findByActividad(int idActividad) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM asistenciaformacion WHERE id_actividad = ?",
                    new AsistenciaFormacionRowMapper(), idActividad
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void updateAsistio(int idAsistencia, boolean asistio) {
        jdbcTemplate.update(
                "UPDATE asistenciaformacion SET asistio = ? WHERE id_asistencia = ?",
                asistio, idAsistencia
        );
    }

    public void updateAsistencia(AsistenciaFormacion asistencia) {
        jdbcTemplate.update(
                "UPDATE asistenciaformacion SET asistio = ?, certificado_ruta = ? WHERE id_asistencia = ?",
                asistencia.isAsistio(), asistencia.getCertificadoRuta(), asistencia.getIdAsistencia()
        );
    }

    public void deleteAsistencia(int idAsistencia) {
        jdbcTemplate.update("DELETE FROM asistenciaformacion WHERE id_asistencia = ?", idAsistencia);
    }
}
