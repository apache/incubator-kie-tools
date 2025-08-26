/*
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

package org.kie.workbench.common.stunner.client.lienzo;

import java.util.function.Consumer;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLStyleElement;
import jsinterop.annotations.JsFunction;

public class StyleInjector {

  public HTMLDocument document = DomGlobal.document;

  private final HTMLElement styleElement;

  private StyleInjector(HTMLElement styleElement) {
    this.styleElement = styleElement;
  }

  public static StyleInjector fromString(String contents) {
    return fromString(contents, null);
  }

  public static StyleInjector fromString(String contents, Consumer<HTMLElement> consumer) {
    HTMLElement element = createHTMLStyleElement();
    element.textContent = contents;
    if(consumer != null) {
      consumer.accept(element);
    }
    return new StyleInjector(element);
  }

  private static HTMLElement createHTMLStyleElement() {
    HTMLStyleElement style = (HTMLStyleElement) DomGlobal.document.createElement("style");
    style.type = "text/css";
    return style;
  }

  private static HTMLElement createStyleElement(
      HTMLElement style, Callback onResolve, Callback onReject) {
    if (onResolve != null) {
      style.onload = (e) -> onResolve.accept(style);
    }
    if (onReject != null) {
      style.onerror =
          (e) -> {
            onReject.accept(style);
            return null;
          };
    }
    return style;
  }

  public static StyleInjector fromUrl(String url) {
    return fromUrl(url, null);
  }

  public static StyleInjector fromUrl(String url, Callback onResolve) {
    return fromUrl(url, onResolve, null);
  }

  public static StyleInjector fromUrl(String url, Callback onResolve, Callback onReject) {
    HTMLElement element = createHTMLLinkElement(onResolve, onReject);
    element.setAttribute("href", url);
    return new StyleInjector(element);
  }

  private static HTMLElement createHTMLLinkElement(Callback onResolve, Callback onReject) {
    HTMLElement style = (HTMLElement) DomGlobal.document.createElement("link");
    style.setAttribute("rel", "stylesheet");
    return createStyleElement(style, onResolve, onReject);
  }

  public StyleInjector setDocument(HTMLDocument document) {
    this.document = document;
    return this;
  }

  public void inject() {
    document.head.appendChild(styleElement);
  }

  public void injectAtStart() {
    document.head.insertBefore(styleElement, DomGlobal.document.head.firstChild);
  }

  @JsFunction
  @FunctionalInterface
  public interface Callback {
    void accept(HTMLElement script);
  }
}
