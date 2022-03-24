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

package org.jboss.errai.ioc.client.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.errai.ioc.client.container.Context;

/**
 * This annotation is used to declare that an implementation of {@link Context}
 * should be used to handle some given set of scopes.
 *
 * At runtime, a bean will be assigned to the scope of the {@link Context}
 * implementation that contained the bean's scope annotation in the context's
 * {@link ScopeContext#value()}.
 *
 *@see Context
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScopeContext {

  /**
   * @return An array of scope annotations for which the annotated
   *         {@link Context} implementation is responsible.
   */
  Class<? extends Annotation>[] value();

}
