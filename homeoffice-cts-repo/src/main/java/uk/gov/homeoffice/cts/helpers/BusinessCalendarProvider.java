package uk.gov.homeoffice.cts.helpers;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.calendar.CtsBusinessCalendar;
import uk.gov.homeoffice.cts.model.CorrespondenceType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Class to provide a calendar based on the correspondence type
 * Created by chris on 08/08/2014.
 */
public class BusinessCalendarProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessCalendarProvider.class);
    private PropertyProvider propertyProvider;

    private CtsBusinessCalendar pqCalendar;
    private CtsBusinessCalendar defaultCalendar;
    private CtsBusinessCalendar allUKCalendar;


    public void init(){
        CtsBusinessCalendar businessCalendarDefault = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<CtsBusinessCalendar>() {
            @Override
            public CtsBusinessCalendar doWork() throws Exception {
                CtsBusinessCalendar businessCalendarDefault = new CtsBusinessCalendar(getPropertyProvider().getBusinessCalendarDefaultProps());
                return businessCalendarDefault;
            }
        });
        this.setDefaultCalendar(businessCalendarDefault);

        CtsBusinessCalendar businessCalendarRecess = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<CtsBusinessCalendar>() {
            @Override
            public CtsBusinessCalendar doWork() throws Exception {
                CtsBusinessCalendar businessCalendarRecess = new CtsBusinessCalendar(getPropertyProvider().getBusinessCalendarRecessProps());
                return businessCalendarRecess;
            }
        });

        this.setPqCalendar(businessCalendarRecess);

        CtsBusinessCalendar businessCalendarAllUK = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<CtsBusinessCalendar>() {
            @Override
            public CtsBusinessCalendar doWork() throws Exception {
                CtsBusinessCalendar businessCalendarAllUK = new CtsBusinessCalendar(getPropertyProvider().getBusinessCalendarAllUKProps());
                return businessCalendarAllUK;
            }
        });

        this.setAllUKCalendar(businessCalendarAllUK);
    }

    public CtsBusinessCalendar getBusinessCalendar (String correspondenceType){
        if (getDefaultCalendar()==null || getPqCalendar()==null || getAllUKCalendar()==null) {
            init();
        }
        if (correspondenceType == null) {
            return getDefaultCalendar();
        }

        // FOI use all UK bank holidays
        if (correspondenceType.equals(CorrespondenceType.FOI.getCode())) {
        	return getAllUKCalendar(); 
    	}

        return getDefaultCalendar();
    }

    private CtsBusinessCalendar getPqCalendar() {
        return pqCalendar;
    }

    public void setPqCalendar(CtsBusinessCalendar pqCalendar) {
        this.pqCalendar = pqCalendar;
    }

    private CtsBusinessCalendar getDefaultCalendar() {
        return defaultCalendar;
    }

    public void setDefaultCalendar(CtsBusinessCalendar defaultCalendar) {
        this.defaultCalendar = defaultCalendar;
    }

    public CtsBusinessCalendar getAllUKCalendar() { return allUKCalendar; }

    public void setAllUKCalendar(CtsBusinessCalendar allUKCalendar) { this.allUKCalendar = allUKCalendar; }

    public PropertyProvider getPropertyProvider() {
        return propertyProvider;
    }

    public void setPropertyProvider(PropertyProvider propertyProvider) {
        this.propertyProvider = propertyProvider;
    }

    public Properties getDefaultProps() {
        return getPropertyProvider().getBusinessCalendarDefaultProps();
    }
    public Properties getRecessProps() {
        return getPropertyProvider().getBusinessCalendarRecessProps();
    }

    public Properties getAllUKProps() {
        return getPropertyProvider().getBusinessCalendarAllUKProps();
    }

}
