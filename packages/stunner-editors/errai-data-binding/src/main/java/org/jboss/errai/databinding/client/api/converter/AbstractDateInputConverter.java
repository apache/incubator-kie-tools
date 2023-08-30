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

import com.google.gwt.core.client.JsDate;
import org.jboss.errai.databinding.client.api.Converter;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class AbstractDateInputConverter implements Converter<Date, String> {

  @Override
  public Class<Date> getModelType() {
    return Date.class;
  }

  @Override
  public Class<String> getComponentType() {
    return String.class;
  }

  @Override
  public Date toModelValue(final String widgetValue) {
    if (widgetValue == null || "".equals(widgetValue)) {
      return null;
    }

    final Double time = JsDate.parse(widgetValue);
    return new Date(time.longValue());
  }

  protected static native String toISOString(final JsDate jsDate) /*-{
    return jsDate.toISOString();
  }-*/;

  protected static JsDate toJsDate(final Date date) {
    final Long time = date.getTime();
    return JsDate.create(time.doubleValue());
  }

  protected static String toISOString(final Date modelValue) {
    final JsDate jsDate = toJsDate(modelValue);
    final String iso = toISOString(jsDate);
    return iso;
  }

}
