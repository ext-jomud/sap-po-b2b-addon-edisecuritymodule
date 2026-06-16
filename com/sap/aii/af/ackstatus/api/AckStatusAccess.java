package com.sap.aii.af.ackstatus.api;

import com.sap.aii.af.ackstatus.api.types.AckCategory;
import com.sap.aii.af.ackstatus.api.types.AckIndicator;
import com.sap.aii.af.ackstatus.api.types.AckPresenceType;
import com.sap.aii.af.ackstatus.api.types.AckStatus;
import com.sap.aii.af.ackstatus.api.types.AckType;
import com.sap.aii.af.ackstatus.api.types.StatusCode;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface AckStatusAccess {
   String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/api/AckStatusAccess.java#1 $";
   String JNDI_NAME = "sap.com/com.sap.aii.af.ackstatus.app/LOCAL/AckStatusAccessBean/com.sap.aii.af.ackstatus.api.AckStatusAccessLocal";

   Boolean createAckStatus(AckStatus var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByMessageId(String var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByMessageId(String var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByMessageIdList(List<String> var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByMessageId(List<String> var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByAckCorrelationId(String var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByAckCorrelationId(String var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByAckCorrelationIdList(List<String> var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByAckCorrelationIdList(List<String> var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByAckType(AckType var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByAckType(AckType var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByAckPresenceType(AckPresenceType var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByAckPresenceType(AckPresenceType var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByAckIndicator(AckIndicator var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByAckIndicator(AckIndicator var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByStatusCode(StatusCode var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByStatusCode(StatusCode var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByNameSpace(String var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByNameSpace(String var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByAckCategory(AckCategory var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByAckCategory(AckCategory var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusByExample(AckStatus var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusByExample(AckStatus var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusBefore(Date var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusBefore(Date var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusAfter(Date var1) throws AckStatusException;

   List<AckStatus> getListOfAckStatusAfter(Date var1) throws AckStatusException;

   Collection<AckStatus> getAckStatusBetween(Date var1, Date var2) throws AckStatusException;

   List<AckStatus> getListOfAckStatusBetween(Date var1, Date var2) throws AckStatusException;

   AckStatus getFinalAckStatusByMessageId(String var1, AckCategory var2) throws AckStatusException;

   Collection<AckStatus> getFinalAckStatusByMessageIdList(List<String> var1, AckCategory var2) throws AckStatusException;

   List<AckStatus> getListOfFinalAckStatusByMessageIdList(List<String> var1, AckCategory var2) throws AckStatusException;

   Collection<String> getAllNameSpaces() throws AckStatusException;

   List<String> getListOfAllNameSpaces() throws AckStatusException;

   Collection<AckType> getAllAckTypes() throws AckStatusException;

   List<AckType> getListOfAllAckTypes() throws AckStatusException;

   Collection<StatusCode> getAllStatusCodes() throws AckStatusException;

   List<StatusCode> getListOfAllStatusCodes() throws AckStatusException;

   Collection<AckType> getAckTypesByNameSpace(String var1) throws AckStatusException;

   List<AckType> getListOfAckTypesByNameSpace(String var1) throws AckStatusException;

   Collection<StatusCode> getStatusCodesByNameSpace(String var1) throws AckStatusException;

   List<StatusCode> getListOfStatusCodesByNameSpace(String var1) throws AckStatusException;
}
