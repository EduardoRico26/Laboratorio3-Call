package edu.escuelaing.arsw.ejercicio63;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AppointmentServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
                .addService(new AppointmentServiceImpl())
                .build();
        server.start();
        System.out.println("AppointmentService iniciado en puerto 50051");
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }

    static class AppointmentServiceImpl extends AppointmentServiceGrpc.AppointmentServiceImplBase {

        private final Map<Integer, Appointment> store = new HashMap<>();
        private final Map<Integer, Student> students = new HashMap<>();
        private final AtomicInteger counter = new AtomicInteger(1);

        public AppointmentServiceImpl() {
            students.put(1, Student.newBuilder().setId(1).setName("Ana García").setInstitutionalEmail("ana@eci.edu.co").build());
            students.put(2, Student.newBuilder().setId(2).setName("Carlos López").setInstitutionalEmail("carlos@eci.edu.co").build());
        }

        @Override
        public void requestAppointment(AppointmentRequest req, StreamObserver<AppointmentResponse> ro) {
            if (!students.containsKey(req.getStudentId())) {
                ro.onNext(AppointmentResponse.newBuilder().setSuccess(false).setMessage("Estudiante no encontrado.").build());
                ro.onCompleted(); return;
            }
            int id = counter.getAndIncrement();
            Appointment a = Appointment.newBuilder()
                    .setId(id).setStudentId(req.getStudentId())
                    .setServiceType(req.getServiceType()).setDate(req.getDate())
                    .setStatus(AppointmentStatus.REQUESTED).build();
            store.put(id, a);
            System.out.printf("[APPOINTMENT] Cita #%d creada para estudiante %d%n", id, req.getStudentId());
            ro.onNext(AppointmentResponse.newBuilder().setSuccess(true).setMessage("Cita #" + id + " creada.").setAppointment(a).build());
            ro.onCompleted();
        }

        @Override
        public void cancelAppointment(CancelRequest req, StreamObserver<CancelResponse> ro) {
            Appointment a = store.get(req.getAppointmentId());
            if (a == null) { ro.onNext(CancelResponse.newBuilder().setSuccess(false).setMessage("Cita no encontrada.").build()); ro.onCompleted(); return; }
            if (a.getStudentId() != req.getStudentId()) { ro.onNext(CancelResponse.newBuilder().setSuccess(false).setMessage("No es el dueño de la cita.").build()); ro.onCompleted(); return; }
            store.put(req.getAppointmentId(), a.toBuilder().setStatus(AppointmentStatus.CANCELLED).build());
            ro.onNext(CancelResponse.newBuilder().setSuccess(true).setMessage("Cita #" + req.getAppointmentId() + " cancelada.").build());
            ro.onCompleted();
        }

        @Override
        public void getAppointments(StudentRequest req, StreamObserver<AppointmentList> ro) {
            List<Appointment> active = new ArrayList<>();
            for (Appointment a : store.values())
                if (a.getStudentId() == req.getStudentId() && a.getStatus() != AppointmentStatus.CANCELLED)
                    active.add(a);
            ro.onNext(AppointmentList.newBuilder().addAllAppointments(active).build());
            ro.onCompleted();
        }
    }
}