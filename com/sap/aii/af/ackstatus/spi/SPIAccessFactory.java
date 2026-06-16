package com.sap.aii.af.ackstatus.spi;

import com.sap.aii.af.ackstatus.api.AckStatusException;
import javax.naming.Context;
import javax.naming.InitialContext;

public class SPIAccessFactory {
   static final String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/spi/SPIAccessFactory.java#1 $";
   private static SPIAccess spiAccess = null;

   private SPIAccessFactory() {
   }

   public static SPIAccess getSPIAccess() throws AckStatusException {
      if (spiAccess != null) {
         return spiAccess;
      } else {
         try {
            Context context = new InitialContext();
            spiAccess = (SPIAccess)context.lookup("com.sap.aii.af.ackstatus.spi");
         } catch (Exception ex) {
            String error = "Failed to lookup SPIAccessImpl from JNDI. Reason: " + ex;
            throw new AckStatusException(error, ex);
         }

         return spiAccess;
      }
   }
}
