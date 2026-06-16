package edu.escuelaing.arsw.ejercicio8;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

public class CapacityServiceServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50074)
                .addService(new CapacityServiceImpl())
                .build();
        server.start();
        System.out.println("CapacityService iniciado en puerto 50074");
        server.awaitTermination();
    }

    static class CapacityServiceImpl extends CapacityServiceGrpc.CapacityServiceImplBase {

        // activityId -> [maxCapacity, current]
        private final Map<String, int[]> capacities = new HashMap<>();

        public CapacityServiceImpl() {
            capacities.put("ACT-1", new int[]{200, 0});
            capacities.put("ACT-2", new int[]{30,  0});
            capacities.put("ACT-3", new int[]{15,  0});
            capacities.put("ACT-4", new int[]{150, 0});
            capacities.put("ACT-5", new int[]{25,  0});
        }

        @Override
        public void getCapacityStatus(CapacityRequest request,
                                      StreamObserver<CapacityResponse> responseObserver) {
            String id = request.getActivityId();
            if (!capacities.containsKey(id)) {
                responseObserver.onNext(CapacityResponse.newBuilder()
                        .setActivityId(id).setMaxCapacity(0)
                        .setCurrent(0).setAvailable(false).build());
            } else {
                int[] cap = capacities.get(id);
                responseObserver.onNext(CapacityResponse.newBuilder()
                        .setActivityId(id)
                        .setMaxCapacity(cap[0])
                        .setCurrent(cap[1])
                        .setAvailable(cap[1] < cap[0])
                        .build());
            }
            responseObserver.onCompleted();
        }

        @Override
        public void registerEntry(EntryRequest request,
                                  StreamObserver<EntryResponse> responseObserver) {
            String id = request.getActivityId();
            if (!capacities.containsKey(id)) {
                responseObserver.onNext(EntryResponse.newBuilder()
                        .setSuccess(false).setMessage("Actividad no existe: " + id).build());
            } else {
                int[] cap = capacities.get(id);
                if (cap[1] >= cap[0]) {
                    responseObserver.onNext(EntryResponse.newBuilder()
                            .setSuccess(false).setMessage("Aforo máximo alcanzado").build());
                } else {
                    cap[1]++;
                    responseObserver.onNext(EntryResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Entrada registrada. Ocupación: " + cap[1] + "/" + cap[0])
                            .build());
                }
            }
            responseObserver.onCompleted();
        }
    }
}