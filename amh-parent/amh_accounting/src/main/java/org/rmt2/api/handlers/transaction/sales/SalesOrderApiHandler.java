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
 * Sales Order common message API handler.
 * 
 * @author roy.terrell
 *
 */
public class SalesOrderApiHandler extends
        AbstractJaxbMessageHandler<AccountingTransactionRequest, AccountingTransactionResponse, List<SalesOrderType>> {

    private static final Logger logger = Logger.getLogger(SalesOrderApiHandler.class);
    public static final String MSG_MISSING_GENERAL_CRITERIA = "Sales order query request must contain a valid general criteria object";
    public static final String MSG_MISSING_SUBJECT_CRITERIA = "Selection criteria is required for Accounting Sales Order fetch operation";
    // public static final String MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT =
    // "Transaction profile is required to contain one and only one transaction for the create transaction operation";
    public static final String MSG_MISSING_TARGET_LEVEL = "Sales order fetch request must contain a target level value";
    public static final String MSG_TARGET_LEVEL_DETAILS_NOT_SUPPORTED = "Sales order details only target level is not supported";

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

}
