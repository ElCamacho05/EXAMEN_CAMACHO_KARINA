package mx.uv.fiee.iinf.paradigmas.networks;

import java.io.IOException;
import java.net.Socket;
import mx.uv.fiee.iinf.paradigmas.networks.models.Persona;
import java.io.ObjectInputStream;

import javax.net.ssl.*;

/**
 * Clase principal que recibe objetos serializados desde un socket.
 */
public class Receiver {

    /**
     * Método principal que inicializa la conexión y recibe objetos.
     */
    public static void main(String[] args) {

        // change receiver settings on
        System.setProperty("javax.net.ssl.trustStore", "Networks/SerializedOverNetwork/src/KEYS/serverkey.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        SocketUtils utils = new SocketUtils ("localhost", 19000);
        utils.Receive ();

    }

    /**
     * Clase interna para manejar la conexión del socket y la recepción de datos.
     */
    private static class SocketUtils {

        private SSLSocket sslSocket;

        /**
         * Constructor que inicializa un socket cliente para conectarse a un servidor.
         * @param address Dirección del servidor.
         * @param port Puerto del servidor.
         */
        public SocketUtils (String address, int port) {
            try {
                // Crear SSLSocket
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                this.sslSocket = (SSLSocket) factory.createSocket(address, port);
                System.out.println("Securely connected to " + address + ":" + port);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("An error occurred while connecting to " + address + ":" + port);
            }
        }

        /**
         * Método para recibir objetos serializados desde el socket.
         */
        public void Receive() {
            try (ObjectInputStream ois = new ObjectInputStream (sslSocket.getInputStream ())) {
                while (true) {
                    try {
                        Persona p = (Persona) ois.readObject (); // Deserialize Persona object
                        System.out.println ("Received UUID: " + p.getUuid ()); // Print UUID
                    } catch (ClassNotFoundException e) {
                        System.err.println ("Class not found: " + e.getMessage ());
                        break;
                    } catch (java.io.EOFException e) {
                        System.err.println ("End of stream reached.");
                        break; // Exit the loop when the stream ends
                    }
                }
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
    }

}
