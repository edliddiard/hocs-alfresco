/*
 * Copyright (C) 2009 Alfresco Software Limited.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.homeoffice.cts.storage;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.alfresco.repo.content.AbstractContentStore;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.filestore.FileContentStore;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.util.GUID;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;

import java.io.*;

/**
 * Amazon S3 Content Store Implementation
 * {@link org.alfresco.repo.content.ContentStore}.
 *
 * @author Luis Sala
 */
public class S3ContentStore extends AbstractContentStore {

    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String url;
    private String port;

    private S3Service s3;
    private S3Bucket bucket;
    //configuration for storage
    private String s3serviceMaxThreadCount;
    private String s3serviceAdminMaxThreadCount;
    private String httpClientConnectionTimeoutMs;
    private String httpClientProxyHost;
    private String HttpClientProxyPort;
    private String httpClientProxyAutoDetect;


    private static final Log logger = LogFactory.getLog(S3ContentStore.class);

    /**
     * Initialize an S3 Content Store.
     *
     * @param accessKey
     *            Amazon Web Services Access Key
     * @param secretKey
     *            Amazon Web Services Secret Key
     * @param bucketName
     *            Name of S3 bucket to store content into.
     */
    public S3ContentStore(String accessKey, String secretKey, String bucketName) {

        this.setAccessKey(accessKey);
        this.setSecretKey(secretKey);
        this.setBucketName(bucketName);

        System.out.println("S3ContentStore Initializing: accessKey="+ this.getAccessKey() +" secretKey="+secretKey+" bucketName="+bucketName);
        // Instantiate S3 Service and create necessary bucket.
        try {
            setS3(new RestS3Service(new AWSCredentials (accessKey, secretKey)));
            System.out.println("S3ContentStore Creating Bucket: bucketName="+bucketName);
            setBucket(getS3().getOrCreateBucket(bucketName));
            System.out.println("S3ContentStore Initialization Complete");
        } catch (S3ServiceException se) {
            se.printStackTrace(System.out);
        } // end try-catch

    } // end constructor

    /**
     * Initialize an S3 Content Store.
     *
     * @param accessKey
     *            Amazon Web Services Access Key
     * @param secretKey
     *            Amazon Web Services Secret Key
     * @param bucketName
     *            Name of S3 bucket to store content into.
     */
    public S3ContentStore(String accessKey, String secretKey, String bucketName, String url, String port) {

        this.setAccessKey(accessKey);
        this.setSecretKey(secretKey);
        this.setBucketName(bucketName);
        this.setUrl(url);
        this.setPort(port);

        logger.error(accessKey);
        logger.error(secretKey);
        logger.error(bucketName);
        logger.error(url);
        logger.error(port);


        System.out.println("S3ContentStore Initializing: accessKey="+accessKey+" secretKey="+secretKey+" bucketName="+bucketName);
        // Instantiate S3 Service and create necessary bucket.
        try {
            Jets3tProperties jets3tProperties = getJetS3Properties();

            setS3(new RestS3Service(new AWSCredentials (accessKey, secretKey), null, null, jets3tProperties));
            System.out.println("S3ContentStore Creating Bucket: bucketName="+bucketName);
            setBucket(getS3().getOrCreateBucket(bucketName));
            System.out.println("S3ContentStore Initialization Complete");
        } catch (S3ServiceException se) {
            se.printStackTrace(System.out);
        } // end try-catch

    } // end constructor

