package org.bionimbuzbox.ca.ext;

import org.bouncycastle.asn1.x509.Extension;

public class SubjectAltNameExtension extends CertExtension {

  private SubjectAltNameExtension(NameType type, String value) {
      super(Extension.subjectAlternativeName, false, type.generalNames(value));
  }
  
  public static SubjectAltNameExtension create(NameType type, String value) {
    return new SubjectAltNameExtension(type, value);
  }

}
