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
package org.kie.workbench.common.dmn.client.shape.def;

import java.util.function.BiConsumer;

import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.client.shape.view.handlers.DMNViewHandlers;
import org.kie.workbench.common.stunner.shapes.client.view.AbstractConnectorView;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

public interface DMNConnectorShapeDef<W extends DMNDefinition, V extends AbstractConnectorView> extends DMNShapeDef<W, V>,
                                                                                                        ConnectorShapeDef<W, V> {

    @Override
    default BiConsumer<W, V> viewHandler() {
        return DMNViewHandlers.CONNECTOR_ATTRIBUTES_HANDLER::handle;
    }
}
