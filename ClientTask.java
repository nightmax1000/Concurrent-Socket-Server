import java.io.*;

@SuppressWarnings("unused")
public class ClientTask implements Runnable{

    private int numDone = 0;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final DataInputStream serverIn;
    private final String command;
    private final String message;
    private String result;
    private String individualTime;
    private String total;
 
    //Constructor
    ClientTask(String message, String command, DataInputStream in, DataInputStream serverIn, DataOutputStream out){

        this.total = "0";
        this.message = message;
        this.in = in;
        this.serverIn = serverIn;
        this.out = out;
        this.command = command;

    }

    public String getTotal(){
        return this.total;
    }

    private void writeToClient(String result, String total){

        Client.printToTerminal(result);
        Client.printToTerminal("The turnaround time for process " + this.numDone + ": " + individualTime + "ms\n");

    }

    @Override
    public synchronized void run(){

        try{
            out.writeUTF(command);// Tells the server which command to execute
            this.result = this.serverIn.readUTF();// Gets the command result from the server
            this.individualTime = this.serverIn.readUTF();// Gets the turnaround time from the server
            this.numDone++;
            writeToClient(this.result, this.individualTime);//Writes the client output

            //Turnaround math
            int temp1 = Integer.parseInt(total);
            int temp2 = Integer.parseInt(individualTime);
            temp1 = temp1 + temp2;
            String addedTime = Integer.toString(temp1);
            total = addedTime;


        }
        catch(IOException e){
            System.out.println("There was an error completing the client task: " + e);
        }
        

    }

}
