# Configuración del cliente

Cómo apuntar el cliente SIGOMEI al servidor RMI.

El archivo `cliente.properties` está **ignorado por git** para que cada
miembro del equipo tenga su propia configuración (host del servidor, puerto)
sin pisar la de los demás.

## 1. Configurar a qué servidor conectarse

Tienes dos opciones; pueden combinarse (env vars tienen prioridad).

### Opción A — Archivo `cliente.properties`

```bash
cp cliente.properties.example cliente.properties
# Editar cliente.properties
```

Contenido:

```properties
rmi.host=localhost   # IP o hostname donde corre el servidor SIGOMEI
rmi.port=1099
```

Para CP-023 (dos clientes contra el mismo servidor en LAN), cada miembro
del equipo pone en su `cliente.properties` la IP de la máquina que está
ejecutando el servidor.

### Opción B — Variables de entorno

```bash
export SIGOMEI_RMI_HOST=192.168.1.42
export SIGOMEI_RMI_PORT=1099
```

También puedes apuntar a un archivo en otra ruta:
```bash
export SIGOMEI_RMI_CONFIG=/ruta/al/cliente.properties
```

## 2. Orden de resolución

`ConfiguracionCliente.desdeEntorno()` resuelve cada parámetro en este orden:

1. Variable de entorno `SIGOMEI_RMI_*`.
2. Valor del archivo `cliente.properties`.
3. Defaults: `localhost`, `1099`.

## 3. Solución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| `Connection refused to host: localhost` | Servidor no está arrancado | Verifica `./gradlew run` en `sigomei-servidor` |
| `NotBoundException: EquipoService` | El servidor arrancó mal o no registró los servicios | Revisa el log del servidor |
| `UnmarshalException` al hacer login | Versiones de clases incompatibles entre cliente y servidor | Asegúrate de que `sigomei.modelo`, `sigomei.remoto` y `sigomei.excepciones` sean idénticos en ambos repos |
| El cliente se conecta pero "Credenciales invalidas" siempre | La BD tiene contraseñas sin hashear | Recarga `script_db.sql` (ya guarda las contraseñas como SHA-256) |
