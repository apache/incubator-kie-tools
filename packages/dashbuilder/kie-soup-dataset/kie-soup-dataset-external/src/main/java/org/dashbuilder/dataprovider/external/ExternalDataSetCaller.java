/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.dashbuilder.dataprovider.external;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.dashbuilder.dataprovider.external.ExternalDataSetSecurityStore.SecurityInfo;
import org.dashbuilder.dataprovider.external.ExternalDataSetSecurityStore.SecurityType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.json.ExternalDataSetJSONParser;

public class ExternalDataSetCaller {

    private static final String BEARER = "Bearer";

    private static final String BASIC = "Basic";

    private static final String AUTHORIZATION_HEADER = "Authorization ";

    private static final String METADATA_URI = "metadata";

    private ExternalDataSetJSONParser parser;

    public ExternalDataSetCaller() {
        // Empty
    }

    public ExternalDataSetCaller(ExternalDataSetJSONParser parser) {
        this.parser = parser;
    }

    public static ExternalDataSetCaller get() {
        var parser = new ExternalDataSetJSONParser(value -> {
            var temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(value);
            var instant = Instant.from(temporalAccessor);
            return Date.from(instant);
        });
        return new ExternalDataSetCaller(parser);
    }

    public DataSetMetadata retrieveMetadata(ExternalDataSetDef def) {
        var defUrl = ExternalDataSetHelper.getUrl(def);
        var metaUrl = defUrl.endsWith("/") ? defUrl : defUrl + "/";
        URL url;
        try {
            url = URI.create(metaUrl).resolve(METADATA_URI).toURL();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid definition URL " + defUrl, e);
        }

        var json = getUrlContent(def, url);
        return parser.parseMetadata(json);

    }

    public DataSet retrieveDataSet(ExternalDataSetDef def) {
        URL url;
        var defUrl = ExternalDataSetHelper.getUrl(def);
        try {
            url = new URL(defUrl);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid definition URL " + defUrl, e);
        }

        var json = getUrlContent(def, url);
        return parser.parseDataSet(json);
    }

    private String getUrlContent(ExternalDataSetDef def, URL url) {
        URLConnection conn;
        try {
            conn = url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException("Not able to open URL " + url.toExternalForm() + " from data set " + def
                    .getName(), e);
        }

        if (conn instanceof HttpURLConnection) {
            var httpConn = (HttpURLConnection) conn;
            ExternalDataSetSecurityStore.get(def).ifPresent(secInfo -> addSecurity(httpConn, secInfo));
        }

        try {
            var response = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            checkResponse(def, conn);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Not able to access external data set", e);
        }
    }

    private void checkResponse(ExternalDataSetDef def, URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            var httpConn = (HttpURLConnection) conn;
            try {
                if (httpConn.getResponseCode() != 200) {
                    throw new RuntimeException("Invalid response when acessing external dataSet " + def.getName() +
                            ": " +
                            httpConn.getResponseCode());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading response code: " + e.getMessage(), e);
            }
        }
    }

    protected void addSecurity(HttpURLConnection conn, SecurityInfo secInfo) {
        if (secInfo.getType() == SecurityType.BASIC) {
            var auth = secInfo.getUsername() + ":" + secInfo.getPassword();
            var encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty(AUTHORIZATION_HEADER, BASIC + " " + new String(encodedAuth));
        }

        if (secInfo.getType() == SecurityType.TOKEN) {
            conn.setRequestProperty(AUTHORIZATION_HEADER, BEARER + " " + secInfo.getToken());
        }
    }

}
