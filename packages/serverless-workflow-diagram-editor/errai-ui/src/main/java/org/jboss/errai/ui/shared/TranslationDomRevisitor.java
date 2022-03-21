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

import java.util.Stack;

import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.w3c.dom.Element;

/**
 * Visit DOM elements and translate sub-trees which are Errai {@link Templated}.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class TranslationDomRevisitor implements DomRevisitor {
  /*
   * Outline:
   * 
   * Root nodes of templates are marked with 'data-i18n-prefix' attribute, allowing translation
   * values to be looked up. Use a stack to keep track of which template we are in. After visiting,
   * if the top of the stack matches an element prefix attribute, we are leaving that template.
   */

  private TemplateTranslationVisitor visitor = new TemplateTranslationVisitor("");
  private final Stack<String> prefixes = new Stack<String>();
  private static final String PREFIX = "data-i18n-prefix";

  @Override
  public boolean visit(Element element) {
    if (visitor.hasAttribute(element, PREFIX))
      prefixes.push(element.getAttribute(PREFIX));

    if (prefixes.empty())
      return !visitor.isTextOnly(element);

    visitor.setI18nPrefix(prefixes.peek());
    return visitor.visit(element);
  }

  @Override
  public void afterVisit(Element element) {
    if (visitor.hasAttribute(element, PREFIX) && element.getAttribute(PREFIX).equals(prefixes.peek()))
      prefixes.pop();
  }
}
