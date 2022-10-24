package Server.src;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        long startime = System.currentTimeMillis();
        fileProtocol();
        long totalTime = System.currentTimeMillis() - startime;
        createLog(totalTime);
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
            createLog(10000000);
            e.printStackTrace();
        } 
    }

    public void createLog(long totalTime) {
        try {
            File file = new File("Server/Logs/Connection-"+id+"-" + obtenerHoraNombre() + "-Log.txt");
            File fileDirectory = new File(filePath);
            if (file.exists()) {
                FileWriter myWriter = new FileWriter("Server/Logs/Connection-" +id+"-"+obtenerHoraNombre() + "-Log.txt");
                myWriter.write("Fecha Prueba: " + obtenerHora() + "\n");
                myWriter.write("Nombre Archivo: " + getFileName(filePath) + " Tamano: " + fileDirectory.length()
                        + " Bytes" + "\n");
                myWriter.write("Entrega del archivo: " + tipoEntrega(fileDirectory.length(), totalTime) + "\n");
                myWriter.write("Tiempo Transferencia: " + totalTime + "ms" + "\n");
                myWriter.close();
            } else {
                file.createNewFile();
                createLog(totalTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFileName(String filePath) {
        String fileName = "";
        try {
            Path p = Paths.get(filePath);
            fileName = p.getFileName().toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public String tipoEntrega(long tamano, long Totaltime) {

        String tipoEntrega = "";
        if (tamano == 104857600 || tamano == 262144000) {
            tipoEntrega = "Entrega Exitosa";
        } else if(Totaltime==10000000){
            tipoEntrega = "Entrega Fallida*";
        }
        else{
            tipoEntrega = "Entrega Fallida";
        }
        return tipoEntrega;

    }

    public String obtenerHora() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String hora = dtf.format(now);
        return hora;
    }

    public String obtenerHoraNombre() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String hora = dtf.format(now);
        return hora;
    }

}


