package AAADEVRECORD.make;

import java.net.URISyntaxException;
import java.util.UUID;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.MediaListener;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.util.logger.Logger;


public class RecordMessage implements BasicState {
	private static final Logger logger = Logger.getLogger(RecordMessage.class);
	


	@Override
	public BasicState playCompleted(final Call call, final UUID requestId,
			final PlayOperationCause cause, final MediaListener mediaListener) {
		call.drop();
		return new Done();
	}

	@Override
	public BasicState digitsCollected(final Call call, final UUID requestId,
			final String digits, final DigitCollectorOperationCause cause,
			final MediaListener mediaListener) {
		call.drop();
		return new Done();
	}

	@Override
	public BasicState recordCompleted(final Call call, final UUID requestId,
			final RecordOperationCause cause, final MediaListener mediaListener)
			throws URISyntaxException, NoAttributeFoundException,
			ServiceNotFoundException {

		logger.info("RecordMessage, recordCompleted() in RecordMessage, requestId = "
				+ requestId + ", cause = " + cause);
		
			
		if (cause == RecordOperationCause.TERMINATION_KEY_PRESSED) {
			
				try {
					playHelsinki play = new playHelsinki(call);
					play.start();
				} catch (Exception e) {
					PlayError playError = new PlayError(call);
					playError.audioError();
					logger.info("Error RecordMessage: " + e);
				}

			return new Done();
			} else {
			logger.error("RecordMessage.recordCompleted dropping call due to unexpected cause");
			call.drop();
			return new Done();
		}
	}

	@Override
	public String getStateName() {
		return "RecordMessage";
	}
} 
