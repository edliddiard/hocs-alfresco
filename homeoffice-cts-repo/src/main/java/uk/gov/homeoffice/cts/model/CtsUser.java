package uk.gov.homeoffice.cts.model;

import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.QName;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by davidt on 20/01/2015.
 */
public class CtsUser {

    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Date passwordExpiryDate;

    public CtsUser(Map<QName, Serializable> userProps) {
        this.userName = (String) userProps.get(ContentModel.PROP_USERNAME);
        this.firstName = (String) userProps.get(ContentModel.PROP_FIRSTNAME);
        this.lastName = (String) userProps.get(ContentModel.PROP_LASTNAME);
        this.email = (String) userProps.get(ContentModel.PROP_EMAIL);
        this.passwordExpiryDate = (Date) userProps.get(CtsUserModel.PROPERTY_PASSWORD_EXPIRY_DATE);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getPasswordExpiryDate() {
        return passwordExpiryDate;
    }

    public void setPasswordExpiryDate(Date passwordExpiryDate) {
        this.passwordExpiryDate = passwordExpiryDate;
    }
}
