package com.example.proyectohibernateg3;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import com.example.proyectohibernateg3.model.Libro;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LibroViewTest {
    @BeforeAll
    static void iniciarJavaFX() throws Exception {
        CompletableFuture<Void> ready = new CompletableFuture<>();
        Platform.startup(() -> ready.complete(null));
        ready.get(15, TimeUnit.SECONDS);
    }

    @AfterAll
    static void cerrarJavaFX() {
        Platform.exit();
    }

    @Test
    void cargaVistaYRechazaCamposVaciosSinConectarABase() throws Exception {
        CompletableFuture<Void> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Parent vista = FXMLLoader.load(HelloApplication.class.getResource("libro-view.fxml"));
                Button guardar = (Button) vista.lookupAll(".button").stream()
                        .filter(node -> ((Button) node).getText().equals("Guardar"))
                        .findFirst().orElseThrow();
                guardar.fire();
                assertTrue(((Label) vista.lookup("#estado")).getText().contains("título"));
                assertNotNull(vista.lookup("#tabla"));
                ((TextField) vista.lookup("#titulo")).setText("El principito");
                ((TextField) vista.lookup("#autor")).setText("Antoine de Saint-Exupéry");
                ((TextField) vista.lookup("#anioPublicacion")).setText("abc");
                guardar.fire();
                assertTrue(((Label) vista.lookup("#estado")).getText().contains("año"));
                ((TextField) vista.lookup("#anioPublicacion")).setText("1943");
                for (String importe : new String[]{"", "-1", "12.345", "1,250.00", "100000000"}) {
                    ((TextField) vista.lookup("#precio")).setText(importe);
                    guardar.fire();
                    assertTrue(((Label) vista.lookup("#estado")).getText().contains("precio"));
                }
                @SuppressWarnings("unchecked")
                TableView<Libro> tabla = (TableView<Libro>) vista.lookup("#tabla");
                tabla.getItems().add(new Libro("El principito", "Autor", 1943, new BigDecimal("250.50")));
                assertEquals(1943, tabla.getColumns().get(3).getCellData(0));
                assertEquals(new BigDecimal("250.50"), tabla.getColumns().get(4).getCellData(0));
                // El ejemplo original también debe seguir siendo cargable.
                assertNotNull(FXMLLoader.<Parent>load(HelloApplication.class.getResource("hello-view.fxml")));
                result.complete(null);
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        });
        result.get(15, TimeUnit.SECONDS);
    }
}
