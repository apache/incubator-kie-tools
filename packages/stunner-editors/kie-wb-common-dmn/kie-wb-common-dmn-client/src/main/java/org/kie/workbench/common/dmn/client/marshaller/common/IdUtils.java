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

package org.kie.workbench.common.dmn.client.marshaller.common;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class IdUtils {

    private static String SEPARATOR_DELIMITER = "#";

    private static String COMBINER_DELIMITER = "-";

    public static final String AUTO_SOURCE_CONNECTION = "-AUTO-SOURCE";

    public static final String AUTO_TARGET_CONNECTION = "-AUTO-TARGET";

    public static final String getPrefixedId(final String prefixId,
                                             final String rawId) {
        return Stream.of(prefixId, rawId)
                .filter(s -> !isEmpty(s))
                .collect(Collectors.joining(SEPARATOR_DELIMITER));
    }

    public static String getRawId(final String prefixedId) {
        if (isEmpty(prefixedId)) {
            return "";
        }

        final String[] parts = prefixedId.split(SEPARATOR_DELIMITER);

        if (parts.length > 0) {
            return parts[parts.length - 1];
        }

        return "";
    }

    public static String getComposedId(final String... parts) {
        return Stream.of(parts)
                .filter(s -> !isEmpty(s))
                .map(s -> s.trim().replaceAll("\\s+", COMBINER_DELIMITER))
                .collect(Collectors.joining(COMBINER_DELIMITER));
    }

    public static String getShapeId(final JSIDMNDiagram diagram,
                                    final List<String> dmnElementIds,
                                    final String dmnElementId) {

        final String diagramName = lower(diagram.getName());
        return getUniqueId("dmnshape", diagramName, dmnElementId, 1, dmnElementIds);
    }

    public static String getEdgeId(final JSIDMNDiagram diagram,
                                   final List<String> dmnElementIds,
                                   final String dmnElementId,
                                   final boolean autoSourceConnection,
                                   final boolean autoTargetConnection) {

        final String diagramName = lower(diagram.getName());
        final String uniqueId = getUniqueId("dmnedge", diagramName, dmnElementId, 1, dmnElementIds);

        String autoConnectionId = "";
        if (autoSourceConnection) {
            autoConnectionId += AUTO_SOURCE_CONNECTION;
        }
        if (autoTargetConnection) {
            autoConnectionId += AUTO_TARGET_CONNECTION;
        }

        return uniqueId + autoConnectionId;
    }

    private static String getUniqueId(final String prefix,
                                      final String diagramName,
                                      final String dmnElementId,
                                      final int seed,
                                      final List<String> dmnElementIds) {

        final String count = seed == 1 ? "" : Integer.toString(seed);
        final String id = getComposedId(prefix, diagramName, count, dmnElementId);

        if (dmnElementIds.contains(id)) {
            return getUniqueId(prefix, diagramName, dmnElementId, seed + 1, dmnElementIds);
        }

        dmnElementIds.add(id);
        return id;
    }

    public static String uniqueId() {
        return UUID.uuid();
    }

    private static String lower(final String s) {
        return s == null ? "" : s.toLowerCase();
    }
}
