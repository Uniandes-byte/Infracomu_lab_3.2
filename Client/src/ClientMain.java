import java.io.File;
import java.util.Objects;
import java.util.Scanner;

public class ClientMain {
    
    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    //Indica el número de clientes.
    private static int numberOfClients;

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------

    public static void main(String[] args){
        // Solicita el número de clientes que harán parte de la transferencia de archivos. 
        getInputForClientNumber();
        System.out.println("");
        
        // Se elimina archivos presentes en ArchivosRecibidos.
        deleteDirectory();

        // Inicializa y comienza cada thread que representa clientes que intentan conectarse al servidor.
        for( int i = 0; i<numberOfClients; i++){
            ClientThread c = new ClientThread(i,numberOfClients);
            System.out.println("Client with id " + i + " is connected.");
            c.run();
        } 
    }

    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------

    // Solicita el número de clientes.
    public static void getInputForClientNumber(){
        Scanner scan = new Scanner(System.in);  // Create a Scanner object
        int selectedOption = -1;//default
        boolean moveOn = false;
        String numConnectionsStr = "";

        while (!moveOn) {
            System.out.println("Input the number of client connections to make:");
            numConnectionsStr = scan.nextLine();
            if (isNumeric(numConnectionsStr)) {
                selectedOption = Integer.parseInt(numConnectionsStr);
                if (selectedOption > 0 ) {
                    moveOn = true;
                }
                else{
                    System.out.println("Input a positive number.");
                }
            }
            else{
                System.out.println("Selected option needs to be a number");
            }
        }

        numberOfClients = Integer.parseInt(numConnectionsStr);
    }

    // Verifica si una cadena corresponde a un número.
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void deleteDirectory(){
        File directory = new File("Client/ArchivosRecibidos");
 
        for (File file: Objects.requireNonNull(directory.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
}
