/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.screens;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.dom.Paragraph;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class EmptyStateView
        implements EmptyState.View {

    @Inject
    @DataField
    Div view;

    @Inject
    @Named("h1")
    @DataField
    Heading title;

    @Inject
    @DataField
    Paragraph message;

    @Override
    public void setMessage(final String title,
                           final String message) {
        this.title.setInnerHTML(title);
        this.message.setInnerHTML(message);
    }

    @Override
    public void init(final EmptyState presenter) {

    }

    @Override
    public HTMLElement getElement() {
        return view;
    }

}
