package uk.gov.homeoffice.cts.helpers;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import uk.gov.homeoffice.cts.email.EmailService;
import uk.gov.homeoffice.cts.model.CtsModel;

public class CtsJavascript extends BaseProcessorExtension {
	private static final String EMAIL = "Email";
	EmailService emailService;
	NodeService nodeService;
	String dcuEmail;

	public void sendEmail(String templateId, String emailAddress, Map<String, String> personalisation) {
		getEmailService().sendEmail(templateId, emailAddress, personalisation);
	}
	
	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	/**
	 * The email address to use for DCU as the sender on their responses
	 * @return
	 */
	public String getDcuEmail() {
		return dcuEmail;
	}
	public void setDcuEmail(String dcuEmail) {
		this.dcuEmail = dcuEmail;
	} 
	
	
	
	
}
