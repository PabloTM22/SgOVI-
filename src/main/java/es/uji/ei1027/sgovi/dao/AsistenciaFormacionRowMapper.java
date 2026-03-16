package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.AsistenciaFormacion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AsistenciaFormacionRowMapper implements RowMapper<AsistenciaFormacion> {

    @Override
    public AsistenciaFormacion mapRow(ResultSet rs, int rowNum) throws SQLException {
        AsistenciaFormacion asistencia = new AsistenciaFormacion();
        asistencia.setIdAsistencia(rs.getInt("id_asistencia"));
        asistencia.setIdActividad(rs.getInt("id_actividad"));
        asistencia.setIdUsuario(rs.getString("id_usuario"));        // nullable
        asistencia.setIdAp(rs.getString("id_ap"));                  // nullable
        asistencia.setFechaInscripcion(rs.getObject("fecha_inscripcion", LocalDateTime.class));
        asistencia.setAsistio(rs.getBoolean("asistio"));
        asistencia.setCertificadoRuta(rs.getString("certificado_ruta")); // nullable
        return asistencia;
    }
}
