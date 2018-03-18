package org.bionimbuzbox.ca;

public interface RootCertificate extends CertificateWithPrivateKey {

  public Signer signCsr(final CSR request);

}
