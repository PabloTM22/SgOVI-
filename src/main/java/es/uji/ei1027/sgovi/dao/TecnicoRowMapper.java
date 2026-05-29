package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Tecnico;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TecnicoRowMapper implements RowMapper<Tecnico> {
    @Override
    public Tecnico mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tecnico tecnico = new Tecnico();
        tecnico.setIdTecnico(rs.getString("id_tecnico"));
        tecnico.setDni(rs.getString("dni"));
        tecnico.setNombre(rs.getString("nombre"));
        tecnico.setApellidos(rs.getString("apellidos"));
        tecnico.setEmail(rs.getString("email"));
        tecnico.setTelefono(rs.getString("telefono"));
        tecnico.setActivo(rs.getBoolean("activo"));
        return tecnico;
    }
}
