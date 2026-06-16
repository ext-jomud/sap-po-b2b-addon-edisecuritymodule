package com.sap.aii.af.ackstatus.api;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class AckStatusAccessFactory {
   private static final String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/api/AckStatusAccessFactory.java#1 $";

   public static final AckStatusAccess newAckStatusAccess() throws AckStatusException {
      String SIGNATURE = "newAckStatusAccess()";

      try {
         return (AckStatusAccess)(new InitialContext()).lookup("sap.com/com.sap.aii.af.ackstatus.app/LOCAL/AckStatusAccessBean/com.sap.aii.af.ackstatus.api.AckStatusAccessLocal");
      } catch (NamingException e) {
         throw new AckStatusException("ACK_STATUS_ACCESS_EXCEPTION", "The Ack Status Access Implementation is NOT found!", e);
      }
   }
}
