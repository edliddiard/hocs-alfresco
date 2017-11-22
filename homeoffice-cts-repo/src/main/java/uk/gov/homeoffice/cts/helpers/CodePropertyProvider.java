package uk.gov.homeoffice.cts.helpers;

import java.util.Properties;

/**
 * Class to provide calendars for testing
 * Created by chris on 12/09/2014.
 */
public class CodePropertyProvider implements PropertyProvider {
    @Override
    public Properties getBusinessCalendarDefaultProps() {
        Properties props = new Properties();
        props.put("hour.format","HH:mm");
        props.put("weekday.monday","0:59-23:59");
        props.put("weekday.tuesday","0:59-23:59");
        props.put("weekday.wednesday","0:59-23:59");
        props.put("weekday.thursday","0:59-23:59");
        props.put("weekday.friday","0:59-23:59");
        props.put("weekday.saturday","");
        props.put("weekday.sunday","");

        props.put("day.format","dd/MM/yyyy");
        props.put("holiday.1","25/08/2014");
        props.put("holiday.2","25/12/2014");
        props.put("holiday.3","26/12/2014");
        props.put("holiday.4", "01/01/2015");
        props.put("holiday.5", "03/04/2015");
        props.put("holiday.6", "06/04/2015");
        props.put("holiday.7", "04/05/2015");
        props.put("holiday.8", "25/05/2015");
        props.put("holiday.9", "31/08/2015");
        props.put("holiday.10", "25/12/2015");
        props.put("holiday.11", "28/12/2015");
        props.put("holiday.12", "01/01/2016");
        props.put("holiday.13", "25/03/2016");
        props.put("holiday.14", "28/03/2016");
        props.put("holiday.15", "02/05/2016");
        props.put("holiday.16", "30/05/2016");
        props.put("holiday.17", "29/08/2016");
        props.put("holiday.18", "26/12/2016");
        props.put("holiday.19", "27/12/2016");
        props.put("holiday.20", "02/01/2017");
        props.put("holiday.21", "14/04/2017");
        props.put("holiday.22", "17/04/2017");
        props.put("holiday.23", "01/05/2017");
        props.put("holiday.24", "29/05/2017");
        props.put("holiday.25", "28/08/2017");
        props.put("holiday.26", "25/12/2017");
        props.put("holiday.27", "26/12/2017");

        props.put("business.day.expressed.in.hours","23");
        props.put("business.week.expressed.in.hours","115");
        props.put("business.month.expressed.in.business.days","21");
        props.put("business.year.expressed.in.business.days","220");

        return props;
    }

    @Override
    public Properties getBusinessCalendarRecessProps() {
        Properties props = new Properties();
        props.put("hour.format","HH:mm");
        props.put("weekday.monday","0:59-23:59");
        props.put("weekday.tuesday","0:59-23:59");
        props.put("weekday.wednesday","0:59-23:59");
        props.put("weekday.thursday","0:59-23:59");
        props.put("weekday.friday","0:59-23:59");
        props.put("weekday.saturday","");
        props.put("weekday.sunday","");

        props.put("day.format","dd/MM/yyyy");
        props.put("holiday.1","25/08/2014");
        props.put("holiday.2","25/12/2014");
        props.put("holiday.3","26/12/2014");
        props.put("holiday.4","22/07/2014-29/08/2014");
        props.put("holiday.5","12/09/2014-10/10/2014");
        props.put("holiday.6","11/11/2014-14/11/2014");
        props.put("holiday.7","18/12/2014-02/01/2015");
        props.put("holiday.8","25/05/2015");
        props.put("holiday.9","31/08/2015");
        props.put("holiday.10","25/12/2015");
        props.put("holiday.11","26/12/2015");
        props.put("holiday.12","01/01/2016");
        props.put("holiday.13","25/03/2016");
        props.put("holiday.14","28/03/2016");
        props.put("holiday.15","02/05/2016");
        props.put("holiday.16","30/05/2016");
        props.put("holiday.17","29/08/2016");
        props.put("holiday.18","26/12/2016");
        props.put("holiday.19","26/12/2016");
        props.put("holiday.20","02/01/2017");
        props.put("holiday.21","14/04/2017");
        props.put("holiday.22","17/04/2017");
        props.put("holiday.23","01/05/2017");
        props.put("holiday.24","29/05/2017");
        props.put("holiday.25","28/08/2017");
        props.put("holiday.26","25/12/2017");
        props.put("holiday.27","26/12/2017");

        props.put("business.day.expressed.in.hours","23");
        props.put("business.week.expressed.in.hours","115");
        props.put("business.month.expressed.in.business.days","21");
        props.put("business.year.expressed.in.business.days","220");

        props.put("nonFinishDay.1","16/01/2015");

        return props;
    }

    @Override
    public Properties getBusinessCalendarAllUKProps() {
        Properties props = new Properties();
        props.put("hour.format","HH:mm");
        props.put("weekday.monday","0:59-23:59");
        props.put("weekday.tuesday","0:59-23:59");
        props.put("weekday.wednesday","0:59-23:59");
        props.put("weekday.thursday","0:59-23:59");
        props.put("weekday.friday","0:59-23:59");
        props.put("weekday.saturday","");
        props.put("weekday.sunday","");

        props.put("day.format","dd/MM/yyyy");
        props.put("holiday.1","25/05/2015");
        props.put("holiday.2","13/07/2015");
        props.put("holiday.3","03/08/2015");
        props.put("holiday.4","31/08/2015");
        props.put("holiday.5","30/11/2015");
        props.put("holiday.6","25/12/2015");
        props.put("holiday.7","28/12/2015");
        props.put("holiday.8","01/01/2016");
        props.put("holiday.9","04/01/2016");
        props.put("holiday.10","17/03/2016");
        props.put("holiday.11","25/03/2016");
        props.put("holiday.12","02/05/2016");
        props.put("holiday.13","30/05/2016");
        props.put("holiday.14","12/07/2016");
        props.put("holiday.15","01/08/2016");
        props.put("holiday.16","29/08/2016");
        props.put("holiday.17","30/11/2016");
        props.put("holiday.18","26/12/2016");
        props.put("holiday.19","27/12/2016");
        props.put("holiday.25","02/01/2017");
        props.put("holiday.26","03/01/2017");
        props.put("holiday.27","17/03/2017");
        props.put("holiday.28","14/04/2017");
        props.put("holiday.29","17/04/2017");
        props.put("holiday.30","01/05/2017");
        props.put("holiday.31","29/05/2017");
        props.put("holiday.32","12/07/2017");
        props.put("holiday.33","07/08/2017");
        props.put("holiday.34","28/08/2017");
        props.put("holiday.35","30/11/2017");
        props.put("holiday.36","25/12/2017");
        props.put("holiday.37","26/12/2017");

        props.put("business.day.expressed.in.hours","23");
        props.put("business.week.expressed.in.hours","115");
        props.put("business.month.expressed.in.business.days","21");
        props.put("business.year.expressed.in.business.days","220");

        return props;
    }
}
