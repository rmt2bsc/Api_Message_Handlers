package org.rmt2.api.handlers.transaction;

import java.util.ArrayList;
import java.util.List;

import org.dto.XactCustomCriteriaDto;
import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.transaction.XactApiFactory;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.XactBasicCriteriaType;
import org.rmt2.jaxb.XactCodeGroupType;
import org.rmt2.jaxb.XactCodeType;
import org.rmt2.jaxb.XactCustomRelationalCriteriaType;
import org.rmt2.jaxb.XactLineitemType;
import org.rmt2.jaxb.XactType;
import org.rmt2.jaxb.XacttypeType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.transaction.XactCodeGroupTypeBuilder;
import org.rmt2.util.accounting.transaction.XactCodeTypeBuilder;
import org.rmt2.util.accounting.transaction.XactItemTypeBuilder;
import org.rmt2.util.accounting.transaction.XactTypeBuilder;
import org.rmt2.util.accounting.transaction.XacttypeTypeBuilder;

import com.RMT2Base;
import com.api.util.RMT2Date;
import com.api.util.RMT2String2;

/**
 * A factory for converting general transaction related JAXB objects to DTO and
 * vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class TransactionJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>XactDto</i> using a valid
     * <i>XactBasicCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link XactBasicCriteriaType}
     * @return an instance of {@link XactDto}
     * @throws {@link com.SystemException} Transaction date could not converted from a String.
     */
    public static final XactDto createBaseXactDtoCriteriaInstance(XactBasicCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        
        XactDto dto = Rmt2XactDtoFactory.createXactBaseInstance(null);
        if (!RMT2String2.isEmpty(jaxbCriteria.getConfirmNo())) {
            dto.setXactConfirmNo(jaxbCriteria.getConfirmNo());    
        }
        if (jaxbCriteria.getXactId() != null) {
            dto.setXactId(jaxbCriteria.getXactId().intValue());    
        }
        if (jaxbCriteria.getXactDate() != null) {
            dto.setXactDate(RMT2Date.stringToDate(jaxbCriteria.getXactDate()));    
        }  
        if (jaxbCriteria.getXactTypeId() != null) {
            dto.setXactTypeId(jaxbCriteria.getXactTypeId().intValue());    
        }
        if (jaxbCriteria.getXactCatgId() != null) {
            dto.setXactCatgId(jaxbCriteria.getXactCatgId().intValue());    
        }
        if (jaxbCriteria.getTenderId() != null) {
            dto.setXactTenderId(jaxbCriteria.getTenderId().intValue());    
        }
        
        return dto;
    }
    
    /**
     * 
     * @param jaxbCriteria
     * @return
     */
    public static final XactCustomCriteriaDto createCustomXactDtoCriteriaInstance(XactCustomRelationalCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        XactCustomCriteriaDto dto = XactApiFactory.createCustomCriteriaInstance();
        if (jaxbCriteria.getTargetLevel() != null && !RMT2String2.isEmpty(jaxbCriteria.getTargetLevel().name())) {
            dto.setTargetLevel(jaxbCriteria.getTargetLevel().name());    
        }
        if (jaxbCriteria.getXactReasonOptions() != null && !RMT2String2.isEmpty(jaxbCriteria.getXactReasonOptions().name())) {
            dto.setXactReasonFilterOption(jaxbCriteria.getXactReasonOptions().name());    
        }
        if (jaxbCriteria.getFromXactAmount() != null) {
            dto.setFromXactAmount(jaxbCriteria.getFromXactAmount().doubleValue());    
        }
        if (jaxbCriteria.getToXactAmount() != null) {
            dto.setToXactAmount(jaxbCriteria.getToXactAmount().doubleValue());    
        }
        if (jaxbCriteria.getFromItemAmount() != null) {
            dto.setFromItemAmount(jaxbCriteria.getFromItemAmount().doubleValue());    
        }
        if (jaxbCriteria.getToItemAmount() != null) {
            dto.setToItemAmount(jaxbCriteria.getToItemAmount().doubleValue());    
        }
        if (jaxbCriteria.getFromXactDate() != null) {
            dto.setFromXactDate(RMT2Date.stringToDate(jaxbCriteria.getFromXactDate()));    
        }
        if (jaxbCriteria.getToXactDate() != null) {
            dto.setToXactDate(RMT2Date.stringToDate(jaxbCriteria.getToXactDate()));    
        }
        
        if (!RMT2String2.isEmpty(jaxbCriteria.getFromRelOpXactAmount())) {
            dto.setFromXactAmountRelOp(jaxbCriteria.getFromRelOpXactAmount());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getToRelOpXactAmount())) {
            dto.setToXactAmountRelOp(jaxbCriteria.getToRelOpXactAmount());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getFromRelOpItemAmount())) {
            dto.setFromItemAmountRelOp(jaxbCriteria.getFromRelOpItemAmount());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getToRelOpItemAmount())) {
            dto.setToItemAmountRelOp(jaxbCriteria.getToRelOpItemAmount());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getFromRelOpXactDate())) {
            dto.setFromXactDateRelOp(jaxbCriteria.getFromRelOpXactDate());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getToRelOpXactDate())) {
            dto.setToXactDateRelOp(jaxbCriteria.getToRelOpXactDate());    
        }
        return dto;
    }
    
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final XactDto createTransactionDtoInstance(XactType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        XactDto dto = Rmt2XactDtoFactory.createXactBaseInstance(null);
        if (jaxbObj.getXactId() != null) {
            dto.setXactId(jaxbObj.getXactId().intValue());    
        }
        if (jaxbObj.getXactType() != null && jaxbObj.getXactType().getXactTypeId() != null) {
            dto.setXactTypeId(jaxbObj.getXactType().getXactTypeId().intValue());    
        }
        if (jaxbObj.getXactSubtype() != null && jaxbObj.getXactSubtype().getXactTypeId() != null) {
            dto.setXactSubtypeId(jaxbObj.getXactSubtype().getXactTypeId().intValue());    
        }
        if (jaxbObj.getXactSubtype() != null && jaxbObj.getXactSubtype().getXactTypeId() != null) {
            dto.setXactSubtypeId(jaxbObj.getXactSubtype().getXactTypeId().intValue());    
        }
        if (jaxbObj.getXactCodeGroup() != null) {
            if (jaxbObj.getXactCodeGroup().getXactCodeGrpId() != null) {
                dto.setXactCodeGrpId(jaxbObj.getXactCodeGroup().getXactCodeGrpId().intValue());
            }
            if (!RMT2String2.isEmpty(jaxbObj.getXactCodeGroup().getDescription())) {
                dto.setXactCodeGrpDescription(jaxbObj.getXactCodeGroup().getDescription());
            }
        }
        if (jaxbObj.getXactCode() != null) {
            if (jaxbObj.getXactCode().getXactCodeId() != null) {
                dto.setXactCodeId(jaxbObj.getXactCode().getXactCodeId().intValue());
            }
            if (!RMT2String2.isEmpty(jaxbObj.getXactCode().getDescription())) {
                dto.setXactCodeDescription(jaxbObj.getXactCode().getDescription());
            }
        }
        if (jaxbObj.getXactAmount() != null) {
            dto.setXactAmount(jaxbObj.getXactAmount().doubleValue());    
        }   
        if (jaxbObj.getXactDate() != null) {
            dto.setXactDate(jaxbObj.getXactDate().toGregorianCalendar().getTime());    
        } 
        if (jaxbObj.getPostedDate() != null) {
            dto.setXactPostedDate(jaxbObj.getPostedDate().toGregorianCalendar().getTime());    
        }
        if (jaxbObj.getTenderId() != null) {
            dto.setXactTenderId(jaxbObj.getTenderId().intValue());    
        }   
        if (jaxbObj.getDocumentId() != null) {
            dto.setDocumentId(jaxbObj.getDocumentId().intValue());    
        }   
        dto.setXactReason(jaxbObj.getXactReason());
        dto.setXactEntityRefNo(jaxbObj.getEntityRefNo());
        dto.setXactNegInstrNo(jaxbObj.getNegInstrNo());
        dto.setXactConfirmNo(jaxbObj.getConfirmNo());
        dto.setXactBankTransInd(jaxbObj.getBankTransInd());
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @param balance
     * @param transactions
     * @return
     */
    public static final XactType createXactJaxbInstance(XactDto dto, double balance, 
            List<XactTypeItemActivityDto> transactions) {
        
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        XacttypeType xt = XacttypeTypeBuilder.Builder.create().withXactTypeId(dto.getXactTypeId())
                .withDescription(dto.getXactTypeDescription()).build();

        XacttypeType xst = XacttypeTypeBuilder.Builder.create().withXactTypeId(dto.getXactSubtypeId()).build();

        XactCodeGroupType xcgt = XactCodeGroupTypeBuilder.Builder.create().withGroupId(dto.getXactCodeGrpId())
                .withDescription(dto.getXactCodeGrpDescription()).build();

        XactCodeType xct = XactCodeTypeBuilder.Builder.create().withXactCodeId(dto.getXactCodeId())
                .withDescription(dto.getXactCodeDescription()).build();
        
        List<XactLineitemType> itemList = null;
        if (transactions != null) {
            itemList = new ArrayList<>();
            for (XactTypeItemActivityDto trans : transactions) {
                XactLineitemType item = XactItemTypeBuilder.Builder.create()
                        .withAmount(trans.getActivityAmount())
                        .withDescription(trans.getXactTypeItemActvName())
                        .withItemId(trans.getXactItemId())
                        .withXactTypeItemActvId(trans.getXactTypeItemActvId())
                        .withXactId(trans.getXactId()).build();
                
                itemList.add(item);
            }
        }
        
        XactType jaxbObj = XactTypeBuilder.Builder.create()
                .withXactId(dto.getXactId())
                .withXactAmount(dto.getXactAmount())
                .withXactDate(dto.getXactDate())
                .withPostedDate(dto.getXactPostedDate())
                .withReason(dto.getXactReason())
                .withConfirmNo(dto.getXactConfirmNo())
                .withEntityRefNo(dto.getXactEntityRefNo())
                .withNegInstrNo(dto.getXactNegInstrNo())
                .withTenderId(dto.getXactTenderId())
                .withBankTransInd(dto.getXactBankTransInd())
                .withDocumentId(dto.getDocumentId())
                .withInvoiceNo(null)
                .withItemTotal(0)
                .withXactType(xt)
                .withXactSubtypeId(xst)
                .withXactCodeGroup(xcgt)
                .withXactCode(xct)
                .withXactItems(itemList)
                .withRecordTracking(rtt).build();
        return jaxbObj;
    }

}

