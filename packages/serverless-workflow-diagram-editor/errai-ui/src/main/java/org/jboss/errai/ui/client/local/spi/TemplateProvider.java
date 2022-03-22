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

package org.jboss.errai.ui.client.local.spi;

import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * Implementations of this type are IOC-managed beans that can supply templates
 * at runtime (see {@link Templated#provider()}).
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface TemplateProvider {

  /**
   * Constructs a template at runtime using the provided location and passes the
   * template's content to the provided rendering callback. Synchronous and
   * asynchronous implementations are supported.
   * 
   * @param location
   *          The location of the template (i.e. a URL or path to a file), must
   *          not be null.
   * @param renderingCallback
   *          The callback that will cause the template to get rendered, must
   *          not be null.
   */
  public void provideTemplate(final String location, final TemplateRenderingCallback renderingCallback);
}
