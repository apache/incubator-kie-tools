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

package org.jboss.errai.ioc.client.container;

import java.lang.annotation.Annotation;

/**
 * A {@link Context} that supports creating of contextual instances.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface HasContextualInstanceSupport extends Context {

  /**
   * Create a contextual bean instance.
   *
   * @param factoryName
   *          The {@link FactoryHandle#getFactoryName() name} of the for the
   *          desired bean instance.
   * @param typeArgs
   *          An array of type arguments from the injection site.
   * @param qualifiers
   *          An array of qualifiers from the injection site.
   * @return An instance of a bean from the {@link Factory} with the given
   *         {@link FactoryHandle#getFactoryName() name}. This instance may or
   *         may not be {@link Proxy proxied}.
   */
  <T> T getContextualInstance(String factoryName, Class<?>[] typeArgs, Annotation[] qualifiers);

}
