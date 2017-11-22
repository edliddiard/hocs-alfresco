package uk.gov.homeoffice.cts.calendar;

import org.jbpm.calendar.BusinessCalendar;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Extends the BusinessCalendar and adds nonFinishDays which
 * are used by PQs for non sitting days in parliament, where the
 * day should count for the deadline but if it is the final
 * day the next business day should be selected
 * Created by chris on 20/08/2014.
 */
public class CtsBusinessCalendar extends BusinessCalendar {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CtsBusinessCalendar.class);
    List<Date> nonFinishDays;
    public CtsBusinessCalendar(Properties calendarProperties){
        super(calendarProperties);

        nonFinishDays = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat(calendarProperties.getProperty("day.format"));

        Iterator iter = calendarProperties.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (key.startsWith("nonFinishDay")) {
                try {
                    Date date = dateFormat.parse(calendarProperties.getProperty(key));
                    nonFinishDays.add(date);
                } catch (ParseException e) {
                    LOGGER.debug("Could not parse nonFinishDay "+calendarProperties.getProperty(key));
                }

            }
        }
    }

    public boolean isNonFinishDay(Date deadlineDate) {
        for (int i = 0; i < nonFinishDays.size(); i++) {
            Date nonFinishDay = nonFinishDays.get(i);
            if((deadlineDate.getYear() == nonFinishDay.getYear())
                    && (deadlineDate.getMonth() == nonFinishDay.getMonth())
                    && (deadlineDate.getDate() == nonFinishDay.getDate())){
                return true;
            }
        }
        return false;
    }
}
