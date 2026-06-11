package edu.escuelaing.arsw.ejercicio23;


import java.net.*;
import java.io.*;
import java.util.Scanner;


public class SalonClient {


    public static void main(String[] args)
            throws Exception {


        Scanner scanner =
                new Scanner(System.in);


        while(true){


            System.out.println(
                    "Ingrese operación:"
            );


            String mensaje =
                    scanner.nextLine();



            Socket socket =
                    new Socket(
                            "localhost",
                            35000
                    );


            PrintWriter out =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );


            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );


            out.println(mensaje);


            String respuesta =
                    in.readLine();


            System.out.println(
                    "Servidor: " + respuesta
            );


            socket.close();
        }
    }
}