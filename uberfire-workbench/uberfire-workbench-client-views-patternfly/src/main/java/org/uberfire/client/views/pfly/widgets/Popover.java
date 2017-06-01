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
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQueryElement;

/**
 * Wrapper component for PatternFly's <a href="http://www.patternfly.org/pattern-library/widgets/#popover">Popover</a>
 */
@Dependent
public class Popover implements IsElement {

    @Inject
    private Anchor anchor;

    @Inject
    private JQuery<JQueryPopoverElement> jQuery;

    @PostConstruct
    public void init() {
        Scheduler.get().scheduleDeferred(() -> jQuery.wrap(getElement()).popover());
    }

    @PreDestroy
    public void destroy() {
        jQuery.wrap(getElement()).popover("destroy");
    }

    public void show() {
        jQuery.wrap(getElement()).popover("show");
    }

    public void hide() {
        jQuery.wrap(getElement()).popover("hide");
    }

    public void toggle() {
        jQuery.wrap(getElement()).popover("toggle");
    }

    public void setTitle(final String title) {
        anchor.setTitle(title);
    }

    public void setContent(final String content) {
        setDataAttribute("content",
                         content);
    }

    public void setContainer(final String container) {
        setDataAttribute("container",
                         container);
    }

    public void setTrigger(final String trigger) {
        setDataAttribute("trigger",
                         trigger);
    }

    public void setTemplate(final String template) {
        setDataAttribute("template",
                         template);
    }

    public void setPlacement(final String placement) {
        setDataAttribute("placement",
                         placement);
    }

    public void setHtml(final Boolean html) {
        setDataAttribute("html",
                         String.valueOf(html));
    }

    protected void setDataAttribute(final String attribute,
                                    final String value) {
        anchor.setAttribute("data-" + attribute,
                            value);
    }

    @Override
    public HTMLElement getElement() {
        return anchor;
    }

    @JsType(isNative = true)
    public interface JQueryPopoverElement extends JQueryElement {

        void popover();

        void popover(String method);
    }
}
