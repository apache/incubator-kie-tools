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


package org.jboss.errai.ui.rebind.ioc.element;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLAppletElement;
import elemental2.dom.HTMLAreaElement;
import elemental2.dom.HTMLAudioElement;
import elemental2.dom.HTMLBRElement;
import elemental2.dom.HTMLBaseElement;
import elemental2.dom.HTMLBaseFontElement;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLDListElement;
import elemental2.dom.HTMLDataListElement;
import elemental2.dom.HTMLDetailsElement;
import elemental2.dom.HTMLDialogElement;
import elemental2.dom.HTMLDirectoryElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLEmbedElement;
import elemental2.dom.HTMLFieldSetElement;
import elemental2.dom.HTMLFontElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLFrameElement;
import elemental2.dom.HTMLFrameSetElement;
import elemental2.dom.HTMLHRElement;
import elemental2.dom.HTMLHeadElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLHtmlElement;
import elemental2.dom.HTMLIFrameElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLIsIndexElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLLegendElement;
import elemental2.dom.HTMLLinkElement;
import elemental2.dom.HTMLMapElement;
import elemental2.dom.HTMLMenuElement;
import elemental2.dom.HTMLMetaElement;
import elemental2.dom.HTMLMeterElement;
import elemental2.dom.HTMLModElement;
import elemental2.dom.HTMLOListElement;
import elemental2.dom.HTMLObjectElement;
import elemental2.dom.HTMLOptGroupElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLOutputElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLParamElement;
import elemental2.dom.HTMLPreElement;
import elemental2.dom.HTMLProgressElement;
import elemental2.dom.HTMLQuoteElement;
import elemental2.dom.HTMLScriptElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLSourceElement;
import elemental2.dom.HTMLStyleElement;
import elemental2.dom.HTMLTableCaptionElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableColElement;
import elemental2.dom.HTMLTableElement;
import elemental2.dom.HTMLTableRowElement;
import elemental2.dom.HTMLTableSectionElement;
import elemental2.dom.HTMLTemplateElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.HTMLTitleElement;
import elemental2.dom.HTMLTrackElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.HTMLVideoElement;

/**
 * @author Tiago Bento <tfernand@redhat.com>
 */
class Elemental2TagMapping {

  private static final Multimap<Class<?>, String> TAG_NAMES_BY_DOM_INTERFACE;

  static Collection<String> getTags(final Class<?> elemental2ElementClass) {

    if (elemental2ElementClass == null || Element.class.equals(elemental2ElementClass)) {
      return Collections.emptyList();
    }

    final Collection<String> tags = TAG_NAMES_BY_DOM_INTERFACE.get(elemental2ElementClass);

    if (tags.isEmpty()) {
      return getTags(elemental2ElementClass.getSuperclass());
    }

    return tags;
  }

  static {
    TAG_NAMES_BY_DOM_INTERFACE = ImmutableMultimap.<Class<?>, String>builder()

            .put(HTMLAnchorElement.class, "a")
            .put(HTMLAppletElement.class, "applet")
            .put(HTMLAreaElement.class, "area")
            .put(HTMLAudioElement.class, "audio")
            .put(HTMLBaseElement.class, "base")
            .put(HTMLBaseFontElement.class, "basefont")
            .put(HTMLBodyElement.class, "body")
            .put(HTMLBRElement.class, "br")
            .put(HTMLButtonElement.class, "button")
            .put(HTMLCanvasElement.class, "canvas")
            .put(HTMLDataListElement.class, "datalist")
            .put(HTMLDetailsElement.class, "details")
            .put(HTMLDialogElement.class, "dialog")
            .put(HTMLDirectoryElement.class, "dir")
            .put(HTMLDivElement.class, "div")
            .put(HTMLDListElement.class, "dl")
            .put(HTMLEmbedElement.class, "embed")
            .put(HTMLFieldSetElement.class, "fieldset")
            .put(HTMLFontElement.class, "font")
            .put(HTMLFormElement.class, "form")
            .put(HTMLFrameElement.class, "frame")
            .put(HTMLFrameSetElement.class, "frameset")
            .put(HTMLHeadElement.class, "head")
            .put(HTMLHeadingElement.class, "h1")
            .put(HTMLHeadingElement.class, "h2")
            .put(HTMLHeadingElement.class, "h3")
            .put(HTMLHeadingElement.class, "h4")
            .put(HTMLHeadingElement.class, "h5")
            .put(HTMLHeadingElement.class, "h6")
            .put(HTMLHRElement.class, "hr")
            .put(HTMLHtmlElement.class, "html")
            .put(HTMLIFrameElement.class, "iframe")
            .put(HTMLImageElement.class, "img")
            .put(HTMLInputElement.class, "input")
            .put(HTMLIsIndexElement.class, "isindex")
            .put(HTMLLabelElement.class, "label")
            .put(HTMLLegendElement.class, "legend")
            .put(HTMLLIElement.class, "li")
            .put(HTMLLinkElement.class, "link")
            .put(HTMLMapElement.class, "map")
            .put(HTMLMenuElement.class, "menu")
            .put(HTMLMetaElement.class, "meta")
            .put(HTMLMeterElement.class, "meter")
            .put(HTMLModElement.class, "del")
            .put(HTMLModElement.class, "ins")
            .put(HTMLObjectElement.class, "object")
            .put(HTMLOListElement.class, "ol")
            .put(HTMLOptGroupElement.class, "optgroup")
            .put(HTMLOptionElement.class, "option")
            .put(HTMLOutputElement.class, "output")
            .put(HTMLParagraphElement.class, "p")
            .put(HTMLParamElement.class, "param")
            .put(HTMLPreElement.class, "pre")
            .put(HTMLProgressElement.class, "progress")
            .put(HTMLQuoteElement.class, "blockquote")
            .put(HTMLQuoteElement.class, "q")
            .put(HTMLScriptElement.class, "script")
            .put(HTMLSelectElement.class, "select")
            .put(HTMLSourceElement.class, "source")
            .put(HTMLStyleElement.class, "style")
            .put(HTMLTableCaptionElement.class, "caption")
            .put(HTMLTableCellElement.class, "td")
            .put(HTMLTableCellElement.class, "th")
            .put(HTMLTableColElement.class, "col")
            .put(HTMLTableColElement.class, "colgroup")
            .put(HTMLTableElement.class, "table")
            .put(HTMLTableRowElement.class, "tr")
            .put(HTMLTableSectionElement.class, "tbody")
            .put(HTMLTableSectionElement.class, "tfoot")
            .put(HTMLTableSectionElement.class, "thead")
            .put(HTMLTemplateElement.class, "template")
            .put(HTMLTextAreaElement.class, "textarea")
            .put(HTMLTitleElement.class, "title")
            .put(HTMLTrackElement.class, "track")
            .put(HTMLUListElement.class, "ul")
            .put(HTMLVideoElement.class, "video")
            .build();
  }
}
