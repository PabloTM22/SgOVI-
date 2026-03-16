package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.RegistroContrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RegistroContratoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addContrato(RegistroContrato contrato) {
        Integer nextId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id_contrato), 0) + 1 FROM registrocontrato",
                Integer.class
        );
        jdbcTemplate.update(
                "INSERT INTO registrocontrato (id_contrato, id_seleccion, fecha_inicio, fecha_fin, pdf_ruta) " +
                        "VALUES (?, ?, ?, ?, ?)",
                nextId, contrato.getIdSeleccion(), contrato.getFechaInicio(),
                contrato.getFechaFin(), contrato.getPdfRuta()
        );
    }

    public RegistroContrato getContrato(int idContrato) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM registrocontrato WHERE id_contrato = ?",
                    new RegistroContratoRowMapper(), idContrato
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<RegistroContrato> getContratos() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM registrocontrato ORDER BY fecha_inicio DESC",
                    new RegistroContratoRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public RegistroContrato findBySeleccion(int idSeleccion) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM registrocontrato WHERE id_seleccion = ?",
                    new RegistroContratoRowMapper(), idSeleccion
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateContrato(RegistroContrato contrato) {
        jdbcTemplate.update(
                "UPDATE registrocontrato SET fecha_inicio = ?, fecha_fin = ?, pdf_ruta = ? WHERE id_contrato = ?",
                contrato.getFechaInicio(), contrato.getFechaFin(),
                contrato.getPdfRuta(), contrato.getIdContrato()
        );
    }

    public void deleteContrato(int idContrato) {
        jdbcTemplate.update("DELETE FROM registrocontrato WHERE id_contrato = ?", idContrato);
    }
}
