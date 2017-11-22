package uk.gov.homeoffice.cts.email;

import org.alfresco.repo.preference.PreferenceServiceImpl;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by chris on 23/04/2015.
 * A patch from Alfresco jira
 * https://issues.alfresco.com/jira/browse/ALF-20897
 */
public class PatchedPreferenceService extends PreferenceServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatchedPreferenceService.class);

    @Override
    public Serializable getPreference(String userName, String preferenceName) {
		/* Patch https://issues.alfresco.com/jira/browse/MNT-10835
		 *  org.alfresco.repo.security.permissions.AccessDeniedException: 03160433
                 * The current user xxx does not have sufficient permissions to get the preferences of the user xxx@xxx
		 *
		 */
        if ("locale".equalsIgnoreCase(preferenceName)){
            try{
                return super.getPreference(userName, preferenceName);
            }catch (AccessDeniedException ex){
                LOGGER.warn("ignoring AccessDeniedException while trying to resolve locale-preference");
                return null;
            }
        }else{
            return super.getPreference(userName, preferenceName);
        }

    }
}
