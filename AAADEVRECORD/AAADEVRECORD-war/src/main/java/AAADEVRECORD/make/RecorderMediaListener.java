package AAADEVRECORD.make;

import java.net.URISyntaxException;
import java.util.UUID;



import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordOperationCause;

public class RecorderMediaListener extends MediaListenerAbstract
{
    private final RecordingStateMachine stateMachine;

    public RecorderMediaListener(final RecordingStateMachine stateMachine)
    {
        this.stateMachine = stateMachine;
    }

    @Override
    public void playCompleted(UUID requestId, PlayOperationCause cause)
    {
        stateMachine.playCompleted(requestId, cause);
    }

    @Override
    public void digitsCollected(UUID requestId, String digits, DigitCollectorOperationCause cause)
    {
        stateMachine.digitsCollected(requestId, digits, cause);
    }

    @Override
    public void recordCompleted(UUID requestId, RecordOperationCause cause)
    {
        try {
			stateMachine.recordCompleted(requestId, cause);
		} catch (URISyntaxException | NoAttributeFoundException
				| ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
