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
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String telefono;

    @Transient
    private JdbcTemplate jdbcTemplate;

    // ===================
    // Constructores
    // ===================
    public Usuario() {
    }

    public Usuario(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Usuario(Long id, String nombre, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
    public void guardar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, email, telefono) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, usuario.getNombre(), usuario.getEmail(), usuario.getTelefono());
    }

    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
        jdbcTemplate.update(sql, usuario.getNombre(), usuario.getEmail(), usuario.getTelefono(), usuario.getId());
    }

    public void eliminar(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT id, nombre, email, telefono FROM usuarios ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Usuario(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("email"),
            rs.getString("telefono")
        ));
    }

    public Usuario obtenerPorId(Long id) {
        String sql = "SELECT id, nombre, email, telefono FROM usuarios WHERE id = ?";
        List<Usuario> resultado = jdbcTemplate.query(sql, (rs, rowNum) -> new Usuario(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("email"),
            rs.getString("telefono")
        ), id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }
}
