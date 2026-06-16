package com.sap.aii.af.ackstatus.spi;

import com.sap.aii.af.ackstatus.api.AckStatusException;

public interface SPIAccess {
   String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/spi/SPIAccess.java#1 $";
   String JNDI_NAME = "com.sap.aii.af.ackstatus.spi";

   void registerAckStatusHandler(String var1, AckStatusHandler var2) throws AckStatusException;

   void unregisterAckStatusHandler(String var1) throws AckStatusException;
}
