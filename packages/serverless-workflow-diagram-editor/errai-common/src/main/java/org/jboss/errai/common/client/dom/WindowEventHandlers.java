/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.dom;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/WindowEventHandlers">Web API</a>
 */
@JsType(isNative = true)
@Deprecated
public interface WindowEventHandlers {
  @JsProperty EventListener<Event> getOnafterprint();
  @JsProperty void setOnafterprint(EventListener<Event> onafterprint);

  @JsProperty EventListener<Event> getOnbeforeprint();
  @JsProperty void setOnbeforeprint(EventListener<Event> onbeforeprint);

  @JsProperty EventListener<Event> getOnbeforeunload();
  @JsProperty void setOnbeforeunload(EventListener<Event> onbeforeunload);

  @JsProperty EventListener<HashChangeEvent> getOnhashchange();
  @JsProperty void setOnhashchange(EventListener<HashChangeEvent> onhashchange);

  @JsProperty EventListener<Event> getOnlanguagechange();
  @JsProperty void setOnlanguagechange(EventListener<Event> onlanguagechange);

  @JsProperty EventListener<?> getOnmessage();
  @JsProperty void setOnmessage(EventListener<?> onmessage);

  @JsProperty EventListener<Event> getOnoffline();
  @JsProperty void setOnoffline(EventListener<Event> onoffline);

  @JsProperty EventListener<Event> getOnonline();
  @JsProperty void setOnonline(EventListener<Event> ononline);

  @JsProperty EventListener<PageTransitionEvent> getOnpagehide();
  @JsProperty void setOnpagehide(EventListener<PageTransitionEvent> onpagehide);

  @JsProperty EventListener<PageTransitionEvent> getOnpageshow();
  @JsProperty void setOnpageshow(EventListener<PageTransitionEvent> onpageshow);

  @JsProperty EventListener<PopStateEvent> getOnpopstate();
  @JsProperty void setOnpopstate(EventListener<PopStateEvent> onpopstate);

  @JsProperty EventListener<StorageEvent> getOnstorage();
  @JsProperty void setOnstorage(EventListener<StorageEvent> onstorage);

  @JsProperty EventListener<?> getOnunload();
  @JsProperty void setOnunload(EventListener<?> onunload);
}
