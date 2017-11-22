package uk.gov.homeoffice.cts.model;


import org.apache.commons.validator.routines.EmailValidator;

public class EmailAccountDetails {

    private String hostName;
    private String userName;
    private String password;
    private String srcFolderName;
    private String destFolderName;
    private String errorFolderName;
    private String caseQueueName;

    public boolean validate(){
        if (isNull(hostName, userName, password, srcFolderName,destFolderName, caseQueueName))
            return false;
        return CorrespondenceType.getByCode(caseQueueName) != null;
       //     return false;
        //return EmailValidator.getInstance().isValid(userName);
    }

    private boolean isNull(Object... args) {
        for (Object arg : args) {
            if (arg==null) return true;
        }
        return false;
    }

    public String getHostName() { return hostName; }

    public void setHostName(String hostName) { this.hostName = hostName; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getSrcFolderName() { return srcFolderName; }

    public void setSrcFolderName(String srcFolderName) { this.srcFolderName = srcFolderName; }

    public String getErrorFolderName() { return errorFolderName; }

    public void setErrorFolderName(String errorFolderName) { this.errorFolderName = errorFolderName; }

    public String getCaseQueueName() { return caseQueueName; }

    public void setCaseQueueName(String caseQueueName) { this.caseQueueName = caseQueueName; }

    public String getDestFolderName() { return destFolderName; }

    public void setDestFolderName(String destFolderName) { this.destFolderName = destFolderName; }

}
