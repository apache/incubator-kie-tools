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
package java.util;

import elemental2.core.JsDate;
import jsinterop.base.Js;

public final class Calendar {
	public static final int MONTH = 2;
	public static final int DAY_OF_MONTH = 5;
	
	public static final int HOUR_OF_DAY = 11;
	public static final int MINUTE = 12;
	public static final int SECOND = 13;
	
	protected Calendar() {
		
	}
	
	public static Calendar getInstance() {
		return Js.uncheckedCast(new JsDate());
	}
	
	public int get(int field) {
		switch(field) {
			case 2:
				return Js.<JsDate>uncheckedCast(this).getMonth();
			case 5:
				return Js.<JsDate>uncheckedCast(this).getDate();
			case 11:
				return Js.<JsDate>uncheckedCast(this).getHours();
			case 12:
				return Js.<JsDate>uncheckedCast(this).getMinutes();
			case 13:
				return Js.<JsDate>uncheckedCast(this).getSeconds();
			default:
				throw new UnsupportedOperationException("Unsupported Calendar field " + field);
		}
	}/*-{
		switch(field) {
		case 2:
		  return this.getMonth();
		case 5:
		  return this.getDate();
		case 11:
		  return this.getHours();
		case 12:
		  return this.getMinutes();
		case 13:
		  return this.getSeconds();
		default:
		  throw "Unsupported Calendar field " + field;
		}
	}-*/;
}