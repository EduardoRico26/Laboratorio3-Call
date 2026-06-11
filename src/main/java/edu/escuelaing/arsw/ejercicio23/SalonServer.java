package edu.escuelaing.arsw.ejercicio23;


import java.net.*;
import java.io.*;


public class SalonServer {


    public static void main(String[] args)
            throws IOException {


        SalonManager manager =
                new SalonManager();


        ServerSocket serverSocket =
                new ServerSocket(35000);


        System.out.println(
                "Servidor de salones iniciado..."
        );


        while(true){


            Socket client =
                    serverSocket.accept();


            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(
                                    client.getInputStream()
                            )
                    );


            PrintWriter out =
                    new PrintWriter(
                            client.getOutputStream(),
                            true
                    );


            String request =
                    in.readLine();


            System.out.println(
                    "Solicitud: " + request
            );


            String response =
                    manager.procesarOperacion(request);


            out.println(response);



            client.close();
        }
    }
}