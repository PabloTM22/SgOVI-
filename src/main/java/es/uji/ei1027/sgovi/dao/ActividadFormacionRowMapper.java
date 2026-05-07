package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ActividadFormacion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ActividadFormacionRowMapper implements RowMapper<ActividadFormacion> {

    @Override
    public ActividadFormacion mapRow(ResultSet rs, int rowNum) throws SQLException {
        ActividadFormacion actividad = new ActividadFormacion();
        actividad.setIdActividad(rs.getInt("id_actividad"));
        actividad.setIdFormador(rs.getInt("id_formador"));
        actividad.setTipoActividad(rs.getString("tipo_actividad"));
        actividad.setTitulo(rs.getString("titulo"));
        actividad.setDescripcion(rs.getString("descripcion"));
        actividad.setFechaActividad(rs.getObject("fecha_actividad", LocalDateTime.class));
        actividad.setLugar(rs.getString("lugar"));
        actividad.setPlazas(rs.getObject("plazas", Integer.class));
        actividad.setNumParticipantes(rs.getObject("num_participantes", Integer.class));
        return actividad;
    }
}

