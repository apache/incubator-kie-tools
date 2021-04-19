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

package org.jboss.errai.ioc.client;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@JsType
public class JsArray<T> {

  @JsIgnore
  public static <T> Iterable<T> iterable(final JsArray<T> array) {
    return () -> {
      return new Iterator<T>() {
        private int i = 0;
        @Override
        public boolean hasNext() {
          return i < array.length();
        }

        @Override
        public T next() {
          if (hasNext()) {
            return array.get(i++);
          }
          else {
            throw new NoSuchElementException();
          }
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    };
  }

  private final T[] wrapped;

  @JsIgnore
  public JsArray(final T[] wrapped) {
    this.wrapped = wrapped;
  }

  public int length() {
    return wrapped.length;
  }

  public T get(int index) {
    return wrapped[index];
  }

  public void set(int index, T value) {
    wrapped[index] = value;
  }

}
