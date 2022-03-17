package Servidor;

public class Main {
    public static void main(String[] args) {
        int porto = 30;
        Servidor servidor = new Servidor(porto);
        servidor.start();

    }
}
