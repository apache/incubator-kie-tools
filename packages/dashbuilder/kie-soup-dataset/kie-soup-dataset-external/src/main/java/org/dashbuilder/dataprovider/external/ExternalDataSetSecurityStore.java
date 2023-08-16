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

import java.util.Optional;

import org.dashbuilder.dataset.def.DataSetDef;

public class ExternalDataSetSecurityStore {

    public static final String SECURITY_PROP_PREFIX = "dataset.external.%s.";
    public static final String USER_PROP = SECURITY_PROP_PREFIX + "user";
    public static final String PASSWORD_PROP = SECURITY_PROP_PREFIX + "password";
    public static final String TOKEN_PROP = SECURITY_PROP_PREFIX + "token";

    private ExternalDataSetSecurityStore() {
        // empty
    }

    public static Optional<SecurityInfo> get(DataSetDef def) {
        var byUUID = get(def.getUUID());
        return byUUID.isPresent() ? byUUID : get(def.getName());
    }

    private static Optional<SecurityInfo> get(String key) {

        var user = System.getProperty(String.format(USER_PROP, key));
        if (user != null && !user.isBlank()) {
            var password = System.getProperty(String.format(PASSWORD_PROP, key));
            return Optional.of(SecurityInfo.basic(user, password));
        }

        var token = System.getProperty(String.format(TOKEN_PROP, key));
        if (token != null) {
            return Optional.of(SecurityInfo.token(token));

        }

        return Optional.empty();
    }

    public static enum SecurityType {
        BASIC,
        TOKEN;
    }

    public static class SecurityInfo {

        private String username;
        private String password;
        private String token;
        private SecurityType type;

        static SecurityInfo token(String token) {
            var secInfo = new SecurityInfo();
            secInfo.token = token;
            secInfo.type = SecurityType.TOKEN;
            return secInfo;
        }

        static SecurityInfo basic(String user, String password) {
            var secInfo = new SecurityInfo();
            secInfo.username = user;
            secInfo.password = password;
            secInfo.type = SecurityType.BASIC;
            return secInfo;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getToken() {
            return token;
        }

        public SecurityType getType() {
            return type;
        }

    }

}