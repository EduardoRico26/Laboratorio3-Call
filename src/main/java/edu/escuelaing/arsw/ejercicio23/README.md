# Ejercicio 2.3 - Sistema de Gestión de Salones mediante TCP

## Objetivo

Diseñar e implementar un sistema cliente-servidor utilizando sockets TCP en Java para gestionar la reserva de salones de la Escuela.

El sistema permite consultar el estado de un salón, reservar salones disponibles y liberar salones previamente reservados mediante un protocolo de comunicación basado en mensajes de texto.

---

# Marco Teórico

La solución utiliza el modelo cliente-servidor mediante el protocolo TCP.

TCP permite establecer una comunicación confiable entre dos aplicaciones mediante una conexión orientada a flujo. El cliente envía solicitudes al servidor y este procesa la petición para retornar una respuesta.

En este ejercicio se define un protocolo propio basado en comandos de texto:

```
OPERACION,SALON
```

Las operaciones soportadas son:

```
CONSULTAR_SALON,E303
RESERVAR_SALON,E303
LIBERAR_SALON,E303
```

El servidor mantiene el estado de los salones en memoria y controla las operaciones solicitadas por los clientes.

---

# Desarrollo del Ejercicio

Se implementó un sistema de gestión de salones compuesto por tres clases principales:

## SalonServer

Representa el servidor TCP encargado de:

* Escuchar conexiones en el puerto `35000`.
* Recibir solicitudes de clientes.
* Procesar las operaciones solicitadas.
* Retornar respuestas al cliente.

---

## SalonClient

Representa el cliente que permite enviar solicitudes al servidor.

El usuario puede ingresar comandos como:

```
CONSULTAR_SALON,E303
```

y recibir la respuesta correspondiente.

---

## SalonManager

Contiene la lógica del sistema.

Inicialmente administra los siguientes salones:

```
E301
E302
E303
E304
```

Cada salón puede encontrarse en dos estados:

```
Disponible
Reservado
```

Las operaciones implementadas son:

| Operación                  | Respuesta                |
| -------------------------- | ------------------------ |
| Consultar salón disponible | SALON_DISPONIBLE         |
| Consultar salón reservado  | SALON_RESERVADO          |
| Reservar salón             | RESERVA_EXITOSA          |
| Liberar salón              | LIBERACION_EXITOSA       |
| Salón inexistente          | ERROR_SALON_NO_EXISTE    |
| Operación incorrecta       | ERROR_OPERACION_INVALIDA |

---

# Protocolo de Comunicación

Ejemplo de solicitud:

Cliente:

```
RESERVAR_SALON,E303
```

Servidor:

```
RESERVA_EXITOSA
```

Ejemplo de consulta:

Cliente:

```
CONSULTAR_SALON,E303
```

Servidor:

```
SALON_RESERVADO
```

---

# Comandos de Ejecución

## Compilación

Desde la raíz del proyecto:

```bash
javac src/main/java/edu/escuelaing/arsw/ejercicio23/*.java
```

---

## Ejecutar Servidor

```bash
java -cp src/main/java edu.escuelaing.arsw.ejercicio23.SalonServer
```

Salida esperada:

```
Servidor de salones iniciado...
```

---

## Ejecutar Cliente

En otra terminal:

```bash
java -cp src/main/java edu.escuelaing.arsw.ejercicio23.SalonClient
```

---



# Análisis de Resultados

La aplicación logró implementar correctamente una comunicación TCP entre cliente y servidor.

El servidor mantiene el estado de los salones en memoria y responde correctamente dependiendo de la operación solicitada.

Además, se utilizó sincronización en las operaciones críticas para evitar inconsistencias cuando varios clientes intenten modificar el estado de un mismo salón simultáneamente.

---

# Evidencias

## Servidor ejecutándose

![alt text](../imagenes/23.2.png)

## Cliente ejecutándose

![alt text](../imagenes/23.png)
---

# Preguntas de Reflexión

## ¿Qué tan fácil sería agregar una nueva operación al protocolo?

Agregar una nueva operación sería sencillo porque el protocolo está basado en comandos de texto.

Por ejemplo, una operación nueva como:

```
CAMBIAR_ESTADO,E303
```

podría agregarse modificando únicamente la lógica del servidor y agregando una nueva condición dentro del procesamiento de comandos.

Sin embargo, al crecer el número de operaciones sería recomendable utilizar un protocolo más formal con estructuras definidas.

---

## ¿Qué ocurre si dos clientes intentan reservar el mismo salón al mismo tiempo?

Si dos clientes intentan reservar el mismo salón simultáneamente podría existir un problema de concurrencia.

Para evitarlo se implementó sincronización en las operaciones que modifican el estado del salón.

De esta manera, solamente un cliente puede realizar la reserva primero y el segundo cliente recibirá:

```
SALON_RESERVADO
```

---

## ¿Dónde está definido realmente el contrato de comunicación: en un archivo formal o en convenciones de texto?

En esta implementación el contrato está definido mediante convenciones de texto.

El cliente y el servidor deben conocer previamente los comandos válidos:

```
OPERACION,SALON
```

No existe un archivo formal que describa el protocolo.

En sistemas más avanzados como gRPC, estos contratos se definen mediante archivos `.proto`, lo que permite una comunicación más estructurada.

---

# Conclusiones

1. Los sockets TCP permiten construir sistemas distribuidos utilizando un modelo cliente-servidor.
2. Definir un protocolo de comunicación claro es fundamental para la interacción entre aplicaciones.
3. El estado compartido debe manejarse cuidadosamente para evitar problemas de concurrencia.
4. Separar la lógica de negocio del servidor mejora la organización y mantenimiento del sistema.
5. Este ejercicio representa una evolución desde una comunicación básica por sockets hacia servicios distribuidos con contratos más definidos.

---

