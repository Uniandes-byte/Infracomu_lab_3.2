import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientThread extends Thread {

    // ----------------------------------------------------------------------
    // CONSTANTS
    // ----------------------------------------------------------------------

    // Representa el número de puerto por el cual se realizará la transferencia de
    // archivos UDP.
    public static final int PORT = 4445;

    // Representa la dirección IP donde se realizará la transferencia de archivos
    // UDP.
    public static final String HOST = "localhost";

    // ----------------------------------------------------------------------
    // ATTRIBUTES
    // ----------------------------------------------------------------------

    // Identificador del cliente
    private int id;

    // Socket que representa la comunicación con el servidor.
    private DatagramSocket clientSocket;

    // Indica el número de clientes.
    private static int numberOfClients;

    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------

    public ClientThread(int pId, int pNumberOfClients) {
        id = pId;
        numberOfClients = pNumberOfClients;

        try {
            clientSocket = new DatagramSocket(PORT);
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

    // Representa el protocolo para recepción de archivos.
    public void fileProtocol() {
        try {
            receiveMsg();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Permite recibir el archivo proveniente del servidor.
    public void receiveMsg() throws IOException {

        // Tamaño de mensaje para fragmentación del archivo. Tamaño de fragmentación:
        // 60Kb.
        byte[] buf = new byte[60 * 1024];

        // Instancia para recibir los datagramas provenientes del servidor.
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        // Se crea el archivo a ser guardado en ArchivosRecibidos con la notación
        // [Número cliente]–Prueba-[Cantidad de conexiones].txt
        File file = new File("Client/ArchivosRecibidos/Cliente" + id + "-Prueba-" + numberOfClients + ".txt");

        // Se crea una instancia de dicho archivo para escribir bytes.
        FileOutputStream fileOutputStream = new FileOutputStream(
                "Client/ArchivosRecibidos/Cliente" + id + "-Prueba-" + numberOfClients + ".txt");

        try {
            while (true) {
                // 10 segundos de Timeout; es decir, tiempo max. a esperar para recibir bytes
                // del servidor.
                clientSocket.setSoTimeout(10000);

                // Recepción de bytes provenientes del servidor.
                clientSocket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);

                // Guardar bytes del archivo en la nueva ruta [Número cliente]–Prueba-[Cantidad
                // de conexiones].txt
                fileOutputStream.write(buf, 0, packet.getLength());
            }

        } catch (SocketTimeoutException e) {
            // Indica el timeout del thread id y guarda los bytes que se hayan escrito en el
            // archivo recibido.
            System.out.println("Timeout client with id " + id);
            System.out.println("");
            fileOutputStream.close();
        }

        // Se cierra la conexión.
        clientSocket.close();
    }

    public void createLog(long totalTime) {
        try {
            File file = new File("Client/Logs/Connection-" + obtenerHoraNombre() + "-Log.txt");
            String filePath = "Client/ArchivosRecibidos/Cliente" + id + "-Prueba-" + numberOfClients + ".txt";
            File fileDirectory = new File(filePath);
            if (file.exists()) {
                FileWriter myWriter = new FileWriter("Client/Logs/Connection-" + obtenerHoraNombre() + "-Log.txt");
                myWriter.write("Fecha Prueba: " + obtenerHora() + "\n");
                myWriter.write("Nombre Archivo: " + getFileName(filePath) + " Tamano: " + fileDirectory.length()
                        + " Bytes" + "\n");
                myWriter.write("Entrega del archivo: " + tipoEntrega(fileDirectory.length()) + "\n");
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

    public String tipoEntrega(long tamano) {

        String tipoEntrega = "";
        if (tamano == 104857600 || tamano == 262144000) {
            tipoEntrega = "Entrega Exitosa";
        } else {
            tipoEntrega = "Entrega Fallida*";
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
