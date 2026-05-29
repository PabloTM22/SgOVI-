package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Tecnico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TecnicoDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Tecnico getTecnico(String idTecnico) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM tecnico WHERE id_tecnico = ?",
                    new TecnicoRowMapper(), idTecnico
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Tecnico> getTecnicos() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM tecnico ORDER BY apellidos, nombre",
                    new TecnicoRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void addTecnico(Tecnico tecnico) {
        jdbcTemplate.update(
                "INSERT INTO tecnico (id_tecnico, dni, nombre, apellidos, email, telefono, activo) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                tecnico.getIdTecnico(), tecnico.getDni(), tecnico.getNombre(),
                tecnico.getApellidos(), tecnico.getEmail(), tecnico.getTelefono(),
                tecnico.isActivo()
        );
    }

    public void updateTecnico(Tecnico tecnico) {
        jdbcTemplate.update(
                "UPDATE tecnico SET dni = ?, nombre = ?, apellidos = ?, email = ?, telefono = ?, " +
                        "activo = ? WHERE id_tecnico = ?",
                tecnico.getDni(), tecnico.getNombre(), tecnico.getApellidos(),
                tecnico.getEmail(), tecnico.getTelefono(),
                tecnico.isActivo(), tecnico.getIdTecnico()
        );
    }

    public void deleteTecnico(String idTecnico) {
        jdbcTemplate.update("DELETE FROM tecnico WHERE id_tecnico = ?", idTecnico);
    }
}