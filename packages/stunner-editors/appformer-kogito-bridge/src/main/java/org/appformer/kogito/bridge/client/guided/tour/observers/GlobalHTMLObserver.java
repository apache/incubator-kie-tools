/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.kogito.bridge.client.guided.tour.observers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.Attr;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.NamedNodeMap;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourObserver;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.api.Disposer;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

public class GlobalHTMLObserver extends GuidedTourObserver<GlobalHTMLObserver> {

    final EventListener CLICK_LISTENER = this::onHTMLElementEvent;

    @Inject
    public GlobalHTMLObserver(final Disposer<GlobalHTMLObserver> selfDisposer) {
        super(selfDisposer);
    }

    @PostConstruct
    public void init() {
        addGlobalEventListener(CLICK, CLICK_LISTENER);
    }

    @Override
    protected void dispose() {
        removeGlobalEventListener(CLICK, CLICK_LISTENER);
        super.dispose();
    }

    void onHTMLElementEvent(final Event event) {
        getMonitorBridge().ifPresent(bridge -> bridge.refresh(getUserInteraction(event)));
    }

    private UserInteraction getUserInteraction(final Event event) {
        final String action = CLICK.toUpperCase();
        final String selector = getSelector(getTarget(event));
        return makeUserInteraction(action, selector);
    }

    private String getSelector(final HTMLElement element) {

        final String tag = element.tagName.toLowerCase();
        final String id = element.id != null && !element.id.isEmpty() ? "#" + element.id : "";
        final String dataAttributes = getDataAttributes(element);
        final String className = element.classList.length > 0 ? "." + String.join(".", element.classList.asList()) : "";

        return tag + dataAttributes + id + className;
    }

    public String getDataAttributes(final HTMLElement element) {
        final NamedNodeMap<Attr> attributes = element.attributes;
        final List<String> dataAttributes = new ArrayList<>();

        for (int i = 0, objectsSize = attributes.length; i < objectsSize; i++) {
            final String nodeName = attributes.item(i).nodeName;
            final String nodeValue = attributes.item(i).nodeValue;
            if (nodeName.startsWith("data-")) {
                dataAttributes.add("[" + nodeName + "=\"" + nodeValue + "\"]");
            }
        }
        return String.join("", dataAttributes);
    }

    private HTMLElement getTarget(final Event e) {
        return (HTMLElement) e.target;
    }

    private void addGlobalEventListener(final String type,
                                        final EventListener eventListener) {
        document().addEventListener(type, eventListener);
    }

    private void removeGlobalEventListener(final String type,
                                           final EventListener eventListener) {
        document().removeEventListener(type, eventListener);
    }

    UserInteraction makeUserInteraction(final String action,
                                        final String target) {
        final UserInteraction userInteraction = new UserInteraction();
        userInteraction.setAction(action);
        userInteraction.setTarget(target);
        return userInteraction;
    }

    HTMLDocument document() {
        return DomGlobal.document;
    }
}
