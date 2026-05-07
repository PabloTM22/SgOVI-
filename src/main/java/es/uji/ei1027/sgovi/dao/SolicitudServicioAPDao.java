package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SolicitudServicioAPDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addSolicitud(SolicitudServicioAP solicitud) {
        jdbcTemplate.update(
                "INSERT INTO solicitud_servicio_ap (id_usuario, tipo_asistencia, estado, preferencias) " +
                        "VALUES (?, ?, ?, ?)",
                solicitud.getIdUsuario(),
                solicitud.getTipoAsistencia(),
                solicitud.getEstado(),
                solicitud.getPreferencias()
        );
    }

    // Obtiene una solicitud por su PK. Devuelve null si no existe
    public SolicitudServicioAP getSolicitud(int idSolicitud) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM solicitud_servicio_ap WHERE id_solicitud = ?",
                    new SolicitudServicioAPRowMapper(),
                    idSolicitud
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Obtiene todas las solicitudes
    public List<SolicitudServicioAP> getSolicitudes() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM solicitud_servicio_ap ORDER BY fecha_solicitud DESC",
                    new SolicitudServicioAPRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // Actualiza el estado y las preferencias de una solicitud
    public void updateSolicitud(SolicitudServicioAP solicitud) {
        jdbcTemplate.update(
                "UPDATE solicitud_servicio_ap SET estado = ?, preferencias = ? " +
                        "WHERE id_solicitud = ?",
                solicitud.getEstado(),
                solicitud.getPreferencias(),
                solicitud.getIdSolicitud()
        );
    }

    // Elimina una solicitud por su PK
    public void deleteSolicitud(int idSolicitud) {
        jdbcTemplate.update(
                "DELETE FROM solicitud_servicio_ap WHERE id_solicitud = ?",
                idSolicitud
        );
    }

    // Obtiene todas las solicitudes de un usuario concreto
    public List<SolicitudServicioAP> findByUsuario(String idUsuario) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM solicitud_servicio_ap WHERE id_usuario = ? " +
                            "ORDER BY fecha_solicitud DESC",
                    new SolicitudServicioAPRowMapper(),
                    idUsuario
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // Obtiene todas las solicitudes con un estado concreto
    public List<SolicitudServicioAP> findByEstado(String estado) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM solicitud_servicio_ap WHERE estado = ? " +
                            "ORDER BY fecha_solicitud DESC",
                    new SolicitudServicioAPRowMapper(),
                    estado
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // Cambia el estado de una solicitud
    public void updateEstado(int idSolicitud, String nuevoEstado) {
        jdbcTemplate.update(
                "UPDATE solicitud_servicio_ap SET estado = ? WHERE id_solicitud = ?",
                nuevoEstado,
                idSolicitud
        );
    }
}