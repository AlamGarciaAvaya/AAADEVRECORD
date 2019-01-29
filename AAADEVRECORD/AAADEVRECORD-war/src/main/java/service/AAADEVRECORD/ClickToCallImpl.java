package service.AAADEVRECORD;


import org.apache.commons.lang3.StringUtils;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.CallFactory;
import com.avaya.collaboration.call.Identity;
import com.avaya.collaboration.call.IdentityFactory;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.util.logger.Logger;

public final class ClickToCallImpl implements ClickToCall
{
    private final Logger logger;

    public ClickToCallImpl()
    {
        this(Logger.getLogger(ClickToCallImpl.class));
    }

    ClickToCallImpl(final Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public void makeCall(final String firstParty, final String secondParty, final String callingId, final String display)
    {
        final Identity identity;
        if (StringUtils.isEmpty(display))
        {
             identity = IdentityFactory.create(callingId);
        }
        else 
        {
            identity = IdentityFactory.create(callingId,display);
        }
        final Call call = CallFactory.create(firstParty, secondParty, identity);
        
        
        call.initiate();

        if (logger.isFinestEnabled())
        {
            logger.finest("makeCall:" + call.getCallingParty().getAddress() + " to secondParty=" +
                    call.getCalledParty().getAddress()); 
            
        }
    }
    
    public void makeOneCall(Participant participant, String extension){
  
    	final Call call = CallFactory.create(participant, extension);
    	call.initiate();
//    	call.divertTo(extension);
    }
}
