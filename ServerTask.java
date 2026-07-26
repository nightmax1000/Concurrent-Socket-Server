import java.io.*;

@SuppressWarnings("unused")
public class ServerTask implements Runnable{

    private final String message;
    private final String command;
    private final Server server;

    //Constructor
    public ServerTask(Server server){
        this.message = server.getMessage();
        this.command = server.getCommand();
        this.server = server;

    }

    @Override
    @SuppressWarnings({ "ConvertToTryWithResources", "deprecation" })
    public synchronized void run(){

        try{
            //System.out.println(command + " command received.");
            long start = System.currentTimeMillis();
            Process p = Runtime.getRuntime().exec(this.command);
            long end = System.currentTimeMillis();
            String total = String.valueOf(end - start);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s;
            String result = "";
            DataInputStream in = this.server.getInput();
            DataOutputStream out = this.server.getOutput();

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

}
