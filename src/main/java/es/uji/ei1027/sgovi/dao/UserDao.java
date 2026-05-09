package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public UserDetails loadUserByUsername(String username, String password) {
        try {
            String id = jdbcTemplate.queryForObject(
                    "SELECT id_tecnico FROM tecnico " +
                            "WHERE (id_tecnico = ? OR email = ?) AND contrasena = ? AND activo = TRUE",
                    String.class, username, username, password
            );
            UserDetails user = new UserDetails();
            user.setUsername(id);
            user.setRol("TECNICO");
            return user;
        } catch (EmptyResultDataAccessException ignored) {}

        try {
            String id = jdbcTemplate.queryForObject(
                    "SELECT id_usuario FROM usuarioovi " +
                            "WHERE (id_usuario = ? OR email = ?) AND contrasena = ? AND aceptado_tecnico = TRUE",
                    String.class, username, username, password
            );
            UserDetails user = new UserDetails();
            user.setUsername(id);
            user.setRol("USUARIO_OVI");
            return user;
        } catch (EmptyResultDataAccessException ignored) {}

        try {
            String id = jdbcTemplate.queryForObject(
                    "SELECT id_ap FROM asistentepersonal " +
                            "WHERE (id_ap = ? OR email = ?) AND contrasena = ? AND activo = TRUE",
                    String.class, username, username, password
            );
            UserDetails user = new UserDetails();
            user.setUsername(id);
            user.setRol("ASISTENTE");
            return user;
        } catch (EmptyResultDataAccessException ignored) {}

        try {
            String id = jdbcTemplate.queryForObject(
                    "SELECT CAST(id_formador AS VARCHAR) FROM formador " +
                            "WHERE (CAST(id_formador AS VARCHAR) = ? OR email = ?) AND contrasena = ?",
                    String.class, username, username, password
            );
            UserDetails user = new UserDetails();
            user.setUsername(id);
            user.setRol("FORMADOR");
            return user;
        } catch (EmptyResultDataAccessException ignored) {}

        return null;
    }
}