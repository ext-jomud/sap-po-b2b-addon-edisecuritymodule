package com.sap.aii.af.ackstatus.api.types;

import java.io.Serializable;

public class StatusCode implements Serializable {
   private static final String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/api/types/StatusCode.java#1 $";
   private static final long serialVersionUID = -8113613618732229775L;
   private final String nameSpace;
   private final String code;
   private final String description;

   public StatusCode(String nameSpace, String code, String description) {
      this.nameSpace = nameSpace;
      this.code = code;
      this.description = description;
   }

   public String getNameSpace() {
      return this.nameSpace;
   }

   public String getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }
}
