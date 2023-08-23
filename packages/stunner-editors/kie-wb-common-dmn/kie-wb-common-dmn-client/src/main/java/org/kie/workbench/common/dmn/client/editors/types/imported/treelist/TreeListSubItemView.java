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
import javax.inject.Named;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class TreeListSubItemView implements TreeListSubItem.View {

    private TreeListSubItem presenter;

    @DataField("description")
    private final HTMLElement description;

    @DataField("details")
    private final HTMLElement details;

    @Inject
    public TreeListSubItemView(@Named("span") final HTMLElement description,
                               @Named("span") final HTMLElement details) {
        this.description = description;
        this.details = details;
    }

    @Override
    public void init(final TreeListSubItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDescription(final String description) {
        this.description.textContent = description;
    }

    @Override
    public void setDetails(final String complement) {
        this.details.textContent = complement;
    }
}
