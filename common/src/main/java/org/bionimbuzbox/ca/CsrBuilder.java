package org.bionimbuzbox.ca;

public interface CsrBuilder {

  public CsrWithPrivateKey generateRequest(DistinguishedName name);

}
