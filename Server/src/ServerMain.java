package Server.src;
import java.io.File;
import java.util.Scanner;
import java.util.Objects;

public class ServerMain {
    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    // Representa la ruta del archivo 1 de 100 MB
    private static final String FILE_1_PATH = "Server/files/file1.txt";

    // Representa la ruta del archivo 1 de 250 MB
    private static final String FILE_2_PATH = "Server/files/file2.txt";

    //----------------------------------------------------------------------
    // ATTRIBUTES
    //----------------------------------------------------------------------

    //Indica el número de clientes.
    private static int numberOfClients;

    // Indica la ruta del archivo elegido a enviar.
    private static String fileOptionPath;

    //----------------------------------------------------------------------
    // MAIN
    //----------------------------------------------------------------------

    public static void main(String[] args){
        // Solicita el número de clientes que harán parte de la transferencia de archivos.
        getInputForClientNumber();
        System.out.println("");
        
        // Solicita qué archivo se desea enviar. 1 indica 100MB. 2 indica 250MB.
        getFileOptionInput();
        System.out.println("");

        deleteLogDirectory();
        // Inicializa y comienza cada thread que representa clientes que intentan conectarse al servidor.
        for( int i = 0; i<numberOfClients; i++){
            ServerThread s = new ServerThread(i, fileOptionPath);
            s.run();
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

    public static void getFileOptionInput(){
        Scanner scan = new Scanner(System.in);  // Create a Scanner object
        int selectedOption = 1;//default
        boolean moveOn = false;

        while (!moveOn) {
            System.out.println("File Options:");
            System.out.println("1. File 1");
            System.out.println("2. File 2");
            String fileOptStr = scan.nextLine();
            if (isNumeric(fileOptStr)) {
                selectedOption = Integer.parseInt(fileOptStr);
                if (selectedOption == 1 || selectedOption == 2) {
                    moveOn = true;
                }
                else{
                    System.out.println("Selected option doesnt exist. Needs to be 1 or 2");
                }
            }
            else{
                System.out.println("Selected option needs to be a number. Needs to be 1 or 2");
            }
        }

        if(selectedOption ==1){
            fileOptionPath = FILE_1_PATH;
        }
        else{
            fileOptionPath = FILE_2_PATH;
        }

    }
    public static void deleteLogDirectory(){
        File directory = new File("Server/Logs");
 
        for (File file: Objects.requireNonNull(directory.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
}
