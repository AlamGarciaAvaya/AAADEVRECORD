package AAADEVRECORD.make;

import java.net.URISyntaxException;
import java.util.UUID;

import service.AAADEVRECORD.MyCallListener;
import service.AAADEVRECORD.MyMediaListener3;
import AAADEVRECORD.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class playHelsinki extends Thread {
	private final Call call;
	private final Logger logger;
	private MediaService mediaServicehelsinky = null;
	private UUID requestid = null;

	public playHelsinki(final Call call) {
		this.call = call;

		logger = Logger.getLogger(MyCallListener.class);

	}

	@Override
	public void run() {

		logger.info("playHelsinky");
		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
		.append("/services/").append(myServiceName).append("/")
		.append("Helsinki.wav");

		PlayItem playItem = null;
		try {
			playItem = MediaFactory.createPlayItem().setInterruptible(true)
					.setIterateCount(5)
					.setSource(sb.toString());
		} catch (URISyntaxException e) {
			logger.error("Error: " + e);
		}

		mediaServicehelsinky = MediaFactory.createMediaService();
		final Participant participant = call.getCallingParty();
		logger.info("playHelsinki handle " + participant.getHandle());
		final MyMediaListener3 myMediaListener3 = new MyMediaListener3();

		requestid = mediaServicehelsinky.play(participant, playItem,
				myMediaListener3);
		logger.info("Requestid playHelsinki: " + requestid);
		
		/*
		 * 
		 */
		PeticionesExternas peticiones = new PeticionesExternas(call);
		try {
			peticiones.peticionesExternas(participant, requestid, mediaServicehelsinky);
			
		} catch (NoAttributeFoundException e) {
			
	    	mediaServicehelsinky.stop(participant, requestid);
	    	PlayError playError = new PlayError(call);
			try {
				playError.audioError();
			} catch (NoAttributeFoundException | ServiceNotFoundException e1) {
				logger.error("Error playHelsinky - playError:" + e);
			}
			logger.error("Error peticionesExternas " + e);
			
		} catch (ServiceNotFoundException e) {
			mediaServicehelsinky.stop(participant, requestid);
	    	PlayError playError = new PlayError(call);
			try {
				playError.audioError();
			} catch (NoAttributeFoundException | ServiceNotFoundException e1) {
				logger.error("Error playHelsinky - playError:" + e);
			}
			logger.error("Error peticionesExternas " + e);
		} catch (URISyntaxException e) {
			mediaServicehelsinky.stop(participant, requestid);
	    	PlayError playError = new PlayError(call);
			try {
				playError.audioError();
			} catch (NoAttributeFoundException | ServiceNotFoundException e1) {
				logger.error("Error playHelsinky - playError:" + e);
			}
			logger.error("Error peticionesExternas " + e);
		} catch (Exception e){
			
			mediaServicehelsinky.stop(participant, requestid);
	    	PlayError playError = new PlayError(call);
			try {
				playError.audioError();
			} catch (NoAttributeFoundException | ServiceNotFoundException e1) {
				logger.error("Error playHelsinky - playError:" + e);
			}
			logger.error("Error peticionesExternas " + e);
		}

	}

}
