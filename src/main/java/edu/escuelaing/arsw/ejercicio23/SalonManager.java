package edu.escuelaing.arsw.ejercicio23;

import java.util.HashMap;
import java.util.Map;

public class SalonManager {

    private Map<String, Boolean> salones;

    public SalonManager() {

        salones = new HashMap<>();

        salones.put("E301", false);
        salones.put("E302", false);
        salones.put("E303", false);
        salones.put("E304", false);
    }


    public synchronized String consultarSalon(String salon){

        if(!salones.containsKey(salon)){
            return "ERROR_SALON_NO_EXISTE";
        }

        if(salones.get(salon)){
            return "SALON_RESERVADO";
        }

        return "SALON_DISPONIBLE";
    }



    public synchronized String reservarSalon(String salon){

        if(!salones.containsKey(salon)){
            return "ERROR_SALON_NO_EXISTE";
        }


        if(salones.get(salon)){
            return "SALON_RESERVADO";
        }


        salones.put(salon,true);

        return "RESERVA_EXITOSA";
    }



    public synchronized String liberarSalon(String salon){

        if(!salones.containsKey(salon)){
            return "ERROR_SALON_NO_EXISTE";
        }


        if(!salones.get(salon)){
            return "SALON_DISPONIBLE";
        }


        salones.put(salon,false);

        return "LIBERACION_EXITOSA";
    }



    public String procesarOperacion(String mensaje){

        String[] datos = mensaje.split(",");


        if(datos.length != 2){
            return "ERROR_OPERACION_INVALIDA";
        }


        String operacion = datos[0];
        String salon = datos[1];


        switch(operacion){

            case "CONSULTAR_SALON":
                return consultarSalon(salon);


            case "RESERVAR_SALON":
                return reservarSalon(salon);


            case "LIBERAR_SALON":
                return liberarSalon(salon);


            default:
                return "ERROR_OPERACION_INVALIDA";
        }
    }
}