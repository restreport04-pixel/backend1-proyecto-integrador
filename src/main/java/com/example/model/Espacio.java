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
@Table(name = "espacio")
public class Espacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer capacidad;
    private Double precioHora;
    private Long tipoEspacioId;

    @Transient
    private JdbcTemplate jdbcTemplate;

    // ===================
    // Constructores
    // ===================
    public Espacio() {
    }

    public Espacio(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Espacio(Long id, String nombre, Integer capacidad, Double precioHora, Long tipoEspacioId) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.precioHora = precioHora;
        this.tipoEspacioId = tipoEspacioId;
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

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Double getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(Double precioHora) {
        this.precioHora = precioHora;
    }

    public Long getTipoEspacioId() {
        return tipoEspacioId;
    }

    public void setTipoEspacioId(Long tipoEspacioId) {
        this.tipoEspacioId = tipoEspacioId;
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
    public void guardar(Espacio espacio) {
        String sql = "INSERT INTO espacio (nombre, capacidad, precio_hora, tipo_espacio_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, espacio.getNombre(), espacio.getCapacidad(), espacio.getPrecioHora(), espacio.getTipoEspacioId());
    }

    public void actualizar(Espacio espacio) {
        String sql = "UPDATE espacio SET nombre = ?, capacidad = ?, precio_hora = ?, tipo_espacio_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, espacio.getNombre(), espacio.getCapacidad(), espacio.getPrecioHora(), espacio.getTipoEspacioId(), espacio.getId());
    }

    public void eliminar(Long id) {
        String sql = "DELETE FROM espacio WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Espacio> listarTodos() {
        String sql = "SELECT id, nombre, capacidad, precio_hora, tipo_espacio_id FROM espacio ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Espacio(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getObject("capacidad", Integer.class),
            rs.getObject("precio_hora", Double.class),
            rs.getObject("tipo_espacio_id", Long.class)
        ));
    }

    public Espacio obtenerPorId(Long id) {
        String sql = "SELECT id, nombre, capacidad, precio_hora, tipo_espacio_id FROM espacio WHERE id = ?";
        List<Espacio> resultado = jdbcTemplate.query(sql, (rs, rowNum) -> new Espacio(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getObject("capacidad", Integer.class),
            rs.getObject("precio_hora", Double.class),
            rs.getObject("tipo_espacio_id", Long.class)
        ), id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }
}
