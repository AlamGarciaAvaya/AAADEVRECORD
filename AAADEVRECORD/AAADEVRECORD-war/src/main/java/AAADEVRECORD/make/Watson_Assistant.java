package AAADEVRECORD.make;

import AAADEVRECORD.util.Constants;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVRECORD.LanguageAttribute;

/**
 *
 * @author umansilla
 */
@SuppressWarnings("deprecation")
public class Watson_Assistant {

	public Watson_Assistant() {

	}

	private static final Logger logger = Logger
			.getLogger(Watson_Assistant.class);

	/**
	 * @param args
	 *            the command line arguments
	 * @return El método statico main de la clase Watson_Assistant recibe un
	 *         String con el téxto que desea que sea analizado por Watson
	 *         Assistant, el métdo regresa la Intención como String. Las
	 *         credenciales se obtienen con la variable statica myBeanObj_WA, la
	 *         cual se inicializa con un POST al servlet WA con la información
	 *         necesaria en formato json
	 * @throws ServiceNotFoundException
	 * @throws NoAttributeFoundException
	 */
	public String main(String text, Call call) throws NoAttributeFoundException,
			ServiceNotFoundException {
		
		String intent2 = null;
		try{
			/*
			 * HTTP
			 */
			
			String userNameAssistant = AttributeStore.INSTANCE.getAttributeValue(Constants.WA_USER_NAME);
			String passwordAssistant =  AttributeStore.INSTANCE.getAttributeValue(Constants.WA_PASSWORD);
			final SSLProtocolType protocolTypeAssistant = SSLProtocolType.TLSv1_2;
			final SSLContext sslContextAssistant = SSLUtilityFactory
					.createSSLContext(protocolTypeAssistant);
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(userNameAssistant,
							passwordAssistant));

			final String URI = "https://gateway.watsonplatform.net/assistant/api/v1/workspaces/"+
			AttributeStore.INSTANCE.getAttributeValue(Constants.WA_WORK_SPACE_ID)+"/message?version=2018-07-10";
			
			final HttpClient clientAssistant = HttpClients.custom()
					.setSslcontext(sslContextAssistant)
					.setHostnameVerifier(new AllowAllHostnameVerifier())
					.build();
			final HttpPost postMethodAssistant = new HttpPost(URI);
			postMethodAssistant.addHeader("Accept", "application/json");
			postMethodAssistant.addHeader("Content-Type", "application/json");

			final String authStringAssistant = userNameAssistant + ":"
					+ passwordAssistant;
			final String authEncBytesAssistant = DatatypeConverter
					.printBase64Binary(authStringAssistant.getBytes());
			postMethodAssistant.addHeader("Authorization", "Basic "
					+ authEncBytesAssistant);

			final String messageBodyAssistant = "{\"input\": {\"text\": \""
					+ text + "\"}}";
			final StringEntity conversationEntityAssistant = new StringEntity(
					messageBodyAssistant);
			postMethodAssistant.setEntity(conversationEntityAssistant);

			final HttpResponse responseAssistant = clientAssistant
					.execute(postMethodAssistant);

			final BufferedReader inputStreamAssistant = new BufferedReader(
					new InputStreamReader(responseAssistant.getEntity()
							.getContent()));

			String line = "";
			final StringBuilder result = new StringBuilder();
			while ((line = inputStreamAssistant.readLine()) != null) {
				result.append(line);
			}
			
			JSONObject json = new JSONObject(result.toString());
			String intentOut = null;
			String confidence = null;

			String intent = json.getString("intents");
			JSONArray array = new JSONArray(intent);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				intent2 = object.get("intent").toString();
				confidence = object.get("confidence").toString();

			}
			
			intent2 = definirIntent(intent2, call);
		}catch(Exception e){
			logger.error("Error en Watson_Assistant " + e);
			String Error = "Error en Watson_Assistant " + e;
			return Error;
		}
		
		return intent2;
	}

	public String definirIntent(String intent, Call call)
			throws NoAttributeFoundException, ServiceNotFoundException {
		String intencion = null;
		LanguageAttribute languageAttribute = new LanguageAttribute(call);
		if (intent.equals("CANCELACIONES")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				intencion = "CANCELACIONES";

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				intencion = "CANCELLATIONS";

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {
				intencion = "CANCELAMENTOS";

			}
		}
		if (intent.equals("FACTURACION")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				intencion = "FACTURACION";

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				intencion = "BILLING";

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {
				intencion = "FATURAMENTO";

			}

		}
		if (intent.equals("HELP_DESK")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				intencion = "SERVICIO_TECNICO";

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				intencion = "HELP_DESK";

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {
				intencion = "SUPORTE_TECNICO";

			}

		}
		if (intent.equals("PRESENTACION")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				intencion = "SERVICIO_A_CLIENTES";

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				intencion = "CUSTOMER_SERVICE";

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {
				intencion = "ATENDIMENTO_AO_CLIENTE";

			}

		}
		if (intent.equals("SERVICIO_A_CLIENTES")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				intencion = "SERVICIO_A_CLIENTES";

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				intencion = "CUSTOMER_SERVICE";

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				intencion = "ATENDIMENTO_AO_CLIENTE";

			}

		}
		if (intent.equals("Irrelevant")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				intencion = "SERVICIO_A_CLIENTES";

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				intencion = "CUSTOMER_SERVICE";

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				intencion = "ATENDIMENTO_AO_CLIENTE";

			}

		}
		if (intent.equals("VENTAS")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				intencion = "VENTAS";

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				intencion = "SALES";

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {
				intencion = "VENDAS";

			}

		}

		return intencion;
	}

}