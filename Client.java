import java.io.*;
import java.net.*;
import java.util.*;

@SuppressWarnings("deprecation")
public class Client {
  
    // Initialize socket and input/output streams
    private Socket socket;
    private DataInputStream in;
    private DataInputStream serverIn;
    private DataOutputStream out;
    static Scanner userInput = new Scanner(System.in);

    //Constructor
    public Client(String address, int port)
    {
        // Establish a connection
        try {
            this.socket = new Socket(address, port);
            System.out.println("Connected\n");

            // Takes input from terminal
            this.in = new DataInputStream(System.in);

            // Takes input from Server
            this.serverIn = new DataInputStream(socket.getInputStream());


            // Sends output to the socket
            this.out = new DataOutputStream(socket.getOutputStream());
        }
        catch (UnknownHostException e) {
            System.out.println(e);
            return;
        }
        catch (IOException e) {
            System.out.println(e);
            return;
        }

        // String to read message from input
        String message = "";

        menu(this, in, serverIn, out, message);

        System.out.println("\nEnding Program\n");

        // Close the connection
        try {
            in.close();
            serverIn.close();
            out.close();
            socket.close();
        }
        catch (IOException i) {
            System.out.println("Error thrown in the client constructor: " + i);
        }
    }


    public static void printToTerminal(String message){

        System.out.println(message);

    }

    // Recursively calls the menu which prompts user input
    static void menu(Client client, DataInputStream in, DataInputStream serverIn, DataOutputStream out, String message){

        // Keep reading until "7" is input
        while (!message.equals("7")) {
            try {

                System.out.println("Please Enter Number Corresponding To Desired Command");
                System.out.println("1) Date and Time\n2) Uptime\n3) Memory Use\n4) Netstat\n5) Current Users\n6) Running Process\n7) Exit Program");
                System.out.print("Selection: ");
                message = in.readLine();
                out.writeUTF(message); //Sends the server the notification of which menu option was chosen
                System.out.println();
                int numRequests;
                String command;

                switch(message){

                    case "1":
                        //System.out.println("Testing Case: " + message);
                        command = "date";
                        numRequests = request();
                        out.writeUTF(Integer.toString(numRequests)); //Tells the server the number of requests being made by the client

                        //System.out.println("Sending " + numRequests + " requests to the server.\n");

                        //getData(client, message, command, in, serverIn, out, numRequests);

                        message = "-1";

                        break;
                    case "2":
                        //System.out.println("\nTesting Case: " + message + "\n");
                        command = "uptime -p";
                        numRequests = request();
                        out.writeUTF(Integer.toString(numRequests)); //Tells the server the number of requests being made by the client

                        //System.out.println("Sending " + numRequests + " requests to the server.\n");

                        //getData(client, message, command, in, serverIn, out, numRequests);

                        message = "-1";

                        break;
                    case "3":
                        //System.out.println("\nTesting Case: " + message + "\n");
                        command = "cat /proc/meminfo";
                        numRequests = request();
                        out.writeUTF(Integer.toString(numRequests)); //Tells the server the number of requests being made by the client

                        //System.out.println("Sending " + numRequests + " requests to the server.\n");

                        //getData(client, message, command, in, serverIn, out, numRequests);

                        message = "-1";


                        break;
                    case "4":
                        //System.out.println("\nTesting Case: " + message + "\n");
                        command = "netstat -atun";
                        numRequests = request();
                        out.writeUTF(Integer.toString(numRequests)); //Tells the server the number of requests being made by the client

                        //System.out.println("Sending " + numRequests + " requests to the server.\n");

                        //getData(client, message, command, in, serverIn, out, numRequests);

                        message = "-1";

                        break;
                    case "5":
                        //System.out.println("\nTesting Case: " + message + "\n");
                        command = "users";
                        numRequests = request();
                        out.writeUTF(Integer.toString(numRequests)); //Tells the server the number of requests being made by the client

                        //System.out.println("Sending " + numRequests + " requests to the server.\n");

                        //getData(client, message, command, in, serverIn, out, numRequests);

                        message = "-1";

                        break;
                    case "6":
                        command = "ps -e";
                        numRequests = request();
                        out.writeUTF(Integer.toString(numRequests)); //Tells the server the number of requests being made by the client

                        //System.out.println("Sending " + numRequests + " requests to the server.\n");

                        //getData(client, message, command, in, serverIn, out, numRequests);

                        message = "-1";
                        break;
                    case "7":
                        System.out.println("Have a good day, User.\n");
                        continue;
                    default:
                        System.out.println("Please Enter A Valid Number");
                }

            }
            catch (IOException i) {
                System.out.println("Error thrown in client's menu method: " + i);
                message = "7";
            }
        }

    }

    static void getData(Client client, String message, String command, DataInputStream in, DataInputStream serverIn, DataOutputStream out, int numRequests){

        // This method starts the number of threads indicated by numRequests,
        // then each thread calls to the server based on the operation chosen by the client. 
        ArrayList<Thread> threads = new ArrayList<>();
        ClientTask runnable = new ClientTask(message, command, in, serverIn, out);

  
        for(int i = 0; i < numRequests; i++){
                
            Thread t = new Thread(runnable);
            threads.add(t);          
        }

        for(int i = 0; i < numRequests; i++){
            threads.get(i).start();
        }

        for(int i = 0; i < numRequests; i++){
            try{
                threads.get(i).join();
            }
            catch(InterruptedException e){}
            
        }

        System.out.println("The total turnaround time was: " + runnable.getTotal() + "ms");
        int average = Integer.parseInt(runnable.getTotal())/numRequests;
        System.out.println("The average turnaround time was: " + average + "ms\n");
        

    }    

    // When called, this method gathers the number of requests desired by the user
    static int request(){

        System.out.print("Please Enter Desired Number Of Requests\nOptions: 1, 5, 10, 15, 20, 25\nSelection: ");
        int numSessions;

        // Attempts to gather input, repeats until the user enters a valid value.
        while(true){

            try{

                numSessions = userInput.nextInt();
                System.out.println();
                

                if(numSessions == 1 
                    || numSessions == 5 
                    || numSessions == 10 
                    || numSessions == 15 
                    || numSessions == 20 
                    || numSessions == 25){
                    break;
                }
                else{
                    System.out.print("Invalid value, please enter a number listed: ");
                }
            }
            catch(InputMismatchException e){

                System.out.println("Invalid input, please enter an integer.");

            }
        }

        return numSessions;
    }

    public static void main(String[] args) {

        // Prompt user for Network Address and store it
        System.out.println("Welcome to the client program, User." + '\n');
        System.out.print("Please Enter Server Network Address: ");

        

        // Captures the address given by the user
        String address = userInput.nextLine();

        // Captures port number from input
        System.out.print("Please Enter Server Port: ");
        int port = userInput.nextInt();
        userInput.nextLine();

        // Sends input to the client class, connecting to the server
        @SuppressWarnings("unused")
        Client client = new Client(address, port);

        userInput.close();

    }
}