    private Jets3tProperties getJetS3Properties() {
        Jets3tProperties jets3tProperties = new Jets3tProperties();
        // Load Jets3tProperties from file
        FileInputStream jets3tPropertiesFile;
        try {
            jets3tPropertiesFile = new FileInputStream("/opt/alfresco/tomcat/shared/classes/jets3t.properties");
            jets3tProperties.loadAndReplaceProperties(jets3tPropertiesFile, "jets3t.properties");
            jets3tPropertiesFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //passed in as constructor argument
        jets3tProperties.setProperty("s3service.s3-endpoint", getUrl());
        //passed in as constructor argument
        jets3tProperties.setProperty("s3service.s3-endpoint-https-port", getPort());
        //always needs to be true otherwise include the bucket name as a domain name
        jets3tProperties.setProperty("s3service.disable-dns-buckets", "true");


        jets3tProperties.setProperty("uploads.storeEmptyDirectories", "true");
        jets3tProperties.setProperty("downloads.restoreLastModifiedDate", "false");
        jets3tProperties.setProperty("s3service.https-only", "true");
        jets3tProperties.setProperty("s3service.max-thread-count", getS3serviceMaxThreadCount());
        jets3tProperties.setProperty("s3service.admin-max-thread-count", getS3serviceAdminMaxThreadCount());
        jets3tProperties.setProperty("s3service.stream-retry-buffer-size", "131072");
        jets3tProperties.setProperty("s3service.internal-error-retry-max", "5");

        jets3tProperties.setProperty("httpclient.connection-timeout-ms", getHttpClientConnectionTimeoutMs());
        jets3tProperties.setProperty("httpclient.socket-timeout-ms", "20000");
        jets3tProperties.setProperty("httpclient.max-connections", "10");
        jets3tProperties.setProperty("httpclient.stale-checking-enabled", "true");
        jets3tProperties.setProperty("httpclient.retry-max", "5");
        jets3tProperties.setProperty("httpclient.proxy-autodetect", getHttpClientProxyAutoDetect());
        jets3tProperties.setProperty("httpclient.proxy-host", getHttpClientProxyHost());
        jets3tProperties.setProperty("httpclient.proxy-port", getHttpClientProxyPort());
        jets3tProperties.setProperty("crypto.algorithm", "PBEWithMD5AndDES");

        return jets3tProperties;
    }



    public ContentReader getReader(String contentUrl) throws ContentIOException {
        try {
            return new S3ContentReader(contentUrl, getS3(), getBucket());
        } catch (Throwable e) {
            throw new ContentIOException("S3ContentStore Failed to get reader for URL: " + contentUrl, e);
        }
    }

    public ContentWriter getWriterInternal(ContentReader existingContentReader, String newContentUrl)
            throws ContentIOException {
        try {
            String contentUrl = null;
            // Was a URL provided?
            if (newContentUrl == null || newContentUrl == "") {
                contentUrl = createNewUrl();
            } else {
                contentUrl = newContentUrl;
            }

            return new S3ContentWriter(contentUrl, existingContentReader, getS3(), getBucket());
        } catch (Throwable e) {
            throw new ContentIOException("S3ContentStore.getWriterInternal(): Failed to get writer.");
        }
    }

    public boolean delete(String contentUrl) throws ContentIOException {

        try {
            logger.debug("S3ContentStore Deleting Object: contentUrl="+contentUrl);
            getS3().deleteObject(getBucket(), contentUrl);
            return true;
        } catch (S3ServiceException e) {
            logger.error("S3ContentStore Delete Operation Failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        } // end try-catch-finally

        return false;
    } // end delete

    // Intended to reset connections, buckets, etc. at some point in the future.
    public void cleanup() {
        // TODO Implement any necessary cleanup.
    } // end cleanup

    public boolean isWriteSupported() {
        // TODO Auto-generated method stub
        return true;
    }
/*
	public Set<String> getUrls(Date createdAfter, Date createdBefore) throws ContentIOException {
		// TODO There is a S3Service.getObject(...) method that may support this.
		return null;
	}
*/

    /**
     * Creates a new content URL.  This must be supported by all
     * stores that are compatible with Alfresco.
     *
     * @return Returns a new and unique content URL
     */
    public static String createNewUrl() {
        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        // create the URL
        StringBuilder sb = new StringBuilder(20);
        sb.append(FileContentStore.STORE_PROTOCOL)
                .append(ContentStore.PROTOCOL_DELIMITER)
                .append(year).append('/')
                .append(month).append('/')
                .append(day).append('/')
                .append(hour).append('/')
                .append(minute).append('/')
                .append(GUID.generate()).append(".bin");
        String newContentUrl = sb.toString();
        // done
        return newContentUrl;

    } // end createNewUrl

    public String getRelativePath(String contentUrl) {
        // take just the part after the protocol
        Pair<String, String> urlParts = super.getContentUrlParts(contentUrl);
        String protocol = urlParts.getFirst();
        String relativePath = urlParts.getSecond();

        return relativePath;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public S3Service getS3() {
        return s3;
    }

    public void setS3(S3Service s3) {
        this.s3 = s3;
    }

    public S3Bucket getBucket() {
        return bucket;
    }

    public void setBucket(S3Bucket bucket) {
        this.bucket = bucket;
    }

    public String getS3serviceMaxThreadCount() {
        return s3serviceMaxThreadCount;
    }

    public void setS3serviceMaxThreadCount(String s3serviceMaxThreadCount) {
        this.s3serviceMaxThreadCount = s3serviceMaxThreadCount;
    }

    public String getS3serviceAdminMaxThreadCount() {
        return s3serviceAdminMaxThreadCount;
    }

    public void setS3serviceAdminMaxThreadCount(String s3serviceAdminMaxThreadCount) {
        this.s3serviceAdminMaxThreadCount = s3serviceAdminMaxThreadCount;
    }

    public String getHttpClientConnectionTimeoutMs() {
        return httpClientConnectionTimeoutMs;
    }

    public void setHttpClientConnectionTimeoutMs(String httpClientConnectionTimeoutMs) {
        this.httpClientConnectionTimeoutMs = httpClientConnectionTimeoutMs;
    }

    public String getHttpClientProxyHost() {
        return httpClientProxyHost;
    }

    public void setHttpClientProxyHost(String httpClientProxyHost) {
        this.httpClientProxyHost = httpClientProxyHost;
    }

    public String getHttpClientProxyAutoDetect() {
        return httpClientProxyAutoDetect;
    }

    public void setHttpClientProxyAutoDetect(String httpClientProxyAutoDetect) {
        this.httpClientProxyAutoDetect = httpClientProxyAutoDetect;
    }

    public String getHttpClientProxyPort() {
        return HttpClientProxyPort;
    }

    public void setHttpClientProxyPort(String httpClientProxyPort) {
        HttpClientProxyPort = httpClientProxyPort;
    }
} // end class S3ContentStore