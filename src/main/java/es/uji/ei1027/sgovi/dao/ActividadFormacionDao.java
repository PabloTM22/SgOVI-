package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ActividadFormacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActividadFormacionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addActividad(ActividadFormacion actividad) {
        Integer nextId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id_actividad), 0) + 1 FROM actividadformacion",
                Integer.class
        );
        jdbcTemplate.update(
                "INSERT INTO actividadformacion (id_actividad, id_formador, tipo_actividad, titulo, descripcion, fecha_actividad, lugar, plazas) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                nextId, actividad.getIdFormador(), actividad.getTipoActividad(),
                actividad.getTitulo(), actividad.getDescripcion(), actividad.getFechaActividad(),
                actividad.getLugar(), actividad.getPlazas()
        );
    }

    public ActividadFormacion getActividad(int idActividad) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM actividadformacion WHERE id_actividad = ?",
                    new ActividadFormacionRowMapper(), idActividad
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ActividadFormacion> getActividades() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM actividadformacion ORDER BY fecha_actividad DESC",
                    new ActividadFormacionRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<ActividadFormacion> findByFormador(int idFormador) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM actividadformacion WHERE id_formador = ? ORDER BY fecha_actividad DESC",
                    new ActividadFormacionRowMapper(), idFormador
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void updateActividad(ActividadFormacion actividad) {
        jdbcTemplate.update(
                "UPDATE actividadformacion SET id_formador = ?, tipo_actividad = ?, titulo = ?, descripcion = ?, " +
                        "fecha_actividad = ?, lugar = ?, plazas = ? WHERE id_actividad = ?",
                actividad.getIdFormador(), actividad.getTipoActividad(), actividad.getTitulo(),
                actividad.getDescripcion(), actividad.getFechaActividad(),
                actividad.getLugar(), actividad.getPlazas(), actividad.getIdActividad()
        );
    }

    public void deleteActividad(int idActividad) {
        jdbcTemplate.update("DELETE FROM actividadformacion WHERE id_actividad = ?", idActividad);
    }
}

