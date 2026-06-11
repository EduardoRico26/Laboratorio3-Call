# Laboratorio3-Call

# Exercise 2.3 - Room Management System using TCP

## Objective

To design and implement a client-server application using TCP sockets in Java to manage classroom reservations at the School.

The system allows clients to query the status of a room, reserve available rooms, and release previously reserved rooms through a text-based communication protocol.

---

# Theoretical Background

The solution uses the client-server model through the TCP protocol.

TCP allows reliable communication between two applications by establishing a connection-oriented communication channel. The client sends requests to the server, and the server processes the request and returns a response.

In this exercise, a custom text-based communication protocol was defined:

```text
OPERATION,ROOM
```

The supported operations are:

```text
CONSULTAR_SALON,E303
RESERVAR_SALON,E303
LIBERAR_SALON,E303
```

The server maintains the state of the rooms in memory and controls all requested operations.

---

# Exercise Development

A room management system was implemented using three main classes:

## SalonServer

The server application is responsible for:

* Listening for TCP connections on port `35000`.
* Receiving client requests.
* Processing requested operations.
* Returning responses to clients.

---

## SalonClient

The client application allows users to send requests to the server.

The user can enter commands such as:

```text
CONSULTAR_SALON,E303
```

and receive the corresponding response.

---

## SalonManager

This component contains the business logic of the system.

The initial available rooms are:

```text
E301
E302
E303
E304
```

Each room can have two states:

```text
Available
Reserved
```

Implemented operations:

| Operation            | Response                 |
| -------------------- | ------------------------ |
| Query available room | SALON_DISPONIBLE         |
| Query reserved room  | SALON_RESERVADO          |
| Reserve room         | RESERVA_EXITOSA          |
| Release room         | LIBERACION_EXITOSA       |
| Non-existing room    | ERROR_SALON_NO_EXISTE    |
| Invalid operation    | ERROR_OPERACION_INVALIDA |

---

# Communication Protocol

Example request:

Client:

```text
RESERVAR_SALON,E303
```

Server:

```text
RESERVA_EXITOSA
```

Example query:

Client:

```text
CONSULTAR_SALON,E303
```

Server:

```text
SALON_RESERVADO
```

---

# Execution Commands

## Compilation

From the project root:

```bash
javac src/main/java/edu/escuelaing/arsw/ejercicio23/*.java
```

---

## Run Server

```bash
java -cp src/main/java edu.escuelaing.arsw.ejercicio23.SalonServer
```

Expected output:

```text
Room management server started...
```

---

## Run Client

In another terminal:

```bash
java -cp src/main/java edu.escuelaing.arsw.ejercicio23.SalonClient
```


---

# Results Analysis

The application successfully implemented TCP communication between a client and a server.

The server maintains the room state in memory and correctly responds according to the requested operation.

Synchronization was also applied to critical operations to avoid inconsistencies when multiple clients attempt to modify the state of the same room simultaneously.

---

# Evidence

## Server Execution

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/23.2.png)

## Client Execution

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/23.png)

---

# Reflection Questions

## How easy would it be to add a new operation to the protocol?

Adding a new operation would be simple because the protocol is based on text commands.

For example, a new operation:

```text
CHANGE_STATUS,E303
```

could be added by modifying the server logic and including a new condition in the command processor.

However, as the number of operations grows, it would be better to use a more formal protocol with structured messages.

---

## What happens if two clients try to reserve the same room at the same time?

If two clients attempt to reserve the same room simultaneously, a concurrency problem could occur.

To prevent this situation, synchronization was implemented on operations that modify the room state.

This guarantees that only one client can reserve the room first, while the second client receives:

```text
SALON_RESERVADO
```

---

## Where is the communication contract actually defined: in a formal file or in text conventions?

In this implementation, the communication contract is defined through text conventions.

The client and server must previously know the valid commands:

```text
OPERATION,ROOM
```

There is no formal contract file describing the protocol.

In more advanced systems such as gRPC, contracts are defined using `.proto` files, allowing more structured communication.

---

# Conclusions

1. TCP sockets allow the construction of distributed systems using the client-server model.
2. A clear communication protocol is essential for interaction between applications.
3. Shared state must be carefully managed to avoid concurrency problems.
4. Separating business logic from server communication improves system organization and maintainability.
5. This exercise represents an evolution from basic socket communication towards distributed services with more structured contracts.

---


