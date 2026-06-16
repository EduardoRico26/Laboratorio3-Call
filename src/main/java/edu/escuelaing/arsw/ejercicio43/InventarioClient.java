package edu.escuelaing.arsw.ejercicio43;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;


public class InventarioClient {


    public static void main(String[] args)
            throws Exception {


        Registry registry =
                LocateRegistry.getRegistry(
                        "localhost"
                );


        InventarioRemote inventario =
                (InventarioRemote)
                registry.lookup(
                        "Inventario"
                );



        System.out.println(
                inventario.consultarEquipos()
        );



        System.out.println(
                inventario.consultarEquipo(
                        "EQ01"
                )
        );



        System.out.println(
                "Reserva: "
                +
                inventario.reservarEquipo(
                        "EQ01"
                )
        );



        System.out.println(
                inventario.consultarEquipo(
                        "EQ01"
                )
        );


    }

}