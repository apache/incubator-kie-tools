/**
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ui.client.local.producer;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.IOCProvider;

import javax.inject.Named;
import java.lang.annotation.Annotation;

/**
 * @author Tiago Bento <tfernand@redhat.com>
 */
@IOCProvider
public class Elemental2HTMLElementProvider implements ContextualTypeProvider<HTMLElement> {

  @Override
  public HTMLElement provide(final Class<?>[] classes, final Annotation[] qualifiers) {

    for (final Annotation annotation : qualifiers) {
      if (annotation.annotationType().equals(Named.class)) {
        final String tagName = ((Named) annotation).value();

        try {
          return (HTMLElement) DomGlobal.document.createElement(tagName);
        } catch (Throwable t) {
          throw new RuntimeException(
                  "An error occurred while attempting to create an element with the tag name [" + tagName + "].", t);
        }
      }
    }

    throw new RuntimeException(
            "Cannot provide an HTMLELement for an injection point of HTMLElement without a @Named qualifier specifying the tag name.");
  }

}
