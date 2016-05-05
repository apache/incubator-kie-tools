/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.wires.bpmn.api.type.BpmnResourceTypeDefinition;
import org.uberfire.ext.wires.bpmn.client.resources.BpmnEditorResources;
import org.uberfire.ext.wires.bpmn.client.resources.i18n.BpmnEditorConstants;

@ApplicationScoped
public class BpmnResourceType
        extends BpmnResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( BpmnEditorResources.INSTANCE.images().typeBpmn() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = BpmnEditorConstants.INSTANCE.bpmnResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) {
            return super.getDescription();
        }
        return desc;
    }
}
