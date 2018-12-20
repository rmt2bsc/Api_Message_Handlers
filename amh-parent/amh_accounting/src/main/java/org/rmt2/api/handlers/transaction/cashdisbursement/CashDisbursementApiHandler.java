package org.rmt2.api.handlers.transaction.cashdisbursement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dao.mapping.orm.rmt2.XactTypeItemActivity;
import org.dto.XactCustomCriteriaDto;
import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.transaction.XactApiException;
import org.modules.transaction.disbursements.DisbursementsApi;
import org.modules.transaction.disbursements.DisbursementsApiFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.XactType;

import com.InvalidDataException;
import com.RMT2Exception;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Cash Disbursement Transaction messages to the Accounting API.
 * 
 * @author rterrell
 *
 */
public class CashDisbursementApiHandler extends XactApiHandler {
    private static final Logger logger = Logger.getLogger(CashDisbursementApiHandler.class);
    public static final String MSG_DATA_FOUND = "Cash disbursement record(s) found";
    public static final String MSG_DATA_NOT_FOUND = "Cash disbursement data not found!";
    public static final String MSG_FAILURE = "Failure to retrieve Cash disbursement transaction(s)";
    
    private DisbursementsApi api;
    
    /**
     * 
     */
    public CashDisbursementApiHandler() {
        super();
        this.api = DisbursementsApiFactory.createApi();
        logger.info(CashDisbursementApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to fetching and creating of cash
     * disbursement transactions.
     * 
     * @param command
     *            The name of the operation.
     * @param payload
     *            The XML message that is to be processed.
     * @return MessageHandlerResults
     * @throws MessageHandlerCommandException
     *             <i>payload</i> is deemed invalid.
     */
    @Override
    public MessageHandlerResults processMessage(String command, Serializable payload) throws MessageHandlerCommandException {
        MessageHandlerResults r = super.processMessage(command, payload);

        if (r != null) {
            String errMsg = ERROR_MSG_TRANS_NOT_FOUND + command;
            if (r.getErrorMsg() != null && r.getErrorMsg().equalsIgnoreCase(errMsg)) {
                // Ancestor was not able to find command.  Continue processing.
            }
            else {
             // This means an error occurred.
                return r;    
            }
        }
        switch (command) {
            case ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET:
                r = this.fetch(this.requestObj);
                break;
                
//            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_CREATE:
//                r = this.create(this.requestObj);
//                break;
                
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    
    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * cash disbursement transaction objects. 
     * <p>
     * Currently, the target level, <i>DETAILS</i>, is not supported.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected MessageHandlerResults fetch(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<XactType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            XactDto criteriaDto = TransactionJaxbDtoFactory
                    .createBaseXactDtoCriteriaInstance(req.getCriteria().getXactCriteria().getBasicCriteria());
            XactCustomCriteriaDto customCriteriaDto = TransactionJaxbDtoFactory
                    .createCustomXactDtoCriteriaInstance(req.getCriteria().getXactCriteria().getCustomCriteria());
            
            this.targetLevel = req.getCriteria().getXactCriteria().getTargetLevel().name().toUpperCase();
            switch (this.targetLevel) {
                case TARGET_LEVEL_HEADER:
                case TARGET_LEVEL_FULL:
                    List<XactDto> dtoList = this.api.get(criteriaDto, customCriteriaDto);
                    if (dtoList == null) {
                        rs.setMessage(CashDisbursementApiHandler.MSG_DATA_NOT_FOUND);
                        rs.setRecordCount(0);
                    }
                    else {
                        queryDtoResults = this.buildJaxbTransaction(dtoList, customCriteriaDto);
                        rs.setMessage(CashDisbursementApiHandler.MSG_DATA_FOUND);
                        rs.setRecordCount(dtoList.size());
                    }
                    break;
                    
                default:
                    String msg = RMT2String.replace(MSG_INCORRECT_TARGET_LEVEL, targetLevel, "%s");
                    throw new RMT2Exception(msg);
            }
            
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(CashDisbursementApiHandler.MSG_FAILURE);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }

    /* (non-Javadoc)
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#create(org.rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected MessageHandlerResults create(AccountingTransactionRequest req) {
        return super.create(req);
    }

    /* (non-Javadoc)
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#validateRequest(org.rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateRequest(req);
        
        // Validate request for fetch operations
        switch (this.command) {
            case ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET:
                // Must contain flag that indicates what level of the transaction object to populate with data
                try {
                    Verifier.verifyNotNull(req.getCriteria());
                    Verifier.verifyNotNull(req.getCriteria().getXactCriteria());
                    Verifier.verifyNotNull(req.getCriteria().getXactCriteria().getTargetLevel());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(MSG_MISSING_TARGET_LEVEL, e);
                }
                
                // Target level "DETAILS" is not supported.
                try {
                    Verifier.verifyFalse(req.getCriteria().getXactCriteria()
                            .getTargetLevel().name()
                            .equalsIgnoreCase(TARGET_LEVEL_DETAILS));
                } catch (VerifyException e) {
                    throw new InvalidRequestException(MSG_DETAILS_NOT_SUPPORTED, e);
                }
                
//            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_CREATE:
//            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE:
//                // Transaction profile must exist
//                try {
//                    Verifier.verifyNotNull(req.getProfile());
//                    Verifier.verifyNotNull(req.getProfile().getTransactions());
//                    Verifier.verifyNotNull(req.getProfile().getTransactions().getTransaction());
//                }
//                catch (VerifyException e) {
//                    throw new InvalidRequestException(MSG_MISSING_PROFILE_DATA, e);    
//                }
//                // Transaction profile must contain one and only one transaction
//                try {
//                    Verifier.verifyNotEmpty(req.getProfile().getTransactions().getTransaction());
//                    Verifier.verifyTrue(req.getProfile().getTransactions().getTransaction().size() == 1);
//                }
//                catch (VerifyException e) {
//                    throw new InvalidRequestException(MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT, e);    
//                }
//                break;
                
            default:
                break;
        }
    }

    /**
     * Builds a List of XactType objects from a List of XactDto objects.
     * 
     * @param results List<{@link XactDto}>
     * @param customCriteriaDto custom relational criteria (optional)
     * @return List<{@link XactType}>
     */
    private List<XactType> buildJaxbTransaction(List<XactDto> results, XactCustomCriteriaDto customCriteriaDto) {
        List<XactType> list = new ArrayList<>();
        
        for (XactDto item : results) {
            List<XactTypeItemActivityDto> xactItems = null;
            
            // retrieve line items if requested
            if (this.targetLevel.equals(TARGET_LEVEL_FULL)) {
                XactTypeItemActivityDto itemCriteria = Rmt2XactDtoFactory.createXactTypeItemActivityInstance((XactTypeItemActivity) null);
                itemCriteria.setXactId(item.getXactId());
                try {
                    xactItems = this.api.getItems(itemCriteria, customCriteriaDto);
                } catch (XactApiException e) {
                    logger.error("Unable to fetch line items for transaction id, " + item.getXactId());
                }    
            }
            
            XactType jaxbObj = TransactionJaxbDtoFactory.createXactJaxbInstance(item, 0, xactItems);
            list.add(jaxbObj);
        }
        return list;
    }
    
    /* (non-Javadoc)
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#buildResponse(java.util.List, com.api.messaging.handler.MessageHandlerCommonReplyStatus)
     */
    @Override
    protected String buildResponse(List<XactType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        return super.buildResponse(payload, replyStatus);
    }

}
