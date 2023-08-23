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
package org.kie.workbench.common.dmn.client.shape.factory;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.client.shape.view.AssociationView;
import org.kie.workbench.common.dmn.client.shape.view.AuthorityRequirementView;
import org.kie.workbench.common.dmn.client.shape.view.InformationRequirementView;
import org.kie.workbench.common.dmn.client.shape.view.KnowledgeRequirementView;

@ApplicationScoped
public class DMNConnectorShapeViewFactory {

    public AssociationView association(final double x1,
                                       final double y1,
                                       final double x2,
                                       final double y2) {
        return new AssociationView(x1,
                                   y1,
                                   x2,
                                   y2);
    }

    public InformationRequirementView informationRequirement(final double x1,
                                                             final double y1,
                                                             final double x2,
                                                             final double y2) {
        return new InformationRequirementView(x1,
                                              y1,
                                              x2,
                                              y2);
    }

    public KnowledgeRequirementView knowledgeRequirement(final double x1,
                                                         final double y1,
                                                         final double x2,
                                                         final double y2) {
        return new KnowledgeRequirementView(x1,
                                            y1,
                                            x2,
                                            y2);
    }

    public AuthorityRequirementView authorityRequirement(final double x1,
                                                         final double y1,
                                                         final double x2,
                                                         final double y2) {
        return new AuthorityRequirementView(x1,
                                            y1,
                                            x2,
                                            y2);
    }
}
