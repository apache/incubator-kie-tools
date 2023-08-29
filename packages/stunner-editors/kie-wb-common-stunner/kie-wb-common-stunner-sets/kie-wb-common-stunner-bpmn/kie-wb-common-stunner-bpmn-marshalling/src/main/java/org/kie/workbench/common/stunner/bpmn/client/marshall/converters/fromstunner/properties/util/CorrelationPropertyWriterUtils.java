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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.core.util.UUID;

public class CorrelationPropertyWriterUtils {

    public static ItemDefinition createItemDefinition(String structureRef) {
        ItemDefinition itemDefinition = Bpmn2Factory.eINSTANCE.createItemDefinition();
        itemDefinition.setId(UUID.uuid());
        itemDefinition.setStructureRef(structureRef);
        return itemDefinition;
    }

    public static FormalExpression createFormalExpression(
            ItemDefinition itemDefinition,
            String language,
            String body) {
        FormalExpression formalExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
        formalExpression.setId(UUID.uuid());
        formalExpression.setEvaluatesToTypeRef(itemDefinition);
        formalExpression.setLanguage(language);
        FormalExpressionBodyHandler.of(formalExpression).setBody(body);
        return formalExpression;
    }
}
