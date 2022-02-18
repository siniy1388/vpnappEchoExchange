package filegetclient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class fileClient implements Runnable {

  public final static int SOCKET_PORT = 16445;
  public final static String SERVER = "localhost";
  public static String FILE_TO_RECEIVED ;

  public final static int FILE_SIZE = Integer.MAX_VALUE;
    int bytesRead;
    int current;
    FileOutputStream fos ;
    BufferedOutputStream bos;
    Socket sock;
  
  public fileClient () { 
    current = 0;
    fos = null;
    bos = null;
    sock = null;
    FILE_TO_RECEIVED = System.getProperty("user.dir")
            +File.separator+"client.conf";
  }  

  @Override
  public void run() { 
    try {
      sock = new Socket(SERVER, SOCKET_PORT);
      System.out.println("Connecting...");

      // receive file
      byte [] mybytearray  = new byte [FILE_SIZE];
      InputStream is = sock.getInputStream();
      fos = new FileOutputStream(FILE_TO_RECEIVED);
      bos = new BufferedOutputStream(fos);
      bytesRead = is.read(mybytearray,0,mybytearray.length);
      current = bytesRead;

      do {
         bytesRead =
            is.read(mybytearray, current, (mybytearray.length-current));
         if(bytesRead >= 0) current += bytesRead;
      } while(bytesRead > -1);

      bos.write(mybytearray, 0 , current);
      bos.flush();
      System.out.println("File " + FILE_TO_RECEIVED
          + " downloaded (" + current + " bytes read)");
    }
      catch (IOException ex) {
          Logger.getLogger(fileClient.class.getName()).log(Level.SEVERE, null, ex);
      }    finally {
      if (fos != null) try {
          fos.close();
      } catch (IOException ex) {
          Logger.getLogger(fileClient.class.getName()).log(Level.SEVERE, null, ex);
      }
      if (bos != null) try {
          bos.close();
      } catch (IOException ex) {
          Logger.getLogger(fileClient.class.getName()).log(Level.SEVERE, null, ex);
      }
      if (sock != null) try {
          sock.close();
      } catch (IOException ex) {
          Logger.getLogger(fileClient.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }    
}