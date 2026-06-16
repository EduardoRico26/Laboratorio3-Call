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


# Exercise 3.3 - Room Management via HTTP

## Objective

Transform the previously developed room management system using TCP into a service based on HTTP.

The objective is to understand how a custom communication protocol can be replaced by clearer HTTP routes, allowing clients such as browsers, Postman, or tools like curl to interact with the server.

---

# Theoretical Framework

This exercise uses the HTTP protocol over a client-server architecture.

Unlike the previous TCP-based exercise, where custom messages were sent such as:

```
RESERVAR_SALON,E303
```

HTTP defines a structure based on:

* HTTP Method (GET, POST)
* Resource path
* Parameters

Example:

```
GET /rooms?id=E303
```

The communication is performed through HTTP requests and responses, where the server processes the request and returns a response in plain text or HTML.

---

# Exercise Development

An HTTP server was implemented using Java basic classes:

* `ServerSocket`
* `Socket`
* Input and output streams

---

# System Operation

The server maintains the rooms in memory:

```
E301
E302
E303
E304
```

Each room can have two states:

```
AVAILABLE
RESERVED
```

---

# Execution Commands

## Compilation

From the project root:

```bash
javac src/main/java/edu/escuelaing/arsw/ejercicio33/*.java
```

---

## Running the server

```bash
java -cp src/main/java edu.escuelaing.arsw.ejercicio33.HttpSalonServer
```

Expected output:

```
HTTP Salon Server running...
```

The server remains waiting for requests.

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/336.png)

---

# Tests Performed

## Query all rooms

Request:

```
GET /rooms
```

Example:

```
http://localhost:35000/rooms
```

Response:

```
E301 : AVAILABLE
E302 : AVAILABLE
E303 : AVAILABLE
E304 : AVAILABLE
```

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/331.png)

---

## Query a specific room

Request:

```
GET /rooms?id=E303
```

Example:

```
http://localhost:35000/rooms?id=E303
```

Response:

```
ROOM_AVAILABLE
```

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/332.png)

---

## Reserve a room

Request:

```
POST /rooms/reserve?id=E303
```

Command:

```bash
curl.exe -X POST "http://localhost:35000/rooms/reserve?id=E303"
```

Response:

```
RESERVATION_SUCCESSFUL
```
![alt text](src/main/java/edu/escuelaing/arsw/imagenes/333.png)

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/334.png)

---

## Release a room

Request:

```
POST /rooms/release?id=E303
```

Command:

```bash
curl.exe -X POST "http://localhost:35000/rooms/release?id=E303"
```

Response:

```
RELEASE_SUCCESSFUL
```

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/335.png)

---

# Analysis

The implementation demonstrates how a client-server system can evolve from a custom text-based protocol into an HTTP-based architecture.

HTTP provides a more organized way to define operations through routes and methods, making the service easier to consume from different clients.

The server keeps the business logic separated from the communication handling, allowing better code organization.

---

# Reflection Questions

## What advantages does HTTP offer compared to a manually defined text protocol?

HTTP provides a standard communication structure that is already supported by multiple clients such as browsers, testing tools, and applications.

Additionally, it clearly separates:

* The action through the HTTP method.
* The resource through the URL.
* The parameters through queries.

For example:

```
POST /rooms/reserve?id=E303
```

is more descriptive than:

```
RESERVAR_SALON,E303
```

It also makes integration with other systems easier.

---

## What limitations does building an HTTP server without a framework have?

Building an HTTP server manually helps understand how communication works, but it has several limitations:

* HTTP requests must be interpreted manually.
* There are no automatic validations.
* Error handling becomes more complex.
* It does not include advanced features such as security, session management, or automatic serialization.

Frameworks such as Spring Boot simplify these tasks and allow the development of more robust services.

---

## How would this solution change if JSON was used instead of HTML?

If JSON was used, responses would contain structured data instead of HTML pages.

Example:

```json
{
  "room": "E303",
  "status": "RESERVED"
}
```

This would allow other programs to consume the information easily.

Additionally, JSON facilitates communication between distributed applications because it does not depend on a visual interface.

---

# Conclusions

1. HTTP allows the construction of more flexible and easier-to-consume services than custom protocols.
2. HTTP methods and routes work as a communication contract between client and server.
3. Separating business logic from server communication improves system evolution.
4. Manual implementation allows understanding the internal operation of HTTP.
5. In real applications, using frameworks and structured formats such as JSON would be recommended.

# Exercise 4.3 - Laboratory Inventory using RMI

## Objective

Implement a distributed system using **RMI (Remote Method Invocation)** to manage a laboratory equipment inventory.

The objective is to design a remote interface that allows a client to query, reserve, and release equipment located on a remote server, avoiding the use of text-based communication protocols.

---

# Theoretical Framework

RMI (Remote Method Invocation) is a Java technology that allows invoking methods from an object running in another machine or process.

