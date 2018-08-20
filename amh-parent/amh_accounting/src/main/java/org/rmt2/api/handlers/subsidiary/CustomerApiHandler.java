package org.rmt2.api.handlers.subsidiary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CustomerDto;
import org.modules.CommonAccountingConst;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.jaxb.TransactionDetailGroup;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Customer related messages to the Accounting API.
 * 
 * @author roy.terrell
 *
 */
public class CustomerApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingTransactionRequest, AccountingTransactionResponse, List<CustomerType>> {
    
    private static final Logger logger = Logger.getLogger(CustomerApiHandler.class);
    private ObjectFactory jaxbObjFactory;
    private CustomerApi api;

    /**
     * @param payload
     */
    public CustomerApiHandler() {
        super();
        SubsidiaryApiFactory f = new SubsidiaryApiFactory();
        this.api = f.createCustomerApi(CommonAccountingConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingTransactionResponse();
        logger.info(CustomerApiHandler.class.getName() + " was instantiated successfully");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.api.messaging.jms.handler.AbstractMessageHandler#processRequest(java
     * .lang.String, java.io.Serializable)
     */
    @Override
    public MessageHandlerResults processMessage(String command, Serializable payload) throws MessageHandlerCommandException {
        MessageHandlerResults r = super.processMessage(command, payload);

        if (r != null) {
            // This means an error occurred.
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.SUBSIDIARY_CUSTOMER_GET:
                r = this.fetch(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * Customer ojects.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetch(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CustomerType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            CustomerDto criteriaDto = SubsidiaryJaxbDtoFactory
                    .createCustomerDtoCriteriaInstance(req.getCriteria().getCustomerCriteria());
            
            List<CustomerDto> dtoList = this.api.get(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Customer data not found!");
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Customer record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve customer(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
   
    private List<CustomerType> buildJaxbListData(List<CustomerDto> results) {
        List<CustomerType> list = new ArrayList<>();
        for (CustomerDto item : results) {
            CustomerType jaxbObj = SubsidiaryJaxbDtoFactory.createCustomerJaxbInstance(item, 0.00, null);
            list.add(jaxbObj);
        }
        return list;
    }
    
   
    
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Customer transaction request element is invalid");
        }
        
        try {
            Verifier.verifyNotNull(req.getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getCustomerCriteria());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Customer transaction selection criteria is required for query operation");
        }
    }

    @Override
    protected String buildResponse(List<CustomerType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getCustomer().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
