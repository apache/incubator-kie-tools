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
import jsinterop.base.Js;

public class Matcher {
   Pattern pattern;
   String s;
   
   Matcher(Pattern pattern, String s) {
	   this.pattern = pattern;
	   this.s = s;
   }
   
   public boolean matches() {
	   return matchesImpl(s, pattern);
   }
   
   private static boolean matchesImpl(String s, Pattern p) {
       return Js.<JsRegExp>uncheckedCast(p).test(s);
   } /*-{
	 return s.matches(p);
   }-*/;
}
