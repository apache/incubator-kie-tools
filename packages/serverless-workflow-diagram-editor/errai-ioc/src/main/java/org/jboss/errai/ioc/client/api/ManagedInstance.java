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

package org.jboss.errai.ioc.client.api;

import java.lang.annotation.Annotation;

import javax.enterprise.context.Dependent;
import javax.inject.Provider;

import org.jboss.errai.ioc.client.container.ClientBeanManager;

/**
 * This type is like {@code javax.inject.Instance} but with automatic life-cycle management.
 *
 * Using an {@code Instance<T>} or the {@link ClientBeanManager} directly requires the caller to manually destroy
 * created instances of {@link Dependent} scoped beans (or else risk a memory leak).
 *
 * A {@code ManagedInstance<T>} holds references to every {@link Dependent} scoped bean it creates. When
 * {@link #destroyAll()} is called, any {@link Dependent} scope instances created by the {@code ManagedInstance<T>} are
 * destroyed.
 *
 * When a {@code ManagedInstance<T>} is injected, its {@link #destroyAll()} is automatically called when the bean
 * instance in which it is injected is destroyed. In other words, the dynamically created {@link Dependent} scoped
 * instances from a {@code ManagedInstance<T>} have life-cycles bound to the bean that injected the
 * {@code ManagedInstance<T>}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface ManagedInstance<T> extends Provider<T>, Iterable<T> {

  /**
   * <p>
   * Obtains a child <tt>ManagedInstance</tt> for the given additional required qualifiers.
   * </p>
   * <p>
   * For {@link Dependent} beans that are created by a child <tt>ManagedInstance</tt> they will be destroyed when:
   * </p>
   * <ul>
   * <li>{@link #destroyAll()} is called on the child <tt>ManagedInstance</tt>
   * <li>{@link #destroyAll()} is called on the parent <tt>ManagedInstance</tt>
   * </ul>
   * <p>
   * Calling {@link #destroyAll()} on a child <tt>ManagedInstance</tt> does <b>not</b> destroy instances created by the
   * parent.
   * </p>
   *
   * @param qualifiers
   *          the additional required qualifiers
   * @return the child <tt>ManagedInstance</tt>
   * @throws IllegalArgumentException
   *           if passed two instances of the same qualifier type, or an instance of an annotation that is not a
   *           qualifier type
   */
  public ManagedInstance<T> select(Annotation... qualifiers);

  /**
   * <p>
   * Obtains a child <tt>Instance</tt> for the given required type and additional required qualifiers.
   * </p>
   * <p>
   * For {@link Dependent} beans that are created by a child <tt>ManagedInstance</tt> they will be destroyed when:
   * </p>
   * <ul>
   * <li>{@link #destroyAll()} is called on the child <tt>ManagedInstance</tt>
   * <li>{@link #destroyAll()} is called on the parent <tt>ManagedInstance</tt>
   * </ul>
   * <p>
   * Calling {@link #destroyAll()} on a child <tt>ManagedInstance</tt> does <b>not</b> destroy instances created by the
   * parent.
   * </p>
   *
   * @param <U>
   *          the required type
   * @param subtype
   *          a {@link java.lang.Class} representing the required type
   * @param qualifiers
   *          the additional required qualifiers
   * @return the child <tt>Instance</tt>
   * @throws IllegalArgumentException
   *           if passed two instances of the same qualifier type, or an instance of an annotation that is not a
   *           qualifier type
   */
  public <U extends T> ManagedInstance<U> select(Class<U> subtype, Annotation... qualifiers);

  /**
   * <p>
   * Determines if there is no bean that matches the required type and qualifiers and is eligible for injection into the
   * class into which the parent <tt>Instance</tt> was injected.
   * </p>
   *
   * @return <tt>true</tt> if there is no bean that matches the required type and qualifiers and is eligible for
   *         injection into the class into which the parent <tt>Instance</tt> was injected, or <tt>false</tt> otherwise.
   */
  public boolean isUnsatisfied();

  /**
   * <p>
   * Determines if there is more than one bean that matches the required type and qualifiers and is eligible for
   * injection into the class into which the parent <tt>Instance</tt> was injected.
   * </p>
   *
   * @return <tt>true</tt> if there is more than one bean that matches the required type and qualifiers and is eligible
   *         for injection into the class into which the parent <tt>Instance</tt> was injected, or <tt>false</tt>
   *         otherwise.
   */
  public boolean isAmbiguous();

  /**
   * <p>
   * Destroys a {@link Dependent} scoped bean instance created by this <tt>ManagedInstance</tt>. If the argument of this
   * method is not a {@link Dependent} scoped bean created by this <tt>ManagedInstance</tt> then this method is a no-op.
   * </p>
   *
   * @param A {@link Dependent} scoped bean instance created by this <tt>ManagedInstance</tt> (or else this method is a no-op).
   */
  public void destroy(T instance);

  /**
   * <p>
   * Destroy every {@link Dependent} scoped bean instance created by this and any child <tt>ManagedInstances</tt>.
   * </p>
   * <p>
   * For an injected <tt>ManagedInstance</tt> this method is automatically called when the bean instance in which it is
   * injected is destroyed.
   * </p>
   */
  public void destroyAll();

}
