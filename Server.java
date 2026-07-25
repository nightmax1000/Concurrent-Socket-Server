import java.io.*;
import java.net.*;
import java.util.Scanner;
//import java.lang.*;

public class Server{

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private String message = null;
    private String command = null;

    @SuppressWarnings({"deprecation", "ConvertToTryWithResources"})
    private void sendData(String command, DataInputStream in, DataOutputStream out){

        //System.out.println("Testing Case: " + message);

        try{

            command = in.readUTF();
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

    private synchronized void verifyRequest(String message, String command, DataInputStream in, DataOutputStream out){

                    while(!message.equals("7")){

                try{
                    message = in.readUTF();

                    switch(message){

                        case "1":
                            sendData(command, in, out);
                            break;
                        case "2":
                            sendData(command, in, out);
                            break;
                        case "3":
                            sendData(command, in, out);
                            break;
                        case "4":
                            sendData(command, in, out);
                            break;
                        case "5":
                            sendData(command, in, out);
                            break;
                        case "6":
                            sendData(command, in, out);
                            break;
                        case "7":
                            continue;
                        default:
                            System.out.println("Invalid Input " + message + " From client, terminating.");
                            message = "7";
                        }
                }
                catch(IOException e){
                    message = "7";
                }
                
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

    //Constructor
    public Server(int port){

        try{

            //After a successful initialization, this completes the server connection
            this.serverSocket = new ServerSocket(port);
            this.socket = serverSocket.accept();
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new DataOutputStream(socket.getOutputStream());

            //A message to the server console to track each instance
            System.out.println("A new server instance was created on port: " + port);

            //The message is the client choice, the command is the Linux/Unix command desired
            this.message = "";
            this.command = "";


            verifyRequest(this.message, this.command, this.in, this.out);


            //After the server has completed the command, end connection
            System.out.println("Ending client connection from port: " + port);
            socket.close();
            in.close();
            out.close();
        }
        catch(IOException e){

            System.out.println(e);

        }

    }

    public static Server initialize(int port){

        Server server = new Server(port);

        return server;

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

                @SuppressWarnings("unused")
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


