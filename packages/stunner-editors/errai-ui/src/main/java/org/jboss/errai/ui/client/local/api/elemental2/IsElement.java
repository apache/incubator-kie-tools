/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.client.local.api.elemental2;

import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * This interface provides a default implementation of {@link org.jboss.errai.common.client.api.elemental2.IsElement#getElement()} for {@link Templated} beans.
 *
 * @author Tiago Bento <tfernand@redhat.com>
 */
public interface IsElement extends org.jboss.errai.common.client.api.elemental2.IsElement {

  @Override
  default HTMLElement getElement() {
    try {
      return Js.cast(TemplateWidgetMapper.get(this).getElement());
    } catch (final Throwable t) {
      throw new RuntimeException(
              "An error occurred while invoking getElement."
                      + " This interface should only be extended by @Templated beans and cannot be invoked before a bean is in service.",
              t);
    }
  }

}
