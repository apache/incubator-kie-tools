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

package org.drools.workbench.screens.workitems.model;

import java.util.List;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class WorkItemsModelContent {

    private String definition;
    private List<String> workItemImages;
    private Overview overview;

    public WorkItemsModelContent() {
    }

    public WorkItemsModelContent( final String definition,
                                  final Overview overview,
                                  final List<String> workItemImages ) {
        this.definition = PortablePreconditions.checkNotNull( "definition",
                                                              definition );
        this.overview = PortablePreconditions.checkNotNull( "overview",
                                                              overview );
        this.workItemImages = PortablePreconditions.checkNotNull( "workItemImages",
                                                                  workItemImages );

    }

    public String getDefinition() {
        return this.definition;
    }

    public List<String> getWorkItemImages() {
        return this.workItemImages;
    }

    public Overview getOverview() {
        return overview;
    }

}
