package com.sap.aii.af.ackstatus.api.types.impl;

import com.sap.aii.af.ackstatus.api.types.AckCategory;
import com.sap.aii.af.ackstatus.api.types.AckIndicator;
import com.sap.aii.af.ackstatus.api.types.AckPresenceType;
import com.sap.aii.af.ackstatus.api.types.AckStatus;
import com.sap.aii.af.ackstatus.api.types.AckType;
import com.sap.aii.af.ackstatus.api.types.StatusCode;
import java.io.Serializable;
import java.util.Date;

public class AckStatusImpl implements AckStatus, Serializable {
   private static final String VERSION_ID = "$Id: //tc/xpi.b2b.toolkit/PIBtwoB2_02_REL/src/SCs/sap.com/PIB2BTOOLKIT/DCs/sap.com/edi/security/module/_comp/ejbModule/com/sap/aii/af/ackstatus/api/types/impl/AckStatusImpl.java#1 $";
   private static final long serialVersionUID = 1932411180569745236L;
   private String reportKey;
   private String messageId;
   private String ackCorrelationId;
   private AckType ackType;
   private AckPresenceType ackPresenceType;
   private AckIndicator ackIndicator;
   private StatusCode statusCode;
   private Date insertTimeStamp;
   private Date ackTimeStamp;

   public AckStatusImpl(String messageId, String ackCorrelationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator) {
      this.messageId = messageId;
      this.ackCorrelationId = ackCorrelationId;
      this.ackType = ackType;
      this.ackPresenceType = ackPresenceType;
      this.ackIndicator = ackIndicator;
      this.insertTimeStamp = new Date();
      this.ackTimeStamp = this.insertTimeStamp;
   }

   public AckStatusImpl(String messageId, String ackCorrelationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator, StatusCode statusCode) {
      this(messageId, ackCorrelationId, ackType, ackPresenceType, ackIndicator);
      this.statusCode = statusCode;
   }

   public AckStatusImpl(Date ackTimeStamp, String messageId, String ackCorrelationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator, StatusCode statusCode) {
      this(messageId, ackCorrelationId, ackType, ackPresenceType, ackIndicator, statusCode);
      this.ackTimeStamp = ackTimeStamp;
   }

   public AckStatusImpl(String messageId, String ackCorrelationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator, StatusCode statusCode, Date insertTimeStamp) {
      this(messageId, ackCorrelationId, ackType, ackPresenceType, ackIndicator, statusCode);
      this.insertTimeStamp = insertTimeStamp;
      this.ackTimeStamp = insertTimeStamp;
   }

   public AckStatusImpl(Date ackTimeStamp, String messageId, String ackCorrelationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator, StatusCode statusCode, Date insertTimeStamp) {
      this(messageId, ackCorrelationId, ackType, ackPresenceType, ackIndicator, statusCode, insertTimeStamp);
      this.ackTimeStamp = insertTimeStamp;
   }

   public AckStatusImpl(String idenfifier, String messageId, String ackCorrelationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator, StatusCode statusCode) {
      this(messageId, ackCorrelationId, ackType, ackPresenceType, ackIndicator, statusCode);
      this.reportKey = idenfifier;
   }

   public AckStatusImpl(String idenfifier, String messageId, String ackCorrelationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator) {
      this(messageId, ackCorrelationId, ackType, ackPresenceType, ackIndicator);
      this.reportKey = idenfifier;
   }

   public AckStatusImpl(String idenfifier, String messageId, String ackCorrelationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator, StatusCode statusCode, Date insertTimeStamp) {
      this(messageId, ackCorrelationId, ackType, ackPresenceType, ackIndicator, statusCode, insertTimeStamp);
      this.reportKey = idenfifier;
   }

