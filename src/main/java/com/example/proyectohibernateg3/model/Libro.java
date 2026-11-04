package com.example.proyectohibernateg3.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 200)
    private String autor;

    @Column(name = "anio_publicacion", nullable = false)
    private Integer anioPublicacion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    protected Libro() {
        // Constructor requerido por JPA.
    }

    public Libro(String titulo, String autor, Integer anioPublicacion, BigDecimal precio) {
        this.titulo = validar(titulo, "El título");
        this.autor = validar(autor, "El autor");
        if (anioPublicacion == null || anioPublicacion < 1 || anioPublicacion > 9999) {
            throw new IllegalArgumentException("El año de publicación debe estar entre 1 y 9999.");
        }
        if (precio == null || precio.signum() < 0
                || precio.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("El precio debe estar entre 0 y 99999999.99.");
        }
        try {
            this.precio = precio.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("El precio admite como máximo dos decimales.");
        }
        this.anioPublicacion = anioPublicacion;
    }

    private static String validar(String texto, String campo) {
        if (texto == null || texto.isBlank() || texto.strip().length() > 200) {
            throw new IllegalArgumentException(campo + " debe contener entre 1 y 200 caracteres.");
        }
        return texto.strip();
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public Integer getAnioPublicacion() { return anioPublicacion; }
    public BigDecimal getPrecio() { return precio; }
}
