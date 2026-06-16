package edu.escuelaing.arsw.ejercicio8;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class AgendaServiceServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50072)
                .addService(new AgendaServiceImpl())
                .build();
        server.start();
        System.out.println("AgendaService iniciado en puerto 50072");
        server.awaitTermination();
    }

    static class AgendaServiceImpl extends AgendaServiceGrpc.AgendaServiceImplBase {

        private final List<Activity> activities = new ArrayList<>();

        public AgendaServiceImpl() {
            activities.add(Activity.newBuilder()
                    .setActivityId("ACT-1").setTitle("Inteligencia Artificial aplicada")
                    .setSpeaker("Dr. Rodríguez").setTimeSlot("09:00")
                    .setLocation("Auditorio A").setType("TALK").build());
            activities.add(Activity.newBuilder()
                    .setActivityId("ACT-2").setTitle("Taller de Docker y Kubernetes")
                    .setSpeaker("Ing. García").setTimeSlot("11:00")
                    .setLocation("Lab 301").setType("WORKSHOP").build());
            activities.add(Activity.newBuilder()
                    .setActivityId("ACT-3").setTitle("Realidad Virtual en educación")
                    .setSpeaker("Ing. López").setTimeSlot("14:00")
                    .setLocation("Sala VR").setType("EXPERIENCE").build());
            activities.add(Activity.newBuilder()
                    .setActivityId("ACT-4").setTitle("Seguridad en microservicios")
                    .setSpeaker("Dra. Martínez").setTimeSlot("09:00")
                    .setLocation("Auditorio B").setType("TALK").build());
            activities.add(Activity.newBuilder()
                    .setActivityId("ACT-5").setTitle("Taller de gRPC con Java")
                    .setSpeaker("Ing. Torres").setTimeSlot("16:00")
                    .setLocation("Lab 302").setType("WORKSHOP").build());
        }

        @Override
        public void getFullAgenda(EmptyAgendaRequest request,
                                  StreamObserver<ActivityList> responseObserver) {
            responseObserver.onNext(ActivityList.newBuilder()
                    .addAllActivities(activities).build());
            responseObserver.onCompleted();
        }

        @Override
        public void getActivitiesBySlot(TimeSlotRequest request,
                                        StreamObserver<ActivityList> responseObserver) {
            List<Activity> filtered = new ArrayList<>();
            for (Activity a : activities) {
                if (a.getTimeSlot().equals(request.getTimeSlot())) {
                    filtered.add(a);
                }
            }
            responseObserver.onNext(ActivityList.newBuilder()
                    .addAllActivities(filtered).build());
            responseObserver.onCompleted();
        }
    }
}