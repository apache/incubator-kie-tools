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

package org.guvnor.structure.backend.config;

import com.thoughtworks.xstream.XStream;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.SecureConfigItem;
import org.kie.soup.xstream.XStreamUtils;

/**
 * Marshall a ConfigGroup to and from XML
 */
public class ConfigGroupMarshaller {

    private final XStream backwardCompatibleXstream = XStreamUtils.createTrustingXStream();
    private final XStream xstream = XStreamUtils.createTrustingXStream();

    public ConfigGroupMarshaller() {

        String[] voidDeny = {"void.class", "Void.class"};

        backwardCompatibleXstream.alias("group",
                                     ConfigGroup.class );
        backwardCompatibleXstream.alias("item",
                                        ConfigItem.class );
        backwardCompatibleXstream.alias("type",
                                        ConfigType.class );
        backwardCompatibleXstream.alias("secureitem",
                                        SecureConfigItem.class);
        // for backward compatibility only
        backwardCompatibleXstream.alias("org.uberfire.backend.server.config.SecureConfigItem",
                                        SecureConfigItem.class);
        backwardCompatibleXstream.denyTypes(voidDeny);

        xstream.alias("group",
                      ConfigGroup.class);
        xstream.alias("item",
                      ConfigItem.class);
        xstream.alias("type",
                      ConfigType.class);
        xstream.alias("secureitem",
                      SecureConfigItem.class);
        xstream.denyTypes(voidDeny);


    }

    public String marshall(final ConfigGroup configGroup) {
        return xstream.toXML(configGroup);
    }

    public ConfigGroup unmarshall(final String xml) {
        return (ConfigGroup) backwardCompatibleXstream.fromXML(xml);
    }
}
