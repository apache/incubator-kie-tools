/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.infra;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.ext.layout.editor.client.components.container.Container;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;

import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

@ApplicationScoped
public class LayoutEditorFocusController {

    private HTMLElement scrollElement;
    private boolean dirty = false;
    private double recordedScrollLeft;
    private double recordedScrollTop;
    

    public void setTargetContainerView(Container.View view) {
        HTMLElement element = Js.cast(view.getElement());
        Scheduler.get().scheduleDeferred(() -> {
            scrollElement = findScrollableParent(element);
        });
    }

    public void recordFocus() {
        if (scrollElement != null && !dirty) {
            recordedScrollLeft = scrollElement.scrollLeft;
            recordedScrollTop = scrollElement.scrollTop;
            dirty = true;
        }
    }
    
    public void restoreFocus() {
        if (scrollElement != null) {
            scrollElement.scrollLeft = recordedScrollLeft;
            scrollElement.scrollTop = recordedScrollTop;
        }
        dirty = false;
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    protected HTMLElement findScrollableParent(HTMLElement element) {
        HTMLElement scrollElement = null;
        while (element != null) {
            if (isScrollable(element)) {
                scrollElement = element;
                break;
            }
            else if (element.parentNode instanceof HTMLElement) {
                element = Js.cast(element.parentNode);
            } else {
                break;
            }
        }
        return scrollElement;
    }

    protected void setScrollableElement(HTMLElement element) {
        this.scrollElement = element;
    }

    private boolean isScrollable(HTMLElement element) {
        return "auto".equals(element.style.overflow);
    }
    
    
    
}
