package com.example.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuarioId;
    private Long espacioId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Double totalPagar;

    @Transient
    private JdbcTemplate jdbcTemplate;

    // ===================
    // Constructores
    // ===================
    public Reserva() {
    }

    public Reserva(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Reserva(Long id, Long usuarioId, Long espacioId, LocalDateTime fechaInicio, LocalDateTime fechaFin, Double totalPagar) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.espacioId = espacioId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.totalPagar = totalPagar;
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getEspacioId() {
        return espacioId;
    }

    public void setEspacioId(Long espacioId) {
        this.espacioId = espacioId;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(Double totalPagar) {
        this.totalPagar = totalPagar;
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
    public void guardar(Reserva reserva) {
        String sql = "INSERT INTO reserva (usuario_id, espacio_id, fecha_inicio, fecha_fin, total_pagar) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, reserva.getUsuarioId(), reserva.getEspacioId(), reserva.getFechaInicio(), reserva.getFechaFin(), reserva.getTotalPagar());
    }

    public void actualizar(Reserva reserva) {
        String sql = "UPDATE reserva SET usuario_id = ?, espacio_id = ?, fecha_inicio = ?, fecha_fin = ?, total_pagar = ? WHERE id = ?";
        jdbcTemplate.update(sql, reserva.getUsuarioId(), reserva.getEspacioId(), reserva.getFechaInicio(), reserva.getFechaFin(), reserva.getTotalPagar(), reserva.getId());
    }

    public void eliminar(Long id) {
        String sql = "DELETE FROM reserva WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Reserva> listarTodos() {
        String sql = "SELECT id, usuario_id, espacio_id, fecha_inicio, fecha_fin, total_pagar FROM reserva ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp tsInicio = rs.getTimestamp("fecha_inicio");
            Timestamp tsFin = rs.getTimestamp("fecha_fin");
            return new Reserva(
                rs.getLong("id"),
                rs.getObject("usuario_id", Long.class),
                rs.getObject("espacio_id", Long.class),
                tsInicio != null ? tsInicio.toLocalDateTime() : null,
                tsFin != null ? tsFin.toLocalDateTime() : null,
                rs.getObject("total_pagar", Double.class)
            );
        });
    }

    public Reserva obtenerPorId(Long id) {
        String sql = "SELECT id, usuario_id, espacio_id, fecha_inicio, fecha_fin, total_pagar FROM reserva WHERE id = ?";
        List<Reserva> resultado = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp tsInicio = rs.getTimestamp("fecha_inicio");
            Timestamp tsFin = rs.getTimestamp("fecha_fin");
            return new Reserva(
                rs.getLong("id"),
                rs.getObject("usuario_id", Long.class),
                rs.getObject("espacio_id", Long.class),
                tsInicio != null ? tsInicio.toLocalDateTime() : null,
                tsFin != null ? tsFin.toLocalDateTime() : null,
                rs.getObject("total_pagar", Double.class)
            );
        }, id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }
}
