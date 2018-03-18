package org.bionimbuzbox.ca;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

final class RootCertificateLoader {
  private RootCertificateLoader() {
  }

  static RootCertificateImpl loadRootCertificate(final String keystorePath,
      final char[] password, final String alias) {
    final File file = new File(keystorePath);
    return loadRootCertificate(file, password, alias);
  }

  static RootCertificateImpl loadRootCertificate(final File keystoreFile,
      final char[] password, final String alias) {
    try {
      final KeyStore keystore = KeyStore.getInstance(RootCertificateImpl.KEYSTORE_TYPE);
      try (InputStream stream = new FileInputStream(keystoreFile)) {
        keystore.load(stream, password);
        return loadRootCertificate(keystore, alias);
      }
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
      throw new CaException(e);
    }
  }

  static RootCertificateImpl loadRootCertificate(final KeyStore keystore, final String alias) {
    try {
      final Certificate certificate = keystore.getCertificate(alias);
      final PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, null);
      if (certificate == null || privateKey == null)
        throw new CaException("Keystore does not contain certificate and key for alias " + alias);
      return new RootCertificateImpl((X509Certificate) certificate, privateKey);
    } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
      throw new CaException(e);
    }
  }
  
  static RootCertificateImpl loadRootCertificate(final String cert, final String key) {
    try {
      InputStream inStream = new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8));
      final Certificate certificate = CertificateFactory.getInstance("X.509")
          .generateCertificate(inStream);
      
      PEMParser pemParser = new PEMParser(new StringReader(key));
      Object keyPair = pemParser.readObject();
      
      PrivateKeyInfo keyInfo = ((PEMKeyPair) keyPair).getPrivateKeyInfo();
      final PrivateKey privateKey = (new JcaPEMKeyConverter()).getPrivateKey(keyInfo);
      
      if (certificate == null || privateKey == null)
        throw new CaException("Certificate and key could not be loaded.");
      return new RootCertificateImpl((X509Certificate) certificate, privateKey);
    } catch (CertificateException | IOException e) {
      throw new CaException(e);
    }
  }
  

}
