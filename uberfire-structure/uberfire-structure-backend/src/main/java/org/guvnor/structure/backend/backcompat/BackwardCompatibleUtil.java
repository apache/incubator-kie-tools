/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.backcompat;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigurationFactory;

@Deprecated
@ApplicationScoped
public class BackwardCompatibleUtil {

    private ConfigurationFactory configurationFactory;

    public BackwardCompatibleUtil() {
    }

    @Inject
    public BackwardCompatibleUtil(ConfigurationFactory configurationFactory) {
        this.configurationFactory = configurationFactory;
    }

    @Deprecated
    public ConfigGroup compat(final ConfigGroup configGroup) {
        if (configGroup != null) {
            final ConfigItem<List<String>> roles = configGroup.getConfigItem("security:roles");
            if (roles != null) {
                configGroup.addConfigItem(configurationFactory.newConfigItem("security:groups",
                                                                             new ArrayList<String>(roles.getValue())));
                configGroup.removeConfigItem("security:roles");
            }
            final ConfigItem<List<String>> groups = configGroup.getConfigItem("security:groups");
            if (groups == null) {
                configGroup.addConfigItem(configurationFactory.newConfigItem("security:groups",
                                                                             new ArrayList<String>()));
            }
        }
        return configGroup;
    }
}
