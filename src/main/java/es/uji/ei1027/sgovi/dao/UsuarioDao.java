package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UsuarioDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addUsuario(Usuario usuario) {
        jdbcTemplate.update(
                "INSERT INTO usuario (username, password, rol, activo) VALUES (?, ?, ?, ?)",
                usuario.getUsername(), usuario.getPassword(),
                usuario.getRol(), usuario.isActivo()
        );
    }

    public Usuario getUsuario(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM usuario WHERE username = ?",
                    new UsuarioRowMapper(),
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updatePassword(String username, String nuevoPasswordHash) {
        jdbcTemplate.update(
                "UPDATE usuario SET password = ? WHERE username = ?",
                nuevoPasswordHash, username
        );
    }

    public void updateActivo(String username, boolean activo) {
        jdbcTemplate.update(
                "UPDATE usuario SET activo = ? WHERE username = ?",
                activo, username
        );
    }

    public void deleteUsuario(String username) {
        jdbcTemplate.update(
                "DELETE FROM usuario WHERE username = ?",
                username
        );
    }
}
