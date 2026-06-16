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

# Exercise 5.3 - University Welfare System with gRPC

## Objective

Design and implement a gRPC service for managing university welfare appointment requests.

The objective of this exercise is to understand how to model a communication contract using Protocol Buffers and implement a distributed client-server system based on gRPC instead of manually defined text protocols.

The system allows students to request, cancel, and consult welfare appointments.

---

# Theoretical Framework

gRPC is a modern remote procedure call framework that allows communication between distributed applications.

Unlike previous approaches such as TCP and HTTP, where the communication structure was manually defined, gRPC uses a contract file (`.proto`) that describes:

* Available services.
* Remote methods.
* Request messages.
* Response messages.
* Data structures.

The communication contract is defined using Protocol Buffers, which automatically generates the necessary classes for the client and server.

Example:

```proto
service AppointmentService {

  rpc RequestAppointment (AppointmentRequest)
      returns (AppointmentResponse);

  rpc CancelAppointment (CancelRequest)
      returns (CancelResponse);

  rpc GetAppointments (StudentRequest)
      returns (AppointmentList);
}
```

This allows different applications to communicate using strongly typed messages.

---

# Exercise Development

A gRPC service was implemented for the University Welfare System.

The project contains:

* A `.proto` file defining the communication contract.
* A gRPC server.
* A gRPC client.
* An in-memory data management system.

The generated classes are created automatically by Maven during compilation.

---

# System Description

The system manages university welfare appointments.

The available services are:

```
MEDICINE
PSYCHOLOGY
DENTISTRY
```

Each appointment contains:

```
id
studentId
serviceType
date
status
```

The possible appointment states are:

```
REQUESTED
CANCELLED
ATTENDED
```

---

# Implemented Operations

## RequestAppointment

Allows a student to request a new appointment.

Rules applied:

* The student must exist.
* The date cannot be empty.
* New appointments start with status:

```
REQUESTED
```

Example response:

```
Appointment created successfully
```

---

## CancelAppointment

Allows canceling an existing appointment.

Validations:

* The appointment must exist.
* The student must own the appointment.
* The appointment cannot already be cancelled.

After cancellation the state changes to:

```
CANCELLED
```

Cancelled appointments are not returned as active.

---

## GetAppointments

Allows consulting active appointments from a student.

Only appointments that are not cancelled are returned.

Example:

```
Appointment #1
Service: MEDICINE
Date: 2026-06-20T09:00
Status: REQUESTED
```

---

# Execution Commands

## Compile the project

From the project root:

```bash
mvn clean compile
```

This command:

* Downloads dependencies.
* Generates gRPC classes.
* Compiles the project.

---

# Run Server

Execute:

```bash
mvn exec:java "-Dexec.mainClass=edu.escuelaing.arsw.ejercicio53.AppointmentGrpcServer"
```

Expected output:

```
=================================================
  University Welfare - gRPC Server
  Listening on port 50052
=================================================
```

The server remains waiting for client requests.

---

# Run Client

Open another terminal and execute:

```bash
mvn exec:java "-Dexec.mainClass=edu.escuelaing.arsw.ejercicio53.AppointmentGrpcClient"
```

The client performs:

1. Appointment requests.
2. Appointment queries.
3. Appointment cancellation.
4. Verification after cancellation.

---

# Evidence

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/531.png)

![alt text](src/main/java/edu/escuelaing/arsw/imagenes/532.png)

---

# Analysis

The implementation demonstrates how gRPC simplifies communication between distributed applications.

Compared with previous implementations using TCP and HTTP, gRPC provides:

* A predefined communication contract.
* Automatic message serialization.
* Generated client and server code.
* Strong typing.

The business logic remains separated from the communication layer, improving the organization and maintainability of the system.

---

# Reflection Questions

## Why is the `.proto` file considered a communication contract?

The `.proto` file defines the exact structure of the communication between client and server.

It specifies:

* Available operations.
* Required parameters.
* Response formats.
* Data types.

Both client and server depend on this definition, which guarantees that both sides understand how to communicate.

---

## How easy would it be to create a client in another language?

It would be relatively easy because gRPC supports multiple programming languages.

A client could be generated in languages such as:

* Java
* Python
* Go
* C#
* JavaScript

The `.proto` file is shared, and each language generates its own client classes automatically.

---

## What differences are found between RMI and gRPC?

RMI is Java-specific and allows remote invocation between Java applications.

gRPC is language-independent and uses Protocol Buffers for communication.

Main differences:

| RMI                       | gRPC                             |
| ------------------------- | -------------------------------- |
| Only Java                 | Multiple languages               |
| Uses Java objects         | Uses serialized messages         |
| Requires Java environment | Uses generated clients           |
| Tightly coupled to JVM    | Designed for distributed systems |

gRPC is more suitable for modern distributed architectures.

---

# Conclusions

1. gRPC provides a structured way to build distributed applications.
2. The `.proto` file works as a formal communication contract.
3. Protocol Buffers simplify message exchange between systems.
4. The generated code reduces manual implementation errors.
5. gRPC allows interoperability between different programming languages.
6. Compared with RMI, gRPC provides a more flexible solution for modern distributed applications.

---

# Bibliography

1. Benavides, L. D., & Gualtero, R. H. (2026). *Introducción a esquemas de nombres, redes, clientes y servicios con Java* [Laboratory Guide]. Escuela Colombiana de Ingeniería Julio Garavito.

2. Benavides, L. D. (2026). *Connectors and Components (C&C) – Call and Return Styles* [Class Presentation]. Escuela Colombiana de Ingeniería Julio Garavito.

3. OpenAI. (2026). *ChatGPT (GPT-5.5 version) [Large Language Model].* https://chatgpt.com/

4. gRPC Authors. (2026). *gRPC Documentation.* https://grpc.io/docs/

