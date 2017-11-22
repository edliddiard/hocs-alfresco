package uk.gov.homeoffice.cts.behaviour;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for logic that needs to be shared across behaviours
 * Created by chris on 03/09/2014.
 */
public class BehaviourHelper {
    /**
     * Check whether two values are different, null and empty string should be considered as the same
     * @param beforeValue
     * @param afterValue
     * @return
     */
    static protected boolean hasChanged(String beforeValue, String afterValue) {
        if(beforeValue == null && afterValue == null){
            return false;
        }
        if((beforeValue == null || beforeValue.equals("")) && (afterValue == null || afterValue.equals(""))){
            return false;
        }
        if((afterValue == null || afterValue.equals("")) && (beforeValue == null || beforeValue.equals(""))){
            return false;
        }
        if(beforeValue == null && afterValue != null){
            return true;
        }
        if(afterValue == null && beforeValue != null){
            return true;
        }
        if(beforeValue != null && !beforeValue.equals(afterValue)){
            return true;
        }
        return false;
    }

    public static boolean hasChangedBoolean(Serializable beforeBool, Serializable afterBool) {
        if(beforeBool==null && afterBool ==null){
            return false;
        }
        if(beforeBool==null && afterBool !=null){
            return true;
        }
        if(beforeBool!=null && afterBool==null){
            return true;
        }
        boolean before = (boolean) beforeBool;
        boolean after = (boolean) afterBool;
        return before != after;

    }

    public static boolean hasChangedSerializable(Serializable beforeSer, Serializable afterSer) {
        if(beforeSer==null && afterSer ==null){
            return false;
        }
        if(beforeSer==null && afterSer !=null){
            return true;
        }
        if(beforeSer!=null && afterSer==null){
            return true;
        }
        return hasChanged(beforeSer.toString(),afterSer.toString());
    }

    public static boolean hasChangedDate(Date beforeDate, Date afterDate) {
        if (beforeDate == null && afterDate == null) {
            return false;
        }
        if(beforeDate==null && afterDate !=null){
            return true;
        }
        if(beforeDate!=null && afterDate==null){
            return true;
        }
        return hasChanged(beforeDate.toString(),afterDate.toString());
    }
}
