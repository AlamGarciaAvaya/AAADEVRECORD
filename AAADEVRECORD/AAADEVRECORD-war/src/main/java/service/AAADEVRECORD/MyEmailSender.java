package service.AAADEVRECORD;

import AAADEVRECORD.util.Constants;

import com.avaya.collaboration.bus.CollaborationBusException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.email.EmailFactory;
import com.avaya.collaboration.email.EmailRequest;
import com.avaya.collaboration.util.logger.Logger;


public final class MyEmailSender
{

    private final Logger logger = Logger.getLogger(getClass());

    public void sendEmail(final String emailTo, final String emailSubject, final String emailBody)
    {
        final EmailRequest emailRequest = EmailFactory.createEmailRequest();
        emailRequest.addTo(emailTo);
        emailRequest.setFrom("lab132@collaboratory.avaya.com");
        emailRequest.setSubject(emailSubject);
        emailRequest.setTextBody(emailBody);
        emailRequest.setListener(new MyEmailListener(emailRequest));
        
        try
        {
        	logger.info("Antes de enviar el email: "+ emailTo + emailSubject + emailBody + emailRequest);
            emailRequest.send();
        }
        catch (final CollaborationBusException e)
        {
            logger.error("Could not send email request", e);
        }
    }

    public void sendCallAlertEmail(final Call call)
    {
        this.sendEmail(Constants.EMAIL, "Call Alert", call.getCallingParty()
		        .getAddress() +
		        " calling " + call.getCalledParty().getAddress() + " at " +
		        System.currentTimeMillis());
    }

}