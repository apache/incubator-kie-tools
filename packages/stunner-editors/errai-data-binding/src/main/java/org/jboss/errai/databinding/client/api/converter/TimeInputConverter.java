/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.databinding.client.api.converter;

import java.util.Date;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class TimeInputConverter extends AbstractDateInputConverter {

  private static final RegExp timeRegex = RegExp.compile("(\\d{2}):(?:(\\d{2}))(?::(\\d{2})(?:.(\\d{3}))?)?");

  @Override
  public Date toModelValue(final String widgetValue) {
    if (widgetValue == null || "".equals(widgetValue)) {
      return null;
    }

    final MatchResult result = timeRegex.exec(widgetValue);

    if (result == null) {
      return null;
    }
    else {
      final int
        hrs = Integer.valueOf(result.getGroup(1)),
        min = Integer.valueOf(result.getGroup(2)),
        sec = (result.getGroupCount() > 3 ? Integer.valueOf(result.getGroup(3)) : 0),
        ms = (result.getGroupCount() > 4 ? Integer.valueOf(result.getGroup(4)) : 0);

      final Date modelValue = new Date(0, 0, 0, hrs, min, sec);
      modelValue.setTime(modelValue.getTime() + ms);

      return modelValue;
    }
  }

  @Override
  public String toWidgetValue(final Date modelValue) {
    if (modelValue == null) {
      return "";
    }

    final String
      hrs = asPaddedString(modelValue.getHours(), 2),
      min = asPaddedString(modelValue.getMinutes(), 2),
      sec = asPaddedString(modelValue.getSeconds(), 2),
      ms = asPaddedString((int) (modelValue.getTime() % 1000), 3);

    return hrs + ":" + min + ":" + sec + "." + ms;
  }

  private static String asPaddedString(final int number, int digits) {
    String retVal = String.valueOf(number);
    if (retVal.length() < digits) {
      retVal = "0" + retVal;
    }

    return retVal;
  }

}
