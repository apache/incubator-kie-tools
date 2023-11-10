/*
 * Copyright © 2019 The GWT Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtproject.regexp.shared;

/**
 * Factory class for creating RegExp implementation depends on the scope of the execution whether
 * client side or server side
 */
class RegExpFactory {

  RegExp compile(String pattern) {
    throw new UnsupportedOperationException();
  }

  RegExp compile(String pattern, String flags) {
    throw new UnsupportedOperationException();
  }

  String quote(String input) {
    throw new UnsupportedOperationException();
  }
}
