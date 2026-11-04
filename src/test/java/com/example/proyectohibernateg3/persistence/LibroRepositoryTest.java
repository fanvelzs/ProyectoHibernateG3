package com.example.proyectohibernateg3.persistence;

import com.example.proyectohibernateg3.model.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LibroRepositoryTest {
    @Test
    void validaDatosAntesDeGuardar() {
        assertThrows(IllegalArgumentException.class, () -> new Libro("  ", "Autor", 2024, new BigDecimal("12.50")));
        assertThrows(IllegalArgumentException.class, () -> new Libro("Título", null, 2024, new BigDecimal("12.50")));
        assertThrows(IllegalArgumentException.class, () -> new Libro("a".repeat(201), "Autor", 2024, new BigDecimal("12.50")));
        assertThrows(IllegalArgumentException.class, () -> new Libro("Título", "a".repeat(201), 2024, new BigDecimal("12.50")));
        Libro libro = new Libro(" Título ", " Autor ", 2024, new BigDecimal("12.50"));
        assertEquals("Título", libro.getTitulo());
        assertEquals("Autor", libro.getAutor());
    }

    @Test
    void validaAnioYPrecioSinRedondearImportes() {
        for (Integer anio : new Integer[]{null, 0, -1, 10000}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Libro("Título", "Autor", anio, BigDecimal.ONE));
        }
        for (BigDecimal precio : new BigDecimal[]{null, new BigDecimal("-0.01"),
                new BigDecimal("100000000"), new BigDecimal("12.345")}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Libro("Título", "Autor", 2024, precio));
        }
        assertEquals(new BigDecimal("0.00"),
                new Libro("Título", "Autor", 1, BigDecimal.ZERO).getPrecio());
        assertEquals(new BigDecimal("99999999.99"),
                new Libro("Título", "Autor", 9999, new BigDecimal("99999999.99")).getPrecio());
    }

    @Test
    void descubreUnidadJPAConfirmaYRevierteTransacciones() {
        Map<String, Object> config = Map.of(
                "jakarta.persistence.jdbc.driver", "org.h2.Driver",
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:" + UUID.randomUUID()
                        + ";MODE=PostgreSQL;INIT=CREATE SCHEMA IF NOT EXISTS catalogos",
                "jakarta.persistence.jdbc.user", "sa",
                "jakarta.persistence.jdbc.password", "",
                "hibernate.dialect", "org.hibernate.dialect.H2Dialect",
                "hibernate.hbm2ddl.auto", "create-drop",
                "hibernate.hbm2ddl.create_namespaces", "false");
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("LibroPU", config)) {
            Libro libro = new Libro("El principito", "Antoine de Saint-Exupéry", 1943, new BigDecimal("250.50"));
            LibroRepository.guardar(emf, libro);
            assertNotNull(libro.getId());
            // Una entidad sin campos obligatorios fuerza un fallo de persistencia.
            Libro invalido = new Libro("Temporal", "Autor", 2024, new BigDecimal("12.50"));
            try {
                var campo = Libro.class.getDeclaredField("titulo");
                campo.setAccessible(true);
                campo.set(invalido, null);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
            assertThrows(RuntimeException.class, () -> LibroRepository.guardar(emf, invalido));
            LibroRepository.guardar(emf, new Libro("Otro libro", "Otro autor", 2024, new BigDecimal("12.50")));
            EntityManager em = emf.createEntityManager();
            try {
                assertEquals("El principito", em.find(Libro.class, libro.getId()).getTitulo());
                assertEquals(1943, em.find(Libro.class, libro.getId()).getAnioPublicacion());
                assertEquals(new BigDecimal("250.50"), em.find(Libro.class, libro.getId()).getPrecio());
                assertEquals(2L, em.createQuery("select count(l) from Libro l", Long.class).getSingleResult());
            } finally {
                em.close();
            }
        }
    }
}

