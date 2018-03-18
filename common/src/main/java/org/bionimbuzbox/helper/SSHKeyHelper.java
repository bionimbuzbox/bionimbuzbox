package org.bionimbuzbox.helper;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import java.io.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSHKeyHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSHKeyHelper.class);

	public static final int DEFAULT_KEY_SIZE = 2048;
	public static final String DEFAULT_COMMENT = "BioNimbuzBox";
	
	public static SSHKeyPair generateSSHKeyPair() {
	  return generateSSHKeyPair(DEFAULT_COMMENT, DEFAULT_KEY_SIZE);
	}
	
	public static SSHKeyPair generateSSHKeyPair(String comment) {
    return generateSSHKeyPair(comment, DEFAULT_KEY_SIZE);
  }
	
	public static SSHKeyPair generateSSHKeyPair(int keySize) {
    return generateSSHKeyPair(DEFAULT_COMMENT, keySize);
  }

	public static SSHKeyPair generateSSHKeyPair(String comment, int keySize) {
	  SSHKeyPair sshKeyPair = null;
	  ByteArrayOutputStream publicKeyOS = new ByteArrayOutputStream();
	  ByteArrayOutputStream privateKeyOS = new ByteArrayOutputStream();
    
    JSch jsch = new JSch();

    try{
      KeyPair keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA, keySize);
      keyPair.writePublicKey(publicKeyOS, comment);
      keyPair.writePrivateKey(privateKeyOS);
      
      sshKeyPair = new SSHKeyPair(publicKeyOS.toString(), 
          privateKeyOS.toString(), keyPair.getFingerPrint());
      
      publicKeyOS.close();
      privateKeyOS.close();
      keyPair.dispose();
      
      LOGGER.info("KeyPair generated! The fingerprint is: {}", sshKeyPair.getFingerPrint());
    }
    catch(Exception e){
      LOGGER.error(e.getMessage(), e);
    }
    return sshKeyPair;
	}
	
	public static class SSHKeyPair {
	  private final String publicKey; 
	  private final String privateKey;
	  private final String fingerPrint;
	  
	  public SSHKeyPair(String publicKey, String privateKey, String fingerPrint) {
      this.publicKey = publicKey;
      this.privateKey = privateKey;
      this.fingerPrint = fingerPrint;
    }

    public String getPublicKey() {
      return publicKey;
    }

    public String getPrivateKey() {
      return privateKey;
    }

    public String getFingerPrint() {
      return fingerPrint;
    }
	  
	}
	
	public static void main(String[] args) {
		SSHKeyPair keyPair = SSHKeyHelper.generateSSHKeyPair(1024);
		System.out.println(keyPair.getPrivateKey());
		System.out.println(keyPair.getPublicKey());

		LOGGER.info(keyPair.getPrivateKey());
		LOGGER.info(keyPair.getPublicKey());
		LOGGER.info(keyPair.getFingerPrint());
	}
}
