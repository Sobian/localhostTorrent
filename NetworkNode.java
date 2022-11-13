import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//todo: zebranie informacji od pozostalych wezlow sieci

public class NetworkNode {

    static protected int ID;
    static protected int TCPport;
    static protected String gateway;
    static protected String gatewayPort;
    static protected boolean isFirstNode = false;


    static ServerSocket nodeServer;
    static Socket nodeSocket;
    static PrintWriter nodeInfoPrintWriter;
    static BufferedReader bufferedReaderNode;
    static ServerSocket serverSocket;
    static Socket clientSocket;
    static PrintWriter respondToClient;
    static BufferedReader bufferedReaderClient;
    static String clientRequest;
    static String nodeInfoString;
    static int clientID;
    static String[] splittedClientMessage;
    static String[] arrayOfRequests;
    static HashMap<Integer, ArrayList<Resource>> mapOfNetwork = new HashMap<>();
    static ArrayList<Resource> listOfAssgnedResources = new ArrayList<>();
    static ArrayList<Resource> listOfResources = new ArrayList<>();

    public static void receiveInfoFromNode() {

    }

    //todo
    public static void sendInfoToNode(String gateway, String gatewayPort) throws IOException {
        nodeSocket = new Socket(gateway, Integer.parseInt(gatewayPort));
        respondToClient.println(listOfResources);
        respondToClient.flush();
        nodeSocket.close();
    }

    public static void receiveClientRequest() throws IOException {
        String tempClientRequest = bufferedReaderClient.readLine();
        clientRequest = tempClientRequest;
    }

    public static int checkRequestLenght() {
        int count = 0;
        for (int i = 0; i < clientRequest.length(); i++) {
            if (clientRequest.charAt(i) == ' ')
                count++;
        }
        return count;
    }

    public static String[] splitClientMessage() {
        splittedClientMessage = new String[checkRequestLenght() + 1];
        for (int i = 0; i < splittedClientMessage.length; i++) {
            splittedClientMessage[i] = clientRequest.split(" ")[i];
        }
        return splittedClientMessage;
    }

    public static void createArrayOfRequsts() {
        arrayOfRequests = new String[splitClientMessage().length - 1];
        for (int i = 1; i < splitClientMessage().length; i++) {
            String temp = splittedClientMessage[i];
            arrayOfRequests[i - 1] = temp;
        }
    }

    public static Resource createResources(char name, int volume) {
        return new Resource(name, volume);
    }

    public static void assignResourcesToClient(int ID, Resource r) {
        listOfAssgnedResources.add(r);
        mapOfNetwork.put(ID, listOfAssgnedResources);
    }

    public static boolean findResourcesByNameVol(char name, int volume) {
        boolean status = false;
        for (int i = 0; i < listOfResources.size(); i++) {
            if (listOfResources.get(i).getName() == name && listOfResources.get(i).getVolume() <= volume)
                status = true;
        }
        return status;
    }

    public static void modifyVolumeOfResource(int idx, int RequestedAmount) {
        Resource modifiedRes = new Resource(arrayOfRequests[idx].charAt(0), listOfResources.get(idx).getVolume() - Integer.parseInt(arrayOfRequests[idx].split(":")[1]));
        listOfResources.add(idx, modifiedRes);
        listOfResources.get(idx).setVolume(listOfResources.get(idx).getVolume() - Integer.parseInt(arrayOfRequests[idx].split(":")[1]));
    }

    //todo
//    public static void gatherInfo() throws IOException {
//        while (bufferedReader!=null){
//            nodeInfo += bufferedReader.readLine();
//        }
//    }

    public static void sendResponse() throws IOException {
        for (int i = 0; i < arrayOfRequests.length; i++) {
            if (mapOfNetwork.containsKey(clientID)) {
                respondToClient.println(listOfAssgnedResources.get(i) + ":" + clientSocket.getInetAddress().getHostName() + ":" + TCPport);
                respondToClient.flush();
            } else {
                respondToClient.println("FAILED");
                respondToClient.flush();
            }
        }
        clientSocket.close();
    }


    public static void main(String[] args) throws IOException {

        ID = Integer.parseInt(args[1]);
        TCPport = Integer.parseInt(args[3]);
        //sprawdzenie czy NetworkNode jest wezlem kontaktowym
        if (!args[4].equals("-gateway")) {
            gateway = null;
            gatewayPort = null;
            isFirstNode = true;
        } else {
            gateway = args[5].split(":")[0];
            gatewayPort = args[5].split(":")[1];
        }

        //dodanie zasobow wynikajacych z parametrow
        if (isFirstNode) {
            for (int i = 4; i < args.length; i++) {
                listOfResources.add(i - 4, createResources(args[i].split(":")[0].charAt(0), Integer.parseInt(args[i].split(":")[1])));
                System.out.println(listOfResources.get(i - 4));
            }
        } else {
            for (int i = 6; i < args.length; i++) {
                listOfResources.add(i - 6, createResources(args[i].split(":")[0].charAt(0), Integer.parseInt(args[i].split(":")[1])));
                System.out.println(listOfResources.get(i - 6));
            }
        }

//        if (isFirstNode) {
//            try{
//                nodeServer = new ServerSocket()
//            }
//            catch (){
//
//            }
//        } else {
//            sendInfoToNode(gateway, gatewayPort);
//        }

        try {
            serverSocket = new ServerSocket(TCPport);
            clientSocket = serverSocket.accept();
            System.out.println("connection from " + clientSocket.getInetAddress().getHostName() + ":" + clientSocket.getPort());
            respondToClient = new PrintWriter(clientSocket.getOutputStream());
            bufferedReaderClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            System.out.println("[NetworkNode] connetion error");
        }



        receiveClientRequest();
        splitClientMessage();
        clientID = Integer.parseInt(splittedClientMessage[0]);
        createArrayOfRequsts();

        System.out.println("array of requested resources: " + Arrays.toString(arrayOfRequests));
        System.out.println("list of available resources before assignment: " + listOfResources);

        for (int i = 0; i < listOfResources.size(); i++) {
            //jesli utworzone zasoby zawieraja zasob o danej nazwie i pojemnosci wiekszej/rownej zapotrzebowaniu klienta
            if (findResourcesByNameVol(arrayOfRequests[i].charAt(0), Integer.parseInt(arrayOfRequests[i].split(":")[1]))) {
                assignResourcesToClient(clientID, listOfResources.get(i));
            }
            int currentVol = listOfResources.get(i).getVolume();
            int requiredVol = Integer.parseInt(arrayOfRequests[i].split(":")[1]);
            Resource modifiedRes = new Resource(arrayOfRequests[i].charAt(0), currentVol - requiredVol);
            Resource modfiedRes = new Resource(listOfResources.get(i).getName(), currentVol-requiredVol);
            listOfResources.set(i, modfiedRes);
        }

        System.out.println("map after assignment [ID=[Resource]]: " + mapOfNetwork.entrySet());
        System.out.println("list of available resources after assignment: " + listOfResources);
        //System.out.println("list of assigned resources: " + listOfAssgnedResources);
        sendResponse();


    }
}
