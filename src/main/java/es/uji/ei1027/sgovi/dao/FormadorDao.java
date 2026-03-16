package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Formador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FormadorDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addFormador(Formador formador) {
        Integer nextId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id_formador), 0) + 1 FROM formador",
                Integer.class
        );
        jdbcTemplate.update(
                "INSERT INTO formador (id_formador, dni, nombre, apellidos, email, telefono) VALUES (?, ?, ?, ?, ?, ?)",
                nextId, formador.getDni(), formador.getNombre(),
                formador.getApellidos(), formador.getEmail(), formador.getTelefono()
        );
    }

    public Formador getFormador(int idFormador) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM formador WHERE id_formador = ?",
                    new FormadorRowMapper(), idFormador
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Formador> getFormadores() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM formador ORDER BY apellidos, nombre",
                    new FormadorRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void updateFormador(Formador formador) {
        jdbcTemplate.update(
                "UPDATE formador SET dni = ?, nombre = ?, apellidos = ?, email = ?, telefono = ? WHERE id_formador = ?",
                formador.getDni(), formador.getNombre(), formador.getApellidos(),
                formador.getEmail(), formador.getTelefono(), formador.getIdFormador()
        );
    }

    public void deleteFormador(int idFormador) {
        jdbcTemplate.update("DELETE FROM formador WHERE id_formador = ?", idFormador);
    }
}

