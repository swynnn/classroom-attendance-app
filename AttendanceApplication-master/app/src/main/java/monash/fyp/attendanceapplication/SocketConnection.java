package monash.fyp.attendanceapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 *  Class for socket connections between application server and mobile application
 */
public class SocketConnection {

    private Socket socket = null;
    private OutputStream toServer ;
    private InputStream fromServer ;

    /**
     * Constructor for SocketConnection class that creates a socket object to connect to the
     * server's socket.
     */
    public SocketConnection(String host, int port){

        try
        {
            socket = new Socket(host, port);
            System.out.println("Connected");

        }
        catch (UnknownHostException u)
        {
            System.out.println(u);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method to get the initial authorization request
     * @param req string containing the request
     * @return authorization request
     */
    public String request(String req){

        try {
            String message = "";
            toServer = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(toServer, true);
            writer.println(req);

            fromServer = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(fromServer));

            message = reader.readLine();
            System.out.println("readLine Success");

            return message;
        }  catch (Exception e){
            return e.toString();
        }

    }

    /***
     * Method to close the server connection
     */
    public void closeConnection(){
        try
        {
            toServer.close();
            fromServer.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

}
