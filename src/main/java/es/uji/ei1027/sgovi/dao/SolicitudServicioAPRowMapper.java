package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.SolicitudServicioAP;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SolicitudServicioAPRowMapper implements RowMapper<SolicitudServicioAP>{
    @Override
    public SolicitudServicioAP mapRow(ResultSet rs, int rowNum) throws SQLException {
        SolicitudServicioAP solicitud = new SolicitudServicioAP();
        solicitud.setIdSolicitud(rs.getInt("id_solicitud"));
        solicitud.setIdUsuario(rs.getString("id_usuario"));
        solicitud.setTipoAsistencia(rs.getString("tipo_asistencia"));
        solicitud.setEstado(rs.getString("estado"));

        // timestamp without time zone → LocalDateTime
        solicitud.setFechaSolicitud(
                rs.getObject("fecha_solicitud", LocalDateTime.class)
        );

        solicitud.setPreferencias(rs.getString("preferencias")); // nullable, ok
        return solicitud;
    }
}
