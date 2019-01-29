package AAADEVRECORD.make;

import java.util.UUID;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.MediaListener;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordOperationCause;

final class Done implements BasicState
{

    @Override
    public BasicState playCompleted(final Call call, final UUID requestId, final PlayOperationCause cause,
            final MediaListener mediaListener)
    {
        return null;
    }

    @Override
    public BasicState digitsCollected(final Call call, final UUID requestId, final String digits,
            final DigitCollectorOperationCause cause, final MediaListener mediaListener)
    {
        return null;
    }

    @Override
    public BasicState recordCompleted(final Call call, final UUID requestId, final RecordOperationCause cause,
            final MediaListener mediaListener)
    {
        return null;
    }

    @Override
    public String getStateName()
    {
        return "Done";
    }
}
