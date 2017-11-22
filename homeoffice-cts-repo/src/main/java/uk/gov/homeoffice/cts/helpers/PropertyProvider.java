package uk.gov.homeoffice.cts.helpers;

import java.util.Properties;

/**
 * Created by chris on 12/09/2014.
 */
public interface PropertyProvider {
    public Properties getBusinessCalendarDefaultProps();
    public Properties getBusinessCalendarRecessProps();
    public Properties getBusinessCalendarAllUKProps();
}
