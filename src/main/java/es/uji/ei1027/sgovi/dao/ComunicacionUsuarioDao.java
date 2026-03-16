package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.ComunicacionUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ComunicacionUsuarioDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addComunicacion(ComunicacionUsuario com) {
        Integer nextId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id_comunicacion), 0) + 1 FROM comunicacionusuario",
                Integer.class
        );
        jdbcTemplate.update(
                "INSERT INTO comunicacionusuario (id_comunicacion, id_seleccion, id_usuario, id_ap, id_tecnico, mensaje) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                nextId, com.getIdSeleccion(), com.getIdUsuario(),
                com.getIdAp(), com.getIdTecnico(), com.getMensaje()
        );
    }

    public ComunicacionUsuario getComunicacion(int idComunicacion) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM comunicacionusuario WHERE id_comunicacion = ?",
                    new ComunicacionUsuarioRowMapper(), idComunicacion
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ComunicacionUsuario> findBySeleccion(int idSeleccion) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM comunicacionusuario WHERE id_seleccion = ? ORDER BY fecha_envio ASC",
                    new ComunicacionUsuarioRowMapper(), idSeleccion
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void deleteComunicacion(int idComunicacion) {
        jdbcTemplate.update("DELETE FROM comunicacionusuario WHERE id_comunicacion = ?", idComunicacion);
    }
}