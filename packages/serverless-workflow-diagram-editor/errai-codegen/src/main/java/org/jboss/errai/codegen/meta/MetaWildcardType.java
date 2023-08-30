/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.codegen.meta;

/**
 * Represents a wildcard type such as {@code ?}, {@code ? extends List} or
 * {@code ? super MyType}.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public interface MetaWildcardType extends MetaType {

  /**
   * Returns the Java source code representation of this wildcard, for example
   * "? extends java.util.Collection" or "? super org.xyz.Foo".
   */
  @Override
  public String getName();

  /**
   * Equivalent to {@link #getName()}.
   */
  @Override
  public String toString();

  /**
   * Returns the lower bounds of this wildcard type. Examples:
   * <ul>
   * <li>{@code <?>} has no lower bounds
   * <li>{@code <? extends List>} has no lower bounds
   * <li>{@code <? super List>} has a lower bound of <tt>{List}</tt>
   * <li>{@code <? super List & Futzable>} has a lower bound of
   * <tt>{List, Futzable}</tt>
   * </ul>
   *
   * @return The lower bounds of this wildcard type. The return value is never
   *         null--if the wildcard has no lower bounds, an empty array is
   *         returned.
   */
  public MetaType[] getLowerBounds();

  /**
   * Returns the upper bounds of this wildcard type. Examples:
   * <ul>
   * <li>{@code <?>} has an upper bound of {@code java.lang.Object}
   * <li>{@code <? extends List>} has an upper bound of <tt>{List}</tt>
   * <li>{@code <? super List>} has an upper bound of {@code java.lang.Object}
   * <li>{@code <? extends List & Futzable>} upper bounds of <tt>{List, Futzable}</tt>
   * </ul>
   *
   * @return The upper bounds of this wildcard type. The return value is never
   *         null--if the wildcard has no upper bounds, an empty array is
   *         returned.
   */
  public MetaType[] getUpperBounds();
}