   public AckStatusImpl(Date ackTimeStamp, String idenfifier, String messageId, String ackCorrelationId, AckType ackType, AckPresenceType ackPresenceType, AckIndicator ackIndicator, StatusCode statusCode, Date insertTimeStamp) {
      this(idenfifier, messageId, ackCorrelationId, ackType, ackPresenceType, ackIndicator, statusCode, insertTimeStamp);
      this.ackTimeStamp = ackTimeStamp;
   }

   public String getMessageId() {
      return this.messageId;
   }

   public void setMessageId(String messageId) {
      this.messageId = messageId;
   }

   public String getAckCorrelationId() {
      return this.ackCorrelationId;
   }

   public void setCorrelationId(String ackCorrelationId) {
      this.ackCorrelationId = ackCorrelationId;
   }

   public AckType getAckType() {
      return this.ackType;
   }

   public void setAckType(AckType ackType) {
      this.ackType = ackType;
   }

   public AckCategory getAckCategory() {
      return this.ackType != null ? this.ackType.getAckCategory() : null;
   }

   public AckPresenceType getAckPresenceType() {
      return this.ackPresenceType;
   }

   public void setAckPresenceType(AckPresenceType ackPresenceType) {
      this.ackPresenceType = ackPresenceType;
   }

   public AckIndicator getAckIndicator() {
      return this.ackIndicator;
   }

   public void setAckIndicator(AckIndicator ackIndicator) {
      this.ackIndicator = ackIndicator;
   }

   public StatusCode getStatusCode() {
      return this.statusCode;
   }

   public void setStatusCode(StatusCode statusCode) {
      this.statusCode = statusCode;
   }

   public String getStatusText() {
      return this.statusCode == null ? null : this.statusCode.getDescription();
   }

   public Date getInsertTimeStamp() {
      return this.insertTimeStamp;
   }

   public Date getAckTimeStamp() {
      return this.ackTimeStamp;
   }

   public String getNameSpace() {
      if (this.ackType == null && this.statusCode == null) {
         return null;
      } else {
         return this.ackType.getNameSpace() != null ? this.ackType.getNameSpace() : (this.statusCode.getNameSpace() != null ? this.statusCode.getNameSpace() : null);
      }
   }

   public String getReportKey() {
      return this.reportKey;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.ackIndicator == null ? 0 : this.ackIndicator.hashCode());
      result = 31 * result + (this.ackPresenceType == null ? 0 : this.ackPresenceType.hashCode());
      result = 31 * result + (this.ackType == null ? 0 : this.ackType.hashCode());
      result = 31 * result + (this.ackCorrelationId == null ? 0 : this.ackCorrelationId.hashCode());
      result = 31 * result + (this.messageId == null ? 0 : this.messageId.hashCode());
      result = 31 * result + (this.statusCode == null ? 0 : this.statusCode.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         AckStatusImpl other = (AckStatusImpl)obj;
         if (this.ackIndicator == null) {
            if (other.ackIndicator != null) {
               return false;
            }
         } else if (!this.ackIndicator.equals(other.ackIndicator)) {
            return false;
         }

         if (this.ackPresenceType == null) {
            if (other.ackPresenceType != null) {
               return false;
            }
         } else if (!this.ackPresenceType.equals(other.ackPresenceType)) {
            return false;
         }

         if (this.ackType == null) {
            if (other.ackType != null) {
               return false;
            }
         } else if (!this.ackType.equals(other.ackType)) {
            return false;
         }

         if (this.ackCorrelationId == null) {
            if (other.ackCorrelationId != null) {
               return false;
            }
         } else if (!this.ackCorrelationId.equals(other.ackCorrelationId)) {
            return false;
         }

         if (this.messageId == null) {
            if (other.messageId != null) {
               return false;
            }
         } else if (!this.messageId.equals(other.messageId)) {
            return false;
         }

         if (this.statusCode == null) {
            if (other.statusCode != null) {
               return false;
            }
         } else if (!this.statusCode.equals(other.statusCode)) {
            return false;
         }

         return true;
      }
   }
}
