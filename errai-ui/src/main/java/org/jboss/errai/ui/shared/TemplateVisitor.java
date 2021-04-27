/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.shared;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Visits the dom and finds elements that need translating.
 */
public class TemplateVisitor implements DomVisitor {
  private String i18nPrefix;
  private final Map<String, String> i18nValues = new HashMap<String, String>();

  public TemplateVisitor(String i18nPrefix) {
    this.i18nPrefix = i18nPrefix;
  }

  @Override
  public boolean visit(Element element) {
    // Developers can mark entire sections of the template as "do not translate"
    if ("dummy".equals(element.getAttribute("data-role"))) {
      return false;
    }
    // If the element either explicitly enables i18n (via an i18n key) or is a text-only
    // node, record it.
    if (hasAttribute(element, "data-i18n-key") || isTextOnly(element)) {
      visitElement(i18nPrefix, element);
      return false;
    }

    if (hasAttribute(element, "title")) {
      visitAttribute(i18nPrefix, element, "title");
    }
    if (hasAttribute(element, "placeholder")) {
      visitAttribute(i18nPrefix, element, "placeholder");
    }
    return true;
  }

  /**
   * Records the translation key/value for an element.
   * 
   * @param i18nKeyPrefix
   * @param element
   */
  protected void visitElement(String i18nKeyPrefix, Element element) {
    String translationKey = i18nKeyPrefix + getOrGenerateTranslationKey(element);
    String translationValue = getTextContent(element);
    i18nValues.put(translationKey, translationValue);
  }

  /**
   * Records the translation key/value for an attribute.
   * 
   * @param i18nKeyPrefix
   * @param element
   * @param attributeName
   */
  protected void visitAttribute(String i18nKeyPrefix, Element element, String attributeName) {
    String elementKey = getElementKey(element);
    // If we couldn't figure out a key for this thing, then just bail.
    if (elementKey == null || elementKey.trim().length() == 0) {
      return;
    }
    String translationKey = i18nKeyPrefix + elementKey;
    translationKey += "-" + attributeName;
    String translationValue = element.getAttribute(attributeName);
    i18nValues.put(translationKey, translationValue);
  }

  protected String getElementKey(Element element) {
    String elementKey;
    if (hasAttribute(element, "data-field")) {
      elementKey = element.getAttribute("data-field");
    }
    else if (hasAttribute(element, "id")) {
      elementKey = element.getAttribute("id");
    }
    else if (hasAttribute(element, "name")) {
      elementKey = element.getAttribute("name");
    }
    else {
      elementKey = getOrGenerateTranslationKey(element);
    }
    return elementKey;
  }

  /**
   * Gets a translation key associated with the given element. If no key attribute exists in this
   * element, generate and assign one.
   * 
   * @param element
   */
  protected String getOrGenerateTranslationKey(Element element) {
    String translationKey = null;
    String currentText = getTextContent(element);
    if (hasAttribute(element, "data-i18n-key")) {
      translationKey = element.getAttribute("data-i18n-key");
    }
    else {
      translationKey = currentText.replaceAll("[:\\s'\"]+", "_");
      if (translationKey.length() > 128) {
        translationKey = translationKey.substring(0, 128) + translationKey.hashCode();
      }
      element.setAttribute("data-i18n-key", translationKey);
    }
    return translationKey;
  }

  /**
   * Returns true if the given element has some text and no element children.
   * 
   * @param element
   */
  public boolean isTextOnly(Element element) {
    NodeList childNodes = element.getChildNodes();
    for (int idx = 0; idx < childNodes.getLength(); idx++) {
      Node item = childNodes.item(idx);
      // As soon as we hit an element, we can return false
      if (item.getNodeType() == Node.ELEMENT_NODE) {
        return false;
      }
    }
    String textContent = getTextContent(element);
    return (textContent != null) && (textContent.trim().length() > 0);
  }

  /**
   * Called to determine if an element has an attribute defined.
   * 
   * @param element
   * @param attributeName
   */
  public boolean hasAttribute(Element element, String attributeName) {
    String attribute = element.getAttribute(attributeName);
    return (attribute != null && attribute.trim().length() > 0);
  }

  /**
   * Gets the text content for the given element.
   * 
   * @param element
   */
  private String getTextContent(Element element) {
    StringBuilder text = new StringBuilder();
    NodeList childNodes = element.getChildNodes();
    boolean first = true;
    for (int idx = 0; idx < childNodes.getLength(); idx++) {
      Node item = childNodes.item(idx);
      if (item.getNodeType() == Node.TEXT_NODE) {
        if (first) {
          first = false;
        }
        else {
          text.append(" ");
        }
        text.append(item.getNodeValue());
      }
    }
    return text.toString();
  }

  public void setI18nPrefix(String i18nPrefix) {
    this.i18nPrefix = i18nPrefix;
  }

  public Map<String, String> getI18nValues() {
    return i18nValues;
  }
}
