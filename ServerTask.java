//import java.io.*;

@SuppressWarnings("unused")
public class ServerTask implements Runnable{

    private final String message;
    private final String command;

    //Constructor
    public ServerTask(String message, String command){
        this.message = message;
        this.command = command;

    }

    @Override
    public synchronized void run(){

        //Execute Server Command??
        
    }

}
