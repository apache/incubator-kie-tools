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

package org.kie.workbench.common.widgets.client.popups.launcher;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated(stylesheet = "AppLauncherItemView.css")
public class AppLauncherItemView implements IsElement {

    @Inject
    @DataField("btn")
    private Anchor anchor;

    @Inject
    @DataField("fa")
    private Span icon;

    @Inject
    @DataField("text")
    private Span text;

    @Override
    public HTMLElement getElement() {
        return anchor;
    }

    public void setName(final String name){
        text.setTextContent(name);
    }

    public void setURL(final String url){
        anchor.setHref(url);
    }

    public void setIcon(final String iconClass){
        icon.getClassList().add(iconClass);
    }

    public String getName(){
        return text.getTextContent();
    }

}
