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

package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;

/**
 * A simple comparator interface used by the {@link QualifierEqualityFactory} generator to create comparators for
 * testing the equality of qualifiers at runtime.
 * <p>
 * NOTE: This interface is only for tested the attribute-equality of like-typed annotations. For instance, you can
 * only compare an annotation of type <tt>Foo</tt> with another annotation of type <tt>Foo</tt>. You cannot
 * compare the equality of <tt>Foo</tt> with <tt>Bar</tt>. Attempting to do so will simply result in a
 * <tt>ClassCastException</tt>.
 *
 * @author Mike Brock
 */
public interface AnnotationComparator<T extends Annotation> {
  /**
   * Tests the equality of two qualifiers. Does not accept null values.
   *
   * @param a1 an annotation to be compared. cannot be null.
   * @param a2 an annotation to be compared. cannot be null.
   * @return true if the annotations match.
   */
  public boolean isEqual(T a1, T a2);

  /**
   * Creates a consistent hashCode based on the attribute values of the annotation.
   *
   * @param a1 the annotation to have a hashCode generated for. cannot be null.
   * @return a consistent hash code.
   */
  public int hashCodeOf(T a1);
}
