package service.AAADEVRECORD;

import java.net.URISyntaxException;

import AAADEVRECORD.make.AttributeStore;
import AAADEVRECORD.util.Constants;
import AAADEVRECORD.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.CallListenerAbstract;
import com.avaya.collaboration.call.CallTerminationCause;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.TheCallListener;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/*
 * This class is needed if an application with call features is written.
 * If you have an application which is doing only HTTP related operations, remove this class from the project.
 * 
 * For HTTP only application, also remove the sip.xml from src/main/java/webapp/WEB-INF and blank out details from
 * CARRule.xml. Look at the files for more details.
 * 
 */
@TheCallListener
public class MyCallListener extends CallListenerAbstract {
	private final Logger logger;

	public MyCallListener() {

		logger = Logger.getLogger(MyCallListener.class);

	}

	@Override
	public final void callIntercepted(final Call call) {
		logger.fine("Entered callIntercepted.");

		if (call.isCalledPhase()) {
			boolean playWelcome = false;

			try {
				promptPlayAndExecute(call, playWelcome);

			} catch (final URISyntaxException e) {
				logger.error("Error doing prompt and collect", e);
			} catch (NoAttributeFoundException e) {
				logger.error("Error doing prompt and collect", e);
			} catch (ServiceNotFoundException e) {
				logger.error("Error doing prompt and collect", e);
			} catch (NoUserFoundException e) {
				logger.error("Error doing prompt and collect", e);
			} catch (NoServiceProfileFoundException e) {
				logger.error("Error doing prompt and collect", e);
			}

		} else {
			logger.info("Snap-in sequenced in calling phase");
		}
	}

	@Override
	public void callAlerting(final Participant participant) {
	}

	@Override
	public void participantDropped(final Call call,
			final Participant droppedParticipant,
			final CallTerminationCause cause) {

	}

	@Override
	public void callAnswered(final Call call) {
		call.allow();
	}

	@Override
	public void callTerminated(final Call call, final CallTerminationCause cause) {
		/*
		 * public final class CallTerminationCause extends Enum An enumeration
		 * of the Call Termination Cause which can be used to find reason of
		 * call termination.
		 */
		if (cause == CallTerminationCause.AFTER_ANSWER) {
			/*
			 * AFTER_ANSWER Call ended normally after it was answered.
			 */
		} else if (cause == CallTerminationCause.ABANDONED
				|| cause == CallTerminationCause.REJECTED
				|| cause == CallTerminationCause.TARGET_DID_NOT_RESPOND) {
			/*
			 * ABANDONED Calling party abandoned call before it was answered.
			 * 
			 * REJECTED The called party rejected the call
			 * 
			 * TARGET_DID_NOT_RESPOND Called target did not respond.
			 */

		}

	}

	private void promptPlayAndExecute(final Call call, final boolean playWelcome)
			throws URISyntaxException, NoAttributeFoundException,
			ServiceNotFoundException, NoUserFoundException,
			NoServiceProfileFoundException {
		// set the Media Inclusion Policy to AS_NEEDED. Media Server is not
		// required for the whole duration of the call.
		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);
		LanguageAttribute languageAtribute = new LanguageAttribute(call);
		String announcement = null;
		/*
		 * Solicitar el idioma por Service Profile
		 */
		if (languageAtribute.getLanguageAttribute().equals("es")) {
			announcement = "Bienvenido_es.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("en")) {
			announcement = "Welcome_en.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("pt")) {
			announcement = "Bem_vindo_pt.wav";
		}
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
				.append(announcement);

		PlayItem playItem = null;

		/*
		 * PlayWelcome == null reproduce el mensaje de bienvenida
		 * "Bienvenido.wav" es el resultado del stringBuilder
		 */
		playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaService = MediaFactory.createMediaService();
		final Participant participant = call.getCallingParty();
		final MyMediaListener myMediaListener = new MyMediaListener(call);
		mediaService.play(participant, playItem, myMediaListener);
	}
}
