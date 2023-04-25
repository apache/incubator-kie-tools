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
import elemental2.core.RegExpResult;

public class Matcher {
  private final JsRegExp regExp;
  private final String input;
  private final RegExpResult matchResult;

  Matcher(Pattern pattern, CharSequence input) {
    this.regExp = pattern.regExp;
    this.input = String.valueOf(input);
    matchResult = regExp.exec(this.input);
  }

  public boolean find() {
    return regExp.test(input);
  }

  public boolean matches() {
    return regExp.test(input);
  }

  public String group(int group) {
    return matchResult.getAt(group);
  }

  public String group(String group) {
    return matchResult.groups.get(group);
  }

  public static String quoteReplacement(String s) {
    if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1)) return s;
    StringBuilder sb = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '\\' || c == '$') {
        sb.append('\\');
      }
      sb.append(c);
    }
    return sb.toString();
  }
}
