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

public interface BasicState {
    BasicState playCompleted(Call call, UUID requestId, PlayOperationCause cause,
            MediaListener mediaListener);
    
    BasicState digitsCollected(Call call, UUID requestId, String digits,
            DigitCollectorOperationCause cause, MediaListener mediaListener);
    
    BasicState recordCompleted(Call call, UUID requestId, RecordOperationCause cause,
            MediaListener mediaListener) throws URISyntaxException, NoAttributeFoundException, ServiceNotFoundException;
    
    String getStateName();
}
