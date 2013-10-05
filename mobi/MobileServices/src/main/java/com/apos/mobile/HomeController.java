package com.apos.mobile;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apos.model.SessionInfo;
import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;
import com.crystaldecisions.sdk.occa.infostore.IRemoteFile;
import com.crystaldecisions.sdk.plugin.desktop.common.IReportFormatOptions;
import com.crystaldecisions.sdk.plugin.desktop.common.IReportProcessingInfo;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger Log = LoggerFactory
			.getLogger(HomeController.class);

	private static final String STR_VERSION = "1.0.04.01";
	public static final String SAP_TOKEN = "X-SAP-LogonToken";
	private static final String CMS_QUERY_FILES = "SELECT SI_FILES,SI_MIME_TYPE, SI_PROCESSINFO.SI_FORMAT_INFO,SI_PROGID, SI_PROGID_MACHINE,SI_FILES FROM CI_INFOOBJECTS WHERE SI_ID=";
	// private static final String CMS_QUERY_FILES =
	// "SELECT * FROM CI_INFOOBJECTS WHERE SI_ID=";
	public static final String CMS_PROG_ID_PDF = "CrystalEnterprise.Pdf";
	public static final String CMS_PROG_ID_EXCEL = "CrystalEnterprise.Excel";
	public static final String CMS_PROG_ID_CSV = "CrystalEnterprise.Txt";
	public static final String CMS_PROG_ID_WORD = "CrystalEnterprise.Word";
	public static final String CMS_PROG_ID_RTF = "CrystalEnterprise.Rtf";
	public static final String CMS_PROG_ID_CRYSTALREPORT = "CrystalEnterprise.Report";
	public static final String CMS_PROG_ID_WEBI = "CrystalEnterprise.Webi";
	public static final String CMS_PROG_ID_FULLCLIENT = "CrystalEnterprise.FullClient";

	public static final String CMS_PROG_ID_AGNOSTIC = "CrystalEnterprise.Agnostic";

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		Log.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

	@RequestMapping(value = "/session.info", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<SessionInfo> getSessioninfo(
			@RequestHeader(SAP_TOKEN) String token) {

		IEnterpriseSession enterpriseSession = null;
		// token = token.split("\"")[1];

		Log.debug("Token:" + token);

		String temp[] = token.split("\"");
		if (temp.length > 1)
			token = temp[1];
		else
			token = temp[0];

		Log.debug("Actual Token:" + token);
		SessionInfo sessionInfo = new SessionInfo();
		try {
			enterpriseSession = CrystalEnterprise.getSessionMgr()
					.logonWithToken(token);

			sessionInfo = new SessionInfo();
			sessionInfo.setMobileServiceVersion(STR_VERSION);
			sessionInfo.setBiPlatformVersion(enterpriseSession
					.getEnterpriseVersion());
			sessionInfo.setHttpCode(HttpStatus.CREATED.value());
			return new ResponseEntity<SessionInfo>(sessionInfo,
					HttpStatus.CREATED);
		} catch (Exception ee) {
			Log.error(ee.getLocalizedMessage(), ee);
			sessionInfo.setMessage(ee.getLocalizedMessage());
			sessionInfo.setHttpCode(HttpStatus.FORBIDDEN.value());
			return new ResponseEntity<SessionInfo>(sessionInfo,
					HttpStatus.FORBIDDEN);

		} finally {
			if (enterpriseSession != null)
				enterpriseSession.logoff();
		}

	}

	@RequestMapping(value = "/instance.content/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> getInstanceContent(
			@RequestHeader(SAP_TOKEN) String token,
			@PathVariable("id") String id, HttpServletResponse response) {

		IEnterpriseSession enterpriseSession = null;
		Log.debug("Token:" + token);
		try {

			String temp[] = token.split("\"");
			if (temp.length > 1)
				token = temp[1];
			else
				token = temp[0];

			Log.debug("Actual Token:" + token);
			enterpriseSession = CrystalEnterprise.getSessionMgr()
					.logonWithToken(token);

			IInfoStore infoStore = (IInfoStore) enterpriseSession
					.getService("InfoStore");
			String cmsQuery = CMS_QUERY_FILES + id;
			Log.debug("CMS Query:" + cmsQuery);

			IInfoObject infoObject = (IInfoObject) infoStore.query(cmsQuery)
					.get(0);

			HttpHeaders httpHeaders = new HttpHeaders();
			Log.debug("Info Object ProgId:" + infoObject.getProgID());
			if (infoObject.getProgID().equalsIgnoreCase(CMS_PROG_ID_PDF)) {
				httpHeaders
						.setContentType(MediaType.valueOf("application/pdf"));
				Log.debug("Format Pdf");

			} else if (infoObject.getProgID().equalsIgnoreCase(
					CMS_PROG_ID_EXCEL)) {
				// httpHeaders.setContentType(MediaType
				// .valueOf("application/vnd.ms-excel"));

				if (infoObject.properties().getProperty("SI_PROGID_MACHINE")
						.toString().equalsIgnoreCase(CMS_PROG_ID_CRYSTALREPORT)) {
					IReportProcessingInfo reportProcInfo = (IReportProcessingInfo) infoObject;

					Log.debug("Excel Format:"
							+ reportProcInfo.getReportFormatOptions()
									.getFormat());
					switch (reportProcInfo.getReportFormatOptions().getFormat()) {
					case IReportFormatOptions.CeReportFormat.EXCEL:
					case IReportFormatOptions.CeReportFormat.EXCEL_DATA_ONLY:
						httpHeaders.setContentType(MediaType
								.valueOf("application/vnd.ms-excel"));
						break;

					default:
						Log.debug("Default");
						httpHeaders
								.setContentType(MediaType
										.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
					}

				} else if (infoObject.properties()
						.getProperty("SI_PROGID_MACHINE").toString()
						.equalsIgnoreCase(CMS_PROG_ID_WEBI)) {

					String mimeType = infoObject.properties()
							.getProperty("SI_MIME_TYPE").toString();
					Log.debug("Mime type:" + mimeType);
					if (mimeType != null) {
						httpHeaders.setContentType(MediaType.valueOf(mimeType));
					}

				} else if (infoObject.properties()
						.getProperty("SI_PROGID_MACHINE").toString()
						.equalsIgnoreCase(CMS_PROG_ID_FULLCLIENT)) {

					Log.debug("Full Client");
					httpHeaders.setContentType(MediaType
							.valueOf("application/vnd.ms-excel"));

				}

				Log.debug("Format Excel");

			} else if (infoObject.getProgID().equalsIgnoreCase(CMS_PROG_ID_CSV)) {
				httpHeaders.setContentType(MediaType.valueOf("text/plain"));
				Log.debug("Format Plain");

			} else if (infoObject.getProgID().equalsIgnoreCase(
					CMS_PROG_ID_AGNOSTIC)) {

				String mimeType = infoObject.properties()
						.getProperty("SI_MIME_TYPE").toString();
				Log.debug("Mime type:" + mimeType);
				if (mimeType != null) {
					httpHeaders.setContentType(MediaType.valueOf(mimeType));
				}
				Log.debug("Format Agnostic");

			}

			else if (infoObject.getProgID().equalsIgnoreCase(CMS_PROG_ID_WORD)
					|| infoObject.getProgID().equalsIgnoreCase(CMS_PROG_ID_RTF)) {
				// httpHeaders
				// .setContentType(MediaType
				// .valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
				httpHeaders
						.setContentType(MediaType.valueOf("application/rtf"));
				Log.debug("Format Word");
			}

			IRemoteFile file = (IRemoteFile) infoObject.getFiles().get(0);
			Log.debug("File Size:" + file.getSize());

			// IContent content = (IContent) infoObject;
			// byte[] contentBytes = content.getContent();
			// Log.debug("Content Size:" + contentBytes.length);
			//
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
					IOUtils.toByteArray(file.getInputStream()), httpHeaders,
					HttpStatus.CREATED);

			return responseEntity;
		} catch (Exception ee) {
			Log.error(ee.getLocalizedMessage(), ee);
			return new ResponseEntity<byte[]>(new byte[0], HttpStatus.FORBIDDEN);

		} finally {
			if (enterpriseSession != null)
				enterpriseSession.logoff();
		}

	}
}
