package com.example.model;

import java.time.LocalDateTime;

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

}
