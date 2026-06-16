package edu.escuelaing.arsw.ejercicio63;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class WellnessClient {

    public static void main(String[] args) {

        // Canal al AppointmentService (puerto 50051)
        ManagedChannel appointmentChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051).usePlaintext().build();

        // Canal al MedicalService (puerto 50052)
        ManagedChannel medicalChannel = ManagedChannelBuilder
                .forAddress("localhost", 50052).usePlaintext().build();

        AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub =
                AppointmentServiceGrpc.newBlockingStub(appointmentChannel);

        MedicalServiceGrpc.MedicalServiceBlockingStub medicalStub =
                MedicalServiceGrpc.newBlockingStub(medicalChannel);

        System.out.println("=================================================");
        System.out.println("  Cliente Bienestar Universitario - Microservicios");
        System.out.println("=================================================\n");

        // 1. Consultar especialidades disponibles
        System.out.println("--- Especialidades disponibles (MedicalService :50052) ---");
        SpecialtyList lista = medicalStub.getAllSpecialties(AllSpecialtiesRequest.newBuilder().build());
        for (Specialty s : lista.getSpecialtiesList()) {
            System.out.printf("  → %s | %s | Cupos: %d%n",
                    s.getName(), s.getDescription(), s.getAvailableSlots());
        }

        // 2. Consultar una especialidad específica
        System.out.println("\n--- Detalle de Psicología (MedicalService :50052) ---");
        SpecialtyResponse resp = medicalStub.getSpecialty(
                SpecialtyRequest.newBuilder().setType(SpecialtyType.SPECIALTY_PSYCHOLOGY).build());
        if (resp.getFound()) {
            Specialty s = resp.getSpecialty();
            System.out.printf("  ✔ %s - %s (Cupos disponibles: %d)%n",
                    s.getName(), s.getDescription(), s.getAvailableSlots());
        }

        // 3. Solicitar citas (AppointmentService)
        System.out.println("\n--- Solicitando citas (AppointmentService :50051) ---");
        AppointmentResponse r1 = appointmentStub.requestAppointment(
                AppointmentRequest.newBuilder()
                        .setStudentId(1).setServiceType(ServiceType.PSYCHOLOGY).setDate("2026-06-20T10:00")
                        .build());
        System.out.println("  " + (r1.getSuccess() ? "✔" : "✘") + " " + r1.getMessage());

        AppointmentResponse r2 = appointmentStub.requestAppointment(
                AppointmentRequest.newBuilder()
                        .setStudentId(2).setServiceType(ServiceType.MEDICINE).setDate("2026-06-21T09:00")
                        .build());
        System.out.println("  " + (r2.getSuccess() ? "✔" : "✘") + " " + r2.getMessage());

        // 4. Consultar citas del estudiante 1
        System.out.println("\n--- Citas activas estudiante 1 (AppointmentService :50051) ---");
        AppointmentList citas = appointmentStub.getAppointments(
                StudentRequest.newBuilder().setStudentId(1).build());
        for (Appointment a : citas.getAppointmentsList()) {
            System.out.printf("  → Cita #%d | %s | %s | %s%n",
                    a.getId(), a.getServiceType(), a.getDate(), a.getStatus());
        }

        appointmentChannel.shutdown();
        medicalChannel.shutdown();
        System.out.println("\nConexiones cerradas.");
    }
}