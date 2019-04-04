package service.AAADEVRECORD;

import java.util.UUID;

import AAADEVRECORD.make.RecordingStateMachine;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.call.media.SendDigitsOperationCause;
import com.avaya.collaboration.util.logger.Logger;

public class MyMediaListener extends MediaListenerAbstract{
	private final Call call;
    private final Logger logger = Logger.getLogger(getClass());
    
    /*
     * Constructor
     */
    public MyMediaListener(final Call call)
    {
        this.call = call;
    }
    
    @Override
    public void playCompleted(final UUID requestId, final PlayOperationCause cause)
    {
        
        final RecordingStateMachine recordingStateMachine = new RecordingStateMachine(call);
        recordingStateMachine.start();
    }
    
    @Override
    public void digitsCollected(final UUID requestId, final String digits, final DigitCollectorOperationCause cause)
    {
    }
    
    @Override
    public void sendDigitsCompleted(final UUID requestId, final SendDigitsOperationCause cause)
    {
    }
    
    @Override
    public void recordCompleted(final UUID requestId, final RecordOperationCause cause)
    {
    }

}
