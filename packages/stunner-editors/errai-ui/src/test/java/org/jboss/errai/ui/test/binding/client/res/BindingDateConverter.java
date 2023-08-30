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


package org.jboss.errai.ui.test.binding.client.res;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.jboss.errai.databinding.client.api.Converter;

/**
 * Converter for testing purposes.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class BindingDateConverter implements Converter<Date, String> {

  public static final Date TEST_DATE;

  static {
    TEST_DATE = DateTimeFormat.getFormat("yyyy/MM/dd").parse("1980/22/06");
  }

  @Override
  public Date toModelValue(String widgetValue) {
    return TEST_DATE;
  }

  @Override
  public String toWidgetValue(Date modelValue) {
    return "testdate";
  }

  @Override
  public Class<Date> getModelType() {
    return Date.class;
  }

  @Override
  public Class<String> getComponentType() {
    return String.class;
  }

}
