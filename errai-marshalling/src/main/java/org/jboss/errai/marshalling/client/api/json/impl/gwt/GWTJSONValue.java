/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.marshalling.client.api.json.impl.gwt;

import com.google.gwt.json.client.*;
import org.jboss.errai.marshalling.client.api.json.*;

/**
 * @author Mike Brock
 */
public class GWTJSONValue implements EJValue {
  final JSONValue value;

  public GWTJSONValue(final JSONValue value) {
    this.value = value;
  }

  @Override
  public EJArray isArray() {
    if (value.isArray() == null) {
      return null;
    }
    else {
      return new GWTJSONArray(value.isArray());
    }
  }

  @Override
  public EJNumber isNumber() {
    if (value.isNumber() == null) {
      return null;
    }
    else {
      return new GWTJSONNumber(value.isNumber());
    }
  }

  @Override
  public boolean isNull() {
    return value == null || value.isNull() != null;
  }

  @Override
  public EJObject isObject() {
    if (value.isObject() == null) {
      return null;
    }
    else {
      return new GWTJSONObject(value.isObject());
    }
  }

  @Override
  public EJBoolean isBoolean() {
    if (value.isBoolean() == null) {
      return null;
    }
    else {
      return new GWTJSONBoolean(value.isBoolean());
    }
  }

  @Override
  public EJString isString() {
    if (value.isString() == null) {
      return null;
    }
    else {
      return new GWTJSONString(value.isString());
    }
  }

  @Override
  public Object getRawValue() {
    return value;
  }
}
