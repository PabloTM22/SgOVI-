package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UsuarioOvi;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioOviRowMapper implements RowMapper<UsuarioOvi> {

    @Override
    public UsuarioOvi mapRow(ResultSet rs, int rowNum) throws SQLException {
        UsuarioOvi usuario = new UsuarioOvi();

        usuario.setIdUsuario(rs.getString("id_usuario"));
        usuario.setDni(rs.getString("dni"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setConsentimientoLopd(rs.getBoolean("consentimiento_lopd"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setAceptadoTecnico(rs.getBoolean("aceptado_tecnico"));

        return usuario;
    }
}
