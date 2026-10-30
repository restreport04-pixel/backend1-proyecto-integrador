package com.example.model;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "tipo_espacio")
public class TipoEspacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;

    @Transient
    private JdbcTemplate jdbcTemplate;

    // ===================
    // Constructores
    // ===================
    public TipoEspacio() {
    }

    public TipoEspacio(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TipoEspacio(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // ===================
    // Getters y Setters
    // ===================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===================
    // Métodos CRUD
    // ===================
    public void guardar(TipoEspacio tipoEspacio) {
        String sql = "INSERT INTO tipo_espacio (nombre, descripcion) VALUES (?, ?)";
        jdbcTemplate.update(sql, tipoEspacio.getNombre(), tipoEspacio.getDescripcion());
    }

    public void actualizar(TipoEspacio tipoEspacio) {
        String sql = "UPDATE tipo_espacio SET nombre = ?, descripcion = ? WHERE id = ?";
        jdbcTemplate.update(sql, tipoEspacio.getNombre(), tipoEspacio.getDescripcion(), tipoEspacio.getId());
    }

    public void eliminar(Long id) {
        String sql = "DELETE FROM tipo_espacio WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<TipoEspacio> listarTodos() {
        String sql = "SELECT id, nombre, descripcion FROM tipo_espacio ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new TipoEspacio(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("descripcion")
        ));
    }

    public TipoEspacio obtenerPorId(Long id) {
        String sql = "SELECT id, nombre, descripcion FROM tipo_espacio WHERE id = ?";
        List<TipoEspacio> resultado = jdbcTemplate.query(sql, (rs, rowNum) -> new TipoEspacio(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("descripcion")
        ), id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }
}
