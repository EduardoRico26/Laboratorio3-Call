package edu.escuelaing.arsw.ejercicio8;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AttendeeServiceServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50071)
                .addService(new AttendeeServiceImpl())
                .build();
        server.start();
        System.out.println("AttendeeService iniciado en puerto 50071");
        server.awaitTermination();
    }

    static class AttendeeServiceImpl extends AttendeeServiceGrpc.AttendeeServiceImplBase {

        private final Map<String, AttendeeResponse> attendees = new HashMap<>();
        private final AtomicInteger counter = new AtomicInteger(1);

        @Override
        public void registerAttendee(RegisterRequest request,
                                     StreamObserver<RegisterResponse> responseObserver) {
            String id = "ATT-" + counter.getAndIncrement();
            attendees.put(id, AttendeeResponse.newBuilder()
                    .setFound(true)
                    .setAttendeeId(id)
                    .setName(request.getName())
                    .setEmail(request.getEmail())
                    .setType(request.getType())
                    .build());

            responseObserver.onNext(RegisterResponse.newBuilder()
                    .setAttendeeId(id)
                    .setSuccess(true)
                    .setMessage("Asistente registrado con ID: " + id)
                    .build());
            responseObserver.onCompleted();
        }

        @Override
        public void getAttendee(AttendeeRequest request,
                                StreamObserver<AttendeeResponse> responseObserver) {
            AttendeeResponse att = attendees.get(request.getAttendeeId());
            if (att == null) {
                responseObserver.onNext(AttendeeResponse.newBuilder()
                        .setFound(false)
                        .build());
            } else {
                responseObserver.onNext(att);
            }
            responseObserver.onCompleted();
        }
    }
}