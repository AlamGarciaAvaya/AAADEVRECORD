package AAADEVRECORD.make;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import service.AAADEVRECORD.LanguageAttribute;
import AAADEVRECORD.util.Constants;
import AAADEVRECORD.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class VPSPOST {

	private transient final Logger logger = Logger.getLogger(MakingPost.class);

	public String[] vpsPOST(Call call) throws Exception {
		String idioma = null;
		LanguageAttribute languageAttribute = new LanguageAttribute(call);
		if (languageAttribute.getLanguageAttribute().equals("es")) {
			logger.info("Se definio el idioma Español");
			idioma = "es-MX";

		}
		if (languageAttribute.getLanguageAttribute().equals("en")) {
			logger.info("Se definio el idioma Ingles");
			idioma = "en-US";
		}
		if (languageAttribute.getLanguageAttribute().equals("pt")) {
			logger.info("Se definio el idioma Portugues");
			idioma = "pt-BR";

		}

		String[] exitCodes = { null, null };
		final String URI = "http://devavaya.ddns.net:8080/AAADEVURIEL_PRUEBAS_WATSON-war-1.0.0.0.0/TranscriptAAADEVRECORD?apikey="
				+ AttributeStore.INSTANCE
						.getAttributeValue(Constants.GOOGLE_CLOUD_SPEECH_TO_TEXT)
				+ "&idioma=" + idioma + "&audio=recordingAAADEVRECORD.wav";

		final HttpClient clientSpeech = HttpClients.createDefault();

		final HttpPost postMethodSpeech = new HttpPost(URI);

		postMethodSpeech.addHeader("Accept", "application/json; charset=UTF-8");
		postMethodSpeech.addHeader("Content-Type",
				"application/json; charset=UTF-8");

		final String messageBodySpeech = "";
		final StringEntity conversationEntitySpeech = new StringEntity(
				messageBodySpeech);
		postMethodSpeech.setEntity(conversationEntitySpeech);

		final HttpResponse responseSpeech = clientSpeech
				.execute(postMethodSpeech);

		final BufferedReader inputStreamSpeech = new BufferedReader(
				new InputStreamReader(responseSpeech.getEntity().getContent(),
						StandardCharsets.ISO_8859_1));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStreamSpeech.readLine()) != null) {
			result.append(line);
		}

		JSONObject json = new JSONObject(result.toString());

		String transcript = json.getString("results");
		JSONArray array = new JSONArray(transcript);
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			// exitCode = object.get("alternatives").toString();
			String alternatives = object.getString("alternatives");
			JSONArray array2 = new JSONArray(alternatives);
			for (int j = 0; j < array2.length(); j++) {
				JSONObject object2 = array2.getJSONObject(i);
				exitCodes[0] = object2.get("transcript").toString();
				exitCodes[1] = object2.get("confidence").toString();
			}

		}

		inputStreamSpeech.close();
		postMethodSpeech.reset();

		return exitCodes;

	}

}
