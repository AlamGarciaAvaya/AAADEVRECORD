package AAADEVRECORD.make;

import java.util.UUID;


import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.MediaListener;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.util.logger.Logger;



public class PlayRecordPrompt implements BasicState {
	
	private static final Logger logger = Logger.getLogger(PlayRecordPrompt.class);
	
	@Override
    public BasicState playCompleted(final Call call, final UUID requestId, final PlayOperationCause cause,
            final MediaListener mediaListener)
    {
        logger.info("playCompleted in PlayRecordPrompt, requestId = " + requestId + ", cause = " + cause);
        if (cause == PlayOperationCause.COMPLETE)
        {
            // Start recording the caller.
            final MediaOperations mediaOperations = new MediaOperations(mediaListener);
            mediaOperations.record(call.getCallingParty());
            return new RecordMessage();
        }
        else
        {
            logger.error("PlayRecordPrompt.playCompleted dropping call due to unexpected cause");
            call.drop();
            return new Done();
        }
    }
	@Override
    public BasicState digitsCollected(final Call call, final UUID requestId, final String digits,
            final DigitCollectorOperationCause cause, final MediaListener mediaListener)
    {
        logger.error("digitsCollected not expected in PlayRecordPrompt state, dropping call");
        call.drop();
        return new Done();
    }

    @Override
    public BasicState recordCompleted(final Call call, final UUID requestId, final RecordOperationCause cause,
            final MediaListener mediaListener)
    {
        logger.error("recordCompleted not expected in PlayRecordPrompt state, dropping call");
        call.drop();
        return new Done();
    }

    @Override
    public String getStateName()
    {
        return "PlayRecordPrompt";
    }

}
