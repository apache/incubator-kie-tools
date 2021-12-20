/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.widgets.client.workitems;

import org.drools.workbench.models.datamodel.workitems.PortableListParameterDefinition;

/**
 * A Widget to display a Work Item List parameter
 */
public class WorkItemListParameterWidget extends WorkItemObjectParameterWidget {

    public WorkItemListParameterWidget( PortableListParameterDefinition ppd,
                                        IBindingProvider bindingProvider,
                                        boolean isReadOnly ) {
        super( ppd,
               bindingProvider,
               isReadOnly );
        this.parameterName.setText( ppd.getName() );
    }

}
