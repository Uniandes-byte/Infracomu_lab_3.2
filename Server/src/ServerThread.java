package Server.src;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerThread extends Thread{

    // ----------------------------------------------------------------------
    // CONSTANTS
    // ----------------------------------------------------------------------
    
    // Representa el número de puerto por el cual se realizará la transferencia de archivos UDP.
    public static final int PORT = 4445;

    // Representa la dirección IP donde se realizará la transferencia de archivos UDP.
    public static final String HOST = "localhost";

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    // Identificador del servidor
    private int id;     

    // Socket que representa la comunicación con el cliente. 
    private DatagramSocket serverSocket;

    // Indica la ruta del archivo elegido a enviar.
    public static String filePath;

    // Indica la dirección IP a usar en la transferencia de archivos al cliente.
    private InetAddress address;

    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------

    public ServerThread(int pId, String pFilePath) {
        id = pId;
        filePath = pFilePath;
        try {
            // Se inicializan los parámetros de conexión.
            address = InetAddress.getByName("localhost");
            serverSocket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // ----------------------------------------------------------------------
    // RUN
    // ----------------------------------------------------------------------

    // Run del thread a ejecutar .start()
    public void run() {
        fileProtocol();
    }

    // ----------------------------------------------------------------------
    // METHODS
    // ----------------------------------------------------------------------

    // Representa el protocolo para envío de archivos.
    public void fileProtocol() {

        // Verifica si la ruta del archivo a enviar el válida. En caso de error se recomienda actualizar a ruta relativa de su pc. 
        File file = new File(filePath);
        if ((!file.exists()) || file.isDirectory()) {
            System.out.println("Error the path is incorrect");
        }
        else{
            sendMsg();
        }

        // Muestra que el mensaje fue envíado.
        System.out.println("Message sent by server with id " + id);
    }

    // Permite enviar el archivo a los clientes.
    public void sendMsg(){
        int bytes = 0;
        File file = new File(filePath);

        try {
            // Se crea una instancia del archivo a ser envíado.
            FileInputStream fileInputStream = new FileInputStream(file);

            // Tamaño de mensaje para fragmentación del archivo. Tamaño de fragmentación: 60Kb.
            byte[] buffer = new byte[60 * 1024];

            // Envío de fragmentos a clientes.
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
                serverSocket.send(packet);
            }

            // Se cierra la conexión.
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}


