package service.AAADEVRECORD;

import java.util.UUID;

import AAADEVRECORD.make.AttributeStore;
import AAADEVRECORD.util.Constants;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.call.media.SendDigitsOperationCause;
import com.avaya.collaboration.util.logger.Logger;


public class MyMediaListener2 extends MediaListenerAbstract{
	private final Call call;
    private final Logger logger = Logger.getLogger(getClass());
//    public static JSONObject json = null;
    
    /*
     * Constructor
     */
    public MyMediaListener2(final Call call, final boolean dropAfterPlayComplete)
    {
        this.call = call;
    }
    
    @Override
    public void playCompleted(final UUID requestId, final PlayOperationCause cause)
    {	
        logger.info("MyMediaListener2, Play completed with the cause " + cause);
        
		logger.info("Hacer llamada");

		String extension;
		try {
			extension = AttributeStore.INSTANCE.getAttributeValue(Constants.AGENT_PHONE);
			// llamar.makeCall(firstParty, secondParty, callingId, display);
			call.divertTo(extension);
		} catch (NoAttributeFoundException | ServiceNotFoundException e) {
			logger.info("Error: " + e);
		}


    }
    
    @Override
    public void digitsCollected(final UUID requestId, final String digits, final DigitCollectorOperationCause cause)
    {

    }
    
    @Override
    public void sendDigitsCompleted(final UUID requestId, final SendDigitsOperationCause cause)
    {
        logger.info("sendDigitsCompleted not expected, requestId = " + requestId);
    }
    
    @Override
    public void recordCompleted(final UUID requestId, final RecordOperationCause cause)
    {
        logger.info("recordCompleted not expected, requestId = " + requestId);
    }

}
