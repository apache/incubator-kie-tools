/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Map;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class KindUtilities {

    public static FunctionDefinition.Kind getKind(final FunctionDefinition function) {
        final Map<QName, String> attributes = function.getAdditionalAttributes();
        return FunctionDefinition.Kind.determineFromString(attributes.get(FunctionDefinition.KIND_QNAME));
    }

    public static void setKind(final FunctionDefinition function,
                               final FunctionDefinition.Kind kind) {
        final Map<String, String> nsContext = function.getNsContext();
        nsContext.put(FunctionDefinition.DROOLS_PREFIX,
                      Namespace.KIE.getUri());
        final Map<QName, String> attributes = function.getAdditionalAttributes();
        attributes.put(FunctionDefinition.KIND_QNAME,
                       kind.code());
    }
}
