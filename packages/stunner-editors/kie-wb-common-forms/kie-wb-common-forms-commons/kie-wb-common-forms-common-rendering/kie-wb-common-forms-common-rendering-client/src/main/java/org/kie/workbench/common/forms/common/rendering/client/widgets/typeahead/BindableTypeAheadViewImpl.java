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


package org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.typeahead.client.base.Dataset;
import org.gwtbootstrap3.extras.typeahead.client.ui.Typeahead;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.common.rendering.client.util.masks.ClientMaskInterpreter;
import org.kie.workbench.common.forms.commons.rendering.shared.util.masks.MaskInterpreter;

@Dependent
@Templated
public class BindableTypeAheadViewImpl<T> extends Composite implements BindableTypeAheadView<T> {

    protected BindableTypeAhead<T> presenter;

    protected MaskInterpreter<T> interpreter;

    private Typeahead<T> typeahead;

    @Inject
    @DataField
    private FlowPanel content;

    @Override
    public void setPresenter(BindableTypeAhead<T> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void init(Dataset<T> dataset,
                     String mask) {
        Assert.notNull("Dataset cannot be null",
                       dataset);

        content.clear();

        typeahead = new Typeahead<T>(dataset);

        interpreter = new ClientMaskInterpreter<T>(mask);

        content.add(typeahead);

        typeahead.addTypeaheadSelectedHandler(event -> {
            presenter.setValue(event.getSuggestion().getData(),
                               true);
        });
    }

    @Override
    public HasValue<T> wrapped() {
        return presenter;
    }

    @Override
    public void setValue(T value) {
        if (typeahead != null) {
            typeahead.setValue(interpreter.render(value));
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        typeahead.setReadOnly(readOnly);
    }
}
