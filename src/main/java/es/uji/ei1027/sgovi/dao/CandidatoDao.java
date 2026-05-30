package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Candidato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository // Importante para que Spring sepa que esto maneja datos
public class CandidatoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // C - CREATE (Añadir un candidato)
    // C - CREATE (Añadir un candidato)
    public void addCandidato(Candidato candidato) {
        jdbcTemplate.update(
                "INSERT INTO AsistentePersonal (id_ap, dni, nombre, apellidos, email, telefono, tipo_ap, formacion, experiencia, disponibilidad, latitud, longitud, consentimiento_lopd, activo) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                candidato.getIdAp(), candidato.getDni(), candidato.getNombre(), candidato.getApellidos(),
                candidato.getEmail(), candidato.getTelefono(), candidato.getTipoAp(), candidato.getFormacion(),
                candidato.getExperiencia(), candidato.getDisponibilidad(), candidato.getLatitud(),
                candidato.getLongitud(), candidato.isConsentimientoLopd(), candidato.isActivo()
        );
    }

    // R - READ (Obtener un candidato por su ID)
    public Candidato getCandidato(String idAp) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM AsistentePersonal WHERE id_ap = ?",
                    new CandidatoRowMapper(),
                    idAp
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // R - READ ALL (Obtener todos los candidatos)
    public List<Candidato> getCandidatos() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM AsistentePersonal",
                    new CandidatoRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // U - UPDATE (Actualizar todos los datos de un candidato)
    // U - UPDATE (Actualizar todos los datos de un candidato)
    public void updateCandidato(Candidato candidato) {
        jdbcTemplate.update(
                "UPDATE AsistentePersonal SET dni=?, nombre=?, apellidos=?, email=?, telefono=?, tipo_ap=?, formacion=?, experiencia=?, disponibilidad=?, latitud=?, longitud=?, consentimiento_lopd=?, estado=?, activo=? " +
                        "WHERE id_ap=?",
                candidato.getDni(), candidato.getNombre(), candidato.getApellidos(),
                candidato.getEmail(), candidato.getTelefono(), candidato.getTipoAp(), candidato.getFormacion(),
                candidato.getExperiencia(), candidato.getDisponibilidad(), candidato.getLatitud(),
                candidato.getLongitud(), candidato.isConsentimientoLopd(), candidato.getEstado(), candidato.isActivo(),
                candidato.getIdAp()
        );
    }

    // D - DELETE (Borrar un candidato)
    public void deleteCandidato(String idAp) {
        jdbcTemplate.update(
                "DELETE FROM AsistentePersonal WHERE id_ap = ?",
                idAp
        );
    }

    public void updateEstado(String idAp, String estado) {
        jdbcTemplate.update(
                "UPDATE AsistentePersonal SET estado = ? WHERE id_ap = ?",
                estado,
                idAp
        );
    }

    public List<Candidato> findByEstado(String estado) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM AsistentePersonal WHERE estado = ? ORDER BY apellidos, nombre",
                    new CandidatoRowMapper(),
                    estado
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // Devuelve candidatos aceptados del tipo indicado, ordenados por proximidad
    // al punto de referencia (latRef, lngRef). Candidatos sin coordenadas aparecen al final.
    public List<Candidato> findAceptadosPorTipo(String tipoAp, double latRef, double lngRef) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM AsistentePersonal " +
                            "WHERE estado = 'aceptado' AND tipo_ap = ? " +
                            "ORDER BY CASE WHEN latitud IS NULL OR longitud IS NULL THEN 1 ELSE 0 END, " +
                            "(COALESCE(latitud,0) - ?)^2 + (COALESCE(longitud,0) - ?)^2",
                    new CandidatoRowMapper(),
                    tipoAp, latRef, lngRef
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    //FILTRO DINÁMICO PARA BUSCAR CANDIDATOS COMO USUARIO

    public List<Candidato> buscar(String tipo,
                                 String texto,
                                 String disponibilidad,
                                 String orden,
                                 double latRef,
                                 double lngRef) {

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM AsistentePersonal " +
                        "WHERE estado = 'aceptado' AND activo = TRUE"
        );
        List<Object> params = new ArrayList<>();

        // Filtro por tipo
        if (tipo != null && !tipo.isBlank() && !"TODOS".equalsIgnoreCase(tipo)) {
            sql.append(" AND tipo_ap = ?");
            params.add(tipo);
        }

        // Búsqueda de texto libre (nombre, apellidos, formación, experiencia)
        if (texto != null && !texto.isBlank()) {
            sql.append(" AND (LOWER(nombre) LIKE ? " +
                    " OR LOWER(apellidos) LIKE ? " +
                    " OR LOWER(COALESCE(formacion,'')) LIKE ? " +
                    " OR LOWER(COALESCE(experiencia,'')) LIKE ?)");
            String like = "%" + texto.toLowerCase() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }

        // Filtro por disponibilidad (búsqueda parcial sobre el campo de texto)
        if (disponibilidad != null && !disponibilidad.isBlank()) {
            sql.append(" AND LOWER(COALESCE(disponibilidad,'')) LIKE ?");
            params.add("%" + disponibilidad.toLowerCase() + "%");
        }

        // Orden
        if ("PROXIMIDAD".equalsIgnoreCase(orden)) {
            // Los que tienen coordenadas primero, luego por distancia al
            // cuadrado (suficiente para ordenar sin necesidad de sqrt).
            sql.append(" ORDER BY " +
                    "CASE WHEN latitud IS NULL OR longitud IS NULL THEN 1 ELSE 0 END, " +
                    "(COALESCE(latitud,0) - ?)^2 + (COALESCE(longitud,0) - ?)^2");
            params.add(latRef);
            params.add(lngRef);
        } else if ("EXPERIENCIA".equalsIgnoreCase(orden)) {
            // Aproximación: la experiencia es texto libre, así que usamos
            // la longitud del texto como heurística. Si tuvieras un campo
            // "anios_experiencia", cámbialo aquí.
            sql.append(" ORDER BY LENGTH(COALESCE(experiencia,'')) DESC, " +
                    "apellidos ASC, nombre ASC");
        } else {
            // Por defecto, orden alfabético
            sql.append(" ORDER BY apellidos ASC, nombre ASC");
        }

        try {
            return jdbcTemplate.query(sql.toString(),
                    new CandidatoRowMapper(),
                    params.toArray());
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }




}