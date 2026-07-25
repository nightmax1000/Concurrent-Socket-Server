import java.io.*;
import java.net.*;
import java.util.Scanner;
//import java.lang.*;

@SuppressWarnings("unused")
public class Server{

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private String message;
    private String command;

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
            //A message to the server console to track each instance
            System.out.println("A new server instance was created on port: " + port);

        }
        catch(IOException e){

            System.out.println(e);

        }

    }

    /**
    * @author Marcus Nix
    * @param port The port for the socket connecttion
    * 
    * This method creates a new server instance by connecting to a newly define port
    */
    public static Server initialize(int port){

        Server server = new Server(port);

        return server;

    }


    //Setter for the client message
    public void setMessage(String message){
        this.message = message;
    }

    //Setter for the console command
    public void setCommand(String command){
        this.command = command;
    }



    @SuppressWarnings({"deprecation", "ConvertToTryWithResources"})
    private void sendData(String command, DataInputStream in, DataOutputStream out){

        //System.out.println("Testing Case: " + message);

        try{
            //System.out.println(command + " command received.");
            long start = System.currentTimeMillis();
            Process p = Runtime.getRuntime().exec(command);
            long end = System.currentTimeMillis();
            String total = String.valueOf(end - start);
                    

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));


            String s;
            String result = "";
            while((s = stdInput.readLine()) != null){

                result = result.concat("\n" + s);
            }

            if(result != null){
                //System.out.println(result + " was the command executed");
                out.writeUTF(result);
                //System.out.println(total + "ms was the execution time.");
                out.writeUTF(total);
                result = "";
            }
                        

            while((s = stdError.readLine()) != null){

                result = result.concat(s);
            }

            if(!result.equals("")){
                System.out.println("Command Error: " + result);
                System.out.println("Client request failed.");
            }
            else{System.out.println("Client request complete");}

            stdInput.close();
            stdError.close();

        }
        catch(IOException e){}
        
        

    }

    public synchronized void verifyRequest(String message, String command, DataInputStream in, DataOutputStream out){

            if(Integer.parseInt(message) >= 1 || Integer.parseInt(message) <= 6){
                sendData(command, in, out);
            }
            else if(!message.equals("7")){
                System.out.println("Invalid Input " + message + " From client, terminating.");
            }
    }

    public void disconnect(Server server, int port){

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

                valid = true;

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


