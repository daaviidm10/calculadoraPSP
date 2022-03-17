package Servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor extends Thread {

    public int porto; // Número de puerto para levantar el server
    public DataInputStream entradaDatos; //Canal de entrada de datos de clientes
    ArrayList <String> nums = new ArrayList();
    ArrayList  <String> ultimaOperacion = new ArrayList();
    public Double resultado = 0.0;

    public Servidor(int puerto) {
        this.porto = puerto;
    }


    @Override
    public void run() {
        try {

            ServerSocket socketServidor = new ServerSocket(this.porto);
            System.out.println("O porto que se está a usar é o " + porto);
            
            //Dejaremos el servidor siempre escuchando
            //En este caso bucle infinito
            String textoRecibido = "";
            while(true) {
                Socket socketCliente = socketServidor.accept();  // aceptamos la conexión

                entradaDatos = new DataInputStream(socketCliente.getInputStream());//Estableceremos canal de comunicación con cliente; Todo lo que llegue del cliente lo recojo en entradaDatos

                textoRecibido = entradaDatos.readUTF(); //Leemos mensaje del cliente, es decir  leeme lo recogido en entradaDatos

                String[] partes = textoRecibido.split(",");// la coma es el indicativo que tiene el servidor para saber que va el operador o el siguiente numero


                nums.add(partes[0]);//Capturamos los números que llegan del cliente
                ultimaOperacion.add(partes[1]);//Capturamos las operaciones que llegan del cliente
                //Evaluamos en función de operando
                if (nums.size() == 2 && ultimaOperacion.get(1).equals("=")) { // hasta que no tenga 2 numeros y el segundo operador sea (=) entra en el if
                    switch (ultimaOperacion.get(0)) {
                        case "+":
                            resultado = Double.parseDouble(nums.get(0)) + Double.parseDouble(nums.get(1));
                            break;
                        case "-":
                            resultado = Double.parseDouble(nums.get(0)) - Double.parseDouble(nums.get(1));
                            break;
                        case "*":
                            resultado = Double.parseDouble(nums.get(0)) * Double.parseDouble(nums.get(1));
                            break;
                        case "/":
                            resultado = Double.parseDouble(nums.get(0)) / Double.parseDouble(nums.get(1));
                            break;
                    }

                    nums.clear();// Pomos os arrays a 0
                    ultimaOperacion.clear();

                    OutputStream auxOut = socketCliente.getOutputStream();
                    DataOutputStream resultado = new DataOutputStream(auxOut);
                    resultado.writeUTF(String.valueOf(this.resultado)); //Enviamos o resultado o Cliente
                    System.out.println(this.resultado);
                }
            }
        } catch (IOException e) {
            System.out.println(" Erro no porto " + porto);

        }
    }
}