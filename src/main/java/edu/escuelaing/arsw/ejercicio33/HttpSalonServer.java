package edu.escuelaing.arsw.ejercicio33;


import java.net.*;
import java.io.*;


public class HttpSalonServer {


    public static void main(String[] args)
            throws Exception {


        SalonManager manager =
                new SalonManager();


        ServerSocket serverSocket =
                new ServerSocket(35000);


        System.out.println(
                "HTTP Salon Server running..."
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
                    request
            );



            String response =
                    processRequest(
                            request,
                            manager
                    );



            out.println(
                    "HTTP/1.1 200 OK"
            );

            out.println(
                    "Content-Type: text/html"
            );

            out.println();


            out.println(
                    "<html><body>"
            );


            out.println(
                    "<h2>Salon System</h2>"
            );


            out.println(
                    "<pre>"
                    + response
                    + "</pre>"
            );


            out.println(
                    "</body></html>"
            );



            client.close();
        }
    }




    private static String processRequest(
            String request,
            SalonManager manager){


        String[] parts = request.split(" ");


        if(parts.length < 2){
            return "ERROR_OPERACION_INVALIDA";
        }


        String method = parts[0];
        String path = parts[1];



        if(method.equals("GET") && path.equals("/rooms")){

            return manager.consultar();

        }



        if(method.equals("GET") && path.startsWith("/rooms?id=")){


            String salon =
                    path.substring(
                            path.indexOf("=") + 1
                    );


            return manager.consultarSalon(salon);

        }




        if(method.equals("POST") &&
                path.startsWith("/rooms/reserve?id=")){


            String salon =
                    path.substring(
                            path.indexOf("=") + 1
                    );


            return manager.reservar(salon);

        }





        if(method.equals("POST") &&
                path.startsWith("/rooms/release?id=")){


            String salon =
                    path.substring(
                            path.indexOf("=") + 1
                    );


            return manager.liberar(salon);

        }



        return "ERROR_OPERACION_INVALIDA";
    }
    
}