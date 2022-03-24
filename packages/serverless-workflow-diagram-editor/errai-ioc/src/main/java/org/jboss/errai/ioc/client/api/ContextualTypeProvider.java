/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.client.api;

import java.lang.annotation.Annotation;

/**
 * A contextual type provider is a provider which receives contextual information about the injection point which
 * is being satisfied with this provider. The provider provides two pieces of contextual information: the raw class
 * values of the most outer type arguments (if present) of the type of the injection point, as well as any
 * qualifiers at that injection point.
 *
 * @param <T> the type provided by this class
 * @see IOCProvider
 */
public interface ContextualTypeProvider<T> {
  /**
   * Called to provide an instance of the type provided for by this type provider. This method accepts two arguments
   * which are provided by the container at runtime, describing the type arguments and qualifiers at the injection
   * point.
   *
   * @param typeargs   the raw class values of the outer-most type arguments. For example, if the injection point
   *                   is of the type <tt>Map&lt;String, List&lt;? extends Number&lt;&gt;&gt;</tt> then the values
   *                   passed to this argument will be <tt>[String.class, List.class]</tt>.
   *
   * @param qualifiers and array of qualifiers at the injection point.
   *
   * @return the type produced by this provider.
   */
  public T provide(Class<?>[] typeargs, Annotation[] qualifiers);
}
