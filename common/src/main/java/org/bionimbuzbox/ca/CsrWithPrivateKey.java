package org.bionimbuzbox.ca;

import java.security.PrivateKey;

public interface CsrWithPrivateKey extends CSR {

  public PrivateKey getPrivateKey();
}
