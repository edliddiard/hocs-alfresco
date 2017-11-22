package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import uk.gov.homeoffice.cts.model.CtsUserModel;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chris on 12/12/2014.
 */
public class AuthenticationInterceptor implements MethodInterceptor {
    private static Log LOGGER = LogFactory.getLog(AuthenticationInterceptor.class);
    private PersonService personService;
    private NodeService nodeService;
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object[] arguments = methodInvocation.getArguments();
        Method method = methodInvocation.getMethod();
        if(method.getName().equals("setAuthentication") && arguments.length > 0){
            //if the user is not the user who's password is being set then add an expiry date
            String username = (String) arguments[0];
            if(!AuthenticationUtil.getFullyAuthenticatedUser().equals(username)){
                LOGGER.debug("Setting expiry date for user " + username + " when resetting their password");
                NodeRef userNodeRef = getPersonService().getPerson(username);
                getNodeService().setProperty(userNodeRef,CtsUserModel.PROPERTY_PASSWORD_EXPIRY_DATE, new DateTime().minusDays(1).toDate());
            }
        }
        return methodInvocation.proceed();
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
