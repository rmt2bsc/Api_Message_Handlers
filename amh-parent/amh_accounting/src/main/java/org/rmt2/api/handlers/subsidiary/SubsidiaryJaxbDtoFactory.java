package org.rmt2.api.handlers.subsidiary;

import java.util.ArrayList;
import java.util.List;

import org.dto.CreditorDto;
import org.dto.CreditorXactHistoryDto;
import org.dto.CustomerDto;
import org.dto.CustomerXactHistoryDto;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CreditorActivityType;
import org.rmt2.jaxb.CreditorCriteriaType;
import org.rmt2.jaxb.CreditorType;
import org.rmt2.jaxb.CreditortypeType;
import org.rmt2.jaxb.CustomerActivityType;
import org.rmt2.jaxb.CustomerCriteriaType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CreditorActivityTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CreditorTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CreditortypeTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerActivityTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;

import com.RMT2Base;
import com.api.util.RMT2String2;

/**
 * A factory for converting subsidiary related JAXB objects such as customers
 * and creditors to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class SubsidiaryJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>CustomerDto</i> using a valid
     * <i>CustomerCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link CustomerCriteriaType}
     * @return an instance of {@link CustomerDto}
     */
    public static final CustomerDto createCustomerDtoCriteriaInstance(CustomerCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        CustomerDto dto = Rmt2SubsidiaryDtoFactory.createCustomerInstance(null, null);
        if (jaxbCriteria.getCustomer() != null) {
            if (jaxbCriteria.getCustomer().getAccountNo() != null
                    && !jaxbCriteria.getCustomer().getAccountNo().isEmpty()) {
                dto.setAccountNo(jaxbCriteria.getCustomer().getAccountNo());    
            }
            if (jaxbCriteria.getCustomer().getAcctDescription() != null
                    && !jaxbCriteria.getCustomer().getAcctDescription().isEmpty()) {
                dto.setDescription(jaxbCriteria.getCustomer().getAcctDescription());    
            }
            if (jaxbCriteria.getCustomer().getAcctId() != null) {
                dto.setAcctId(jaxbCriteria.getCustomer().getAcctId().intValue());    
            }
            if (jaxbCriteria.getCustomer().getActive() != null) {
                dto.setActive(jaxbCriteria.getCustomer().getActive().intValue());    
            }
            if (jaxbCriteria.getCustomer().getCustomerId() != null) {
                dto.setCustomerId(jaxbCriteria.getCustomer().getCustomerId().intValue());    
            }
            if (jaxbCriteria.getCustomer().getBusinessContactDetails() != null) {
                if (jaxbCriteria.getCustomer().getBusinessContactDetails().getBusinessId() != null) {
                    dto.setContactId(jaxbCriteria.getCustomer().getBusinessContactDetails().getBusinessId().intValue());    
                }
                if (jaxbCriteria.getCustomer().getBusinessContactDetails().getLongName() != null
                        && !jaxbCriteria.getCustomer().getBusinessContactDetails().getLongName().isEmpty()) {
                    dto.setContactName(jaxbCriteria.getCustomer().getBusinessContactDetails().getLongName());    
                }
            }
            
            // TODO: In the future, we can make provisions to handle person related data.
        }
        
        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final CustomerDto createCustomerDtoInstance(CustomerType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        CustomerDto dto = Rmt2SubsidiaryDtoFactory.createCustomerInstance(null, null);
        if (RMT2String2.isNotEmpty(jaxbObj.getAccountNo())) {
            dto.setAccountNo(jaxbObj.getAccountNo());    
        }
        if (RMT2String2.isNotEmpty(jaxbObj.getAcctDescription())) {
            dto.setDescription(jaxbObj.getAcctDescription());    
        }
        if (jaxbObj.getAcctId() != null) {
            dto.setAcctId(jaxbObj.getAcctId().intValue());    
        }
        if (jaxbObj.getActive() != null) {
            dto.setActive(jaxbObj.getActive().intValue());    
        }
        if (jaxbObj.getCustomerId() != null) {
            dto.setCustomerId(jaxbObj.getCustomerId().intValue());    
        }
        if (jaxbObj.getBusinessContactDetails() != null) {
            if (jaxbObj.getBusinessContactDetails().getBusinessId() != null) {
                dto.setContactId(jaxbObj.getBusinessContactDetails().getBusinessId().intValue());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getLongName())) {
                dto.setContactName(jaxbObj.getBusinessContactDetails().getLongName());    
            }
            if (jaxbObj.getBusinessContactDetails().getCategory() != null) {
                if (jaxbObj.getBusinessContactDetails().getCategory().getGroupId() != null) {
                    dto.setCategoryId(jaxbObj.getBusinessContactDetails().getCategory().getGroupId().intValue());    
                }    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactEmail())) {
                dto.setContactEmail(jaxbObj.getBusinessContactDetails().getContactEmail());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactFirstname())) {
                dto.setContactFirstname(jaxbObj.getBusinessContactDetails().getContactFirstname());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactLastname())) {
                dto.setContactLastname(jaxbObj.getBusinessContactDetails().getContactLastname());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactPhone())) {
                dto.setContactPhone(jaxbObj.getBusinessContactDetails().getContactPhone());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactExt())) {
                dto.setContactExt(jaxbObj.getBusinessContactDetails().getContactExt());    
            }
            if (jaxbObj.getBusinessContactDetails().getEntityType() != null) {
                if (jaxbObj.getBusinessContactDetails().getEntityType().getCodeId() != null) {
                    dto.setEntityTypeId(jaxbObj.getBusinessContactDetails().getEntityType().getCodeId().intValue());    
                }
            }
            if (jaxbObj.getBusinessContactDetails().getServiceType() != null) {
                if (jaxbObj.getBusinessContactDetails().getServiceType().getCodeId() != null) {
                    dto.setServTypeId(jaxbObj.getBusinessContactDetails().getServiceType().getCodeId().intValue());    
                }
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getShortName())) {
                dto.setShortName(jaxbObj.getBusinessContactDetails().getShortName());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getTaxId())) {
                dto.setTaxId(jaxbObj.getBusinessContactDetails().getTaxId());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getWebsite())) {
                dto.setWebsite(jaxbObj.getBusinessContactDetails().getWebsite());    
            }
            if (jaxbObj.getBusinessContactDetails().getAddress() != null) {
                if (jaxbObj.getBusinessContactDetails().getAddress().getAddrId() != null) {
                    dto.setAddrId(jaxbObj.getBusinessContactDetails().getAddress().getAddrId().intValue());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getAddr1())) {
                    dto.setAddr1(jaxbObj.getBusinessContactDetails().getAddress().getAddr1());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getAddr2())) {
                    dto.setAddr2(jaxbObj.getBusinessContactDetails().getAddress().getAddr2());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getAddr3())) {
                    dto.setAddr3(jaxbObj.getBusinessContactDetails().getAddress().getAddr3());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getAddr4())) {
                    dto.setAddr4(jaxbObj.getBusinessContactDetails().getAddress().getAddr4());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getPhoneMain())) {
                    dto.setPhoneCompany(jaxbObj.getBusinessContactDetails().getAddress().getPhoneMain());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getPhonePager())) {
                    dto.setPhonePager(jaxbObj.getBusinessContactDetails().getAddress().getPhonePager());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax())) {
                    dto.setPhoneFax(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax())) {
                    dto.setPhoneFax(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax());    
                }
                if (jaxbObj.getBusinessContactDetails().getAddress().getZip() != null) {
                    if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getZip().getCity())) {
                        dto.setCity(jaxbObj.getBusinessContactDetails().getAddress().getZip().getCity());    
                    }
                    if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getZip().getState())) {
                        dto.setState(jaxbObj.getBusinessContactDetails().getAddress().getZip().getState());    
                    }
                    if (jaxbObj.getBusinessContactDetails().getAddress().getZip().getZipcode() != null) {
                        dto.setZip(jaxbObj.getBusinessContactDetails().getAddress().getZip().getZipcode().intValue());    
                    }
                }
            }
        }
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final CustomerType createCustomerJaxbInstance(CustomerDto dto,
            double balance, List<CustomerXactHistoryDto> transactions) {
        
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        BusinessType businessContactDetails = BusinessTypeBuilder.Builder.create()
                .withBusinessId(dto.getContactId())
                .withLongname(dto.getContactName()).build();
        
        List<CustomerActivityType> catList = null;
        if (transactions != null) {
            catList = new ArrayList<>();
            for (CustomerXactHistoryDto trans : transactions) {
                CustomerActivityType cat = CustomerActivityTypeBuilder.Builder.create()
                        .withAmount(trans.getActivityAmount())
                        .withCustomerActivityId(trans.getActivityId())
                        .withCustomerId(trans.getCustomerId())
                        .withXactDetails(null)
                        .withXactId(trans.getXactId()).build();
                
                catList.add(cat);
            }
        }
        
        CustomerType jaxbObj = CustomerTypeBuilder.Builder.create()
                .withCustomerId(dto.getCustomerId())
                .withAcctId(dto.getAcctId())
                .withBusinessType(businessContactDetails)
                .withPersonType(null)
                .withAccountNo(dto.getAccountNo())
                .withCreditLimit(dto.getCreditLimit())
                .withAcctDescription(dto.getDescription())
                .withBalance(balance)
                .withActive(dto.getActive())
                .withTransactions(catList)
                .withRecordTracking(rtt).build();
        return jaxbObj;
    }

    
    /**
     * Creates an instance of <i>CreditorDto</i> using a valid
     * <i>CreditorCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link CreditorCriteriaType}
     * @return an instance of {@link CreditorDto}
     */
    public static final CreditorDto createCreditorDtoCriteriaInstance(CreditorCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        CreditorDto dto = Rmt2SubsidiaryDtoFactory.createCreditorInstance(null, null);
        if (RMT2String2.isEmpty(jaxbCriteria.getAccountNo())) {
            dto.setAccountNo(jaxbCriteria.getAccountNo());
        }
        if (jaxbCriteria.getAcctId() != null) {
            dto.setAcctId(jaxbCriteria.getAcctId().intValue());
        }
        if (jaxbCriteria.getCreditorId() != null) {
            dto.setCreditorId(jaxbCriteria.getCreditorId().intValue());
        }
        if (jaxbCriteria.getBusinessId() != null) {
            dto.setContactId(jaxbCriteria.getBusinessId().intValue());
        }
        if (RMT2String2.isEmpty(jaxbCriteria.getBusinessName())) {
            dto.setContactName(jaxbCriteria.getBusinessName());
        }
        if (RMT2String2.isEmpty(jaxbCriteria.getExtAccountNo())) {
            dto.setExtAccountNumber(jaxbCriteria.getExtAccountNo());
        }
        if (jaxbCriteria.getCreditorTypeId() != null) {
            dto.setCreditorTypeId(jaxbCriteria.getCreditorTypeId().intValue());
        }
        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final CreditorDto createCreditorDtoInstance(CreditorType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        CreditorDto dto = Rmt2SubsidiaryDtoFactory.createCreditorInstance(null, null);
        if (jaxbObj.getCreditorId() != null) {
            dto.setCreditorId(jaxbObj.getCreditorId().intValue());    
        }
        if (RMT2String2.isNotEmpty(jaxbObj.getAccountNo())) {
            dto.setAccountNo(jaxbObj.getAccountNo());    
        }
        if (RMT2String2.isNotEmpty(jaxbObj.getExtAccountNo())) {
            dto.setExtAccountNumber(jaxbObj.getExtAccountNo());    
        }
        if (jaxbObj.getAcctId() != null) {
            dto.setAcctId(jaxbObj.getAcctId().intValue());    
        }
        if (jaxbObj.getActive() != null) {
            dto.setActive(jaxbObj.getActive().intValue());    
        }
        if (jaxbObj.getApr() != null) {
            dto.setApr(jaxbObj.getApr().doubleValue());
        }
        if (jaxbObj.getCreditLimit() != null) {
            dto.setCreditLimit(jaxbObj.getCreditLimit().doubleValue());
        }
        if (jaxbObj.getCreditorType() != null) {
            if (jaxbObj.getCreditorType().getCreditorTypeId() != null) {
                dto.setCreditorTypeId(jaxbObj.getCreditorType().getCreditorTypeId().intValue());
            }
        }
        if (jaxbObj.getContactDetails() != null) {
            if (jaxbObj.getContactDetails().getBusinessId() != null) {
                dto.setContactId(jaxbObj.getContactDetails().getBusinessId().intValue());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getLongName())) {
                dto.setContactName(jaxbObj.getContactDetails().getLongName());    
            }
            if (jaxbObj.getContactDetails().getCategory() != null) {
                if (jaxbObj.getContactDetails().getCategory().getGroupId() != null) {
                    dto.setCategoryId(jaxbObj.getContactDetails().getCategory().getGroupId().intValue());    
                }    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getContactEmail())) {
                dto.setContactEmail(jaxbObj.getContactDetails().getContactEmail());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getContactFirstname())) {
                dto.setContactFirstname(jaxbObj.getContactDetails().getContactFirstname());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getContactLastname())) {
                dto.setContactLastname(jaxbObj.getContactDetails().getContactLastname());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getContactPhone())) {
                dto.setContactPhone(jaxbObj.getContactDetails().getContactPhone());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getContactExt())) {
                dto.setContactExt(jaxbObj.getContactDetails().getContactExt());    
            }
            if (jaxbObj.getContactDetails().getEntityType() != null) {
                if (jaxbObj.getContactDetails().getEntityType().getCodeId() != null) {
                    dto.setEntityTypeId(jaxbObj.getContactDetails().getEntityType().getCodeId().intValue());    
                }
            }
            if (jaxbObj.getContactDetails().getServiceType() != null) {
                if (jaxbObj.getContactDetails().getServiceType().getCodeId() != null) {
                    dto.setServTypeId(jaxbObj.getContactDetails().getServiceType().getCodeId().intValue());    
                }
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getShortName())) {
                dto.setShortName(jaxbObj.getContactDetails().getShortName());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getTaxId())) {
                dto.setTaxId(jaxbObj.getContactDetails().getTaxId());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getWebsite())) {
                dto.setWebsite(jaxbObj.getContactDetails().getWebsite());    
            }
            if (jaxbObj.getContactDetails().getAddress() != null) {
                if (jaxbObj.getContactDetails().getAddress().getAddrId() != null) {
                    dto.setAddrId(jaxbObj.getContactDetails().getAddress().getAddrId().intValue());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getAddr1())) {
                    dto.setAddr1(jaxbObj.getContactDetails().getAddress().getAddr1());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getAddr2())) {
                    dto.setAddr2(jaxbObj.getContactDetails().getAddress().getAddr2());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getAddr3())) {
                    dto.setAddr3(jaxbObj.getContactDetails().getAddress().getAddr3());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getAddr4())) {
                    dto.setAddr4(jaxbObj.getContactDetails().getAddress().getAddr4());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getPhoneMain())) {
                    dto.setPhoneCompany(jaxbObj.getContactDetails().getAddress().getPhoneMain());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getPhonePager())) {
                    dto.setPhonePager(jaxbObj.getContactDetails().getAddress().getPhonePager());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getPhoneFax())) {
                    dto.setPhoneFax(jaxbObj.getContactDetails().getAddress().getPhoneFax());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getPhoneFax())) {
                    dto.setPhoneFax(jaxbObj.getContactDetails().getAddress().getPhoneFax());    
                }
                if (jaxbObj.getContactDetails().getAddress().getZip() != null) {
                    if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getZip().getCity())) {
                        dto.setCity(jaxbObj.getContactDetails().getAddress().getZip().getCity());    
                    }
                    if (RMT2String2.isNotEmpty(jaxbObj.getContactDetails().getAddress().getZip().getState())) {
                        dto.setState(jaxbObj.getContactDetails().getAddress().getZip().getState());    
                    }
                    if (jaxbObj.getContactDetails().getAddress().getZip().getZipcode() != null) {
                        dto.setZip(jaxbObj.getContactDetails().getAddress().getZip().getZipcode().intValue());    
                    }
                }
            }
        }
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final CreditorType createCreditorJaxbInstance(CreditorDto dto,
            double balance, List<CreditorXactHistoryDto> transactions) {
        
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        BusinessType businessContactDetails = BusinessTypeBuilder.Builder.create()
                .withBusinessId(dto.getContactId())
                .withLongname(dto.getContactName()).build();
        
        CreditortypeType creditorType = CreditortypeTypeBuilder.Builder.create()
                .withCreditorTypeId(dto.getCreditorTypeId()).build();
        
        List<CreditorActivityType> catList = null;
        if (transactions != null) {
            catList = new ArrayList<>();
            for (CreditorXactHistoryDto trans : transactions) {
                CreditorActivityType cat = CreditorActivityTypeBuilder.Builder.create()
                        .withAmount(trans.getActivityAmount())
                        .withCreditorActivityId(trans.getActivityId())
                        .withCreditorId(trans.getCreditorId())
                        .withXactId(trans.getXactId())
                        .withXactDetails(null)
                        .withXactId(trans.getXactId()).build();
                
                catList.add(cat);
            }
        }
        
        CreditorType jaxbObj = CreditorTypeBuilder.Builder.create()
                .withCreditorId(dto.getCreditorId())
                .withAcctId(dto.getAcctId())
                .withBusinessType(businessContactDetails)
                .withCreditorytypeType(creditorType)
                .withAccountNo(dto.getAccountNo())
                .withExtAccountNo(dto.getExtAccountNumber())
                .withApr(dto.getApr())
                .withCreditLimit(dto.getCreditLimit())
                .withBalance(balance)
                .withActive(dto.getActive())
                .withTransactions(catList)
                .withRecordTracking(rtt).build();
        return jaxbObj;
    }
}

