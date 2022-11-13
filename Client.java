package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    String IP = "";
    int PORT;


    /*Podczas uruchomienia klient otrzymuje jako parametr adres IP jednego z węzłów
    sieci (dowolnego, zwanego dalej „węzłem kontaktowym”), numer portu TCP na którym ten węzeł nasłuchuje na zgłoszenia
    od klientów oraz listę zasobów do alokacji (w takim samym formacie jak opisany powyżej dla węzłów sieci).*/

    /*Po uzyskaniu połączenia klient przesyła w formie pojedynczej linii tekstu komunikat o następującym formacie:
    <identyfikator> <zasób>:<liczność> [<zasób>:liczność]*/

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket clientSocket = new Socket("localhost",9991);

        PrintWriter pr = new PrintWriter(clientSocket.getOutputStream());
        pr.println("171 A:5 C:3");
        System.out.println("171 A:5 C:3");
        pr.flush();

        InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String str = br.readLine();
        System.out.println(str);
        str = br.readLine();
        System.out.println(str);
    }
}
