package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UserDetails;
import org.jasypt.util.password.BasicPasswordEncryptor;
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
        BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();

        try {
            String[] row = jdbcTemplate.queryForObject(
                    "SELECT id_tecnico, contrasena FROM tecnico " +
                            "WHERE (id_tecnico = ? OR email = ?) AND activo = TRUE",
                    (rs, n) -> new String[]{rs.getString("id_tecnico"), rs.getString("contrasena")},
                    username, username);
            if (row != null && encryptor.checkPassword(password, row[1])) {
                UserDetails user = new UserDetails();
                user.setUsername(row[0]);
                user.setRol("TECNICO");
                return user;
            }
        } catch (EmptyResultDataAccessException ignored) {}

        try {
            String[] row = jdbcTemplate.queryForObject(
                    "SELECT id_usuario, contrasena FROM usuarioovi " +
                            "WHERE (id_usuario = ? OR email = ?) AND aceptado_tecnico = TRUE",
                    (rs, n) -> new String[]{rs.getString("id_usuario"), rs.getString("contrasena")},
                    username, username);
            if (row != null && encryptor.checkPassword(password, row[1])) {
                UserDetails user = new UserDetails();
                user.setUsername(row[0]);
                user.setRol("USUARIO_OVI");
                return user;
            }
        } catch (EmptyResultDataAccessException ignored) {}

        try {
            String[] row = jdbcTemplate.queryForObject(
                    "SELECT id_ap, contrasena FROM asistentepersonal " +
                            "WHERE (id_ap = ? OR email = ?) AND activo = TRUE",
                    (rs, n) -> new String[]{rs.getString("id_ap"), rs.getString("contrasena")},
                    username, username);
            if (row != null && encryptor.checkPassword(password, row[1])) {
                UserDetails user = new UserDetails();
                user.setUsername(row[0]);
                user.setRol("ASISTENTE");
                return user;
            }
        } catch (EmptyResultDataAccessException ignored) {}

        try {
            String[] row = jdbcTemplate.queryForObject(
                    "SELECT CAST(id_formador AS VARCHAR), contrasena FROM formador " +
                            "WHERE (CAST(id_formador AS VARCHAR) = ? OR email = ?)",
                    (rs, n) -> new String[]{rs.getString(1), rs.getString("contrasena")},
                    username, username);
            if (row != null && encryptor.checkPassword(password, row[1])) {
                UserDetails user = new UserDetails();
                user.setUsername(row[0]);
                user.setRol("FORMADOR");
                return user;
            }
        } catch (EmptyResultDataAccessException ignored) {}

        return null;
    }
}
