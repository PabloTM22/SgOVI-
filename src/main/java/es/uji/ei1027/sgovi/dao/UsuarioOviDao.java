package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UsuarioOvi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioOviDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addUsuario(UsuarioOvi usuario) {
        jdbcTemplate.update(
                "INSERT INTO UsuarioOVI (id_usuario, dni, nombre, apellidos, email, telefono, consentimiento_lopd, estado) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                usuario.getIdUsuario(), usuario.getDni(), usuario.getNombre(), usuario.getApellidos(),
                usuario.getEmail(), usuario.getTelefono(), usuario.isConsentimientoLopd(), usuario.getEstado()
        );
    }

    public UsuarioOvi getUsuario(String idUsuario) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM UsuarioOVI WHERE id_usuario = ?",
                    new UsuarioOviRowMapper(),
                    idUsuario
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UsuarioOvi getUsuarioByDni(String dni) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM UsuarioOVI WHERE dni = ?",
                    new UsuarioOviRowMapper(),
                    dni
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<UsuarioOvi> getUsuarios() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM UsuarioOVI ORDER BY apellidos, nombre",
                    new UsuarioOviRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void updateUsuario(UsuarioOvi usuario) {
        jdbcTemplate.update(
                "UPDATE UsuarioOVI SET dni=?, nombre=?, apellidos=?, email=?, telefono=?, consentimiento_lopd=?, estado=? " +
                        "WHERE id_usuario=?",
                usuario.getDni(), usuario.getNombre(), usuario.getApellidos(),
                usuario.getEmail(), usuario.getTelefono(), usuario.isConsentimientoLopd(), usuario.getEstado(),
                usuario.getIdUsuario()
        );
    }

    public void deleteUsuario(String idUsuario) {
        jdbcTemplate.update(
                "DELETE FROM UsuarioOVI WHERE id_usuario = ?",
                idUsuario
        );
    }

    public List<UsuarioOvi> findByEstado(String estado) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM UsuarioOVI WHERE estado = ? ORDER BY apellidos, nombre",
                    new UsuarioOviRowMapper(),
                    estado
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void updateEstado(String idUsuario, String estado) {
        jdbcTemplate.update(
                "UPDATE UsuarioOVI SET estado = ? WHERE id_usuario = ?",
                estado,
                idUsuario
        );
    }
}