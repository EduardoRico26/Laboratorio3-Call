package edu.escuelaing.arsw.ejercicio73;

import edu.escuelaing.arsw.ejercicio63.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.*;

public class WellnessGateway {

    private static final Map<String, List<String>> gymReservations = new HashMap<>();

    private static final Map<String, String> recreationResources = new HashMap<>();
    static {
        recreationResources.put("R01", "Cancha de futbol");
        recreationResources.put("R02", "Mesa de ping-pong");
        recreationResources.put("R03", "Sala de juegos");
    }
    private static final Map<String, Integer> reservedBy = new HashMap<>();

    public static void main(String[] args) {

        ManagedChannel appointmentChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051).usePlaintext().build();
        ManagedChannel medicalChannel = ManagedChannelBuilder
                .forAddress("localhost", 50052).usePlaintext().build();

        AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub =
                AppointmentServiceGrpc.newBlockingStub(appointmentChannel);
        MedicalServiceGrpc.MedicalServiceBlockingStub medicalStub =
                MedicalServiceGrpc.newBlockingStub(medicalChannel);

        System.out.println("=================================================");
        System.out.println("  WellnessGateway - Bienestar Universitario");
        System.out.println("  Punto de entrada unico a todos los servicios");
        System.out.println("=================================================\n");

        System.out.println("--- Operacion 1: requestAppointment ---");
        requestAppointment(appointmentStub, 1, ServiceType.MEDICINE, "2026-06-20T09:00");
        requestAppointment(appointmentStub, 1, ServiceType.PSYCHOLOGY, "2026-06-22T10:00");
        requestAppointment(appointmentStub, 2, ServiceType.DENTISTRY, "2026-06-21T14:00");
        System.out.println();

        System.out.println("--- Operacion 2: getStudentWellnessSummary (estudiante 1) ---");
        getStudentWellnessSummary(appointmentStub, medicalStub, 1);
        System.out.println();

        System.out.println("--- Operacion 3: reserveGymSession ---");
        reserveGymSession(1, "Lunes 07:00-08:00");
        reserveGymSession(2, "Martes 18:00-19:00");
        reserveGymSession(1, "Miercoles 07:00-08:00");
        System.out.println();

        System.out.println("--- Operacion 4: reserveRecreationResource ---");
        reserveRecreationResource(1, "R01");
        reserveRecreationResource(2, "R01");
        reserveRecreationResource(2, "R02");
        System.out.println();

        System.out.println("--- Resumen final - Estudiante 1 ---");
        getStudentWellnessSummary(appointmentStub, medicalStub, 1);

        appointmentChannel.shutdown();
        medicalChannel.shutdown();
        System.out.println("\nGateway cerrado.");
    }

    private static void requestAppointment(
            AppointmentServiceGrpc.AppointmentServiceBlockingStub stub,
            int studentId, ServiceType type, String date) {

        AppointmentResponse r = stub.requestAppointment(
                AppointmentRequest.newBuilder()
                        .setStudentId(studentId)
                        .setServiceType(type)
                        .setDate(date)
                        .build());

        System.out.printf("  [%s] Estudiante %d | %s | %s | %s%n",
                r.getSuccess() ? "OK" : "ERROR",
                studentId, type, date, r.getMessage());
    }

    private static void getStudentWellnessSummary(
            AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub,
            MedicalServiceGrpc.MedicalServiceBlockingStub medicalStub,
            int studentId) {

        System.out.println("  Citas activas (AppointmentService):");
        AppointmentList citas = appointmentStub.getAppointments(
                StudentRequest.newBuilder().setStudentId(studentId).build());

        if (citas.getAppointmentsCount() == 0) {
            System.out.println("    Sin citas activas");
        } else {
            for (Appointment a : citas.getAppointmentsList()) {
                SpecialtyResponse specialty = medicalStub.getSpecialty(
                        SpecialtyRequest.newBuilder()
                                .setType(mapToSpecialtyType(a.getServiceType()))
                                .build());

                String specialtyName = specialty.getFound()
                        ? specialty.getSpecialty().getName()
                        : a.getServiceType().name();

                System.out.printf("    Cita #%d | %s | %s | %s%n",
                        a.getId(), specialtyName, a.getDate(), a.getStatus());
            }
        }

        System.out.println("  Especialidades disponibles (MedicalService):");
        SpecialtyList especialidades = medicalStub.getAllSpecialties(
                AllSpecialtiesRequest.newBuilder().build());
        for (Specialty s : especialidades.getSpecialtiesList()) {
            System.out.printf("    %s - Cupos disponibles: %d%n",
                    s.getName(), s.getAvailableSlots());
        }

        System.out.println("  Sesiones de gimnasio (GymService):");
        List<String> sesiones = gymReservations.getOrDefault("student_" + studentId, List.of());
        if (sesiones.isEmpty()) {
            System.out.println("    Sin sesiones reservadas");
        } else {
            sesiones.forEach(s -> System.out.println("    " + s));
        }

        System.out.println("  Recursos recreativos (RecreationService):");
        boolean tieneRecurso = false;
        for (Map.Entry<String, Integer> e : reservedBy.entrySet()) {
            if (e.getValue() == studentId) {
                System.out.printf("    %s (%s)%n",
                        recreationResources.get(e.getKey()), e.getKey());
                tieneRecurso = true;
            }
        }
        if (!tieneRecurso) {
            System.out.println("    Sin recursos reservados");
        }
    }

    private static void reserveGymSession(int studentId, String timeSlot) {
        String key = "student_" + studentId;
        gymReservations.computeIfAbsent(key, k -> new ArrayList<>()).add(timeSlot);
        System.out.printf("  [OK] Estudiante %d | Gimnasio reservado: %s%n", studentId, timeSlot);
    }

    private static void reserveRecreationResource(int studentId, String resourceId) {
        if (!recreationResources.containsKey(resourceId)) {
            System.out.printf("  [ERROR] Recurso %s no existe.%n", resourceId);
            return;
        }
        if (reservedBy.containsKey(resourceId)) {
            System.out.printf("  [ERROR] %s (%s) ya esta reservado por estudiante %d.%n",
                    recreationResources.get(resourceId), resourceId, reservedBy.get(resourceId));
            return;
        }
        reservedBy.put(resourceId, studentId);
        System.out.printf("  [OK] Estudiante %d | Recurso reservado: %s (%s)%n",
                studentId, recreationResources.get(resourceId), resourceId);
    }

    private static SpecialtyType mapToSpecialtyType(ServiceType st) {
        return switch (st) {
            case MEDICINE   -> SpecialtyType.SPECIALTY_MEDICINE;
            case PSYCHOLOGY -> SpecialtyType.SPECIALTY_PSYCHOLOGY;
            case DENTISTRY  -> SpecialtyType.SPECIALTY_DENTISTRY;
            default         -> SpecialtyType.SPECIALTY_MEDICINE;
        };
    }
}