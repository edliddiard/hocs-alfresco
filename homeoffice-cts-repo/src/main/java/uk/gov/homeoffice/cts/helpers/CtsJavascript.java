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

	public void sendEmail(String userName, String subject, String templateName, String urlExtension, NodeRef caseNodeRef, Map<String, String> additionalTemplateData) {
		getEmailService().sendEmail(userName, subject, templateName, urlExtension, caseNodeRef, additionalTemplateData);
	}
	/**
	 * Method to send response email to the person requesting information
	 * @param caseNodeRef
	 * @param subject
	 * @param templateName
	 * @param replyAddress
	 */
	public void sendResponseEmail(NodeRef caseNodeRef, String subject, String templateName, String replyAddress ){
		Map<QName,Serializable> props = getNodeService().getProperties(caseNodeRef);
		
		if(props.get(CtsModel.PROP_HMPO_RESPONSE) != null && ((String)props.get(CtsModel.PROP_HMPO_RESPONSE)).equals(EMAIL)){
			getEmailService().sendExternalResponseEmail(subject, templateName, caseNodeRef, replyAddress, props);
		}else {
			//throw an exception?
		}
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
