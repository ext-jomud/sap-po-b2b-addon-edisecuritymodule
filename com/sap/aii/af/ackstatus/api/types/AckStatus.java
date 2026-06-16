package com.sap.aii.af.ackstatus.api.types;

import java.util.Date;

public interface AckStatus {
   String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/api/types/AckStatus.java#1 $";

   String getReportKey();

   String getMessageId();

   String getAckCorrelationId();

   String getNameSpace();

   AckType getAckType();

   AckCategory getAckCategory();

   AckPresenceType getAckPresenceType();

   AckIndicator getAckIndicator();

   StatusCode getStatusCode();

   String getStatusText();

   Date getInsertTimeStamp();

   Date getAckTimeStamp();
}
