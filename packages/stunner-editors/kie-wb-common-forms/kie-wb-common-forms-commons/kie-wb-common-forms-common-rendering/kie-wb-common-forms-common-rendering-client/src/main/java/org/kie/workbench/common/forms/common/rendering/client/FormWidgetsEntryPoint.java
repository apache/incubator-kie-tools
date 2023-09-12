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


package org.kie.workbench.common.forms.common.rendering.client;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.ScriptInjector;
import org.gwtbootstrap3.extras.typeahead.client.TypeaheadClientBundle;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;

@EntryPoint
@Bundle("resources/i18n/FormWidgetsConstants.properties")
public class FormWidgetsEntryPoint {

    @PostConstruct
    protected void init() {
        /*
        TODO fix this:
         Temporary fix: We need to inject manually the TypeAhead script without removing the script tag to avoid errors.
        */
        ScriptInjector.fromString(TypeaheadClientBundle.INSTANCE.typeahead().getText())
                .setWindow(ScriptInjector.TOP_WINDOW)
                .setRemoveTag(false)
                .inject();
    }
}
