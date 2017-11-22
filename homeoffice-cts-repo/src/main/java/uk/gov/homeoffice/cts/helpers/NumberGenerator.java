package uk.gov.homeoffice.cts.helpers;

/**
 * Created by chris on 25/07/2014.
 */
public interface NumberGenerator {
    /**
     * Generates next number in a sequence
     * @param year
     * @return
     */
    public String nextNumber(String year);
    public String nextNumber(String year,String nodeRef);
}
