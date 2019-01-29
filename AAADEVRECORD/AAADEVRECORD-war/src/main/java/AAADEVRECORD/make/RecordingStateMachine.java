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
		logger.info("RecordingStateMachine, start()");

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
			logger.info("RecordingStateMachine starting in state "
					+ currentState.getStateName());
		} catch (final Exception exception) {
			logger.error("start dropping call due to exception: ", exception);
			call.drop();
		}
	}

	public void playCompleted(UUID requestId, PlayOperationCause cause) {
		logger.info("RecordingStateMachine, playCompleted");
		

		
		
		checkStateTransition(currentState.playCompleted(call, requestId, cause,
				recorderMediaListener));
		

	}

	public void digitsCollected(UUID requestId, String digits,
			DigitCollectorOperationCause cause) {
		logger.info("RecordingStateMachine, digitsCollected");
		checkStateTransition(currentState.digitsCollected(call, requestId,
				digits, cause, recorderMediaListener));
	}

	public void recordCompleted(UUID requestId, RecordOperationCause cause)
			throws URISyntaxException, NoAttributeFoundException,
			ServiceNotFoundException {
		logger.info("RecordingStateMachine, recordCompleted");
		checkStateTransition(currentState.recordCompleted(call, requestId,
				cause, recorderMediaListener));




				
//				logger.info("playHelsinky");
//				call.getCallPolicies().setMediaServerInclusion(
//						MediaServerInclusion.AS_NEEDED);
//
//				final StringBuilder sb = new StringBuilder();
//				sb.append("http://135.169.18.7/services/AAADEVRECORD/Helsinki.wav");
//
//				PlayItem playItem = null;
//				try {
//					playItem = MediaFactory.createPlayItem().setInterruptible(true)
//							.setIterateCount(1)
//							.setSource(sb.toString());
//				} catch (URISyntaxException e) {
//					logger.error("Error: " + e);
//				}
//
//				 MediaService mediaServicehelsinky = MediaFactory.createMediaService();
//				 Participant participanthelsinky = call.getCallingParty();
//				logger.info("playHelsinki handle " + participanthelsinky.getHandle());
//				final MyMediaListener3 myMediaListener3 = new MyMediaListener3();
//
//				UUID requestidhelsinki = mediaServicehelsinky.play(participanthelsinky, playItem,
//						myMediaListener3);
//				
//				
//				logger.info("Requestid playHelsinki: " + requestidhelsinki);
//				
//				
//				
//				long start = System.currentTimeMillis();
//				
//				try {
//					Thread.sleep(10000);
//					mediaServicehelsinky.stop(participanthelsinky, requestidhelsinki);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				long end = System.currentTimeMillis();
//				logger.info("Tiempo de espera " + (end - start));
		
		
		
		
	}

	private void checkStateTransition(final BasicState nextState) {
		if (nextState != null) {
			logger.info("RecordingStateMachine, checkStateTransition() changing state from "
					+ currentState.getStateName()
					+ " to "
					+ nextState.getStateName());
			currentState = nextState;
		}
	}

}
