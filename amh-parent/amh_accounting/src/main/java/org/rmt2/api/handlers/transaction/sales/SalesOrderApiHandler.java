package org.rmt2.api.handlers.transaction.sales;

import java.util.List;

import org.apache.log4j.Logger;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.api.handlers.AccountingtMsgHandlerUtility;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.TransactionDetailGroup;

import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;

/**
 * Common message API handler for the Sales Order API.
 * 
 * @author roy.terrell
 *
 */
public class SalesOrderApiHandler extends
        AbstractJaxbMessageHandler<AccountingTransactionRequest, AccountingTransactionResponse, List<SalesOrderType>> {

    private static final Logger logger = Logger.getLogger(SalesOrderApiHandler.class);
    public static final String MSG_MISSING_GENERAL_CRITERIA = "Transaction request must contain a valid general criteria object";
    public static final String MSG_MISSING_SUBJECT_CRITERIA = "Selection criteria is required for Accounting Transaction fetch operation";
    public static final String MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT = "Transaction profile is required to contain one and only one transaction for the create transaction operation";
    
    

    protected SalesApi api;
    protected ObjectFactory jaxbObjFactory;
    protected String targetLevel;

    /**
     * Create SalesOrderApiHandler object
     * 
     * @param connection
     *            an instance of {@link DaoClient}
     */
    public SalesOrderApiHandler() {
        super();
        this.api = SalesApiFactory.createApi();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingTransactionResponse();

        // Load cache data
        AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        logger.info(SalesOrderApiHandler.class.getName() + " was instantiated successfully");
    }
    
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidRequestException {
        return;
    }

  @Override
  protected String buildResponse(List<SalesOrderType> payload, MessageHandlerCommonReplyStatus replyStatus) {
      if (replyStatus != null) {
          ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
          this.responseObj.setReplyStatus(rs);
      }

      if (payload != null) {
          TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();
          profile.setSalesOrders(jaxbObjFactory.createSalesOrderListType());
          profile.getSalesOrders().getSalesOrder().addAll(payload);
          this.responseObj.setProfile(profile);
      }

      String xml = this.jaxb.marshalMessage(this.responseObj);
      return xml;
  }
  
  
//    /**
//     * Processes requests pertaining to fetching and creating of common
//     * transactions.
//     * 
//     * @param command
//     *            The name of the operation.
//     * @param payload
//     *            The XML message that is to be processed.
//     * @return MessageHandlerResults
//     * @throws MessageHandlerCommandException
//     *             <i>payload</i> is deemed invalid.
//     */
//    @Override
//    public MessageHandlerResults processMessage(String command, Serializable payload) throws MessageHandlerCommandException {
//        MessageHandlerResults r = super.processMessage(command, payload);
//
//        if (r != null) {
//            // This means an error occurred.
//            return r;
//        }
//        switch (command) {
//            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE:
//                r = this.reverse(this.requestObj);
//                break;
//
//            default:
//                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE, MessagingConstants.RETURN_STATUS_BAD_REQUEST,
//                        ERROR_MSG_TRANS_NOT_FOUND + command);
//        }
//        return r;
//    }
//
//
//    /**
//     * Handler for invoking the appropriate API in order to reverse a general
//     * accounting Transaction object.
//     * 
//     * @param req
//     *            an instance of {@link AccountingTransactionRequest}
//     * @return an instance of {@link MessageHandlerResults}
//     */
//    protected MessageHandlerResults reverse(AccountingTransactionRequest req) {
//        MessageHandlerResults results = new MessageHandlerResults();
//        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
//        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
//        List<SalesOrderType> tranRresults = new ArrayList<>();
//        int newXactId = 0;
//        int oldXactId = 0;
//
//        try {
//            // Set reply status
//            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
//            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);
//            List<XactTypeItemActivityDto> itemsDtoList = TransactionJaxbDtoFactory.createXactItemDtoInstance(reqXact.getLineitems().getLineitem());
//
//            oldXactId = xactDto.getXactId();
//            newXactId = this.api.reverse(xactDto, itemsDtoList);
//            xactDto.setXactId(newXactId);
//            XactType XactResults = TransactionJaxbDtoFactory.createXactJaxbInstance(xactDto, 0, itemsDtoList);
//            tranRresults.add(XactResults);
//            String msg = RMT2String.replace(SalesOrderHandlerConst.MSG_REVERSE_SUCCESS, String.valueOf(oldXactId), "%s1");
//            msg = RMT2String.replace(msg, String.valueOf(newXactId), "%s2");
//            rs.setMessage(msg);
//            rs.setRecordCount(1);
//
//            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
//            this.responseObj.setHeader(req.getHeader());
//        } catch (Exception e) {
//            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
//            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
//            rs.setMessage("Failure to reverse Transaction: " + oldXactId);
//            rs.setExtMessage(e.getMessage());
//        } finally {
//            this.api.close();
//        }
//
//        String xml = this.buildResponse(tranRresults, rs);
//        results.setPayload(xml);
//        return results;
//    }
//
//    
//    
//    @Override
//    protected String buildResponse(List<SalesOrderType> payload, MessageHandlerCommonReplyStatus replyStatus) {
//        if (replyStatus != null) {
//            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
//            this.responseObj.setReplyStatus(rs);
//        }
//
//        if (payload != null) {
//            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();
//            profile.setSalesOrders(jaxbObjFactory.createSalesOrderListType());
//            profile.getSalesOrders().getSalesOrder().addAll(payload);
//            this.responseObj.setProfile(profile);
//        }
//
//        String xml = this.jaxb.marshalMessage(this.responseObj);
//        return xml;
//    }
//    /**
//   * Handler for invoking the appropriate API in order to fetch one or more
//   * Transaction objects.
//   * <p>
//   * Currently, the target level, <i>DETAILS</i>, is not supported.
//   * 
//   * @param req
//   *            an instance of {@link AccountingTransactionRequest}
//   * @return an instance of {@link MessageHandlerResults}
//   */
//  protected MessageHandlerResults fetch(AccountingTransactionRequest req) {
//      MessageHandlerResults results = new MessageHandlerResults();
//      MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
//      List<SalesOrderType> queryDtoResults = null;
//
//      try {
//          // Set reply status
//          rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
//          XactDto criteriaDto = TransactionJaxbDtoFactory.createBaseXactDtoCriteriaInstance(req.getCriteria().getXactCriteria().getBasicCriteria());
//
//          this.targetLevel = req.getCriteria().getXactCriteria().getTargetLevel().name().toUpperCase();
//          switch (this.targetLevel) {
//              case TARGET_LEVEL_HEADER:
//              case TARGET_LEVEL_FULL:
//                  List<XactDto> dtoList = this.api.getXact(criteriaDto);
//                  if (dtoList == null) {
//                      rs.setMessage(SalesOrderApiHandler.MSG_DATA_NOT_FOUND);
//                      rs.setRecordCount(0);
//                  }
//                  else {
//                      queryDtoResults = this.buildJaxbTransaction(dtoList);
//                      rs.setMessage(SalesOrderApiHandler.MSG_DATA_FOUND);
//                      rs.setRecordCount(dtoList.size());
//                  }
//                  break;
//
//              default:
//                  String msg = RMT2String.replace(MSG_INCORRECT_TARGET_LEVEL, targetLevel, "%s");
//                  throw new RMT2Exception(msg);
//          }
//
//          rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
//          this.responseObj.setHeader(req.getHeader());
//      } catch (Exception e) {
//          logger.error("Error occurred during API Message Handler operation, " + this.command, e);
//          rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
//          rs.setMessage("Failure to retrieve Transaction(s)");
//          rs.setExtMessage(e.getMessage());
//      } finally {
//          this.api.close();
//      }
//
//      String xml = this.buildResponse(queryDtoResults, rs);
//      results.setPayload(xml);
//      return results;
//  }
//    /**
//     * Builds a List of XactType objects from a List of XactDto objects.
//     * 
//     * @param results
//     *            List<{@link XactDto}>
//     * @return List<{@link XactType}>
//     */
//    private List<SalesOrderType> buildJaxbTransaction(List<XactDto> results) {
//        List<SalesOrderType> list = new ArrayList<>();
//
//        for (XactDto item : results) {
//            List<XactTypeItemActivityDto> xactItems = null;
//
//            // retrieve line items if requested
//            if (this.targetLevel.equals(TARGET_LEVEL_FULL)) {
//                try {
//                    xactItems = this.api.getXactTypeItemActivityExt(item.getXactId());
//                } catch (XactApiException e) {
//                    logger.error("Unable to fetch line items for transaction id, " + item.getXactId());
//                }
//            }
//
//            SalesOrderType jaxbObj = TransactionJaxbDtoFactory.createXactJaxbInstance(item, 0, xactItems);
//            list.add(jaxbObj);
//        }
//        return list;
//    }
//    /**
//     * Handler for invoking the appropriate API in order to create a general
//     * accounting Transaction object.
//     * 
//     * @param req
//     *            an instance of {@link AccountingTransactionRequest}
//     * @return an instance of {@link MessageHandlerResults}
//     */
//    protected MessageHandlerResults create(AccountingTransactionRequest req) {
//        MessageHandlerResults results = new MessageHandlerResults();
//        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
//        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
//        List<SalesOrderType> tranRresults = new ArrayList<>();
//
//        try {
//            // Set reply status
//            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
//            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);
//            List<XactTypeItemActivityDto> itemsDtoList = TransactionJaxbDtoFactory.createXactItemDtoInstance(reqXact.getLineitems().getLineitem());
//
//            int newXactId = this.api.update(xactDto, itemsDtoList);
//            xactDto.setXactId(newXactId);
//            XactType XactResults = TransactionJaxbDtoFactory.createXactJaxbInstance(xactDto, 0, itemsDtoList);
//            tranRresults.add(XactResults);
//            rs.setMessage("New Accounting Transaction was created: " + XactResults.getXactId());
//            rs.setRecordCount(1);
//
//            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
//            this.responseObj.setHeader(req.getHeader());
//        } catch (Exception e) {
//            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
//            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
//            rs.setMessage("Failure to create Transaction");
//            rs.setExtMessage(e.getMessage());
//        } finally {
//            this.api.close();
//        }
//
//        String xml = this.buildResponse(tranRresults, rs);
//        results.setPayload(xml);
//        return results;
//    }
//    /**
//     * Validates the existence of a request's search criteria.
//     * 
//     * @param req
//     * @throws InvalidDataException
//     */
//    protected void validateSearchRequest(AccountingTransactionRequest req) throws InvalidDataException {
//        try {
//            Verifier.verifyNotNull(req.getCriteria());
//            Verifier.verifyNotNull(req.getCriteria().getXactCriteria());
//        } catch (VerifyException e) {
//            throw new InvalidRequestException(MSG_MISSING_GENERAL_CRITERIA);
//        }
//
//        // Must contain flag that indicates what level of the transaction object
//        // to populate with data
//        try {
//            Verifier.verifyNotNull(req.getCriteria().getXactCriteria().getTargetLevel());
//        } catch (VerifyException e) {
//            throw new InvalidRequestException(MSG_MISSING_TARGET_LEVEL, e);
//        }
//
//        // Target level "DETAILS" is not supported.
//        try {
//            Verifier.verifyFalse(req.getCriteria().getXactCriteria().getTargetLevel().name().equalsIgnoreCase(TARGET_LEVEL_DETAILS));
//        } catch (VerifyException e) {
//            throw new InvalidRequestException(MSG_DETAILS_NOT_SUPPORTED, e);
//        }
//    }
//
//    /**
//     * Validates the existence of a request's update data.
//     * 
//     * @param req
//     * @throws InvalidDataException
//     */
//    protected void validateUpdateRequest(AccountingTransactionRequest req) throws InvalidDataException {
//        // Transaction profile must exist
//        try {
//            Verifier.verifyNotNull(req.getProfile());
//        } catch (VerifyException e) {
//            throw new InvalidRequestException(MSG_MISSING_PROFILE_DATA, e);
//        }
//
//        // Must include transaction section.
//        try {
//            Verifier.verifyNotNull(req.getProfile().getTransactions());
//        } catch (VerifyException e) {
//            throw new InvalidRequestException(MSG_MISSING_TRANSACTION_SECTION, e);
//        }
//
//        // Transaction profile must contain one and only one transaction
//        try {
//            Verifier.verifyTrue(req.getProfile().getTransactions().getTransaction().size() == 1);
//        } catch (VerifyException e) {
//            throw new InvalidRequestException(MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT, e);
//        }
//    }
//
//    /**
//     * Validates the existence of the request's search criteria or profile data
//     * depending on the type of request submitted.
//     * 
//     * @param req
//     *            instance of {@link AccountingTransactionRequest}
//     */
//    @Override
//    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
//        try {
//            Verifier.verifyNotNull(req);
//        } catch (VerifyException e) {
//            throw new InvalidRequestException("Transaction request element is invalid");
//        }
//
//        // Validate request for fetch operations
//        switch (this.command) {
//            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET:
//                this.validateSearchRequest(req);
//                break;
//
//            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_CREATE:
//            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE:
//                this.validateUpdateRequest(req);
//
//                // Transaction profile must exist
//                break;
//            default:
//                break;
//        }
//    }

    
}
