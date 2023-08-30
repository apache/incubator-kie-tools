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
package org.kie.workbench.common.dmn.client.shape.view;

import org.kie.workbench.common.dmn.client.shape.view.connections.AuthorityRequirementConnection;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresConnectorViewExt;
import org.kie.workbench.common.stunner.shapes.client.view.AbstractConnectorView;

import static org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents.DESKTOP_CONNECTOR_EVENT_TYPES;

public class AuthorityRequirementView extends WiresConnectorViewExt<AbstractConnectorView> {

    public AuthorityRequirementView(final double x1,
                                    final double y1,
                                    final double x2,
                                    final double y2) {
        this(new AuthorityRequirementConnection(x1,
                                                y1,
                                                x2,
                                                y2));
    }

    private AuthorityRequirementView(final AuthorityRequirementConnection line) {
        super(DESKTOP_CONNECTOR_EVENT_TYPES,
              line.getLine(),
              line.getHead(),
              line.getTail());
    }
}
