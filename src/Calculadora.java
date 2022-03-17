import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Calculadora {
    private JPanel panel1;
    private JTextField textArea1;
    private JButton a8Button;
    private JButton button2;
    private JButton a7Button;
    private JButton a9Button;
    private JButton a4Button;
    private JButton a5Button;
    private JButton a6Button;
    private JButton button8;
    private JButton a1Button;
    private JButton a2Button;
    private JButton a3Button;
    private JButton button12;
    private JButton a0Button;
    private JButton button14;
    private JButton button15;
    private JButton button16;
    private JButton acButton;
    private JButton offButton;
    private static Double resultado;
    private boolean primerNumero;

    private static final String Direccion = "localhost";
    private static final int Porto = 30;

    public Calculadora() {
        primerNumero = true;
        ActionListener escucha = new insertarNumerosPantalla(); // Instancia en clase interna insertarNumerosPantalla
        ActionListener operador = new Operaciones(); // Instancia en la clase interna Operaciones
        // LLamamos al metodo ponerEscuchaBoton y enviamos los botones y el ActionListener deseado.
        EscuchaDeBotones(escucha, a0Button);
        EscuchaDeBotones(escucha, a1Button);
        EscuchaDeBotones(escucha, a2Button);
        EscuchaDeBotones(escucha, a3Button);
        EscuchaDeBotones(escucha, a4Button);
        EscuchaDeBotones(escucha, a5Button);
        EscuchaDeBotones(escucha, a6Button);
        EscuchaDeBotones(escucha, a7Button);
        EscuchaDeBotones(escucha, a8Button);
        EscuchaDeBotones(escucha, a9Button);
        EscuchaDeBotones(operador, button2);
        EscuchaDeBotones(operador, button8);
        EscuchaDeBotones(operador, button12);
        EscuchaDeBotones(operador, button15);
        EscuchaDeBotones(operador, button16);

        // Creamos los Listener desde el form con los botones que deseamos controlar de modo independiente
        acButton.addActionListener(actionEvent -> {
            textArea1.setText(null);
            textArea1.setText("0");
            resultado = 0.0;
            primerNumero = true;
        });
        offButton.addActionListener(actionEvent -> {
            try {
                apagarCalculadora();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        button14.addActionListener(actionEvent -> {
            String texto = textArea1.getText();
            if (texto.length() <= 1 && texto.equals("0")) {
                // Hay el 0 en el textArea (valor por defecto)
                textArea1.setText(textArea1.getText().concat("."));
                primerNumero = false;
            } else {
                // Ya hay numero en el Field
                if (!validarPunto(textArea1.getText())) {
                    textArea1.setText(textArea1.getText().concat("."));

                }
            }


        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Calculadora");
        frame.setContentPane(new Calculadora().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500); // Le damos un tamano deseado porque el pack() lo pone demasiado pequeno
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); //Situamos el frame en el centro de la pantalla
    }


    public void EscuchaDeBotones(ActionListener escucha, JButton boton) {
        boton.addActionListener(escucha); // Ponemos a la escucha el boton que reciba como parametro con el ActionListener que reciba
    }

    public void apagarCalculadora() throws InterruptedException {
            System.exit(0);
    }
    private void createUIComponents() {
        // Instanciamos por obligacion al declarar custom los componentes seleccionados
        textArea1 = new JTextField("0"); // Le ponemos 0 como valor en el constructor
    }

    public boolean validarPunto(String textoLabel) {

        boolean validacion = false;
        //Bucle para comprobar el tama�o del String del display
        for (int i = 0; i < textoLabel.length(); i++) {
            if (textoLabel.substring(i, i + 1).equals(".")) { // Va comprobando uno a uno cada caracter del String con m�todo SubString
                validacion = true; // Si encuentra el . cambiamos el booleano a true y rompemos el bucle
                break;
            }
        }
        return validacion;
    }

    class insertarNumerosPantalla implements ActionListener {

        private String LecturaNumeros;

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            LecturaNumeros = actionEvent.getActionCommand();// Asignacion al String de los botones pulsados con getActionCommand()
            if (primerNumero) {
                textArea1.setText(""); //la pantalla queda en blanco
                primerNumero = false;
            }
            textArea1.setText(textArea1.getText().concat(LecturaNumeros)); // Lo que tengo en la pantalla concatenado con el dato de entrada
        }
    }

    class Operaciones extends Thread implements ActionListener {

        private String ultimaOperacion = "=";

        private double datoConvertido;
        private static String datoPantalla;
        private ConectarServer conexServer;


        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Thread hilo = new Thread(new Thread(() -> {
                try {
                    String operacion = actionEvent.getActionCommand();
                    conexServer = new ConectarServer();
                    datoConvertido = Double.parseDouble(textArea1.getText()); // Pasamos a Double los datos recogidos del display de la calculadoraCliente
                    datoPantalla = String.valueOf(datoConvertido);
                    conexServer.envioNumsServer(datoPantalla.concat(",").concat(operacion));
                    ultimaOperacion = operacion; // Asignamos a ultimaOperacion el valor del boton de operacion pulsado
                } finally {
                    try {
                        conexServer.cerrarConexionSocket();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));
            hilo.start();
            primerNumero = true; // Dejamos de concatenar en el display
        }
        public void acumularOperacion(Double numero, String operacio){
        }

    }

    class ConectarServer  extends Thread {

        static boolean BucleInf = true;
        static Socket socketCliente;

        public synchronized void envioNumsServer(String numero) {
            try {
                socketCliente = new Socket(Direccion, Porto);
                while (BucleInf) {
                    if ("SAIR".equalsIgnoreCase(numero)) {
                        BucleInf = false;
                        numero = "sair";
                    } else {
                        OutputStream auxOut = socketCliente.getOutputStream();
                        DataOutputStream infoSalida = new DataOutputStream(auxOut);
                        infoSalida.writeUTF(numero); // Enviamos al server el parámetro recibido
                        reciboDatos();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void reciboDatos() throws IOException {
            // Recibo e imprimo en pantalla el mensaje que me envía el server
            InputStream Input = socketCliente.getInputStream();
            DataInputStream infoEntrada = new DataInputStream(Input);// infoEntrada -> lo que le va a llegar al cliente
            String lectura = infoEntrada.readUTF();
            System.out.println(lectura);
            textArea1.setText(lectura);
        }

        public void cerrarConexionSocket() throws IOException {
            socketCliente.close();
        }
    }
}

