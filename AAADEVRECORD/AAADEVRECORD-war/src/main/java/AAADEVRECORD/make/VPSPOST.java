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
		
		String vpsPostFQDN = AttributeStore.INSTANCE.getAttributeValue(Constants.GOOGLE_POST);
		
		final String URI = "http://"+vpsPostFQDN+"/AAADEVURIEL_PRUEBAS_WATSON-war-1.0.0.0.0/TranscriptAAADEVRECORD?apikey="
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
	
	public String[] vpsSSLPOST(Call call) throws ClientProtocolException, IOException, SSLUtilityException{
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
		
		
		 // TODO code application logic here
        CloseableHttpClient httpClient = null;
        final ModelHttp modelHttp = new ModelHttp();
        modelHttp.setRestUri("https://devavaya.ddns.net:8443/AAADEVURIEL_PRUEBAS_WATSON-war-1.0.0.0.0/TranscriptAAADEVRECORD?apikey=AIzaSyAb8txQ6r49Cj67Bq4sJi9QE_k1CJusVYU" + "&idioma=" + idioma + "&audio=recordingAAADEVRECORD.wav");

        /*
        *   application/json, application/xml, text/xml, text/json, text/plain
         */
        modelHttp.setContentType("application/json");
        modelHttp.setConnectTimeout("60000");
        modelHttp.setSocketTimeout("60000");
        modelHttp.setTlsVersion("TLS 1.2");
        modelHttp.setRequestMethod("POST");
        //"HTTP Basic Authentication"
        modelHttp.setHttpAuth("None");
        /*
            modelHttp.setuserName;
            modelHttp.setPassword;
         */

        int connectTimeoutInt = modelHttp.getDEFAULT_CONNECT_TIMEOUT();
        //sTRINGuTILS (COMMONS lANG3)
        if (StringUtils.isNumeric(modelHttp.getConnectTimeout())) {
            try {
                connectTimeoutInt = Integer.valueOf(modelHttp.getConnectTimeout()).intValue();
            } catch (NumberFormatException tooBig) {
                System.out.println("RestServiceExecution: Connection Timeout has an unsupported value: " + modelHttp.getConnectTimeout() + ". Use the default vaule instead.");
            }
        }

        int socketTimeoutInt = modelHttp.getDEFAULT_SOCKET_TIMEOUT();
        if (StringUtils.isNumeric(modelHttp.getSocketTimeout())) {
            try {
                socketTimeoutInt = Integer.valueOf(modelHttp.getSocketTimeout()).intValue();
            } catch (NumberFormatException tooBig) {
                System.out.println("RestServiceExecution: Socket Timeout has an unsupported value: " + modelHttp.getSocketTimeout() + ". Use the default vaule instead.");
            }
        }

        boolean hasPayload = (modelHttp.getPayload() != null) && (!modelHttp.getPayload().isEmpty());

        //NO INPUT DATA
//        Map<String, String> inputVars = fillInputVarsMap();
        Map<String, String> headerValues = new HashMap();
        //CUSTOMIZED HEADERS

        HttpClientBuilder clientBuilder = getHttpClientBuilder(modelHttp.getRestUri(), modelHttp.getTlsVersion(), connectTimeoutInt, socketTimeoutInt);

        if (("HTTP Basic Authentication".equalsIgnoreCase(modelHttp.getHttpAuth()))
                && (modelHttp.getUsername() != null) && (!modelHttp.getUsername().isEmpty())) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(null, -1), new UsernamePasswordCredentials(modelHttp
                    .getUsername(), modelHttp.getPassword()));
            httpClient = clientBuilder.setDefaultCredentialsProvider(credsProvider).build();
        } else {
            httpClient = clientBuilder.build();
        }

        HttpUriRequest httpRequest = createRequest(modelHttp.getRequestMethod(), modelHttp.getContentType(), modelHttp.getRestUri(), modelHttp.getPayload(), headerValues);

        final HttpResponse response = httpClient.execute(httpRequest);

        final BufferedReader inputStream = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

        String line = "";
        final StringBuilder result = new StringBuilder();
        while ((line = inputStream.readLine()) != null) {
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

		return exitCodes;
	}
	
	
	public static HttpUriRequest createRequest(String method, String contentType, String urlStr, String payloadStr, Map<String, String> headerValues) {
        HttpUriRequest httpRequest = null;

        URI uri = generateUri(urlStr);

        if ("POST".equalsIgnoreCase(method)) {
            try {
                httpRequest = new HttpPost(uri);
            } catch (Exception e) {
                System.out.println("Failed to create HTTP POST request, Message = "
                        + ExceptionUtils.getRootCauseMessage(e));

                throw new IllegalStateException("Error encountered during execution of REST Service task: " + ExceptionUtils.getRootCauseMessage(e), e);
            }
            if ((contentType != null) && (!contentType.isEmpty())) {
                httpRequest.addHeader("Content-Type", contentType);
            }
            if ((payloadStr != null) && (!payloadStr.isEmpty())) {
                try {
                    ((HttpPost) httpRequest).setEntity(new StringEntity(payloadStr));
                } catch (UnsupportedEncodingException e) {
                    System.out.println("Faile to set the payload for HTTP POST. " + e.toString());

                    throw new IllegalStateException("Error encountered during execution of REST Service task: " + ExceptionUtils.getRootCauseMessage(e), e);
                }
            }
        } else {
            System.out.println("Unsupported REST HTTP Method, method = " + method);
            return null;
        }
        httpRequest.addHeader("Accept", "*/*");
        for (Map.Entry<String, String> entry : headerValues.entrySet()) {
            httpRequest.addHeader((String) entry.getKey(), (String) entry.getValue());
        }
        return httpRequest;
    }

    public static HttpClientBuilder getHttpClientBuilder(String urlStr, String tlsVersion, int connectTimeout, int socketTimeout) throws SSLUtilityException, MalformedURLException {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();

        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).useSystemProperties();

        URL url = new URL(urlStr);
        if ("https".equalsIgnoreCase(url.getProtocol())) {

            final SSLProtocolType protocol = SSLProtocolType.TLSv1_2;
            final SSLContext sslContext = SSLUtilityFactory.createSSLContext(protocol);

            SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            builder.setSSLSocketFactory(sslConnectionFactory);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = null;

            try {
                SSLConnectionSocketFactory trustSelfSignedSocketFactory = new SSLConnectionSocketFactory(
                        new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
                        SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                socketFactoryRegistry = RegistryBuilder
                        .<ConnectionSocketFactory>create()
                        .register("http", new PlainConnectionSocketFactory())
                        .register("https", trustSelfSignedSocketFactory)
                        .build();
            } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
                System.out.println("Error: " + e.toString());
            }
            HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(socketFactoryRegistry);

            builder.setConnectionManager(ccm);
        }
        return builder;
    }

    public static URI generateUri(String urlStr) {
        URL url = null;
        URI uri = null;
        try {
            url = new URL(urlStr);

            uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), null, null);
            String encodedUrl = uri.getScheme() + ":" + uri.getRawSchemeSpecificPart();

            String encodedQueryString = "";
            if (StringUtils.isNotBlank(url.getQuery())) {
                encodedQueryString = encodeQuery(url.getQuery());
            }
            if (StringUtils.isNotBlank(encodedQueryString)) {
                encodedUrl = encodedUrl + "?" + encodedQueryString;
                uri = new URI(encodedUrl);
            }
        } catch (Exception e) {
            System.out.println("Failed to encode REST service URL, Message = "
                    + ExceptionUtils.getRootCauseMessage(e));

            throw new IllegalStateException("Error encountered during execution of REST Service task: " + ExceptionUtils.getRootCauseMessage(e), e);
        }

        return uri;
    }

    public static String encodeQuery(String query)
            throws UnsupportedEncodingException {
        String encodedQueryString = "";
        String[] pairs = query.split("&");

        String key = "";
        String value = "";
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            key = idx > 0 ? URLEncoder.encode(pair.substring(0, idx), "UTF-8") : pair;
            value = (idx > 0) && (pair.length() > idx + 1) ? URLEncoder.encode(pair.substring(idx + 1), "UTF-8") : null;
            if (encodedQueryString.isEmpty()) {
                encodedQueryString = key + "=" + value;
            } else {
                encodedQueryString = encodedQueryString + "&" + key + "=" + value;
            }
        }
        return encodedQueryString;
    }

}
