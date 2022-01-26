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

package org.jboss.errai.common.client.api;

/**
 * Non-instantiable utility for self-checking code.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Mike Brock
 */
public class Assert {

  private Assert() {}

  /**
   * Throws NullPointerException if the value is null with an error message.
   *
   * @param <V>
   * @param errorMessage an error message to be displayed as part of the NullPointerException thrown.
   * @param value the value that must not be null
   * @throws NullPointerException if value is null.
   * @return
   */
  public static <V> V notNull(final String errorMessage, final V value) {
    if (value == null) {
      throw new NullPointerException(errorMessage);
    }
    return value;
  }

  /**
   * Throws NullPointerException if the value is null.
   * 
   * @param <V>
   * @param value the value that must not be null
   * @throws NullPointerException if value is null.
   * @return
   */
  public static <V> V notNull(final V value) {
    if (value == null) {
      throw new NullPointerException();
    }
    return value;
  }
}
