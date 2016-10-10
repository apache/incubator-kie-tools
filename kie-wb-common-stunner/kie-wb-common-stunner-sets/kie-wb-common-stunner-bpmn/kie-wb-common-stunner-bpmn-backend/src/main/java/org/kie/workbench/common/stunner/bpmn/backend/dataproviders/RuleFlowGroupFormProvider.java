/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.backend.dataproviders;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.FormRenderingContext;

import java.util.Map;
import java.util.TreeMap;

public class RuleFlowGroupFormProvider implements SelectorDataProvider {
    // NOTE - this provides dummy data for now until integration with
    // workbench is complete

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public SelectorData getSelectorData( FormRenderingContext context ) {
        // Map<Object, String> -- object is the value - string is text shown
        Map<Object, String> values = new TreeMap<>();
        values.put( "sampleruleflow1", "sampleruleflow1" );
        values.put( "sampleruleflow2", "sampleruleflow2" );
        values.put( "sampleruleflow3", "sampleruleflow3" );
        return new SelectorData( values, null );
    }
}
