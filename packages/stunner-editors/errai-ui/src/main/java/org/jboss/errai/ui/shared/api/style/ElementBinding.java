/*
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.shared.api.style;

import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

/**
 * @author Mike Brock
 */
@SuppressWarnings("deprecation")
public class ElementBinding {
  private final Element element;
  private final EventListener newListener;
  private final EventListener originalEventListener;
  private final Object beanInstance;

  public ElementBinding(final StyleBindingsRegistry registery, final Element element, final Object beanInstance) {
    this.element = element;
    this.originalEventListener = DOM.getEventListener(element);
    this.beanInstance = beanInstance;

    this.newListener = new EventListener() {
      @Override
      public void onBrowserEvent(Event event) {
        if (event.getTypeInt() == Event.ONCHANGE) {
          registery.updateStyles();
        }

        if (originalEventListener != null) {
          originalEventListener.onBrowserEvent(event);
        }
      }
    };

    DOM.setEventListener(element, newListener);
  }

  public Element getElement() {
    return element;
  }

  public Object getBeanInstance() {
    return beanInstance;
  }

  public void clean() {
    if (originalEventListener != null) {
      if (DOM.getEventListener(element) != newListener) {
        LoggerFactory.getLogger(getClass())
          .warn("cannot unwrap element binding for: " + element + "; found unexpected listener.");
      }
      else {
        DOM.setEventListener(element, originalEventListener);
      }
    }
  }
}
