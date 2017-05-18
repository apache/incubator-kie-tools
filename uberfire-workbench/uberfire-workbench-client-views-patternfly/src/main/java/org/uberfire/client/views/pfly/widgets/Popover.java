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

package org.uberfire.client.views.pfly.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.HTMLElement;

@Dependent
public class Popover implements IsElement {

    @Inject
    private Anchor anchor;

    @PostConstruct
    public void init() {
        init(anchor);
    }

    public void setTitle(final String title) {
        anchor.setTitle(title);
    }

    public void setContent(final String content) {
        anchor.setAttribute("data-content",
                            content);
    }

    @Override
    public HTMLElement getElement() {
        return anchor;
    }

    public native void init(final HTMLElement e) /*-{
        $wnd.jQuery(e).popover({
            container: 'body',
            trigger: 'focus'
        });
    }-*/;
}
