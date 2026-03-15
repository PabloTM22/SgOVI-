package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Candidato;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CandidatoRowMapper implements RowMapper<Candidato> {

    @Override
    public Candidato mapRow(ResultSet rs, int rowNum) throws SQLException {
        Candidato candidato = new Candidato();

        candidato.setIdAp(rs.getString("id_ap"));
        candidato.setDni(rs.getString("dni"));
        candidato.setNombre(rs.getString("nombre"));
        candidato.setApellidos(rs.getString("apellidos"));
        candidato.setEmail(rs.getString("email"));
        candidato.setTelefono(rs.getString("telefono"));
        candidato.setTipoAp(rs.getString("tipo_ap"));
        candidato.setFormacion(rs.getString("formacion"));
        candidato.setExperiencia(rs.getString("experiencia"));
        candidato.setDisponibilidad(rs.getString("disponibilidad"));

        // Usamos getObject para que si en la BD es NULL, en Java sea null y no 0.0
        candidato.setLatitud(rs.getObject("latitud", Double.class));
        candidato.setLongitud(rs.getObject("longitud", Double.class));

        candidato.setEstadoAceptado(rs.getBoolean("estado_aceptado"));
        candidato.setContrasena(rs.getString("contrasena"));
        candidato.setActivo(rs.getBoolean("activo"));

        return candidato;
    }
}