package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Seleccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SeleccionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addSeleccion(Seleccion seleccion) {
        Integer nextId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id_seleccion), 0) + 1 FROM seleccion",
                Integer.class
        );
        jdbcTemplate.update(
                "INSERT INTO seleccion (id_seleccion, id_solicitud, id_ap, estado) VALUES (?, ?, ?, ?)",
                nextId, seleccion.getIdSolicitud(), seleccion.getIdAp(), seleccion.getEstado()
        );
    }

    public Seleccion getSeleccion(int idSeleccion) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM seleccion WHERE id_seleccion = ?",
                    new SeleccionRowMapper(), idSeleccion
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Seleccion> getSelecciones() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM seleccion ORDER BY fecha_propuesta DESC",
                    new SeleccionRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Seleccion> findBySolicitud(int idSolicitud) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM seleccion WHERE id_solicitud = ?",
                    new SeleccionRowMapper(), idSolicitud
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void updateEstado(int idSeleccion, String nuevoEstado) {
        jdbcTemplate.update(
                "UPDATE seleccion SET estado = ? WHERE id_seleccion = ?",
                nuevoEstado, idSeleccion
        );
    }

    public void updateSeleccion(Seleccion seleccion) {
        jdbcTemplate.update(
                "UPDATE seleccion SET id_solicitud = ?, id_ap = ?, estado = ? WHERE id_seleccion = ?",
                seleccion.getIdSolicitud(), seleccion.getIdAp(),
                seleccion.getEstado(), seleccion.getIdSeleccion()
        );
    }

    public void deleteSeleccion(int idSeleccion) {
        jdbcTemplate.update("DELETE FROM seleccion WHERE id_seleccion = ?", idSeleccion);
    }
}
