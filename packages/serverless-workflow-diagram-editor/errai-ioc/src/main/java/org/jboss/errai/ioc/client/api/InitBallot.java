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

/**
 * An <tt>InitBallot</tt> is injected by the container into a bean which declares a dependency on it. By doing so, the bean
 * enjoins itself into the startup contract of the framework services. Thus, it is expected to call {@link #voteForInit()}
 * to indicate that it is okay to proceed with initialization. The class must be parameterized with a class which
 * represents the startup dependency.
 * <p>
 * The container imposes a fixed amount of time for which there must be a call to {@link #voteForInit()}. If the call
 * is not made within this time period, then the dependency will be deemed unsatisified and the container will produce
 * an error.
 *
 * @param <T> A type reference to be used to uniquely identify the dependency. This is typically the same class
 *           which declares the <tt>InitBallot</tt>, but not necessarily. If more than component injects an
 *           <tt>InitBallot</tt> with the same type parameter, they will all represent the same dependency.
 * @author Mike Brock
 */
public interface InitBallot<T> {
  /**
   * When called, the class's lock on the startup procedure for framework services is released.
   */
  public void voteForInit();
}
