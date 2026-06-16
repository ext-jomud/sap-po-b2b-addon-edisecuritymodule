package com.sap.aii.af.ackstatus.api.types;

import java.io.Serializable;

public class AckType implements Serializable {
   private static final String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/api/types/AckType.java#1 $";
   private static final long serialVersionUID = -5468037617363665228L;
   private final String nameSpace;
   private AckCategory ackCategory = null;
   private final String name;
   private String description = "";

   public AckType(String nameSapce, String name, AckCategory ackCategory, String description) {
      this.nameSpace = nameSapce;
      this.name = name;
      this.ackCategory = ackCategory;
      this.description = description;
   }

   public AckType(String nameSapce, String name, AckCategory ackCategory) {
      this.nameSpace = nameSapce;
      this.name = name;
      this.ackCategory = ackCategory;
   }

   public AckType(String nameSapce, String name, String description) {
      this.nameSpace = nameSapce;
      this.name = name;
      this.description = description;
   }

   public AckType(String nameSapce, String name) {
      this.nameSpace = nameSapce;
      this.name = name;
   }

   public String getNameSpace() {
      return this.nameSpace;
   }

   public AckCategory getAckCategory() {
      return this.ackCategory;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }
}
