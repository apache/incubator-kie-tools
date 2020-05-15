/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.file.api;

import java.util.Properties;

import org.uberfire.java.nio.file.spi.FileSystemProvider;

public class FileSystemUtils {

    public static final String CFG_KIE_CONTROLLER_OCP_ENABLED = "org.kie.server.controller.openshift.enabled";
    public static final String SIMPLIFIED_MONITORING_ENABLED = "org.appformer.server.simplified.monitoring.enabled";
    public static final String K8S_FS_SCHEME = "k8s";
    private static final String GIT_FS_SCHEME = "git";

    private static ThreadLocal<Properties> configProps = ThreadLocal.withInitial(System::getProperties);

    private FileSystemUtils() {
    }

    public static Properties getConfigProps() {
        return configProps.get();
    }

    public static boolean isOpenShiftSupported() {
        return "true".equals(getConfigProps().getProperty(CFG_KIE_CONTROLLER_OCP_ENABLED, "false"));
    }

    public static boolean isSimplifiedMonitoringEnabled() {
        return "true".equals(getConfigProps().getProperty(SIMPLIFIED_MONITORING_ENABLED, "false"));
    }

    public static boolean isGitDefaultFileSystem() {
        return FileSystemProviders.getDefaultProvider().getScheme().equals(GIT_FS_SCHEME);
    }

    public static boolean isK8SFileSystemProviderAsDefault(FileSystemProvider provider) {
        return K8S_FS_SCHEME.equals(provider.getScheme()) && isOpenShiftSupported() && isSimplifiedMonitoringEnabled();
    }
}
