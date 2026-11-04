package com.example.proyectohibernateg3;

import com.example.proyectohibernateg3.model.Libro;
import com.example.proyectohibernateg3.persistence.LibroRepository;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.concurrent.Callable;
import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibroController {
    private static final Logger LOG = Logger.getLogger(LibroController.class.getName());
    @FXML private TextField titulo;
    @FXML private TextField autor;
    @FXML private TextField anioPublicacion;
    @FXML private TextField precio;
    @FXML private Label estado;
    @FXML private HBox acciones;
    @FXML private TableView<Libro> tabla;
    @FXML private TableColumn<Libro, Long> idCol;
    @FXML private TableColumn<Libro, String> tituloCol;
    @FXML private TableColumn<Libro, String> autorCol;
    @FXML private TableColumn<Libro, Integer> anioCol;
    @FXML private TableColumn<Libro, BigDecimal> precioCol;

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        tituloCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTitulo()));
        autorCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getAutor()));
        anioCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getAnioPublicacion()));
        precioCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPrecio()));
    }

    @FXML
    private void cargar() {
        ejecutar(LibroRepository::listar, libros -> {
            tabla.getItems().setAll(libros);
            estado.setText("Libros encontrados: " + libros.size());
        });
    }

    @FXML
    private void guardar() {
        final Libro libro;
        try {
            // Validar primero los textos obligatorios para mostrar el error en orden.
            if (titulo.getText().isBlank() || autor.getText().isBlank()) {
                throw new IllegalArgumentException("El título y el autor son obligatorios.");
            }
            int anio;
            try {
                anio = Integer.parseInt(anioPublicacion.getText().strip());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El año de publicación debe ser un número entero entre 1 y 9999.");
            }
            BigDecimal importe;
            String textoPrecio = precio.getText().strip();
            if (!textoPrecio.matches("[0-9]+([.,][0-9]{1,2})?")) {
                throw new IllegalArgumentException("El precio debe ser un número no negativo con hasta dos decimales, sin separadores de miles.");
            }
            importe = new BigDecimal(textoPrecio.replace(',', '.'));
            libro = new Libro(titulo.getText(), autor.getText(), anio, importe);
        } catch (IllegalArgumentException e) {
            estado.setText(e.getMessage());
            return;
        }
        ejecutar(() -> {
            LibroRepository.guardar(libro);
            return libro;
        }, guardado -> {
            tabla.getItems().add(guardado);
            titulo.clear();
            autor.clear();
            anioPublicacion.clear();
            precio.clear();
            estado.setText("Libro guardado con ID " + guardado.getId());
        });
    }

    private <T> void ejecutar(Callable<T> operacion, Consumer<T> terminado) {
        acciones.setDisable(true);
        titulo.setDisable(true);
        autor.setDisable(true);
        anioPublicacion.setDisable(true);
        precio.setDisable(true);
        estado.setText("Procesando…");
        Task<T> task = new Task<>() {
            @Override protected T call() throws Exception { return operacion.call(); }
        };
        task.setOnSucceeded(event -> {
            habilitar();
            terminado.accept(task.getValue());
        });
        task.setOnFailed(event -> {
            habilitar();
            LOG.log(Level.SEVERE, "Error de persistencia", task.getException());
            estado.setText("No se pudo completar la operación. Revisa DB_URL, DB_USER, "
                    + "DB_PASSWORD y el esquema catalogos (consulta README.md). Detalles en la consola.");
        });
        Thread worker = new Thread(task, "libros-db");
        worker.setDaemon(true);
        worker.start();
    }

    private void habilitar() {
        acciones.setDisable(false);
        titulo.setDisable(false);
        autor.setDisable(false);
        anioPublicacion.setDisable(false);
        precio.setDisable(false);
    }
}
