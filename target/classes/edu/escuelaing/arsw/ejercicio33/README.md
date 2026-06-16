# Ejercicio 3.3 - Gestión de Salones vía HTTP

## Objetivo

Transformar el sistema de gestión de salones desarrollado anteriormente con TCP a un servicio basado en HTTP.

El objetivo es comprender cómo un protocolo propio de comunicación puede ser reemplazado por rutas HTTP más claras, permitiendo que clientes como navegadores, Postman o herramientas como curl puedan interactuar con el servidor.

---

# Marco Teórico

Este ejercicio utiliza el protocolo HTTP sobre una arquitectura cliente-servidor.

A diferencia del ejercicio basado en TCP donde se enviaban mensajes personalizados como:

```
RESERVAR_SALON,E303
```

HTTP define una estructura basada en:

* Método HTTP (GET, POST)
* Ruta del recurso
* Parámetros

Ejemplo:

```
GET /rooms?id=E303
```

La comunicación se realiza mediante solicitudes y respuestas HTTP, donde el servidor procesa la petición y retorna una respuesta en texto plano o HTML.

---

# Desarrollo del Ejercicio

Se implementó un servidor HTTP utilizando las clases básicas de Java:

* `ServerSocket`
* `Socket`
* Streams de entrada y salida


---

# Funcionamiento del Sistema

El servidor mantiene en memoria los salones:

```
E301
E302
E303
E304
```

Cada salón puede tener dos estados:

```
DISPONIBLE
RESERVADO
```

---

# Comandos de Ejecución

## Compilación

Desde la raíz del proyecto:

```bash
javac src/main/java/edu/escuelaing/arsw/ejercicio33/*.java
```

---

## Ejecutar servidor

```bash
java -cp src/main/java edu.escuelaing.arsw.ejercicio33.HttpSalonServer
```

Salida esperada:

```
HTTP Salon Server running...
```

El servidor queda esperando solicitudes.

![alt text](../imagenes/336.png)

---


# Pruebas Realizadas

## Consultar todos los salones

Petición:

```
GET /rooms
```

Ejemplo:

```
http://localhost:35000/rooms
```

Respuesta:

```
E301 : DISPONIBLE
E302 : DISPONIBLE
E303 : DISPONIBLE
E304 : DISPONIBLE
```
![alt text](../imagenes/331.png)

---

## Consultar un salón específico

Petición:

```
GET /rooms?id=E303
```

Ejemplo:

```
http://localhost:35000/rooms?id=E303
```

Respuesta:

```
SALON_DISPONIBLE
```
![alt text](../imagenes/332.png)

---

## Reservar un salón

Petición:

```
POST /rooms/reserve?id=E303
```

Comando:

```bash
curl.exe -X POST "http://localhost:35000/rooms/reserve?id=E303"
```

Respuesta:

```
RESERVA_EXITOSA
```

![alt text](../imagenes/333.png)

![alt text](../imagenes/334.png)

---

## Liberar un salón

Petición:

```
POST /rooms/release?id=E303
```

Comando:

```bash
curl.exe -X POST "http://localhost:35000/rooms/release?id=E303"
```

Respuesta:

```
LIBERACION_EXITOSA
```

![alt text](../imagenes/335.png)

---



---

# Análisis

La implementación demuestra cómo un sistema cliente-servidor puede evolucionar desde un protocolo de texto propio hacia una arquitectura basada en HTTP.

HTTP proporciona una forma más organizada de definir operaciones mediante rutas y métodos, haciendo que el servicio sea más fácil de consumir desde diferentes clientes.

El servidor mantiene la lógica del negocio separada del manejo de la comunicación, permitiendo una mejor organización del código.

---



# Preguntas de Reflexión

## ¿Qué ventajas ofrece HTTP frente a un protocolo de texto definido manualmente?

HTTP ofrece una estructura estándar de comunicación que ya es conocida por múltiples clientes como navegadores, herramientas de pruebas y aplicaciones.

Además, permite separar claramente:

* La acción mediante el método HTTP.
* El recurso mediante la URL.
* Los parámetros mediante consultas.

Por ejemplo:

```
POST /rooms/reserve?id=E303
```

es más descriptivo que:

```
RESERVAR_SALON,E303
```

También facilita la integración con otros sistemas.

---

## ¿Qué limitaciones tiene construir un servidor HTTP sin framework?

Construir un servidor HTTP manualmente permite entender cómo funciona la comunicación, pero tiene varias limitaciones:

* Se debe interpretar manualmente la solicitud HTTP.
* No existen validaciones automáticas.
* Manejar errores es más complejo.
* No incluye características avanzadas como seguridad, manejo de sesiones o serialización automática.

Frameworks como Spring Boot simplifican estas tareas y permiten crear servicios más robustos.

---

## ¿Cómo cambiaría esta solución si se usara JSON en lugar de HTML?

Si se utilizara JSON, las respuestas serían datos estructurados en lugar de páginas HTML.

Ejemplo:

```json
{
  "salon": "E303",
  "estado": "RESERVADO"
}
```

Esto permitiría que otros programas consumieran la información fácilmente.

Además, JSON facilita la comunicación entre aplicaciones distribuidas porque no depende de una interfaz visual.

---

# Conclusiones

1. HTTP permite construir servicios más flexibles y fáciles de consumir que protocolos personalizados.
2. Los métodos y rutas HTTP funcionan como un contrato de comunicación entre cliente y servidor.
3. Separar la lógica del negocio del servidor facilita la evolución del sistema.
4. La implementación manual permite comprender el funcionamiento interno de HTTP.
5. En aplicaciones reales sería recomendable utilizar frameworks y formatos estructurados como JSON.

