package org.rmt2.api.handlers.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.VendorItemDto;
import org.modules.CommonAccountingConst;
import org.modules.inventory.InventoryApi;
import org.modules.inventory.InventoryApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.InventoryDetailGroup;
import org.rmt2.jaxb.InventoryRequest;
import org.rmt2.jaxb.InventoryResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.jaxb.VendorItemType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Vendor Item related messages to the Accounting
 * API.
 * 
 * @author roy.terrell
 *
 */
public class VendorItemApiHandler extends 
                  AbstractJaxbMessageHandler<InventoryRequest, InventoryResponse, List<VendorItemType>> {
    
    private static final Logger logger = Logger.getLogger(VendorItemApiHandler.class);
    private ObjectFactory jaxbObjFactory;
    private InventoryApi api;

    /**
     * @param payload
     */
    public VendorItemApiHandler() {
        super();
        InventoryApiFactory f = new InventoryApiFactory();
        this.api = f.createApi(CommonAccountingConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createInventoryResponse();
        logger.info(VendorItemApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.INVENTORY_VENDOR_ITEM_GET:
                r = this.fetch(this.requestObj);
                break;
            case ApiTransactionCodes.INVENTORY_VENDOR_ASSIGNED_ITEMS_GET:
                r = this.fetchVendorAssignedItems(this.requestObj);
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
     * Vendor Item type ojects.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetch(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<VendorItemType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            VendorItemDto criteriaDto = InventoryJaxbDtoFactory
                    .createVendorItemDtoCriteriaInstance(req.getCriteria().getVendorItemCriteria());
            
            int itemId = criteriaDto.getItemId();
            List<VendorItemDto> dtoList = this.api.getVendorItem(criteriaDto.getVendorId(), 
                    (itemId <= 0 ? null : itemId));
            if (dtoList == null) {
                rs.setMessage("Vendor item data not found!");
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Vendor item record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve vendor item data");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * Vendor Assigned Item type ojects.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchVendorAssignedItems(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<VendorItemType> queryDtoResults = null;
        int vendorId = 0;
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            VendorItemDto criteriaDto = InventoryJaxbDtoFactory
                    .createVendorItemDtoCriteriaInstance(req.getCriteria().getVendorItemCriteria());
            vendorId = criteriaDto.getVendorId();
            List<VendorItemDto> dtoList = this.api.getVendorAssignItems(criteriaDto.getVendorId());
            if (dtoList == null) {
                rs.setMessage("Vendor assigned item data not found for vendor id, " + vendorId);
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Vendor assigned item record(s) found for vendor id, " + vendorId);
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve vendor assigned item data for vendor id, " + vendorId);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<VendorItemType> buildJaxbListData(List<VendorItemDto> results) {
        List<VendorItemType> list = new ArrayList<>();
        for (VendorItemDto item : results) {
            VendorItemType jaxbObj = InventoryJaxbDtoFactory.createVendorItemTypeJaxbInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
   
    
    @Override
    protected void validateRequest(InventoryRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Inventory vendo item type message request element is invalid");
        }
        
        try {
            Verifier.verifyNotNull(req.getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getVendorItemCriteria());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Vendor item selection criteria is required for query operation");
        }
        
        if (this.command.equals(ApiTransactionCodes.INVENTORY_VENDOR_ASSIGNED_ITEMS_GET)) {
            try {
                Verifier.verifyNotNull(req.getCriteria().getVendorItemCriteria().getCreditorId());
            }
            catch (VerifyException e) {
                throw new InvalidRequestException("Creditor Id criteria is required for vendor assigned item query operation");
            }
        }
    }

    @Override
    protected String buildResponse(List<VendorItemType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            InventoryDetailGroup profile = this.jaxbObjFactory.createInventoryDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getVendorItem().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}