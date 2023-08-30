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
public class DateTimeInputConverter extends AbstractDateInputConverter {

  private static final RegExp dateTimePattern = RegExp.compile("^\\d{3}\\d+-\\d{2}-\\d{2}(T\\d{2}:\\d{2}(:\\d{2}(\\.\\d{3})?)?)?");

  @Override
  public String toWidgetValue(final Date modelValue) {
    final String iso = toISOString(modelValue);
    final MatchResult result = dateTimePattern.exec(iso);

    if (result == null) {
      return "";
    }
    else {
      return result.getGroup(0);
    }
  }

}
