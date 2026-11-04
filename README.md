# ProyectoHibernateG3

Catálogo básico de libros con JavaFX 17, Hibernate 6 y PostgreSQL.
Permite guardar libros (título, autor, año de publicación y precio) y consultar los registros existentes.
El año debe estar entre 1 y 9999 y el precio entre 0 y 99999999.99, con hasta dos decimales.
El formulario acepta punto o coma decimal sin separadores de miles.
La vista Hello original se conserva como ejemplo independiente.

## Requisitos y ejecución (PowerShell)

Usar un **JDK 17** (no un JRE 8). Maven se descarga con el wrapper incluido.
En este equipo hay un JDK compatible en la ruta del ejemplo:

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\.jdks\ms-17.0.20"
.\mvnw.cmd clean verify
```

Para usar PostgreSQL:

1. Crear una base de datos para el proyecto.
2. Ejecutar `database/schema.sql` dentro de esa base. El script no elimina datos;
   si ya existe una tabla `catalogos.libros`, comprobar que su estructura coincida.
   Para una tabla de la versión anterior, ejecutar `database/migration-anio-precio.sql`:
   añade las columnas sin eliminar libros. Completar los años y precios reales de
   los registros antiguos y aplicar las instrucciones `SET NOT NULL` del mismo script.
3. Configurar las variables en la misma terminal desde donde se inicia la aplicación:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/nombre_real_de_la_base?connectTimeout=5&socketTimeout=15'
$env:DB_USER = 'postgres'
$env:DB_PASSWORD = 'contraseña_real'
.\mvnw.cmd javafx:run
```

En IntelliJ, seleccionar JDK 17 para el proyecto y Maven, recargar el `pom.xml`
y definir esas variables en la configuración de ejecución de `Launcher`.
No guardar contraseñas en el repositorio.

La conexión se inicia al pulsar **Consultar libros** o **Guardar**. Si falla,
la ventana muestra un aviso y la consola registra el detalle. Hibernate usa
`validate`: verifica las tablas sin crearlas ni modificarlas automáticamente.
No se han configurado credenciales reales en `persistence.xml`.

## Comprobaciones

`mvnw.cmd clean verify` compila y ejecuta pruebas de validación, transacciones
y carga FXML. Las pruebas de persistencia usan H2 en memoria; no requieren ni
modifican PostgreSQL. La prueba JavaFX requiere una sesión gráfica de Windows.
Una prueba con H2 no sustituye la comprobación de la conexión PostgreSQL real.
