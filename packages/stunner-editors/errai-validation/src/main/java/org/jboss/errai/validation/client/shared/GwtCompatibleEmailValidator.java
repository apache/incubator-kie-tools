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


package org.jboss.errai.validation.client.shared;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.gwt.regexp.shared.RegExp;

/**
 * Implements the same validation semantics as the Hibernate email validator,
 * but uses the GWT regular expressions API so the validation can succeed on the
 * client as well as the server.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class GwtCompatibleEmailValidator implements ConstraintValidator<GwtCompatibleEmail, String> {

  private static String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
  private static String DOMAIN = ATOM + "+(\\." + ATOM + "+)*";
  private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

  private RegExp pattern = RegExp.compile(
      "^" + ATOM + "+(\\." + ATOM + "+)*@("
          + DOMAIN
          + "|"
          + IP_DOMAIN
          + ")$",
          "i"
      );

  @Override
  public void initialize(GwtCompatibleEmail constraintAnnotation) {
    // no op
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.length() == 0) {
      return true;
    }
    return pattern.test(value);
  }

}
