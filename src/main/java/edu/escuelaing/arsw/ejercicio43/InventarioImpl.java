package edu.escuelaing.arsw.ejercicio43;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class InventarioImpl
        extends UnicastRemoteObject
        implements InventarioRemote {


    private Map<String,Equipo> equipos;


    public InventarioImpl()
            throws RemoteException {


        equipos = new HashMap<>();


        equipos.put(
                "EQ01",
                new Equipo(
                        "EQ01",
                        "Osciloscopio",
                        "Lab Electronica"
                )
        );


        equipos.put(
                "EQ02",
                new Equipo(
                        "EQ02",
                        "Multimetro",
                        "Lab Electronica"
                )
        );


        equipos.put(
                "EQ03",
                new Equipo(
                        "EQ03",
                        "Arduino",
                        "Lab Sistemas"
                )
        );

    }




    @Override
    public List<String> consultarEquipos()
            throws RemoteException {


        List<String> respuesta =
                new ArrayList<>();


        for(Equipo e : equipos.values()){

            respuesta.add(
                    e.toString()
            );
        }


        return respuesta;

    }





    @Override
    public String consultarEquipo(
            String codigo)
            throws RemoteException {


        if(!equipos.containsKey(codigo)){
            return "EQUIPO_NO_EXISTE";
        }


        return equipos.get(codigo).toString();

    }





    @Override
    public synchronized boolean reservarEquipo(
            String codigo)
            throws RemoteException {


        if(!equipos.containsKey(codigo)){
            return false;
        }


        Equipo e =
                equipos.get(codigo);


        if(e.isReservado()){
            return false;
        }


        e.reservar();


        return true;

    }





    @Override
    public synchronized boolean liberarEquipo(
            String codigo)
            throws RemoteException {


        if(!equipos.containsKey(codigo)){
            return false;
        }


        Equipo e =
                equipos.get(codigo);


        if(!e.isReservado()){
            return false;
        }


        e.liberar();


        return true;

    }

}