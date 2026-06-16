package edu.escuelaing.arsw.ejercicio53;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servidor gRPC para el Sistema de Bienestar Universitario.
 *
 * Gestiona citas de servicios: MEDICINE, PSYCHOLOGY, DENTISTRY.
 * Toda la información se mantiene en memoria.
 */
public class AppointmentGrpcServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50052)
                .addService(new AppointmentServiceImpl())
                .build();

        server.start();
        System.out.println("=================================================");
        System.out.println("  Bienestar Universitario - gRPC Server");
        System.out.println("  Escuchando en puerto 50052");
        System.out.println("=================================================");

        // Gancho para apagar limpiamente con Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nApagando servidor...");
            server.shutdown();
        }));

        server.awaitTermination();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Implementación del servicio
    // ─────────────────────────────────────────────────────────────────────────

    static class AppointmentServiceImpl
            extends AppointmentServiceGrpc.AppointmentServiceImplBase {

        /** Almacén en memoria: appointmentId → Appointment */
        private final Map<Integer, Appointment> appointmentsStore = new HashMap<>();

        /** Almacén de estudiantes registrados (precargados para demo) */
        private final Map<Integer, Student> studentsStore = new HashMap<>();

        /** Contador autoincremental de IDs para citas */
        private final AtomicInteger idCounter = new AtomicInteger(1);

        public AppointmentServiceImpl() {
            // Estudiantes de ejemplo
            studentsStore.put(1, Student.newBuilder()
                    .setId(1)
                    .setName("Ana García")
                    .setInstitutionalEmail("ana.garcia@eci.edu.co")
                    .build());

            studentsStore.put(2, Student.newBuilder()
                    .setId(2)
                    .setName("Carlos López")
                    .setInstitutionalEmail("carlos.lopez@eci.edu.co")
                    .build());

            studentsStore.put(3, Student.newBuilder()
                    .setId(3)
                    .setName("María Torres")
                    .setInstitutionalEmail("maria.torres@eci.edu.co")
                    .build());

            System.out.println("Estudiantes de prueba registrados: " + studentsStore.size());
        }

        // ── 1. RequestAppointment ─────────────────────────────────────────────

        @Override
        public void requestAppointment(AppointmentRequest request,
                                       StreamObserver<AppointmentResponse> responseObserver) {

            int studentId = request.getStudentId();

            // Validar que el estudiante exista
            if (!studentsStore.containsKey(studentId)) {
                AppointmentResponse response = AppointmentResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Estudiante con ID " + studentId + " no encontrado.")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            // Validar que la fecha no esté vacía
            if (request.getDate() == null || request.getDate().isBlank()) {
                AppointmentResponse response = AppointmentResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("La fecha de la cita no puede estar vacía.")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            // Crear la cita con estado REQUESTED
            int newId = idCounter.getAndIncrement();
            Appointment appointment = Appointment.newBuilder()
                    .setId(newId)
                    .setStudentId(studentId)
                    .setServiceType(request.getServiceType())
                    .setDate(request.getDate())
                    .setStatus(AppointmentStatus.REQUESTED)
                    .build();

            appointmentsStore.put(newId, appointment);

            System.out.printf("[REQUEST] Cita #%d creada - Estudiante %d - Servicio: %s - Fecha: %s%n",
                    newId, studentId, request.getServiceType(), request.getDate());

            AppointmentResponse response = AppointmentResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Cita solicitada exitosamente. ID asignado: " + newId)
                    .setAppointment(appointment)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        // ── 2. CancelAppointment ─────────────────────────────────────────────

        @Override
        public void cancelAppointment(CancelRequest request,
                                      StreamObserver<CancelResponse> responseObserver) {

            int appointmentId = request.getAppointmentId();
            int studentId     = request.getStudentId();

            Appointment existing = appointmentsStore.get(appointmentId);

            // Validar existencia
            if (existing == null) {
                responseObserver.onNext(CancelResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("La cita #" + appointmentId + " no existe.")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            // Validar propietario
            if (existing.getStudentId() != studentId) {
                responseObserver.onNext(CancelResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("El estudiante " + studentId
                                + " no es el dueño de la cita #" + appointmentId + ".")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            // Validar que no esté ya cancelada
            if (existing.getStatus() == AppointmentStatus.CANCELLED) {
                responseObserver.onNext(CancelResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("La cita #" + appointmentId + " ya estaba cancelada.")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            // Cambiar estado a CANCELLED (reconstruir inmutable)
            Appointment cancelled = existing.toBuilder()
                    .setStatus(AppointmentStatus.CANCELLED)
                    .build();
            appointmentsStore.put(appointmentId, cancelled);

            System.out.printf("[CANCEL] Cita #%d cancelada por estudiante %d%n",
                    appointmentId, studentId);

            responseObserver.onNext(CancelResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Cita #" + appointmentId + " cancelada exitosamente.")
                    .build());
            responseObserver.onCompleted();
        }

        // ── 3. GetAppointments ───────────────────────────────────────────────

        @Override
        public void getAppointments(StudentRequest request,
                                    StreamObserver<AppointmentList> responseObserver) {

            int studentId = request.getStudentId();

            // Filtrar citas del estudiante que NO estén canceladas
            List<Appointment> activeAppointments = new ArrayList<>();
            for (Appointment a : appointmentsStore.values()) {
                if (a.getStudentId() == studentId
                        && a.getStatus() != AppointmentStatus.CANCELLED) {
                    activeAppointments.add(a);
                }
            }

            System.out.printf("[GET] Estudiante %d tiene %d cita(s) activa(s)%n",
                    studentId, activeAppointments.size());

            AppointmentList list = AppointmentList.newBuilder()
                    .addAllAppointments(activeAppointments)
                    .build();

            responseObserver.onNext(list);
            responseObserver.onCompleted();
        }
    }
}
