import java.io.*;
import java.net.*;
import java.util.Scanner;
//import java.lang.*;

public class Server{

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    @SuppressWarnings("deprecation")
    private void sendData(String message, String command, DataInputStream in, DataOutputStream out){

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

            if(result != ""){
                System.out.println("Command Error: " + result);
                System.out.println("Client request failed.");
            }
            else{System.out.println("Client request complete");}

            stdInput.close();
            stdError.close();

        }
        catch(IOException e){}
        
        

    }

    public Server(int port){

        try{

            System.out.println("Initializing server");

            serverSocket = new ServerSocket(port);
            System.out.println("Server started" );

            System.out.println("Waiting for the client...");

            socket = serverSocket.accept();
            System.out.println("Client accepted");

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());

            String message = "";
            String command = "";

            while(!message.equals("7")){

                try{
                    message = in.readUTF();

                    switch(message){

                        case "1":
                            sendData(message, command, in, out);
                            break;
                        case "2":
                            sendData(message, command, in, out);
                            break;
                        case "3":
                            sendData(message, command, in, out);
                            break;
                        case "4":
                            sendData(message, command, in, out);
                            break;
                        case "5":
                            sendData(message, command, in, out);
                            break;
                        case "6":
                            sendData(message, command, in, out);
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
                    continue;
                }
                
            }

            System.out.println("Ending Client Connection");

            socket.close();
            in.close();
            out.close();


        }
        catch(IOException e){

            System.out.println(e);

        }


    }

    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String args[]){

        Scanner userInput = new Scanner(System.in);
        boolean valid = false;
        int port = -1;

        

        while(!valid){
            System.out.println("Welcome to the server program, User." + '\n');
            System.out.print("Please Specify Port Number: ");

            try{
                port = userInput.nextInt();
                userInput.nextLine();

                System.out.println("Connecting To Port: " + port);

                @SuppressWarnings("unused")
                Server server = new Server(port);

                valid = true;

            }
            catch(IllegalArgumentException e){
                System.out.println("Illegal Port entered, please specify a valid port.\n");
                valid = false;
                userInput.reset();
            }
            

        }
        


        userInput.close();
        
    }


}


