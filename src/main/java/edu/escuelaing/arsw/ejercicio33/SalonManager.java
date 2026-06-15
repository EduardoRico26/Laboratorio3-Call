package edu.escuelaing.arsw.ejercicio33;


import java.util.HashMap;
import java.util.Map;


public class SalonManager {


    private Map<String, Boolean> salones;


    public SalonManager(){

        salones = new HashMap<>();

        salones.put("E301", false);
        salones.put("E302", false);
        salones.put("E303", false);
        salones.put("E304", false);

    }



    public synchronized String consultar(){

        StringBuilder respuesta =
                new StringBuilder();


        for(String salon : salones.keySet()){


            respuesta.append(
                    salon + " : "
            );


            if(salones.get(salon)){
                respuesta.append("RESERVADO");
            }else{
                respuesta.append("DISPONIBLE");
            }


            respuesta.append("\n");
        }


        return respuesta.toString();
    }





    public synchronized String consultarSalon(
            String salon){


        if(!salones.containsKey(salon)){
            return "ERROR_SALON_NO_EXISTE";
        }


        if(salones.get(salon)){
            return "SALON_RESERVADO";
        }


        return "SALON_DISPONIBLE";
    }




    public synchronized String reservar(
            String salon){


        if(!salones.containsKey(salon)){
            return "ERROR_SALON_NO_EXISTE";
        }


        if(salones.get(salon)){
            return "SALON_RESERVADO";
        }


        salones.put(salon,true);


        return "RESERVA_EXITOSA";
    }





    public synchronized String liberar(
            String salon){


        if(!salones.containsKey(salon)){
            return "ERROR_SALON_NO_EXISTE";
        }


        if(!salones.get(salon)){
            return "SALON_DISPONIBLE";
        }


        salones.put(salon,false);


        return "LIBERACION_EXITOSA";
    }

}