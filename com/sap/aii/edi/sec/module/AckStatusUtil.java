package com.sap.aii.edi.sec.module;

import com.sap.aii.af.ackstatus.api.AckStatusAccess;
import com.sap.aii.af.ackstatus.api.AckStatusAccessFactory;
import com.sap.aii.af.ackstatus.api.AckStatusException;
import com.sap.aii.af.ackstatus.api.types.AckCategory;
import com.sap.aii.af.ackstatus.api.types.AckIndicator;
import com.sap.aii.af.ackstatus.api.types.AckPresenceType;
import com.sap.aii.af.ackstatus.api.types.AckStatus;
import com.sap.aii.af.ackstatus.api.types.AckType;
import com.sap.aii.af.ackstatus.api.types.StatusCode;
import com.sap.aii.af.ackstatus.api.types.impl.AckStatusImpl;
import javax.resource.ResourceException;

public class AckStatusUtil {
   public static String ADAPTER_NAMESPACE = "http://sap.com/xi/XI/EDISeparator";
   public static AckType ACK_TYPE_AUTACK;

   public boolean createAckStatusRequested(String xiMessageId, String correlationId, AckType ackType) throws ResourceException {
      return this.createAck(xiMessageId, correlationId, ackType, AckPresenceType.REQUESTED, AckIndicator.YELLOW, this.getStatusCode(EDIAckStatusCode.ACK_REQUESTED));
   }

   public boolean createAckStatusNotRequested(String xiMessageId, String correlationId, AckType ackType) throws ResourceException {
      return this.createAck(xiMessageId, correlationId, ackType, AckPresenceType.NOT_REQUESTED, AckIndicator.GREEN, this.getStatusCode(EDIAckStatusCode.ACK_NOT_REQUESTED));
   }

   public boolean createAckStatusRejectedGeneratered(String xiMessageId, String correlationId, AckType ackType) throws ResourceException {
      return this.createAck(xiMessageId, correlationId, ackType, AckPresenceType.GENERATED, AckIndicator.RED, this.getStatusCode(EDIAckStatusCode.REJECTED_GENERATED));
   }

   public boolean createAckStatusAcceptedGeneratered(String xiMessageId, String correlationId, AckType ackType) throws ResourceException {
      return this.createAck(xiMessageId, correlationId, ackType, AckPresenceType.GENERATED, AckIndicator.YELLOW, this.getStatusCode(EDIAckStatusCode.ACCEPTED_GENERATED));
   }

   public boolean createAckStatusReceiptPending(String xiMessageId, String correlationId, AckType ackType) throws ResourceException {
      return this.createAck(xiMessageId, correlationId, ackType, AckPresenceType.RECIPT_PENDING, AckIndicator.YELLOW, this.getStatusCode(EDIAckStatusCode.ACK_SOLICITED));
   }

   public boolean createAckStatusNotSolicited(String xiMessageId, String correlationId, AckType ackType) throws ResourceException {
      return this.createAck(xiMessageId, correlationId, ackType, AckPresenceType.NOT_SOLICITED, AckIndicator.GREEN, this.getStatusCode(EDIAckStatusCode.ACK_NOT_SOLICITED));
   }

   public boolean createAckStatusAckReceived(String xiMessageId, String correlationId, AckType ackType) throws ResourceException {
      return this.createAck(xiMessageId, correlationId, ackType, AckPresenceType.RECEIVED, AckIndicator.YELLOW, this.getStatusCode(EDIAckStatusCode.ACK_RECEIVED));
   }

   public boolean createAckStatusAcceptedReceived(String xiMessageId, String correlationId, AckType ackType) throws ResourceException {
      return this.createAck(xiMessageId, correlationId, ackType, AckPresenceType.VALIDATED, AckIndicator.GREEN, this.getStatusCode(EDIAckStatusCode.ACCEPTED_RECV));
   }

   public boolean createAckStatusRejectedReceived(String xiMessageId, String correlationId, AckType ackType) throws ResourceException {
      return this.createAck(xiMessageId, correlationId, ackType, AckPresenceType.VALIDATED, AckIndicator.RED, this.getStatusCode(EDIAckStatusCode.REJECTED_RECV));
   }

   private boolean createAck(String messageId, String correlationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator, StatusCode statusCode) throws ResourceException {
      try {
         AckStatusAccess ackStatusAccess = AckStatusAccessFactory.newAckStatusAccess();
         AckStatus ackStatus = new AckStatusImpl(messageId, correlationId, ackType, ackPresenceType, ackIndicator, statusCode);
         return ackStatusAccess.createAckStatus(ackStatus);
      } catch (AckStatusException e) {
         if (!"ACK_STATUS_ACCESS_EXCEPTION".equals(e.getErrorCode())) {
            throw new ResourceException(e);
         } else {
            return false;
         }
      }
   }

   private StatusCode getStatusCode(EDIAckStatusCode ediStatusCode) {
      return ediStatusCode == null ? null : new StatusCode(ADAPTER_NAMESPACE, ediStatusCode.getCode(), ediStatusCode.getReasonText());
   }

   static {
      ACK_TYPE_AUTACK = new AckType(ADAPTER_NAMESPACE, "AUTACK", AckCategory.FUNCTIONAL, "Secure authentication and acknowledgement message (AUTACK)");
   }
}
