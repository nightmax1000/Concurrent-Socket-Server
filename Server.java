import java.io.*;
import java.net.*;
import java.util.*;
//import java.lang.*;

@SuppressWarnings("unused")
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
    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

    //Setter for the console command
    public void setCommand(String command){
        this.command = command;
    }

    public String getCommand(){
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

    public synchronized boolean verifyRequest(int numRequests, String message){

        if(Integer.parseInt(message) >= 1 || Integer.parseInt(message) <= 6){
            System.out.println("Client option " + message + " verified");
            return true;
        }
        else if(!message.equals("7")){
            System.out.println("Invalid Input " + message + " From client, terminating.");
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

    public static void menu(Server server){
        try{
            while(!server.message.equals("7")){
                server.setMessage(server.in.readUTF());
                int numRequests = Integer.parseInt(server.in.readUTF());
                System.out.println("Test -> Client Chose Option: " + server.message + "\n" + "NumRequests: " + numRequests);

                boolean ready = server.verifyRequest(numRequests, server.message);//Ensures that the client sends valid request

                if(ready){
                    server.setCommand(server.in.readUTF());
                    System.out.println("Ready to transmit request: " + server.getCommand());
                    Server.startTask(server, numRequests);
                }

            }
        }
        catch(EOFException eof){System.out.println("End of datastream reached");}
        catch(IOException e){System.out.println("Error thrown in Server.java.main: " + e);}
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String args[]){

        Scanner userInput = new Scanner(System.in);
        boolean valid = false;
        int port;

        

        while(!valid){
            System.out.println("Welcome to the server program, User." + '\n');
            System.out.print("Please Specify Port Number: ");

            try{
                port = userInput.nextInt();
                userInput.nextLine();

                System.out.println("Connecting To Port: " + port);

                Server server = Server.initialize(port); //Sets up the server connection

                menu(server);
                
                valid = true;
                disconnect(server, port);

            }
            catch(IllegalArgumentException e){
                System.out.println("Illegal or ineligible port entered, please specify a valid port.\n");
                valid = false;
                userInput.reset();
            }
            

        }
        
        userInput.close();
        
    }


}


