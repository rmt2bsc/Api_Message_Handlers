package org.rmt2.api.handler.transaction.sales;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.ApiMessageHandlerConst;
import org.dao.mapping.orm.rmt2.SalesOrderStatus;
import org.dao.mapping.orm.rmt2.SalesOrderStatusHist;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderStatusDto;
import org.dto.SalesOrderStatusHistDto;
import org.dto.adapter.orm.transaction.sales.Rmt2SalesOrderDtoFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiConst;
import org.modules.transaction.sales.SalesApiException;
import org.modules.transaction.sales.SalesApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.transaction.receipts.CashReceiptsMockData;
import org.rmt2.api.handlers.transaction.sales.CreateSalesOrderAutoInvoiceCashReceiptApiHandler;
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
 * Tests the creation, invoicing, and cash receipt of a sales order as one
 * transaction for the Sales Order message handler
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        CreateSalesOrderAutoInvoiceCashReceiptApiHandler.class, SalesApiFactory.class,
        SystemConfigurator.class })
public class SalesOrderCreateAndInvoiceAndCashReceiptMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    protected static final double TEST_ORDER_TOTAL = 755.94;

    private SalesApi mockApi;

    /**
     * 
     */
    public SalesOrderCreateAndInvoiceAndCashReceiptMessageHandlerTest() {
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
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderCreateAndInvoiceAndCashReceiptRequest.xml");

        SalesOrderStatusHist ormStatusHist = new SalesOrderStatusHist();
        ormStatusHist.setSoStatusId(SalesApiConst.STATUS_CODE_CLOSED);
        SalesOrderStatus ormStatus = new SalesOrderStatus();
        ormStatus.setSoStatusId(SalesApiConst.STATUS_CODE_CLOSED);
        ormStatus.setDescription("Invoice");
        SalesOrderStatusHistDto mockStatusHistDto = Rmt2SalesOrderDtoFactory.createSalesOrderStatusHistoryInstance(ormStatusHist);
        SalesOrderStatusDto mockStatusDto = Rmt2SalesOrderDtoFactory.createSalesOrderStatusInstance(ormStatus);

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenReturn(SalesOrderMockData.NEW_XACT_ID);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for creating a sales order");
        }

        try {
            when(this.mockApi.invoiceSalesOrder(isA(SalesOrderDto.class), isA(List.class), eq(true))).thenReturn(
                    SalesOrderMockData.NEW_INVOICE_ID);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for invoicing a sales order");
        }

        try {
            when(this.mockApi.getCurrentStatus(isA(Integer.class))).thenReturn(mockStatusHistDto);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for creating a sales order status history DTO object");
        }

        try {
            when(this.mockApi.getStatus(isA(Integer.class))).thenReturn(mockStatusDto);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for creating a sales order status DTO object");
        }

        MessageHandlerResults results = null;
        CreateSalesOrderAutoInvoiceCashReceiptApiHandler handler = new CreateSalesOrderAutoInvoiceCashReceiptApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE, request);
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
            Assert.assertEquals(SalesApiConst.STATUS_CODE_CLOSED, a.getStatus().getStatusId().intValue());
            Assert.assertEquals("Invoice", a.getStatus().getDescription());

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
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCreateAndInvoiceRequest.xml");

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenThrow(
                    new SalesApiException("A Sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        CreateSalesOrderAutoInvoiceCashReceiptApiHandler handler = new CreateSalesOrderAutoInvoiceCashReceiptApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE, request);
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
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderCreateAndInvoiceAndCashReceiptRequest.xml");

        MessageHandlerResults results = null;
        CreateSalesOrderAutoInvoiceCashReceiptApiHandler handler = new CreateSalesOrderAutoInvoiceCashReceiptApiHandler();
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
        Assert.assertEquals(CreateSalesOrderAutoInvoiceCashReceiptApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE",
                actualRepsonse.getReplyStatus().getMessage());
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
        CreateSalesOrderAutoInvoiceCashReceiptApiHandler handler = new CreateSalesOrderAutoInvoiceCashReceiptApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE, request);
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
        CreateSalesOrderAutoInvoiceCashReceiptApiHandler handler = new CreateSalesOrderAutoInvoiceCashReceiptApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE, request);
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
        CreateSalesOrderAutoInvoiceCashReceiptApiHandler handler = new CreateSalesOrderAutoInvoiceCashReceiptApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE, request);
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
        CreateSalesOrderAutoInvoiceCashReceiptApiHandler handler = new CreateSalesOrderAutoInvoiceCashReceiptApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE, request);
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
