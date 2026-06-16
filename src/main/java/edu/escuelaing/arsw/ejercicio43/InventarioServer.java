package edu.escuelaing.arsw.ejercicio43;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class InventarioServer {


    public static void main(String[] args)
            throws Exception {


        InventarioImpl inventario =
                new InventarioImpl();


        Registry registry =
                LocateRegistry.createRegistry(
                        1099
                );


        registry.rebind(
                "Inventario",
                inventario
        );


        System.out.println(
                "RMI Inventory Server running..."
        );

    }

}