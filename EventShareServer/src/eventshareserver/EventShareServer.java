/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventshareserver;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Miteto
 */
public class EventShareServer {

    public static boolean running;
    public static int imgNum = 0;
    
	public static void main(String [] args)
	{
            
                try {
                    int portNo = 1111;
                    int numClients = 0;
                    running = true;

                    // instantiates a socket for accepting connection  	
                    ServerSocket connectionSocket = new ServerSocket(portNo);
/**/                System.out.println("Now ready to accept a connection");  

                    while(running)
                    {
                            Socket clientSocket = connectionSocket.accept();
                            numClients++;
                            imgNum++;
/**/                        System.out.println("Connection accepted from client #" + numClients);
                            ImageThread clientThread = new ImageThread(clientSocket, imgNum);
                            clientThread.start();
                            System.out.println(Thread.activeCount());
                    }
                    connectionSocket.close();
/**/                System.out.println("Connection socket closed");

                    // Code appears to hang if the main finishes before all threads have completed.
                    // Stop the main finishing until the clients are all finished with
                    while(Thread.activeCount() >= 1)
                    {
                            System.out.println("Waiting for remaining clients to finish");
                            Thread.sleep(500);
                    }
                } // end try
                catch (Exception ex) {
                        ex.printStackTrace( );
                } // end catch
            } // end else
    
}
