package org.rmt2.api.handler.transaction.sales;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.ApiMessageHandlerConst;
import org.dto.SalesOrderDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiException;
import org.modules.transaction.sales.SalesApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.transaction.receipts.CashReceiptsMockData;
import org.rmt2.api.handlers.transaction.receipts.CashReceiptsApiHandler;
import org.rmt2.api.handlers.transaction.sales.SalesOrderApiCreationHandler;
import org.rmt2.api.handlers.transaction.sales.SalesOrderHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;
import com.api.util.RMT2String;

/**
 * Tests the sales order creation API message handler
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, SalesOrderApiCreationHandler.class, SalesApiFactory.class,
        SystemConfigurator.class })
public class SalesOrderCreateMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    protected static final double TEST_ORDER_TOTAL = 755.94;

    private SalesApi mockApi;

    /**
     * 
     */
    public SalesOrderCreateMessageHandlerTest() {
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#setUp()
     */
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockApi = Mockito.mock(SalesApi.class);
        PowerMockito.mockStatic(SalesApiFactory.class);
        PowerMockito.when(SalesApiFactory.createApi()).thenReturn(this.mockApi);
        doNothing().when(this.mockApi).close();

        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        return;
    }

    /**
     * Test the ReceivePayment method successfully
     */
    @Test
    public void testSuccess_create() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCreateRequest.xml");

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenReturn(SalesOrderMockData.NEW_XACT_ID);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for creating a sales order");
        }

        MessageHandlerResults results = null;
        SalesOrderApiCreationHandler handler = new SalesOrderApiCreationHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String expectedMsg = RMT2String.replace(SalesOrderHandlerConst.MSG_CREATE_SUCCESS, String.valueOf(CashReceiptsMockData.NEW_XACT_ID), "%s");
        Assert.assertEquals(expectedMsg, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getSalesOrders());
        Assert.assertTrue(actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(); ndx++) {
            SalesOrderType a = actualRepsonse.getProfile().getSalesOrders().getSalesOrder().get(ndx);
            Assert.assertNotNull(a.getSalesOrderId());
            Assert.assertEquals(SalesOrderMockData.NEW_XACT_ID, a.getSalesOrderId().intValue());
            Assert.assertNotNull(a.getCustomerId());
            Assert.assertEquals(SalesOrderMockData.CUSTOMER_ID, a.getCustomerId().intValue());
            Assert.assertEquals(TEST_ORDER_TOTAL, a.getOrderTotal().doubleValue(), 0);

            // Test that order total equals sum of sales order items
            double itemTotal = 0;
            for (SalesOrderItemType item : a.getSalesOrderItems().getSalesOrderItem()) {
                itemTotal += item.getMarkup().doubleValue() * item.getUnitCost().doubleValue();
            }
            Assert.assertEquals(TEST_ORDER_TOTAL, itemTotal, 0);
        }
    }

    @Test
    public void test_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCreateRequest.xml");

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenThrow(
                    new SalesApiException("A Sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        SalesOrderApiCreationHandler handler = new SalesOrderApiCreationHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_CREATE_FAILURE, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("A Sales order API test error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryInvalidTranCodeRequest.xml");

        MessageHandlerResults results = null;
        CashReceiptsApiHandler handler = new CashReceiptsApiHandler();
        try {
            results = handler.processMessage("INCORRECT_TRAN_CODE", request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(CashReceiptsApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCreateRequest_Missing_Profile.xml");

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenThrow(
                    new SalesApiException("A Sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        SalesOrderApiCreationHandler handler = new SalesOrderApiCreationHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ApiMessageHandlerConst.MSG_MISSING_PROFILE_DATA, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_SalesOrder_Structure() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCreateRequest_Missing_SalesOrder_Structure.xml");

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenThrow(
                    new SalesApiException("A Sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        SalesOrderApiCreationHandler handler = new SalesOrderApiCreationHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_SALESORDER_STRUCTURE, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_SalesOrder_List() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCreateRequest_SalesOrder_List_Empty.xml");

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenThrow(
                    new SalesApiException("A Sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        SalesOrderApiCreationHandler handler = new SalesOrderApiCreationHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_SALESORDER_LIST_EMPTY, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Too_Many_SalesOrders() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCreateRequest_Too_Many_SalesOrders.xml");

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenThrow(
                    new SalesApiException("A Sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        SalesOrderApiCreationHandler handler = new SalesOrderApiCreationHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_SALESORDER_LIST_CONTAINS_TOO_MANY, actualRepsonse.getReplyStatus().getMessage());
    }
}