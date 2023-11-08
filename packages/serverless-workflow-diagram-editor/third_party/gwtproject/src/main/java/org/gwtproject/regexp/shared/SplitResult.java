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

public interface SplitResult {

  /**
   * Returns one the strings split off.
   *
   * @param index the index of the string to be returned.
   * @return The index'th string resulting from the split.
   */
  String get(int index);

  /**
   * Returns the number of strings split off.
   *
   * @return the length
   */
  int length();

  /**
   * Sets (overrides) one of the strings split off.
   *
   * @param index the index of the string to be set.
   * @param value the string to be set
   */
  void set(int index, String value);
}