# Exercise 6.3 - University Wellness Decomposition with gRPC

## Objective

Based on the previous University Wellness appointment system developed with gRPC, an initial microservices architecture was implemented.

The main objective is to separate system responsibilities into independent services that can run and communicate remotely.

---

# Theoretical Framework

A microservices architecture consists of dividing a large application into smaller independent services, where each service has a specific responsibility.

In this exercise, gRPC is used as the communication mechanism between services, using `.proto` files as communication contracts.

Each service exposes remote methods that can be consumed by external clients.

---

# Microservices Design

The solution was divided into the following services:

```
                 +----------------+
                 | WellnessClient |
                 +-------+--------+
                         |
          +--------------+--------------+
          |                             |
          v                             v

+---------------------+       +---------------------+
| AppointmentService  |       |    MedicalService   |
|       :50051        |       |       :50052        |
+---------------------+       +---------------------+

Manages appointments       Manages available
and schedules              medical specialties
```

---

# Implemented Services

## AppointmentService

Port:

```
50051
```

Responsibility:

Manages university wellness appointments.

Available operations:

```proto
rpc RequestAppointment
rpc CancelAppointment
rpc GetAppointments
```

Managed data:

```
Appointment
-------------
id
studentId
serviceType
date
status
```

Possible states:

```
REQUESTED
CANCELLED
ATTENDED
```

This service stores appointment information in memory.

---

## MedicalService

Port:

```
50052
```

Responsibility:

Manages basic information about available medical specialties.

Available operations:

```proto
rpc GetSpecialty
rpc GetAllSpecialties
```

Managed data:

```
Specialty
-------------
id
type
name
description
availableSlots
```

Available specialties:

```
MEDICINE
PSYCHOLOGY
DENTISTRY
```

---

# gRPC Contracts

Two `.proto` files were implemented.

## AppointmentProto

Defines:

```
AppointmentService
Student
Appointment
AppointmentRequest
AppointmentResponse
CancelRequest
CancelResponse
```

---

## MedicalProto

Defines:

```
MedicalService
Specialty
SpecialtyRequest
SpecialtyResponse
SpecialtyList
```

The `.proto` files work as communication contracts between client and server, allowing the automatic generation of the required communication classes.

---

# Project Execution

## Compilation

From the project root:

```bash
mvn clean compile
```

Expected result:

```
BUILD SUCCESS
```

---

# Running the Services

Different terminals must be opened.

---

## Terminal 1 - AppointmentService

Run:

```bash
mvn exec:java "-Dexec.mainClass=edu.escuelaing.arsw.ejercicio63.AppointmentServer"
```

Expected output:

```
AppointmentService iniciado en puerto 50051
```


![alt text](src/main/java/edu/escuelaing/arsw/imagenes/631.png)

---

## Terminal 2 - MedicalService

Run:

```bash
mvn exec:java "-Dexec.mainClass=edu.escuelaing.arsw.ejercicio63.MedicalServer"
```

Expected output:

```
MedicalService iniciado en puerto 50052
```


![alt text](src/main/java/edu/escuelaing/arsw/imagenes/632.png)

---

## Terminal 3 - Client

Run:

```bash
mvn exec:java "-Dexec.mainClass=edu.escuelaing.arsw.ejercicio63.WellnessClient"
```


![alt text](src/main/java/edu/escuelaing/arsw/imagenes/633.png)

---

# Performed Tests

The client directly consumes both implemented services.

---

## Specialty Query

Request sent to:

```
MedicalService :50052
```

Response:

```
Dentistry | Oral health and dental procedures | Slots: 4

Psychology | Emotional support and mental health | Slots: 6

General Medicine | General consultations and minor emergencies | Slots: 10
```

---

## Query Specific Specialty

Request:

```
PSYCHOLOGY
```

Response:

```
Psychology - Emotional support and mental health
Available slots: 6
```

---

## Appointment Creation

Request sent to:

```
AppointmentService :50051
```

Result:

```
Appointment #1 created.

Appointment #2 created.
```

Appointments are stored with:

```
REQUESTED
```

status.

---

## Active Appointment Query

Student query:

```
studentId = 1
```

Response:

```
Appointment #1 | PSYCHOLOGY | 2026-06-20T10:00 | REQUESTED
```

---

# Analysis

The solution demonstrates an initial separation of the university wellness system.

Each service has a specific responsibility:

- AppointmentService manages appointment-related logic.
- MedicalService manages medical information.

Communication is performed using gRPC with contracts defined through protobuf files.

Each service can run independently on a different port.

---

# Reflection Questions

## Why did you decide to separate these services and not others?

The services were separated because they represent different responsibilities inside the system.

Appointments require operations such as:

- Creating requests.
- Cancelling appointments.
- Querying schedules.

Medical information only requires managing specialties and availability.

Separating them allows each service to evolve independently.

---

## What data belongs to each service?

### AppointmentService

Manages:

```
Appointment
Student
ServiceType
AppointmentStatus
```

Its main responsibility is appointment management.

---

### MedicalService

Manages:

```
Specialty
SpecialtyType
AvailableSlots
```

Its information is related to available medical services.

---

## What risk appears when the client knows all services?

If the client directly knows every service, the system becomes more coupled.

Possible problems:

- The client must know every port.
- Internal changes can affect the client.
- Services lose some independence.

In real architectures, an API Gateway is usually added as a single entry point.

---

# Conclusions

1. Microservices architecture allows large systems to be divided into independent components.

2. gRPC simplifies communication between services through protobuf-defined contracts.

3. Separating responsibilities improves code organization and maintenance.

4. Each service can run independently.

5. A client can consume multiple remote services using different communication channels.

6. A future improvement would be adding more services such as GymService and RecreationService following the same architecture.
