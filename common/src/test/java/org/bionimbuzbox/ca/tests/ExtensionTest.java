package org.bionimbuzbox.ca.tests;

import static org.bionimbuzbox.ca.CA.createSelfSignedCertificate;
import static org.bionimbuzbox.ca.CA.dn;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bionimbuzbox.ca.CA;
import org.bionimbuzbox.ca.CertificateWithPrivateKey;
import org.bionimbuzbox.ca.CsrWithPrivateKey;
import org.bionimbuzbox.ca.RootCertificate;
import org.bionimbuzbox.ca.ext.ExtKeyUsageExtension;
import org.bionimbuzbox.ca.ext.KeyUsageExtension;
import org.bionimbuzbox.ca.ext.KeyUsageExtension.KeyUsage;
import org.bionimbuzbox.ca.ext.NameType;
import org.bionimbuzbox.ca.ext.SubjectAltNameExtension;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;

public class ExtensionTest {

  public ExtensionTest() {
    // TODO Auto-generated constructor stub
  }

  public static void test1() {
    RootCertificate rootCertificate = createSelfSignedCertificate(dn("CN=CA-Test")).build();
    
    GeneralNames subjectAltName = new GeneralNames(
        new GeneralName(GeneralName.dNSName, "*"));
    
    CsrWithPrivateKey serverCSR = CA.createCsr().generateRequest(dn("CN=*"));
    CertificateWithPrivateKey serverCertificate = rootCertificate.signCsr(serverCSR)
        .setSerialNumber(CA.generateRandomSerialNumber())
        .setNotBefore(ZonedDateTime.now())
        .validDuringYears(1)
        .addExtension(Extension.subjectAlternativeName, false, subjectAltName)
        .sign()
        .attachPrivateKey(serverCSR.getPrivateKey());
    
    System.out.println(serverCertificate.print());
  }
  
  public static void test2() {
    RootCertificate rootCertificate = createSelfSignedCertificate(dn("CN=CA-Test")).build();
       
    CsrWithPrivateKey serverCSR = CA.createCsr().generateRequest(dn("CN=*"));
    CertificateWithPrivateKey serverCertificate = rootCertificate.signCsr(serverCSR)
        .setSerialNumber(CA.generateRandomSerialNumber())
        .setNotBefore(ZonedDateTime.now())
        .validDuringYears(1)
        .addExtension(SubjectAltNameExtension.create(NameType.DNS_NAME, "*"))
        .sign()
        .attachPrivateKey(serverCSR.getPrivateKey());
    
    System.out.println(serverCertificate.print());
  }
  
  public static void test3() {
    RootCertificate rootCertificate = createSelfSignedCertificate(dn("CN=CA-Test")).build();
    
    CsrWithPrivateKey serverCSR = CA.createCsr().generateRequest(dn("CN=*"));
    CertificateWithPrivateKey serverCertificate = rootCertificate.signCsr(serverCSR)
        .setSerialNumber(CA.generateRandomSerialNumber())
        .setNotBefore(ZonedDateTime.now())
        .validDuringYears(1)
        .addExtension(SubjectAltNameExtension.create(NameType.DNS_NAME, "*"))
        .sign()
        .attachPrivateKey(serverCSR.getPrivateKey());
    
    System.out.println(serverCertificate.print());
  }
  
  public static void test4() {
    String path = "/Users/tiago/work/bionimbuzbox/docker-tls-test";
    
    RootCertificate rootCertificate = CA.createSelfSignedCertificate(CA.dn("CN=BioNimbuzBoxCA"))
        .build();
    rootCertificate.save(path + "/ca.pem");
    rootCertificate.saveKey(path + "/ca-key.pem");
    
    List<GeneralName> names = new ArrayList<>();
    GeneralName g1 = new GeneralName(GeneralName.dNSName, "server-1.swarm.nyc3");
    names.add(g1);
    GeneralName g2 = new GeneralName(GeneralName.dNSName, "localhost");
    names.add(g2);
    GeneralName g3 = new GeneralName(GeneralName.iPAddress, "192.168.99.101");
    names.add(g3);
    GeneralName g4 = new GeneralName(GeneralName.iPAddress, "127.0.0.1");
    names.add(g4);
    
    GeneralNames subjectAltName = new GeneralNames(names.toArray(new GeneralName[0]));
    
    CsrWithPrivateKey clientCSR = CA.createCsr().generateRequest(CA.dn("CN=BioNimbuzBoxClient"));
    CertificateWithPrivateKey clientCertificate = rootCertificate.signCsr(clientCSR)
        .setSerialNumber(CA.generateRandomSerialNumber())
        .setNotBefore(ZonedDateTime.now())
        .validDuringYears(1)
        .addExtension(Extension.basicConstraints, true, new BasicConstraints(false))
        .addExtension(KeyUsageExtension.create(true, KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_ENCIPHERMENT, KeyUsage.KEY_AGREEMENT))
        .addExtension(ExtKeyUsageExtension.create(KeyPurposeId.id_kp_clientAuth))
        .sign()
        .attachPrivateKey(clientCSR.getPrivateKey());
    
    clientCertificate.save(path + "/client-cert.pem");
    clientCertificate.saveKey(path + "/client-key.pem");
    
    CsrWithPrivateKey serverCSR = CA.createCsr().generateRequest(dn("CN=*"));
    CertificateWithPrivateKey serverCertificate = rootCertificate.signCsr(serverCSR)
        .setSerialNumber(CA.generateRandomSerialNumber())
        .setNotBefore(ZonedDateTime.now())
        .validDuringYears(1)
        .addExtension(Extension.basicConstraints, true, new BasicConstraints(false))
        .addExtension(KeyUsageExtension.create(true, KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_ENCIPHERMENT, KeyUsage.KEY_AGREEMENT))
        .addExtension(ExtKeyUsageExtension.create(KeyPurposeId.id_kp_serverAuth))
        .addExtension(Extension.subjectAlternativeName, false, subjectAltName)
        .sign()
        .attachPrivateKey(serverCSR.getPrivateKey());
    
    serverCertificate.save(path + "/server-cert.pem");
    serverCertificate.saveKey(path + "/server-key.pem");
  }
  
  public static void main(String[] args) {
    test4();
    
  }
}
