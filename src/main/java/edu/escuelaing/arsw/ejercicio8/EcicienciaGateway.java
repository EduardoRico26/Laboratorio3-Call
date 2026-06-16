package edu.escuelaing.arsw.ejercicio8;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class EcicienciaGateway {

    // Canales hacia cada microservicio
    private final AttendeeServiceGrpc.AttendeeServiceBlockingStub  attendeeStub;
    private final AgendaServiceGrpc.AgendaServiceBlockingStub      agendaStub;
    private final WorkshopServiceGrpc.WorkshopServiceBlockingStub  workshopStub;
    private final CapacityServiceGrpc.CapacityServiceBlockingStub  capacityStub;

    public EcicienciaGateway() {
        ManagedChannel attendeeChannel = ManagedChannelBuilder
                .forAddress("localhost", 50071).usePlaintext().build();
        ManagedChannel agendaChannel = ManagedChannelBuilder
                .forAddress("localhost", 50072).usePlaintext().build();
        ManagedChannel workshopChannel = ManagedChannelBuilder
                .forAddress("localhost", 50073).usePlaintext().build();
        ManagedChannel capacityChannel = ManagedChannelBuilder
                .forAddress("localhost", 50074).usePlaintext().build();

        attendeeStub  = AttendeeServiceGrpc.newBlockingStub(attendeeChannel);
        agendaStub    = AgendaServiceGrpc.newBlockingStub(agendaChannel);
        workshopStub  = WorkshopServiceGrpc.newBlockingStub(workshopChannel);
        capacityStub  = CapacityServiceGrpc.newBlockingStub(capacityChannel);
    }

    // ── Operación 1: Registrar asistente ──────────────────────────────────
    public String registerAttendee(String name, String email, String type) {
        RegisterResponse r = attendeeStub.registerAttendee(
                RegisterRequest.newBuilder()
                        .setName(name).setEmail(email).setType(type).build());
        return r.getMessage() + " (ID: " + r.getAttendeeId() + ")";
    }

    // ── Operación 2: Consultar agenda completa ────────────────────────────
    public void printFullAgenda() {
        ActivityList list = agendaStub.getFullAgenda(
                EmptyAgendaRequest.newBuilder().build());
        System.out.println("\n===== AGENDA ECICIENCIA =====");
        for (Activity a : list.getActivitiesList()) {
            System.out.printf("[%s] %s - %s | Ponente: %s | Lugar: %s%n",
                    a.getTimeSlot(), a.getType(), a.getTitle(),
                    a.getSpeaker(), a.getLocation());
        }
    }

    // ── Operación 3: Consultar agenda por franja ──────────────────────────
    public void printAgendaBySlot(String slot) {
        ActivityList list = agendaStub.getActivitiesBySlot(
                TimeSlotRequest.newBuilder().setTimeSlot(slot).build());
        System.out.println("\n===== ACTIVIDADES EN FRANJA " + slot + " =====");
        if (list.getActivitiesCount() == 0) {
            System.out.println("No hay actividades en esa franja.");
            return;
        }
        for (Activity a : list.getActivitiesList()) {
            System.out.printf("  - %s (%s) | %s%n",
                    a.getTitle(), a.getType(), a.getLocation());
        }
    }

    // ── Operación 4: Reservar cupo en taller ─────────────────────────────
    public String reserveWorkshopSpot(String attendeeId, String workshopId) {
        ReserveSpotResponse r = workshopStub.reserveSpot(
                ReserveSpotRequest.newBuilder()
                        .setAttendeeId(attendeeId).setWorkshopId(workshopId).build());
        return r.getMessage();
    }

    // ── Operación 5: Ver talleres disponibles ─────────────────────────────
    public void printWorkshops() {
        WorkshopList list = workshopStub.getWorkshops(
                EmptyWorkshopRequest.newBuilder().build());
        System.out.println("\n===== TALLERES =====");
        for (Workshop w : list.getWorkshopsList()) {
            int available = w.getCapacity() - w.getReserved();
            System.out.printf("[%s] %s - %s | Cupos disponibles: %d/%d%n",
                    w.getWorkshopId(), w.getTitle(), w.getTimeSlot(),
                    available, w.getCapacity());
        }
    }

    // ── Operación 6: Consultar aforo de actividad ─────────────────────────
    public void printCapacity(String activityId) {
        CapacityResponse r = capacityStub.getCapacityStatus(
                CapacityRequest.newBuilder().setActivityId(activityId).build());
        System.out.printf("%nAforo %s: %d/%d | %s%n",
                activityId, r.getCurrent(), r.getMaxCapacity(),
                r.getAvailable() ? "DISPONIBLE" : "LLENO");
    }

    // ── Operación 7: Registrar entrada a actividad ────────────────────────
    public String registerEntry(String activityId, String attendeeId) {
        EntryResponse r = capacityStub.registerEntry(
                EntryRequest.newBuilder()
                        .setActivityId(activityId).setAttendeeId(attendeeId).build());
        return r.getMessage();
    }

    // ── Menú principal ────────────────────────────────────────────────────
    public static void main(String[] args) {
        EcicienciaGateway gateway = new EcicienciaGateway();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║   GATEWAY - PLATAFORMA ECICIENCIA ║");
        System.out.println("╚══════════════════════════════════╝");

        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Registrar asistente");
            System.out.println("2. Ver agenda completa");
            System.out.println("3. Ver actividades por franja horaria");
            System.out.println("4. Ver talleres disponibles");
            System.out.println("5. Reservar cupo en taller");
            System.out.println("6. Consultar aforo de actividad");
            System.out.println("7. Registrar entrada a actividad");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            String op = sc.nextLine().trim();
            switch (op) {
                case "1" -> {
                    System.out.print("Nombre: ");      String name  = sc.nextLine();
                    System.out.print("Email: ");       String email = sc.nextLine();
                    System.out.print("Tipo (STUDENT/PROFESSOR/EXTERNAL): ");
                    String type = sc.nextLine();
                    System.out.println(gateway.registerAttendee(name, email, type));
                }
                case "2" -> gateway.printFullAgenda();
                case "3" -> {
                    System.out.print("Franja (ej: 09:00): ");
                    gateway.printAgendaBySlot(sc.nextLine().trim());
                }
                case "4" -> gateway.printWorkshops();
                case "5" -> {
                    System.out.print("Tu ID de asistente: ");  String aid = sc.nextLine();
                    System.out.print("ID del taller (WS-1/WS-2/WS-3): ");
                    String wid = sc.nextLine();
                    System.out.println(gateway.reserveWorkshopSpot(aid, wid));
                }
                case "6" -> {
                    System.out.print("ID de actividad (ACT-1 ... ACT-5): ");
                    gateway.printCapacity(sc.nextLine().trim());
                }
                case "7" -> {
                    System.out.print("ID actividad: ");  String actId = sc.nextLine();
                    System.out.print("Tu ID asistente: "); String attId = sc.nextLine();
                    System.out.println(gateway.registerEntry(actId, attId));
                }
                case "0" -> running = false;
                default  -> System.out.println("Opción inválida.");
            }
        }
        System.out.println("Gateway cerrado.");
    }
}