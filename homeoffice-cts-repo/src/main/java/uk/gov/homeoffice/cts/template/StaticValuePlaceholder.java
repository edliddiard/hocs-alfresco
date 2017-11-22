package uk.gov.homeoffice.cts.template;

/**
 * Created by davidt on 11/11/2014.
 */
public class StaticValuePlaceholder extends Placeholder {

    private String value;

    public StaticValuePlaceholder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
