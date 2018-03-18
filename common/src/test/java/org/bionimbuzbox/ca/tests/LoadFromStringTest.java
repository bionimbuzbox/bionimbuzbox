package org.bionimbuzbox.ca.tests;

import static org.bionimbuzbox.ca.CA.createSelfSignedCertificate;
import static org.bionimbuzbox.ca.CA.dn;
import static org.bionimbuzbox.ca.CA.loadRootCertificate;
import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;
import org.bionimbuzbox.ca.DistinguishedName;
import org.bionimbuzbox.ca.RootCertificate;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoadFromStringTest {
  
  private static RootCertificate ca;
  private static ZonedDateTime time = ZonedDateTime.now();
  private static String certificate;
  private static String key;
  
  @BeforeClass
  public static void setup() {
    final DistinguishedName caName = dn("CN=CA-Test");
    ca = createSelfSignedCertificate(caName).build();
    certificate = ca.print();
    key = ca.printKey();
  }
  
  @Test
  public void saveToKeystoreFileAndBack() {
    final RootCertificate ca2 = loadRootCertificate(certificate, key);
    assertEquals(ca.print(), ca2.print());
    assertEquals(ca.printKey(), ca2.printKey());
  }

}
