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

    // C - CREATE (Añadir un usuario OVI)
    public void addUsuario(UsuarioOvi usuario) {
        jdbcTemplate.update(
                "INSERT INTO UsuarioOVI (id_usuario, dni, nombre, apellidos, email, telefono, consentimiento_lopd, aceptado_tecnico) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                usuario.getIdUsuario(), usuario.getDni(), usuario.getNombre(), usuario.getApellidos(),
                usuario.getEmail(), usuario.getTelefono(), usuario.isConsentimientoLopd(), usuario.isAceptadoTecnico()
        );
    }

    // R - READ (Obtener un usuario por su ID)
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

    // R - READ ALL (Obtener todos los usuarios)
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

    // U - UPDATE (Actualizar todos los datos de un usuario)
    public void updateUsuario(UsuarioOvi usuario) {
        jdbcTemplate.update(
                "UPDATE UsuarioOVI SET dni=?, nombre=?, apellidos=?, email=?, telefono=?, consentimiento_lopd=?, aceptado_tecnico=? " +
                        "WHERE id_usuario=?",
                usuario.getDni(), usuario.getNombre(), usuario.getApellidos(),
                usuario.getEmail(), usuario.getTelefono(), usuario.isConsentimientoLopd(), usuario.isAceptadoTecnico(),
                usuario.getIdUsuario()
        );
    }

    // D - DELETE (Borrar un usuario)
    public void deleteUsuario(String idUsuario) {
        jdbcTemplate.update(
                "DELETE FROM UsuarioOVI WHERE id_usuario = ?",
                idUsuario
        );
    }

    // Busca usuarios filtrando por si han sido aceptados por el técnico
    public List<UsuarioOvi> findByAceptado(boolean aceptadoTecnico) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM UsuarioOVI WHERE aceptado_tecnico = ?",
                    new UsuarioOviRowMapper(),
                    aceptadoTecnico
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // Actualiza SOLO el campo aceptado_tecnico
    public void updateAceptado(String idUsuario, boolean aceptadoTecnico) {
        jdbcTemplate.update(
                "UPDATE UsuarioOVI SET aceptado_tecnico = ? WHERE id_usuario = ?",
                aceptadoTecnico,
                idUsuario
        );
    }
}
