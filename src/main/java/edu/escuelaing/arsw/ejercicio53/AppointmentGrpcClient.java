package edu.escuelaing.arsw.ejercicio53;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Cliente gRPC de demostración para el Sistema de Bienestar Universitario.
 *
 * Ejecuta un flujo completo:
 *   1. Solicitar tres citas para dos estudiantes distintos.
 *   2. Consultar las citas activas de cada estudiante.
 *   3. Cancelar una cita.
 *   4. Verificar que la cita cancelada ya no aparezca como activa.
 */
public class AppointmentGrpcClient {

    public static void main(String[] args) throws InterruptedException {

        // ── Crear canal hacia el servidor ─────────────────────────────────
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        AppointmentServiceGrpc.AppointmentServiceBlockingStub stub =
                AppointmentServiceGrpc.newBlockingStub(channel);

        System.out.println("=================================================");
        System.out.println("  Cliente Bienestar Universitario - gRPC");
        System.out.println("=================================================\n");

        // ── 1. Solicitar citas ────────────────────────────────────────────

        System.out.println("--- Solicitando citas ---");

        AppointmentResponse r1 = stub.requestAppointment(
                AppointmentRequest.newBuilder()
                        .setStudentId(1)
                        .setServiceType(ServiceType.MEDICINE)
                        .setDate("2026-06-20T09:00")
                        .build());
        printResponse(r1);

        AppointmentResponse r2 = stub.requestAppointment(
                AppointmentRequest.newBuilder()
                        .setStudentId(1)
                        .setServiceType(ServiceType.PSYCHOLOGY)
                        .setDate("2026-06-22T10:30")
                        .build());
        printResponse(r2);

        AppointmentResponse r3 = stub.requestAppointment(
                AppointmentRequest.newBuilder()
                        .setStudentId(2)
                        .setServiceType(ServiceType.DENTISTRY)
                        .setDate("2026-06-21T14:00")
                        .build());
        printResponse(r3);

        // Caso de error: estudiante inexistente
        AppointmentResponse rErr = stub.requestAppointment(
                AppointmentRequest.newBuilder()
                        .setStudentId(99)
                        .setServiceType(ServiceType.MEDICINE)
                        .setDate("2026-06-25T08:00")
                        .build());
        printResponse(rErr);

        // ── 2. Consultar citas activas ────────────────────────────────────

        System.out.println("\n--- Citas activas del estudiante 1 ---");
        AppointmentList list1 = stub.getAppointments(
                StudentRequest.newBuilder().setStudentId(1).build());
        printList(list1);

        System.out.println("\n--- Citas activas del estudiante 2 ---");
        AppointmentList list2 = stub.getAppointments(
                StudentRequest.newBuilder().setStudentId(2).build());
        printList(list2);

        // ── 3. Cancelar una cita ─────────────────────────────────────────

        System.out.println("\n--- Cancelando cita #1 (estudiante 1) ---");
        int citaAcancelar = r1.getAppointment().getId();

        CancelResponse cancelOk = stub.cancelAppointment(
                CancelRequest.newBuilder()
                        .setAppointmentId(citaAcancelar)
                        .setStudentId(1)
                        .build());
        System.out.println("Resultado: " + cancelOk.getMessage());

        // Intento de cancelar con estudiante equivocado
        System.out.println("\n--- Intentando cancelar cita #1 con estudiante 2 (debe fallar) ---");
        CancelResponse cancelFail = stub.cancelAppointment(
                CancelRequest.newBuilder()
                        .setAppointmentId(citaAcancelar)
                        .setStudentId(2)
                        .build());
        System.out.println("Resultado: " + cancelFail.getMessage());

        // ── 4. Verificar que la cita cancelada no aparezca ───────────────

        System.out.println("\n--- Citas activas del estudiante 1 (post-cancelación) ---");
        AppointmentList list1After = stub.getAppointments(
                StudentRequest.newBuilder().setStudentId(1).build());
        printList(list1After);

        // ── Cerrar canal ──────────────────────────────────────────────────
        channel.shutdown();
        System.out.println("\nConexión cerrada.");
    }

    // ── Helpers de impresión ──────────────────────────────────────────────────

    private static void printResponse(AppointmentResponse r) {
        if (r.getSuccess()) {
            Appointment a = r.getAppointment();
            System.out.printf("  ✔ [Cita #%d] %s | %s | %s | Estado: %s%n",
                    a.getId(),
                    a.getServiceType(),
                    a.getDate(),
                    "Estudiante " + a.getStudentId(),
                    a.getStatus());
        } else {
            System.out.println("  ✘ Error: " + r.getMessage());
        }
    }

    private static void printList(AppointmentList list) {
        if (list.getAppointmentsCount() == 0) {
            System.out.println("  (Sin citas activas)");
            return;
        }
        for (Appointment a : list.getAppointmentsList()) {
            System.out.printf("  → Cita #%d | %s | %s | Estado: %s%n",
                    a.getId(), a.getServiceType(), a.getDate(), a.getStatus());
        }
    }
}
