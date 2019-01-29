package AAADEVRECORD.make;



import java.util.UUID;

import AAADEVRECORD.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.DigitOptions;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaListener;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.call.media.RecordItem;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class MediaOperations {

    private final MediaListener mediaListener;
    private final MediaService mediaService;
    
    private static final Logger logger = Logger.getLogger(MediaOperations.class);
    private final int MAX_RECORD_TIME = 60000; // 60 seconds
    
    /*
     * Constructor
     */
    public MediaOperations(final MediaListener mediaListener)
    {
        this.mediaListener = mediaListener;
        this.mediaService = MediaFactory.createMediaService();
    }
    
    /*
     * playTtsPrompt recibe como argumentos, el texto que será reproducido y el participante al cual será reproducido.
     */
    public void playTtsPrompt(final String Beep, final Participant participant)
    {	logger.info("MediaOperations, playTtsPrompt()");
        try
        {
            final PlayItem playItem = MediaFactory.createPlayItem();
            playItem.setSource(Beep)	//RecordingConstants
                    .setInterruptible(true) //Interrumpir el anuncio
                    .setIterateCount(1);
  
            final UUID requestId = mediaService.play(participant, playItem, mediaListener);
            logger.info("playTtsPrompt prompt = \"" + Beep + "\", requestId = " + requestId);
        }
        catch (final Exception exception)
        {
            logger.error("playTtsPrompt caught exception: ", exception);
            throw new IllegalStateException(exception);
        }
    }
    
    public void promptAndCollect(final String textToSpeak, final int numberOfDigits, final Participant participant)
    {	logger.info("MediaOperations, promptAndCollect()");
        try
        {	
        	/*
        	 * Pepara la petición para reproducir el audio.
        	 */
            final PlayItem playItem = MediaFactory.createPlayItem();
            playItem.setSource(textToSpeak)
                    .setInterruptible(true)
                    .setIterateCount(1);

            final DigitOptions digitOptions = MediaFactory.createDigitOptions();
            

            digitOptions.setNumberOfDigits(numberOfDigits)
                    .setTerminationKey("#")
                    .setFlushBuffer(true);
            
  
            final UUID requestId = mediaService.promptAndCollect(participant, playItem, digitOptions, mediaListener);
            logger.info("promptAndCollect prompt = \"" + textToSpeak + "\", requestId = " + requestId);
        }
        catch (final Exception exception)
        {
            logger.error("playTtsPrompt caught exception: ", exception);
            throw new IllegalStateException(exception);
        }
    }
    
    public void record(final Participant participant)
    {	logger.info("MediaOperations, record()");
    	/*
    	 * Recupera la ruta del archivo guardado
    	 */
        final String storageUrl = formRecordingStoreUrl();
        /*
         * Prepara los parámetros para realizar la grabación.
         * public interface RecordItem
         * Provides methods to access or modify the properties related to a record operation.
         */
        final RecordItem recordItem = MediaFactory.createRecordItem();

        recordItem.setMaxDuration(MAX_RECORD_TIME)
                .setTerminationKey("#")
                .setFileUri(storageUrl);
        
        /*
         * The result of the media request is passed to the callback methods on this class. 
         * An instance of the MediaListener is passed into the MediaService media request methods, such as play, 
         * collect digits, send digits and record.
         */
        mediaService.record(participant, recordItem, mediaListener);
        
        logger.info("record storage URL = " + storageUrl);
    }
    
    public String formRecordingPlayUrl()
    {	logger.info("MediaOperations, formRecordingPlayUrl()");
    /*
     * Define la ruta para reproducir el archivo que fue grabado
     */
        final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
        final String trafficInterfaceAddress = addressRetriever.getTrafficInterfaceAddress();
        final String myServiceName = ServiceUtil.getServiceDescriptor().getName();
        final StringBuilder sb = new StringBuilder();
        sb.append("http://")
                .append(trafficInterfaceAddress)
                .append("/services/")
                .append(myServiceName)
                .append("/recording")
                .append(myServiceName)
                .append(".wav");
        return sb.toString();
    }

    private String formRecordingStoreUrl()
    {	logger.info("MediaOperations, formRecordingStoreUrl()");
    /*
     * Define la ruta del archivo grabado para ser almacenado (incluye StoreRecordingServlet)
     * Pribar si hace un POST al Servlet StoreRecordingServlet
     */
        final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
        final String trafficInterfaceAddress = addressRetriever.getTrafficInterfaceAddress();
        final String myServiceName = ServiceUtil.getServiceDescriptor().getName();
        final StringBuilder sb = new StringBuilder();
        sb.append("http://")
                .append(trafficInterfaceAddress)
                .append("/services/")
                .append(myServiceName)
                .append("/StoreRecordingServlet/")
                .append("recording")
                .append(myServiceName)
                .append(".wav");
        return sb.toString();
    }
}
