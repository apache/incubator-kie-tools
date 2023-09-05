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
package org.gwtproject.regexp.client;

import elemental2.core.JsArray;
import org.gwtproject.regexp.shared.SplitResult;

/** GWT wrapper for Javascript string.split results. */
public class NativeSplitResult implements SplitResult {

  private JsArray<String> jsArray;

  protected NativeSplitResult(JsArray<String> result) {
    jsArray = result;
  }

  @Override
  public String get(int index) {
    return jsArray.getAt(index);
  }

  @Override
  public int length() {
    return jsArray.length;
  }

  @Override
  public void set(int index, String value) {
    jsArray.setAt(index, value);
  }
}
