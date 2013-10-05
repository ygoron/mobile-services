/**
 * 
 */
package com.apos.mobile;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.apos.model.BILogonRequest;
import com.apos.model.BILogonToken;
import com.apos.model.SessionInfo;

/**
 * @author Yuri Goron
 * 
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class UnitTesting {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	// private static final String CMS_NAME="WIN-EIGGAIRFOUM";
	private static final String CMS_USER = "admin";
	private static final String CMS_USER_PASSWORD = "apos123";
	private static final String CMS_AUTH_TYPE = "secEnterprise";
	// private static final String URL_LOGON =
	// "http://win-eiggairfoum:6405/biprws/logon/long";
	// private static final String URL_LOGOFF =
	// "http://win-eiggairfoum:6405/biprws/logoff";

	private static final String URL_LOGON = "http://win-bi41rampup:6405/biprws/logon/long";
	private static final String URL_LOGOFF = "http://win-bi41rampup:6405/biprws/logoff";

//	private static final String URL_MOBILE_SERVCIES = "http://localhost:8080/mobile";
	private static final String URL_MOBILE_SERVCIES = "http://win-bi41rampup:8080/AposMobileServices";
	
	private static final int PDF_INSTANCE_ID = 7044;

	private static String SAP_TOKEN;
	private static final Logger Log = Logger.getLogger(UnitTesting.class);

	// @Configuration
	// static class ContextConfiguration {
	//
	// // this bean will be injected into the OrderServiceTest class
	// @Bean
	// public RestTemplate restTemplate() {
	// RestTemplate restTemplate = new RestTemplate();
	// // set properties, etc.
	// return restTemplate;
	// }
	// }

	@Before
	public void initTests() {
		HttpHeaders requestHeaders = getJsonHeaders();
		BILogonRequest biLogonRequest = new BILogonRequest();
		biLogonRequest.setAuthType(CMS_AUTH_TYPE);
		biLogonRequest.setCmsPassword(CMS_USER_PASSWORD);
		biLogonRequest.setCmsUser(CMS_USER);
		HttpEntity<BILogonRequest> httpEntity = new HttpEntity<BILogonRequest>(
				biLogonRequest, requestHeaders);
		ResponseEntity<BILogonToken> resultLogon = restTemplate.exchange(
				URL_LOGON, HttpMethod.POST, httpEntity, BILogonToken.class);
		SAP_TOKEN = resultLogon.getBody().getCmsToken();
		assertNotNull("Not a valid Token", SAP_TOKEN);
		Log.debug("Logon Token:" + SAP_TOKEN);

	}

	@After
	public void destroyTests() {

		HttpHeaders requestHeaders = getJsonHeaders();
		requestHeaders.add(HomeController.SAP_TOKEN, SAP_TOKEN);
		HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);
		ResponseEntity<String> result = restTemplate.exchange(URL_LOGOFF,
				HttpMethod.POST, httpEntity, String.class);
		Log.debug(result.getStatusCode());
		assertNotSame("Logoff Failed", result.getStatusCode().value(), 200);

	}

	@Test
	public void testSessionInfo_1() throws JsonParseException,
			JsonMappingException, IOException {
		assertNotNull(restTemplate);
		HttpHeaders requestHeaders = getJsonHeaders();
		requestHeaders.add(HomeController.SAP_TOKEN, "\"" + SAP_TOKEN + "\"");
		HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);
		ResponseEntity<String> result = restTemplate.exchange(
				URL_MOBILE_SERVCIES + "/session.info", HttpMethod.GET,
				httpEntity, String.class);

		assertNotSame("Sessiong Info Failed", result.getStatusCode().value(),
				200);
		Log.debug("Result:" + result.getBody());

		SessionInfo sessionInfo = objectMapper.readValue(result.getBody(),
				SessionInfo.class);
		assertNotNull("Can't Parse Session info", sessionInfo);
		Log.debug("BI Pltaform Version:" + sessionInfo.getBiPlatformVersion());

	}

	@Test
	public void testGetExtFormatInstance_2() {

		assertNotNull(restTemplate);
		HttpHeaders requestHeaders = getAllHeaders();
		requestHeaders.add(HomeController.SAP_TOKEN, "\"" + SAP_TOKEN + "\"");
		HttpEntity<byte[]> httpEntity = new HttpEntity<byte[]>(requestHeaders);
		
		HttpEntity<byte[]> result = restTemplate.exchange(
				URL_MOBILE_SERVCIES + "/instance.content/" + PDF_INSTANCE_ID,
				HttpMethod.GET, httpEntity, byte[].class);

		if (result.getBody().length <= 0) {
			fail("Failed Received Content");
		}
		Log.debug("Result Length:"+result.getBody().length);

	}

	/**
	 * Gets Xml Type and Content
	 * 
	 * @return
	 */
	private static HttpHeaders getJsonHeaders() {

		HttpHeaders requestHeaders = new HttpHeaders();
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.APPLICATION_JSON);
		requestHeaders.setAccept(mediaTypes);
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		return requestHeaders;

	}
	
	/**
	 * Gets Xml Type and Content
	 * 
	 * @return
	 */
	private static HttpHeaders getAllHeaders() {

		HttpHeaders requestHeaders = new HttpHeaders();
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.ALL);
		requestHeaders.setAccept(mediaTypes);
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		return requestHeaders;

	}


}
