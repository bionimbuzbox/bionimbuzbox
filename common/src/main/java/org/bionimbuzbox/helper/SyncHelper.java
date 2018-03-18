package org.bionimbuzbox.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;

public class SyncHelper {

  public SyncHelper() {
    // TODO Auto-generated constructor stub
  }
  
  public static void scp(String from, String to) {
    Scp scp = new Scp();
    
    int portSSH = 22;
    String srvrSSH = "ssh.your.domain";
    String userSSH = "anyuser"; 
    String pswdSSH = new String (  );
    String localFile = "C:\\localfile.txt";
    String remoteDir = "/uploads/";

    //scp.setKeyfile(keyfile);
    scp.setPort( portSSH );
    scp.setLocalFile( localFile );
    scp.setTodir( userSSH + ":" + pswdSSH + "@" + srvrSSH + ":" + remoteDir );
    //scp.setProject( new Project() );
    scp.setTrust( true );
    scp.execute();
  }

  public static void sync(String from, String to, Path key) {

    String[] cmd = new String[]{
        "/bin/sh",
        "-c",
        String.format("rsync -az -e \"ssh -i %s\" %s %s", key, from, to)
    };
    
    try {
      ProcessBuilder pb = new ProcessBuilder(cmd);
      Process p = pb.start();
      BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      
      String ligne = "";

      while ((ligne = output.readLine()) != null) {
          System.out.println(ligne);
      }

      while ((ligne = error.readLine()) != null) {
       System.out.println(ligne);
      }
      
      int val = p.waitFor();
      if (val != 0) {
        throw new Exception("Exception during RSync; return code = " + val);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  public static void main(String[] args) {
    // ~/.docker/machine/machines/t1/id_rsa docker@192.168.99.100
    
    String from = "/Users/tiago/work/eclipse-workspace/main/id_rsa-*";
    String to = "docker@192.168.99.100:/tmp";
    Path key = Paths.get("/Users/tiago/.docker/machine/machines/t1/id_rsa");
    
    sync(from, to, key);
  }

}
