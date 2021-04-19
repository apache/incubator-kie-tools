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

import org.jboss.errai.common.client.api.annotations.Element;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/HTMLAnchorElement">Web API</a>
 */
@JsType(isNative = true)
@Element("a")
@Deprecated
public interface Anchor extends HTMLElement {
  @JsProperty String getCharset();
  @JsProperty void setCharset(String charset);

  @JsProperty String getCoords();
  @JsProperty void setCoords(String coords);

  @JsProperty String getHash();
  @JsProperty void setHash(String hash);

  @JsProperty String getHost();
  @JsProperty void setHost(String host);

  @JsProperty String getHostname();
  @JsProperty void setHostname(String hostname);

  @JsProperty String getHref();
  @JsProperty void setHref(String href);

  @JsProperty String getHreflang();
  @JsProperty void setHreflang(String hreflang);

  @JsProperty String getMedia();
  @JsProperty void setMedia(String media);

  @JsProperty String getName();
  @JsProperty void setName(String name);

  @JsProperty String getOrigin();

  @JsProperty String getPathname();
  @JsProperty void setPathname(String pathname);

  @JsProperty String getPort();
  @JsProperty void setPort(String port);

  @JsProperty String getProtocol();
  @JsProperty void setProtocol(String protocol);

  @JsProperty String getRel();
  @JsProperty void setRel(String rel);

  @JsProperty DOMTokenList getRelList();

  @JsProperty String getRev();
  @JsProperty void setRev(String rev);

  @JsProperty String getSearch();
  @JsProperty void setSearch(String search);

  @JsProperty String getShape();
  @JsProperty void setShape(String shape);

  @JsProperty String getTarget();
  @JsProperty void setTarget(String target);

  @JsProperty String getType();
  @JsProperty void setType(String type);
}
