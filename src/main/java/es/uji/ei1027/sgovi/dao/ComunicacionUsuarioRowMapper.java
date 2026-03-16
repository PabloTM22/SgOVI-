package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ComunicacionUsuario;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ComunicacionUsuarioRowMapper implements RowMapper<ComunicacionUsuario> {

    @Override
    public ComunicacionUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        ComunicacionUsuario com = new ComunicacionUsuario();
        com.setIdComunicacion(rs.getInt("id_comunicacion"));
        com.setIdSeleccion(rs.getInt("id_seleccion"));
        com.setIdUsuario(rs.getString("id_usuario"));   // nullable
        com.setIdAp(rs.getString("id_ap"));             // nullable
        com.setIdTecnico(rs.getString("id_tecnico"));   // nullable
        com.setMensaje(rs.getString("mensaje"));
        com.setFechaEnvio(rs.getObject("fecha_envio", LocalDateTime.class));
        return com;
    }
}
