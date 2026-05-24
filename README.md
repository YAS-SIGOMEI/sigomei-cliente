# SIGOMEI — Cliente

**Cliente JavaFX del Sistema Distribuido para la Gestión de Órdenes de
Mantenimiento de Equipos Industriales.**

| | |
|---|---|
| **Equipo** | Yaelito's Solution |
| **Integrantes** | Yael Franco Toledo · José Emilio León Cruz · Rafael Alejandro Beltran Santos |

## Descripción

Este repositorio contiene el **cliente** del proyecto SIGOMEI. Se comunica
exclusivamente vía **Java RMI** con el servidor (repo `sigomei-servidor`)
— nunca toca la base de datos directamente.

El cliente **no tiene driver JDBC, ni credenciales de Postgres, ni código
DAO** — todas las operaciones se invocan a través de las interfaces remotas
`AutenticacionRemota`, `EquipoRemoto`, `TecnicoRemoto` y `OrdenRemota`.

## Stack tecnológico

- **Lenguaje:** Java 21
- **Build:** Gradle (con wrapper)
- **UI:** JavaFX 21 (plugin `org.openjfx.javafxplugin`)
- **Comunicación:** Java RMI (cliente del registro en puerto 1099)

## Requisitos previos

- **JDK 21** (`java -version` debe reportar 21).
- **Servidor SIGOMEI corriendo** (ver repo `sigomei-servidor`).
- *No necesitas instalar Gradle ni JavaFX:* el wrapper descarga Gradle y el
  plugin de JavaFX baja la dependencia automáticamente.

## Puesta en marcha

### 1. Levantar el servidor

En el repo `sigomei-servidor`:

```bash
./gradlew run
```

Espera a ver `SIGOMEI Server running on port 1099`.

### 2. Configurar a qué servidor conectar

Sigue [`configuracion_cliente.md`](./configuracion_cliente.md). Para
desarrollo local con servidor en la misma máquina no necesitas configurar
nada (los defaults son `localhost:1099`).

### 3. Arrancar el cliente

```bash
./gradlew run            # macOS / Linux
gradlew.bat run          # Windows
```

Se abre la ventana de login. Credenciales precargadas para probar:

| Usuario        | Contraseña | Rol         |
|----------------|------------|-------------|
| `coordinador1` | `coord123` | COORDINADOR |
| `supervisor1`  | `sup123`   | SUPERVISOR  |

## Logging

El cliente escribe a:

- **Consola**: para inspección durante desarrollo.
- **`sigomei-cliente.0.log`** (en el directorio de trabajo): rotado cada
  2 MB, conserva los últimos 3 archivos.

## Estructura de paquetes

```
src/main/java/sigomei/
├── modelo/        POJOs Serializable del dominio (copia exacta del servidor)
├── remoto/        Interfaces RMI (copia exacta del servidor)
├── excepciones/   Excepciones de negocio (copia exacta del servidor)
└── cliente/
    ├── ClienteMain.java           Bootstrap JavaFX + RMI lookup
    ├── ConfiguracionCliente.java  Resuelve host/port (env vars o cliente.properties)
    ├── Sesion.java                Singleton con referencias remotas + usuario actual
    ├── logging/                   LoggingConfigCliente
    └── vista/
        ├── LoginController.java + Login.fxml
        └── DashboardController.java + Dashboard.fxml

src/main/resources/sigomei/cliente/vista/   FXML de las pantallas
```

## Cómo se conecta al servidor

`ClienteMain.start()`:

1. Carga la configuración (`ConfiguracionCliente.desdeEntorno()`).
2. Hace `LocateRegistry.getRegistry(host, port)`.
3. `registry.lookup("AutenticacionService" / "EquipoService" / ...)` para cada
   uno de los 4 servicios.
4. Guarda las referencias remotas en `Sesion` (singleton accesible desde
   los controllers FXML).
5. Carga `Login.fxml` y muestra la ventana.

## CP-023 — Dos clientes simultáneos

Para validar la concurrencia (RNF-02), abre **dos terminales** y corre
`./gradlew run` en cada una. Cada invocación es un proceso JavaFX
independiente con su propia ventana. Ambos conectan al mismo servidor y
operan en paralelo.

## Empaquetar para despliegue

```bash
./gradlew installDist
```

Genera `build/install/sigomei-cliente/` con:
- `bin/sigomei-cliente` (script Unix) y `.bat` (Windows).
- `lib/` con todos los JARs (incluido el módulo JavaFX).

Coloca tu `cliente.properties` (o exporta `SIGOMEI_RMI_*`) en el directorio
de trabajo y ejecuta el script.
