package org.rmt2.api.handlers.maint;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dto.TracksDto;
import org.modules.audiovideo.AudioVideoApi;
import org.modules.audiovideo.AudioVideoFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MultimediaRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Message handler for updating media track related messages for the Media API.
 * 
 * @author roy.terrell
 *
 */
public class TrackUpdateApiHandler extends AudioVideoApiHandler {
    
    private static final Logger logger = Logger.getLogger(TrackUpdateApiHandler.class);

    /**
     * @param payload
     */
    public TrackUpdateApiHandler() {
        super();
        logger.info(TrackUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_TRACK_UPDATE:
                r = this.doOperation(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to update a media track
     * object.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     */
    @Override
    protected void processTransactionCode(MultimediaRequest req) {
        int rc = 0;
        try {
            // Get criteria data
            TracksDto criteriaDto = TrackJaxbDtoFactory.createTracksDtoInstance(req.getProfile().getAudioVideoDetails()
                    .getArtist().get(0).getProjects().getProject().get(0).getTracks().getTrack().get(0));

            boolean isNew = criteriaDto.getTrackId() == 0;

            // Make API call
            AudioVideoApi api = AudioVideoFactory.createApi();
            rc = api.updateTrack(criteriaDto);
            String msg = null;
            if (rc > 0) {
                if (isNew) {
                    msg = RMT2String.replace(TrackApiHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS, String.valueOf(rc),
                            ApiMessageHandlerConst.MSG_PLACEHOLDER);
                    this.rs.setMessage(msg);
                }
                else {
                    msg = RMT2String.replace(TrackApiHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS,
                            String.valueOf(criteriaDto.getTrackId()), ApiMessageHandlerConst.MSG_PLACEHOLDER);
                    this.rs.setMessage(msg);
                }
            }
            else {
                msg = RMT2String.replace(TrackApiHandlerConst.MESSAGE_UPDATE_NO_CHANGE, String.valueOf(criteriaDto.getTrackId()),
                        ApiMessageHandlerConst.MSG_PLACEHOLDER);
                this.rs.setMessage(msg);
            }
            this.rs.setRecordCount(1);
            this.jaxbResults.add(null);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(TrackApiHandlerConst.MESSAGE_UPDATE_ERROR);
            rs.setExtMessage(e.getMessage());
        }
    }

    
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        super.validateRequest(req);
        try {
            Verifier.verify(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.MEDIA_TRACK_UPDATE));
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Invalid transaction code for this message handler: "
                    + req.getHeader().getTransaction());
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_ERROR);
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_AUDIOVIDEODETAILS);
        }
        
        try {
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().size() > 0);
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects());
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().size() > 0);
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().get(0).getTracks());
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().get(0).getTracks().getTrack().size() > 0);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_TRACKS);
        }
        
        try {
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().size() == 1);
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().size() == 1);
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().get(0)
                    .getTracks().getTrack().size() == 1);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(TrackApiHandlerConst.MESSAGE_UPDATE_TOO_MANY_TRACKS);
        }
    }

}
