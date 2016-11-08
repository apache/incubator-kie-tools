/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.selector;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.extras.typeahead.client.base.Dataset;
import org.gwtbootstrap3.extras.typeahead.client.base.SuggestionCallback;
import org.kie.workbench.common.forms.common.rendering.client.widgets.flatViews.impl.ObjectFlatView;
import org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAhead;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.model.impl.relations.ObjectSelectorFieldDefinition;

@Dependent
public class ObjectSelectorFieldRenderer extends FieldRenderer<ObjectSelectorFieldDefinition> {

    @Inject
    protected BindableTypeAhead widget;

    protected Dataset dataset = new Dataset() {
        @Override
        public void findMatches( String query, SuggestionCallback callback ) {

        }
    };

    @Override
    public String getName() {
        return ObjectSelectorFieldDefinition.CODE;
    }

    @Override
    public void initInputWidget() {
        widget.init( field.getMask(), dataset );
    }

    @Override
    public IsWidget getInputWidget() {
        return widget;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        return new ObjectFlatView( field.getMask() );
    }

    @Override
    public String getSupportedCode() {
        return ObjectSelectorFieldDefinition.CODE;
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {
        widget.setReadOnly( readOnly );
    }
}
