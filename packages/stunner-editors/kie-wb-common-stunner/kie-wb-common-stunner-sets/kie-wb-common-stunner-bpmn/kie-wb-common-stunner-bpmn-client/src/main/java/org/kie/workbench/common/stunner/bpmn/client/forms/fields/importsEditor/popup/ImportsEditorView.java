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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup;

import java.util.List;

import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;

public interface ImportsEditorView {

    void init(final Presenter presenter);

    List<DefaultImport> getDefaultImports();

    void setDefaultImports(final List<DefaultImport> defaultImports);

    List<WSDLImport> getWSDLImports();

    void setWSDLImports(final List<WSDLImport> wsdlImports);

    void hideView();

    void showView();

    interface Presenter {

        void ok();

        void cancel();

        void setCallback(final ImportsEditor.GetDataCallback callback);

        void show();

        ImportsValue getImports();
    }
}
