package edu.escuelaing.arsw.ejercicio8;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkshopServiceServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50073)
                .addService(new WorkshopServiceImpl())
                .build();
        server.start();
        System.out.println("WorkshopService iniciado en puerto 50073");
        server.awaitTermination();
    }

    static class WorkshopServiceImpl extends WorkshopServiceGrpc.WorkshopServiceImplBase {

        private final Map<String, Workshop> workshops = new HashMap<>();
        // workshopId -> set de attendeeIds que reservaron
        private final Map<String, Set<String>> reservations = new HashMap<>();

        public WorkshopServiceImpl() {
            String[][] data = {
                {"WS-1", "Docker y Kubernetes", "Ing. García",  "11:00", "30"},
                {"WS-2", "gRPC con Java",        "Ing. Torres", "16:00", "25"},
                {"WS-3", "Machine Learning",     "Dr. Pérez",   "09:00", "20"},
            };
            for (String[] d : data) {
                workshops.put(d[0], Workshop.newBuilder()
                        .setWorkshopId(d[0]).setTitle(d[1])
                        .setInstructor(d[2]).setTimeSlot(d[3])
                        .setCapacity(Integer.parseInt(d[4])).setReserved(0)
                        .build());
                reservations.put(d[0], new HashSet<>());
            }
        }

        @Override
        public void getWorkshops(EmptyWorkshopRequest request,
                                 StreamObserver<WorkshopList> responseObserver) {
            WorkshopList.Builder list = WorkshopList.newBuilder();
            for (Map.Entry<String, Workshop> e : workshops.entrySet()) {
                int res = reservations.get(e.getKey()).size();
                list.addWorkshops(e.getValue().toBuilder().setReserved(res).build());
            }
            responseObserver.onNext(list.build());
            responseObserver.onCompleted();
        }

        @Override
        public void reserveSpot(ReserveSpotRequest request,
                                StreamObserver<ReserveSpotResponse> responseObserver) {
            String wid = request.getWorkshopId();
            String aid = request.getAttendeeId();

            if (!workshops.containsKey(wid)) {
                responseObserver.onNext(ReserveSpotResponse.newBuilder()
                        .setSuccess(false).setMessage("Taller no existe: " + wid).build());
            } else {
                Set<String> res = reservations.get(wid);
                if (res.contains(aid)) {
                    responseObserver.onNext(ReserveSpotResponse.newBuilder()
                            .setSuccess(false).setMessage("Ya tienes reserva en este taller").build());
                } else if (res.size() >= workshops.get(wid).getCapacity()) {
                    responseObserver.onNext(ReserveSpotResponse.newBuilder()
                            .setSuccess(false).setMessage("Taller sin cupos disponibles").build());
                } else {
                    res.add(aid);
                    responseObserver.onNext(ReserveSpotResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Reserva exitosa en taller " + wid).build());
                }
            }
            responseObserver.onCompleted();
        }

        @Override
        public void cancelReservation(CancelSpotRequest request,
                                      StreamObserver<CancelSpotResponse> responseObserver) {
            String wid = request.getWorkshopId();
            String aid = request.getAttendeeId();

            if (!workshops.containsKey(wid) || !reservations.get(wid).contains(aid)) {
                responseObserver.onNext(CancelSpotResponse.newBuilder()
                        .setSuccess(false).setMessage("Reserva no encontrada").build());
            } else {
                reservations.get(wid).remove(aid);
                responseObserver.onNext(CancelSpotResponse.newBuilder()
                        .setSuccess(true).setMessage("Reserva cancelada en taller " + wid).build());
            }
            responseObserver.onCompleted();
        }
    }
}