/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.wildfly.access;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.guvnor.ala.wildfly.access.exceptions.WildflyClientException;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.create;
import static org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE;
import static org.apache.http.entity.mime.MultipartEntityBuilder.create;
import static org.apache.http.impl.client.HttpClients.custom;
import static org.guvnor.ala.runtime.RuntimeState.RUNNING;
import static org.guvnor.ala.runtime.RuntimeState.STOPPED;
import static org.guvnor.ala.runtime.RuntimeState.UNKNOWN;

/**
 * Wildfly 10 Remote client
 */
@JsonIgnoreType
public class WildflyClient {

    protected static final Logger LOG = LoggerFactory.getLogger(WildflyClient.class);
    private final String providerName;
    private final String user;
    private final String password;
    private final String host;
    private final int port;
    private final int managementPort;

    public WildflyClient(String providerName,
                         String user,
                         String password,
                         String host,
                         int port,
                         int managementPort) {
        this.providerName = providerName;
        this.user = user;
        this.password = password;
        this.host = host;
        this.managementPort = managementPort;
        this.port = port;
    }

    /*
     * Deploys a new WAR file to the Wildfly Instance
     * @param File to be deployed, it must be a deployable file
     * @return the 200 on successful deployment
     * @throw a WildflyClientException with the status code on failure
     * @throw a WildflyClientException with the throwable in case of an internal exception
     */
    public int deploy(File file) throws WildflyClientException {

        // the digest auth backend
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host,
                              managementPort),
                new UsernamePasswordCredentials(user,
                                                password));

        CloseableHttpClient httpclient = custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        HttpPost post = new HttpPost("http://" + host + ":" + managementPort + "/management-upload");

        post.addHeader("X-Management-Client-Name",
                       "HAL");

        // the file to be uploaded
        FileBody fileBody = new FileBody(file);

        // the DMR operation
        ModelNode operation = new ModelNode();
        operation.get("address").add("deployment",
                                     file.getName());
        operation.get("operation").set("add");
        operation.get("runtime-name").set(file.getName());
        operation.get("enabled").set(true);
        operation.get("content").add().get("input-stream-index").set(0);  // point to the multipart index used

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            operation.writeBase64(bout);
        } catch (IOException ex) {
            getLogger(WildflyClient.class.getName()).log(SEVERE,
                                                         null,
                                                         ex);
        }

        // the multipart
        MultipartEntityBuilder builder = create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("uploadFormElement",
                        fileBody);
        builder.addPart("operation",
                        new ByteArrayBody(bout.toByteArray(),
                                          create("application/dmr-encoded"),
                                          "blob"));
        HttpEntity entity = builder.build();

        post.setEntity(entity);

        try {
            HttpResponse response = httpclient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new WildflyClientException("Error Deploying App Status Code: " + statusCode);
            }
            return statusCode;
        } catch (IOException ex) {
            LOG.error("Error Deploying App : " + ex.getMessage(),
                      ex);
            throw new WildflyClientException("Error Deploying App : " + ex.getMessage(),
                                             ex);
        }
    }

    /*
     * Undeploys a new WAR file to the Wildfly Instance
     * @param String deploymentName
     * @return the 200 on successful undeployment
     * @throw a WildflyClientException with the status code on failure
     * @throw a WildflyClientException with the throwable in case of an internal exception
     */
    public int undeploy(String deploymentName) throws WildflyClientException {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host,
                              managementPort),
                new UsernamePasswordCredentials(user,
                                                password));

        CloseableHttpClient httpclient = custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        final HttpPost post = new HttpPost("http://" + host + ":" + managementPort + "/management");

        post.addHeader("X-Management-Client-Name",
                       "GUVNOR-ALA");

        // the DMR operation
        ModelNode operation = new ModelNode();
        operation.get("operation").set("remove");
        operation.get("address").add("deployment",
                                     deploymentName);

        post.setEntity(new StringEntity(operation.toJSONString(true),
                                        APPLICATION_JSON));

        try {
            HttpResponse response = httpclient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new WildflyClientException("Error Undeploying App Status Code: " + statusCode);
            }
            return statusCode;
        } catch (IOException ex) {
            LOG.error("Error Undeploying App : " + ex.getMessage(),
                      ex);
            throw new WildflyClientException("Error Undeploying App : " + ex.getMessage(),
                                             ex);
        }
    }

    public void close() {

    }

    /*
     * Start the application specified by the deploymentName
     * @param String deploymentName
     * @return the 200 on successful start
     * @throw a WildflyClientException with the status code on failure
     * @throw a WildflyClientException with the throwable in case of an internal exception
     */
    public int start(String deploymentName) throws WildflyClientException {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host,
                              managementPort),
                new UsernamePasswordCredentials(user,
                                                password));

        CloseableHttpClient httpclient = custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        final HttpPost post = new HttpPost("http://" + host + ":" + managementPort + "/management");

        post.addHeader("X-Management-Client-Name",
                       "GUVNOR-ALA");

        // the DMR operation
        ModelNode operation = new ModelNode();
        operation.get("operation").set("deploy");
        operation.get("address").add("deployment",
                                     deploymentName);

        post.setEntity(new StringEntity(operation.toJSONString(true),
                                        APPLICATION_JSON));

        try {
            HttpResponse response = httpclient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new WildflyClientException("Error Starting App Status Code: " + statusCode);
            }
            return statusCode;
        } catch (IOException ex) {
            getLogger(WildflyClient.class.getName()).log(SEVERE,
                                                         null,
                                                         ex);
            throw new WildflyClientException("Error Starting App : " + ex.getMessage(),
                                             ex);
        }
    }

    /*
     * Stop the application specified by the deploymentName
     * @param String deploymentName
     * @return the 200 on successful stop
     * @throw a WildflyClientException with the status code on failure
     * @throw a WildflyClientException with the throwable in case of an internal exception
     */
    public int stop(String deploymentName) throws WildflyClientException {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host,
                              managementPort),
                new UsernamePasswordCredentials(user,
                                                password));

        CloseableHttpClient httpclient = custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        final HttpPost post = new HttpPost("http://" + host + ":" + managementPort + "/management");

        post.addHeader("X-Management-Client-Name",
                       "GUVNOR-ALA");

        // the DMR operation
        ModelNode operation = new ModelNode();
        operation.get("operation").set("undeploy");
        operation.get("address").add("deployment",
                                     deploymentName);

        post.setEntity(new StringEntity(operation.toJSONString(true),
                                        APPLICATION_JSON));

        try {
            HttpResponse response = httpclient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new WildflyClientException("Error Stopping App Status Code: " + statusCode);
            }
            return statusCode;
        } catch (IOException ex) {
            LOG.error("Error Stopping App : " + ex.getMessage(),
                      ex);
            throw new WildflyClientException("Error Stopping App : " + ex.getMessage(),
                                             ex);
        }
    }

    public void restart(String id) throws WildflyClientException {

    }

    /*
     * Returns the state of the application
     * @param String deploymentName
     * @return WildflyAppState for the given deployment name
     * @throw a WildflyClientException with the status code on failure
     * @throw a WildflyClientException with the throwable in case of an internal exception
     */
    public WildflyAppState getAppState(String deploymentName) throws WildflyClientException {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host,
                              managementPort),
                new UsernamePasswordCredentials(user,
                                                password));

        CloseableHttpClient httpclient = custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        final HttpPost post = new HttpPost("http://" + host + ":" + managementPort + "/management");

        post.addHeader("X-Management-Client-Name",
                       "GUVNOR-ALA");

        // the DMR operation
        ModelNode operation = new ModelNode();
        operation.get("operation").set("read-resource");
        operation.get("address").add("deployment",
                                     deploymentName);
        operation.get("resolve-expressions").set("true");

        post.setEntity(new StringEntity(operation.toJSONString(true),
                                        APPLICATION_JSON));
        try {
            HttpResponse response = httpclient.execute(post);
            String json = EntityUtils.toString(response.getEntity());
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            // use the isxxx methods to find out the type of jsonelement. In our
            // example we know that the root object is the Albums object and
            // contains an array of dataset objects
            if (element.isJsonObject()) {
                JsonObject outcome = element.getAsJsonObject();
                JsonElement resultElement = outcome.get("result");
                String enabled = null;
                if (resultElement != null) {
                    JsonObject result = resultElement.getAsJsonObject();
                    enabled = result.get("enabled").getAsString();
                }
                String state;
                if (Boolean.TRUE.toString().equals(enabled)) {
                    state = RUNNING;
                } else if (Boolean.FALSE.toString().equals(enabled)) {
                    state = STOPPED;
                } else {
                    state = UNKNOWN;
                }
                return new WildflyAppState(state,
                                           new Date());
            }
        } catch (IOException ex) {
            LOG.error("Error Getting App State : " + ex.getMessage(),
                      ex);
            throw new WildflyClientException("Error Getting App State : " + ex.getMessage(),
                                             ex);
        }
        return new WildflyAppState(UNKNOWN,
                                   new Date());
    }

    public String testConnection() throws WildflyClientException {
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(host,
                                                   managementPort),
                                     new UsernamePasswordCredentials(user,
                                                                     password));

        HttpPost post = new HttpPost("http://" + host + ":" + managementPort + "/management");
        post.addHeader("X-Management-Client-Name",
                       "GUVNOR-ALA");

        ModelNode op = new ModelNode();
        op.get("operation").set("read-resource");
        post.setEntity(new StringEntity(op.toJSONString(true),
                                        ContentType.APPLICATION_JSON));

        try (
                CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
                CloseableHttpResponse httpResponse = httpclient.execute(post);
        ) {
            if (HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()) {
                throw new Exception("Authentication failed. ");
            } else {
                String json = EntityUtils.toString(httpResponse.getEntity());
                ModelNode returnVal = ModelNode.fromJSONString(json);
                String productName = returnVal.get("result").get("product-name").asString();
                String productVersion = returnVal.get("result").get("product-version").asString();
                String releaseVersion = returnVal.get("result").get("release-version").asString();
                String releaseCodeName = returnVal.get("result").get("release-codename").asString();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(productName + ", " + productVersion);
                stringBuilder.append(" (" + releaseCodeName + ", " + releaseVersion + ")");
                return stringBuilder.toString();
            }
        } catch (Exception e) {
            throw new WildflyClientException(e.getMessage(),
                                             e);
        }
    }

    public String getProviderName() {
        return providerName;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getManagementPort() {
        return managementPort;
    }
}