Unlike previous exercises where custom protocols were implemented using TCP or HTTP routes, in RMI the client works with a remote interface and invokes methods as if the object were locally available.

The communication contract is defined through an interface that extends:

```java id="7z6n4k"
java.rmi.Remote
```

and its methods must declare:

```java id="m4w4l2"
throws RemoteException
```

The architecture used is:

```id="sxh0s9"
RMI Client
      |
      |
Remote Interface
      |
      |
Remote Object
      |
      |
Server
```

---

# Exercise Development

A laboratory inventory system was implemented using RMI.

---

# System Operation

The server maintains the available equipment in memory:

```id="8n8t9d"
EQ01 - Oscilloscope - Electronics Lab
EQ02 - Multimeter - Electronics Lab
EQ03 - Arduino - Systems Lab
```

Each equipment item contains:

* Equipment code
* Equipment name
* Laboratory
* Status

Possible states:

```id="5e8b7t"
AVAILABLE
RESERVED
```

---

# Remote Interface

The communication contract was defined through the remote interface:

```java id="8m0wha"
InventarioRemote
```

Available methods:

```java id="79txjd"
List<String> consultarEquipos()

String consultarEquipo(String codigo)

boolean reservarEquipo(String codigo)

boolean liberarEquipo(String codigo)
```

These methods can be invoked remotely by the client.

---

# Execution Commands

## Compilation

From the project root:

```bash id="y7zj25"
javac src/main/java/edu/escuelaing/arsw/ejercicio43/*.java
```

---

## Run RMI Server

In a first terminal:

```bash id="yq5r6p"
java -cp src/main/java edu.escuelaing.arsw.ejercicio43.InventarioServer
```

Expected output:

```id="m0h0i2"
RMI Inventory Server running...
```

The server remains published waiting for client connections.

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/431.png)

---

## Run RMI Client

In a second terminal:

```bash id="o3z6gx"
java -cp src/main/java edu.escuelaing.arsw.ejercicio43.InventarioClient
```


---

# Tests Performed

## Query all equipment

Request:

```id="k19xqf"
consultarEquipos()
```

Response:

```id="n5x7tw"
EQ01 - Oscilloscope - Electronics Lab - AVAILABLE

EQ02 - Multimeter - Electronics Lab - AVAILABLE

EQ03 - Arduino - Systems Lab - AVAILABLE
```

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/432.png)

---

## Query specific equipment

Request:

```id="qj3j8s"
consultarEquipo("EQ01")
```

Response:

```id="k3z8fa"
EQ01 - Oscilloscope - Electronics Lab - AVAILABLE
```

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/433.png)

---

## Reserve equipment

Request:

```id="qg6v8b"
reservarEquipo("EQ01")
```

Response:

```id="2a8m3d"
true
```

After reservation:

```id="9w7d4x"
EQ01 - Oscilloscope - Electronics Lab - RESERVED
```

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/434.png)

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/435.png)

---

## Release equipment

Request:

```id="3v0k8m"
liberarEquipo("EQ01")
```

Response:

```id="5q8w4m"
true
```

Final state:

```id="4c5x9r"
EQ01 - Oscilloscope - Electronics Lab - AVAILABLE
```

---

# Analysis

The implementation demonstrates the difference between message-based communication and object-oriented communication.

With RMI, the client does not need to know how the server logic is implemented. It only needs to know the remote interface.

This allows a more organized design, where the communication contract is defined through Java methods.

Synchronization was also applied to reservation and release operations to prevent inconsistencies when multiple clients try to modify the same equipment simultaneously.

---

# Reflection Questions

## What changed when moving from HTTP to RMI?

The main change is the communication model.

In HTTP, the client sends requests using routes:

```id="q9r8vp"
POST /rooms/reserve?id=E303
```

and the server manually interprets these requests.

In RMI, the client directly invokes methods from a remote object:

```java id="x8k4q2"
reservarEquipo("EQ01")
```

This makes communication closer to the object-oriented programming paradigm.

---

## Where is the communication contract defined?

The contract is defined in the remote interface:

```java id="7m8c2x"
InventarioRemote
```

This interface specifies the available methods and their parameters.

Unlike previous exercises where the contract was based on text conventions, here there is a formal definition in code.

---

## What problems would this system have if a client was not written in Java?

RMI is mainly designed for Java applications.

A client developed in another programming language would have difficulties because it needs to understand:

* The remote interface.
* Java serialization.
* The internal RMI communication protocol.

For heterogeneous systems, technologies such as:

* REST
* HTTP
* JSON
* gRPC

would be more appropriate because they support communication between different languages.

---

# Conclusions

1. RMI allows the construction of distributed systems using remote object invocation.
2. The remote interface works as a formal contract between client and server.
3. Compared with HTTP, RMI reduces the need to manually interpret messages.
4. Shared state management requires synchronization to avoid concurrency problems.
5. For systems involving different technologies, standard protocols such as REST or JSON are usually more suitable.

---


