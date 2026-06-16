package edu.escuelaing.arsw.ejercicio63;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.*;

public class MedicalServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50052)
                .addService(new MedicalServiceImpl())
                .build();
        server.start();
        System.out.println("MedicalService iniciado en puerto 50052");
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }

    static class MedicalServiceImpl extends MedicalServiceGrpc.MedicalServiceImplBase {

        private final Map<SpecialtyType, Specialty> specialties = new HashMap<>();

        public MedicalServiceImpl() {
            specialties.put(SpecialtyType.SPECIALTY_MEDICINE, Specialty.newBuilder()
                    .setId(1).setType(SpecialtyType.SPECIALTY_MEDICINE)
                    .setName("Medicina General")
                    .setDescription("Consultas generales y urgencias menores")
                    .setAvailableSlots(10).build());

            specialties.put(SpecialtyType.SPECIALTY_PSYCHOLOGY, Specialty.newBuilder()
                    .setId(2).setType(SpecialtyType.SPECIALTY_PSYCHOLOGY)
                    .setName("Psicología")
                    .setDescription("Apoyo emocional y salud mental")
                    .setAvailableSlots(6).build());

            specialties.put(SpecialtyType.SPECIALTY_DENTISTRY, Specialty.newBuilder()
                    .setId(3).setType(SpecialtyType.SPECIALTY_DENTISTRY)
                    .setName("Odontología")
                    .setDescription("Salud oral y procedimientos dentales")
                    .setAvailableSlots(4).build());
        }

        @Override
        public void getSpecialty(SpecialtyRequest req, StreamObserver<SpecialtyResponse> ro) {
            Specialty s = specialties.get(req.getType());
            if (s == null) {
                ro.onNext(SpecialtyResponse.newBuilder().setFound(false).setMessage("Especialidad no encontrada.").build());
            } else {
                System.out.printf("[MEDICAL] Consultada especialidad: %s%n", s.getName());
                ro.onNext(SpecialtyResponse.newBuilder().setFound(true).setSpecialty(s).build());
            }
            ro.onCompleted();
        }

        @Override
        public void getAllSpecialties(AllSpecialtiesRequest req, StreamObserver<SpecialtyList> ro) {
            ro.onNext(SpecialtyList.newBuilder().addAllSpecialties(specialties.values()).build());
            ro.onCompleted();
        }
    }
}