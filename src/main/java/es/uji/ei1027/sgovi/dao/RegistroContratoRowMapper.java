package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.RegistroContrato;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class RegistroContratoRowMapper implements RowMapper<RegistroContrato> {

    @Override
    public RegistroContrato mapRow(ResultSet rs, int rowNum) throws SQLException {
        RegistroContrato contrato = new RegistroContrato();
        contrato.setIdContrato(rs.getInt("id_contrato"));
        contrato.setIdSeleccion(rs.getInt("id_seleccion"));
        contrato.setFechaInicio(rs.getObject("fecha_inicio", LocalDate.class));
        contrato.setFechaFin(rs.getObject("fecha_fin", LocalDate.class));   // nullable
        contrato.setPdfRuta(rs.getString("pdf_ruta"));                       // nullable
        return contrato;
    }
}
