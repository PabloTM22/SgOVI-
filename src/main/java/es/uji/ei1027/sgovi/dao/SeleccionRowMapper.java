package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Seleccion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SeleccionRowMapper implements RowMapper<Seleccion> {

    @Override
    public Seleccion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(rs.getInt("id_seleccion"));
        seleccion.setIdSolicitud(rs.getInt("id_solicitud"));
        seleccion.setIdAp(rs.getString("id_ap"));
        seleccion.setEstado(rs.getString("estado"));
        seleccion.setFechaPropuesta(rs.getObject("fecha_propuesta", LocalDateTime.class));
        return seleccion;
    }
}
