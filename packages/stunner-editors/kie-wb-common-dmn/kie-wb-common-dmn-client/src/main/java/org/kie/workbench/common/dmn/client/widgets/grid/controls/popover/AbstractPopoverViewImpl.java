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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.popover;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwt.dom.client.BrowserEvents;
import elemental2.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.PopoverOptions;

public abstract class AbstractPopoverViewImpl implements PopoverView {

    static final String ESCAPE_KEY = "Escape";

    static final String ESC_KEY = "Esc";

    static final String ENTER_KEY = "Enter";

    static final String PLACEMENT = "auto top";

    @DataField("popover")
    protected Div popoverElement;

    @DataField("popover-content")
    protected Div popoverContentElement;

    protected JQueryProducer.JQuery<Popover> jQueryPopover;

    protected Popover popover;

    protected boolean isVisible;

    protected EventListener closedByKeyboardEventListener = null;

    protected Optional<Consumer<CanBeClosedByKeyboard>> closedByKeyboardCallback = Optional.empty();

    protected AbstractPopoverViewImpl() {
        //CDI proxy
    }

    public AbstractPopoverViewImpl(final Div popoverElement,
                                   final Div popoverContentElement,
                                   final JQueryProducer.JQuery<Popover> jQueryPopover) {
        this.popoverElement = popoverElement;
        this.popoverContentElement = popoverContentElement;
        this.jQueryPopover = jQueryPopover;
    }

    @Override
    public void setOnClosedByKeyboardCallback(final Consumer<CanBeClosedByKeyboard> callback) {
        closedByKeyboardCallback = Optional.ofNullable(callback);
    }

    @Override
    public void show(final Optional<String> popoverTitle) {
        final PopoverOptions options = createOptions();

        popoverTitle.ifPresent(t -> popoverElement.setAttribute("title", t));
        popover = jQueryPopover.wrap(this.getElement());
        popover.addShowListener(() -> isVisible = true);
        popover.addShownListener(() -> {
            isVisible = true;
            setKeyDownListeners();
            onShownFocus();
        });
        popover.addHideListener(() -> isVisible = false);
        popover.addHiddenListener(() -> {
            isVisible = false;
            clearKeyDownListeners();
        });
        popover.popover(options);
        popover.show();
    }

    PopoverOptions createOptions() {
        final PopoverOptions options = createPopoverOptionsInstance();
        options.setContent((element) -> popoverContentElement);
        options.setAnimation(false);
        options.setHtml(true);
        options.setPlacement(PLACEMENT);

        return options;
    }

    PopoverOptions createPopoverOptionsInstance() {
        return new PopoverOptions();
    }

    protected void onShownFocus() {
        popoverContentElement.getStyle().setProperty("outline", "none");
        popoverContentElement.focus();
    }

    @Override
    public void hide() {
        if (Objects.nonNull(popover)) {
            popover.hide();
            popover.destroy();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    protected void setKeyDownListeners() {
        if (Objects.isNull(closedByKeyboardEventListener)) {
            closedByKeyboardEventListener = getKeyDownEventListener();
            popoverElement.addEventListener(BrowserEvents.KEYDOWN,
                                            closedByKeyboardEventListener,
                                            false);
        }
    }

    protected void clearKeyDownListeners() {
        if (Objects.nonNull(closedByKeyboardEventListener)) {
            popoverElement.removeEventListener(BrowserEvents.KEYDOWN,
                                               closedByKeyboardEventListener,
                                               false);
            closedByKeyboardEventListener = null;
        }
    }

    protected EventListener getKeyDownEventListener() {
        return (e) -> keyDownEventListener(e);
    }

    public void keyDownEventListener(final Object event) {
        if (event instanceof KeyboardEvent) {
            final KeyboardEvent keyEvent = (KeyboardEvent) event;
            if (isEnterKeyPressed(keyEvent)) {
                hide();
                keyEvent.stopPropagation();
                onClosedByKeyboard();
            } else if (isEscapeKeyPressed(keyEvent)) {
                reset();
                hide();
                onClosedByKeyboard();
            }
        }
    }

    public boolean isEscapeKeyPressed(final KeyboardEvent event) {
        return Objects.equals(event.key, ESC_KEY) || Objects.equals(event.key, ESCAPE_KEY);
    }

    public boolean isEnterKeyPressed(final KeyboardEvent event) {
        return Objects.equals(event.key, ENTER_KEY);
    }

    public void onClosedByKeyboard() {
        getClosedByKeyboardCallback().ifPresent(c -> c.accept(this));
    }

    public Optional<Consumer<CanBeClosedByKeyboard>> getClosedByKeyboardCallback() {
        return closedByKeyboardCallback;
    }
}
