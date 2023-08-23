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

package org.kie.workbench.common.dmn.client.editors.types.imported.treelist;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Node;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class TreeListSubItem {

    private final View view;
    private String description;
    private String details;

    @Inject
    public TreeListSubItem(final View view) {
        this.view = view;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
        this.view.setDescription(description);
    }

    public String getDetails() {
        return details;
    }

    // Details will be used for class properties.
    public void setDetails(final String details) {
        this.details = details;
        this.view.setDetails(details);
    }

    public Node getElement() {
        return view.getElement();
    }

    public interface View extends UberElemental<TreeListSubItem>,
                                  IsElement {

        void setDescription(final String description);

        void setDetails(final String complement);
    }
}