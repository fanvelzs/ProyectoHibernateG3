package com.example.proyectohibernateg3.persistence;

import com.example.proyectohibernateg3.model.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LibroRepository {
    private static EntityManagerFactory factory;
    private static boolean cerrado;

    private LibroRepository() { }

    private static EntityManagerFactory factory() {
        if (cerrado) {
            throw new IllegalStateException("La aplicación se está cerrando.");
        }
        if (factory == null) {
            Map<String, Object> properties = new HashMap<>();
            configurar(properties, "DB_URL", "jakarta.persistence.jdbc.url");
            configurar(properties, "DB_USER", "jakarta.persistence.jdbc.user");
            configurar(properties, "DB_PASSWORD", "jakarta.persistence.jdbc.password");
            factory = Persistence.createEntityManagerFactory("LibroPU", properties);
        }
        return factory;
    }

    private static void configurar(Map<String, Object> properties, String variable, String propiedad) {
        String value = System.getenv(variable);
        if (value != null) {
            properties.put(propiedad, value);
        }
    }

    public static synchronized List<Libro> listar() {
        EntityManager em = factory().createEntityManager();
        try {
            return em.createQuery("select l from Libro l order by l.id", Libro.class).getResultList();
        } finally {
            em.close();
        }
    }

    public static synchronized void guardar(Libro libro) {
        guardar(factory(), libro);
    }

    static void guardar(EntityManagerFactory emf, Libro libro) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(libro);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Comparte el bloqueo de las operaciones para no cerrar una transacción activa.
    public static synchronized void close() {
        cerrado = true;
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
        factory = null;
    }
}
