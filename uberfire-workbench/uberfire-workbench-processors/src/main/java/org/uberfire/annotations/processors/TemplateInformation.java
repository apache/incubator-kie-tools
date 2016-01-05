/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.annotations.processors;

import java.util.ArrayList;
import java.util.List;

public class TemplateInformation {

    private WorkbenchPanelInformation defaultPanel;
    private List<WorkbenchPanelInformation> templateFields = new ArrayList<WorkbenchPanelInformation>();

    public void addTemplateField( WorkbenchPanelInformation field ) {
        templateFields.add( field );
    }

    public List<WorkbenchPanelInformation> getTemplateFields() {
        return templateFields;
    }

    public void setDefaultPanel( WorkbenchPanelInformation defaultPanel ) {
        this.defaultPanel = defaultPanel;
    }

    public WorkbenchPanelInformation getDefaultPanel() {
        return defaultPanel;
    }

    public boolean thereIsTemplateFields() {
        return  ((!getTemplateFields().isEmpty())||defaultPanel!=null);
    }
}
