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

package org.drools.workbench.screens.globals.client.type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.globals.client.resources.GlobalsEditorResources;
import org.drools.workbench.screens.globals.client.resources.i18n.GlobalsEditorConstants;
import org.drools.workbench.screens.globals.type.GlobalResourceTypeDefinition;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class GlobalResourceType
        extends GlobalResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( GlobalsEditorResources.INSTANCE.images().typeGlobalVariable() );

    @Inject
    private TranslationService translationService;

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = translationService.getTranslation( GlobalsEditorConstants.GlobalResourceTypeDescription );
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
