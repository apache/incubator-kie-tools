/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.java.nio.fs.cloud;

import java.nio.charset.StandardCharsets;

public class CloudClientConstants {
    public static final String DEFAULT_TOKEN_LOCATION = "/var/run/secrets/kubernetes.io/serviceaccount/token";
    public static final String ENV_VAR_API_SERVICE_HOST = "KUBERNETES_SERVICE_HOST";
    public static final String ENV_VAR_API_SERVER_PORT = "KUBERNETES_SERVICE_PORT";
    public static final String ENCODING = System.getProperty("file.encoding", StandardCharsets.UTF_8.name());
}
