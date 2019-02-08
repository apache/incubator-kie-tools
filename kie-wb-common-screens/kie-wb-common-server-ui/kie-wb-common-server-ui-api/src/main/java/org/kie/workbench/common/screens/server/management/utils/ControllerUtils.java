/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.utils;

import java.util.Properties;

import static org.kie.server.common.KeyStoreHelperUtil.loadControllerPassword;

public final class ControllerUtils {

    /**
     * Settings used to connect to KIE Server Controller
     */
    public static final String KIE_SERVER_CONTROLLER = "org.kie.workbench.controller";
    public static final String CFG_KIE_CONTROLLER_USER = "org.kie.workbench.controller.user";
    public static final String CFG_KIE_CONTROLLER_PASSWORD = "org.kie.workbench.controller.pwd";
    public static final String CFG_KIE_CONTROLLER_TOKEN = "org.kie.workbench.controller.token";
    public static final String CFG_KIE_CONTROLLER_OCP_ENABLED = "org.kie.workbench.controller.openshift.enabled";

    private static ThreadLocal<Properties> configProps = new ThreadLocal<>().withInitial(System::getProperties);

    private ControllerUtils() {}

    public static Properties getConfigProps() {
        return configProps.get();
    }

    public static boolean useEmbeddedController() {
        return getControllerURL() == null;
    }

    public static String getControllerURL() {
        return getConfigProps().getProperty(KIE_SERVER_CONTROLLER);
    }

    public static String getControllerUser() {
        return getConfigProps().getProperty(CFG_KIE_CONTROLLER_USER, "kieserver");
    }

    public static String getControllerPassword() {
        return loadControllerPassword(getConfigProps().getProperty(CFG_KIE_CONTROLLER_PASSWORD, "kieserver1!"));
    }

    public static String getControllerToken() {
        return getConfigProps().getProperty(CFG_KIE_CONTROLLER_TOKEN);
    }

    public static boolean isOpenShiftSupported() {
        return "true".equals(getConfigProps().getProperty(CFG_KIE_CONTROLLER_OCP_ENABLED, "false"));
    }
}
