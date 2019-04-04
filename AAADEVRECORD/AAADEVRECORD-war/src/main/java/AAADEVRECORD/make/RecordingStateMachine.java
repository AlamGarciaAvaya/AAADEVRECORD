package AAADEVRECORD.make;

import java.net.URISyntaxException;
import java.util.UUID;

import AAADEVRECORD.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;


public class RecordingStateMachine {
	private final Call call;
	private final RecorderMediaListener recorderMediaListener;
	private BasicState currentState;
	private static final Logger logger = Logger
			.getLogger(RecordingStateMachine.class);
	
	
	public RecordingStateMachine(final Call call) {
		this.call = call;
		this.recorderMediaListener = new RecorderMediaListener(this);
	}

	public void start() {
		final MediaOperations mediaOperations = new MediaOperations(
				recorderMediaListener);
		try {
			/*
			 * Determina la URL del servicio
			 */
			final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
			final String trafficInterfaceAddress = addressRetriever
					.getTrafficInterfaceAddress();

			final String myServiceName = ServiceUtil.getServiceDescriptor()
					.getName();
			
			final StringBuilder sb = new StringBuilder();
			sb.append("http://").append(trafficInterfaceAddress)
					.append("/services/").append(myServiceName).append("/")
					.append("Beep.wav");
			/*------------------------------------------------------------------------------*/
			mediaOperations.playTtsPrompt(sb.toString(),call.getCallingParty());
			currentState = new PlayRecordPrompt();
		} catch (final Exception exception) {
			logger.error("start dropping call due to exception: ", exception);
			call.drop();
		}
	}

	public void playCompleted(UUID requestId, PlayOperationCause cause) {

		checkStateTransition(currentState.playCompleted(call, requestId, cause,
				recorderMediaListener));
	}

	public void digitsCollected(UUID requestId, String digits,
			DigitCollectorOperationCause cause) {
		checkStateTransition(currentState.digitsCollected(call, requestId,
				digits, cause, recorderMediaListener));
	}

	public void recordCompleted(UUID requestId, RecordOperationCause cause)
			throws URISyntaxException, NoAttributeFoundException,
			ServiceNotFoundException {
		checkStateTransition(currentState.recordCompleted(call, requestId,
				cause, recorderMediaListener));
	
		
	}

	private void checkStateTransition(final BasicState nextState) {
		if (nextState != null) {
			currentState = nextState;
		}
	}

}
