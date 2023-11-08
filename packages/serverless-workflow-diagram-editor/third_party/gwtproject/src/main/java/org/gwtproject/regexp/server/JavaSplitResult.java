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
package org.gwtproject.regexp.server;

import org.gwtproject.regexp.shared.GwtIncompatible;
import org.gwtproject.regexp.shared.SplitResult;

/** Pure Java implementation of a regular expression split result. */
@GwtIncompatible
public class JavaSplitResult implements SplitResult {

  private final String[] result;

  public JavaSplitResult(String[] result) {
    this.result = result;
  }

  @Override
  public String get(int index) {
    return result[index];
  }

  @Override
  public int length() {
    return result.length;
  }

  @Override
  public void set(int index, String value) {
    result[index] = value;
  }
}
