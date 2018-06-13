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

package org.kie.workbench.common.screens.server.management.client.widget.card.footer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class FooterView extends Composite
        implements FooterPresenter.View {

    @Inject
    @DataField("copy-url")
    Icon copyUrl;

    @Inject
    @DataField("url")
    Anchor url;

    @Inject
    @DataField("version")
    Span version;

    public static native void glueCopy(final Element element) /*-{
        var clip = new $wnd.ZeroClipboard(element);
    }-*/;

    @Override
    public void setupUrl(final String url) {
        this.url.setText(url);
        this.url.setHref(url);
        this.copyUrl.getElement().setPropertyString("data-clipboard-text", url);
        glueCopy(this.copyUrl.getElement());
    }

    @Override
    public void setupVersion(final String version) {
        this.version.setText(version);
    }

    @Override
    public void hideUrlElements() {
        url.setVisible(false);
        copyUrl.setVisible(false);
    }
}
