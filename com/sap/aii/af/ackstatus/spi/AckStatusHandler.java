package com.sap.aii.af.ackstatus.spi;

import com.sap.aii.af.ackstatus.api.types.AckType;
import com.sap.aii.af.ackstatus.api.types.StatusCode;
import java.io.Serializable;
import java.util.Collection;

public interface AckStatusHandler extends Serializable {
   String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/spi/AckStatusHandler.java#1 $";

   Collection<AckType> getAckTypes();

   Collection<StatusCode> getStatusCodes();
}
