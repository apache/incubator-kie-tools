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

package org.guvnor.structure.server.config;

import java.util.List;
import java.util.Map;

public interface ConfigurationService {

    public static final String LAST_MODIFIED_MARKER_FILE = ".lastmodified";

    void startBatch();

    void endBatch();

    List<ConfigGroup> getConfiguration(final ConfigType type);

    List<ConfigGroup> getConfiguration(final ConfigType type,
                                       final String namespace);

    Map<String, List<ConfigGroup>> getConfigurationByNamespace(final ConfigType type);

    boolean addConfiguration(final ConfigGroup configGroup);

    boolean updateConfiguration(final ConfigGroup configGroup);

    boolean removeConfiguration(final ConfigGroup configGroup);

    boolean cleanUpSystemRepository();
}
