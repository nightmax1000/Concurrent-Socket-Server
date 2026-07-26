import java.io.*;
import java.net.*;
import java.util.*;
//import java.lang.*;

public class Server{

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private String message;
    private String command;
    private int numRequests;

    //Constructor
    public Server(int port){

        try{

            //After a successful initialization, this completes the server connection
            this.serverSocket = new ServerSocket(port);
            this.socket = serverSocket.accept();
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new DataOutputStream(socket.getOutputStream());
            this.message = "";
            this.command = "";
            this.numRequests = 0;
            //A message to the server console to track each instance
            System.out.println("Connection Established");

        }
        catch(IOException e){

            System.out.println(e);

        }

    }

    /**
    * @author Marcus Nix
    * @param port The port for the socket connection
    * 
    * This method starts the server by connecting it to a newly define port
    */
    public static Server initialize(int port){

        Server server = new Server(port);

        return server;

    }

    public DataInputStream getInput(){
        return this.in;
    }

    public DataOutputStream getOutput(){
        return this.out;
    }

    //Setter for the client message
    public synchronized void setMessage(String message){
        this.message = message;
    }

    public synchronized String getMessage(){
        return this.message;
    }

    //Setter for the console command
    public synchronized void setCommand(String command){
        this.command = command;
    }

    public synchronized String getCommand(){
        return this.command;
    }

    public static void startTask(Server server, int numRequests){

        ArrayList<Thread> threads = new ArrayList<>();
        ServerTask runnable = new ServerTask(server);

        for(int i = 0; i < numRequests; i++){
            Thread t = new Thread(runnable);
            threads.add(t);
        }

        for(int i = 0; i < numRequests; i++){
            //Use this snippet to start a new ServerTask
            threads.get(i).start();
        }

        // Get the threads to reconnect with each other
        for(int i = 0; i < numRequests; i++){
            try{
                threads.get(i).join();
            }
            catch(InterruptedException e){}       
        }



        //server.sendData(server.command, server.in, server.out);

    }

    public boolean verifyRequest(Server server, int numRequests, String message){

        System.out.println("Test -> Client Chose Option: " + server.message + " " + "NumRequests: " + server.numRequests);

        if((message.contains("0-9") && !message.contains("A-Za-z")) || Integer.parseInt(message) >= 1 || Integer.parseInt(message) <= 6){
            System.out.println("Client option " + message + " verified");
            return true;
        }
        else{
            try{
                server.setMessage(Integer.toString(numRequests));
                server.numRequests = Integer.parseInt(server.in.readUTF());
            }
            catch(IOException e){
                System.out.println("The client option came in incorrect but the program was unable to resolve itself. Try again.");
            }
        }

        return false;
    }

    public static void disconnect(Server server, int port){

        try{
            server.socket.close();
            server.in.close();
            server.out.close();

            System.out.println("Server instance disconnected from port: " + port);

        }
        catch(IOException e){
            System.out.println(e);
        }
        
    }

    public static boolean menu(Server server){
        try{
            String message;
            String numRequests;

            message = server.in.readUTF();
            numRequests = server.in.readUTF();

            while(!server.message.equals("7")){

                if(message.matches("[1-7]")){

                    System.out.println("verified");

                    server.setMessage(message);
                    server.numRequests = Integer.parseInt(numRequests);

                    boolean ready = server.verifyRequest(server, server.numRequests, server.message);//Ensures that the client sends valid request

                    if(ready){
                        server.setCommand(server.in.readUTF());
                        System.out.println("Ready to transmit request: " + server.getCommand());
                        Server.startTask(server, server.numRequests);
                    }

                }
                else{
                    message = numRequests;
                    numRequests = server.in.readUTF();
                    continue;
                }

                message = server.in.readUTF();
                numRequests = server.in.readUTF();

                
            }
            return true;
        }
        catch(EOFException eof){
            System.out.println("\n\nEnding Program");
            return false;
        }
        catch(IOException e){
            System.out.println("Error thrown in Server.java.main: " + e);
            return false;
        }
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String args[]){

        Scanner userInput = new Scanner(System.in);
        boolean valid = false;
        int port;
        Server server = null;

        System.out.println("Welcome to the server program, User." + '\n');
        System.out.print("Please Specify Port Number: ");

        while(!valid){
            try{
                port = userInput.nextInt();
                userInput.nextLine();

                System.out.println("Connecting To Port: " + port);

                server = Server.initialize(port); //Sets up the server connection 

                valid = true;

            }
            catch(IllegalArgumentException e){
                System.out.println("Illegal or ineligible port entered, please specify a valid port.\n");
                valid = false;
                userInput.reset();
            }
        }

        boolean done = false;
        while(!done){
            try{
                menu(server);
                done = true;
            }
            catch(IllegalArgumentException e){
                done = false;
            }
            
        }

        
        userInput.close();
        
    }


}


