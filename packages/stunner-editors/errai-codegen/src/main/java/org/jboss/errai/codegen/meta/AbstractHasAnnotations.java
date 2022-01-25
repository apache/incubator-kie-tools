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

package org.jboss.errai.codegen.meta;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.Assert;

/**
 * Contains shared functionality by all implementations of
 * {@link HasAnnotations}.
 * 
 * @author Christian Sadilek<csadilek@redhat.com>
 */
public abstract class AbstractHasAnnotations implements HasAnnotations {

  private Set<String> annotationPresentCache = null;

  /**
   * Checks if the provided annotation is present on this element (type, method,
   * field or parameter).
   * 
   * @param annotation
   *          the annotation type, must not be null.
   * @return true if annotation is present, otherwise false.
   */
  @Override
  public boolean isAnnotationPresent(final Class<? extends Annotation> annotation) {
    Assert.notNull(annotation);
    if (annotationPresentCache == null) {
      annotationPresentCache = new HashSet<String>();
      for (final Annotation a : getAnnotations()) {
        annotationPresentCache.add(a.annotationType().getName());
      }
    }

    return annotationPresentCache.contains(annotation.getName());
  }
}
