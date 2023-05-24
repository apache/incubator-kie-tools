/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package java.util.regex;

import elemental2.core.JsRegExp;

public class Pattern {

  final JsRegExp regExp;

  private Pattern(String regExp) {
    this.regExp = new JsRegExp(regExp);
  }

  public static Pattern compile(String regex) {
    return new Pattern(regex);
  }

  public Matcher matcher(CharSequence input) {
    return new Matcher(this, input);
  }

  public static String quote(String s) {
    int slashEIndex = s.indexOf("\\E");
    if (slashEIndex == -1) return "\\Q" + s + "\\E";

    int lenHint = s.length();
    lenHint =
        (lenHint < Integer.MAX_VALUE - 8 - lenHint) ? (lenHint << 1) : (Integer.MAX_VALUE - 8);

    StringBuilder sb = new StringBuilder(lenHint);
    sb.append("\\Q");
    int current = 0;
    do {
      sb.append(s, current, slashEIndex).append("\\E\\\\E\\Q");
      current = slashEIndex + 2;
    } while ((slashEIndex = s.indexOf("\\E", current)) != -1);

    return sb.append(s, current, s.length()).append("\\E").toString();
  }
}
