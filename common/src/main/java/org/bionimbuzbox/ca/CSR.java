package org.bionimbuzbox.ca;

import java.security.PublicKey;

public interface CSR {

  public DistinguishedName getSubject();

  public PublicKey getPublicKey();

}
