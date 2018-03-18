package org.bionimbuzbox.ca;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import org.bionimbuzbox.ca.Signer.SignerWithSerial;
import org.bionimbuzbox.ca.ext.CrlDistPointExtension;
import org.bionimbuzbox.ca.ext.KeyUsageExtension;
import org.bionimbuzbox.ca.ext.KeyUsageExtension.KeyUsage;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;

class RootCertificateBuilderImpl implements RootCertificateBuilder {

  private String crlUri = null;

  private final KeyPair pair;
  private final SignerWithSerial signer;

  RootCertificateBuilderImpl(final DistinguishedName subject) {
    pair = KeysUtil.generateKeyPair();
    signer = new SignerImpl(pair, subject, pair.getPublic(), subject)
        .setRandomSerialNumber();
  }

  @Override
  public RootCertificateBuilder setNotBefore(final ZonedDateTime notBefore) {
    signer.setNotBefore(notBefore);
    return this;
  }

  @Override
  public RootCertificateBuilder setNotAfter(final ZonedDateTime notAfter) {
    signer.setNotAfter(notAfter);
    return this;
  }

  @Override
  public RootCertificateBuilder validDuringYears(final int years) {
    signer.validDuringYears(years);
    return this;
  }

  @Override
  public RootCertificateBuilder setCrlUri(final String crlUri) {
    this.crlUri = crlUri;
    return this;
  }

  @Override
  public RootCertificate build() {
    signer.addExtension(KeyUsageExtension.create(
        KeyUsage.KEY_CERT_SIGN,
        KeyUsage.CRL_SIGN));

    if (crlUri != null) {
      signer.addExtension(CrlDistPointExtension.create(crlUri));
    }

    // This is a CA
    signer.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

    final X509Certificate rootCertificate = signer.sign().getX509Certificate();

    return new RootCertificateImpl(rootCertificate, pair.getPrivate());
  }

}
