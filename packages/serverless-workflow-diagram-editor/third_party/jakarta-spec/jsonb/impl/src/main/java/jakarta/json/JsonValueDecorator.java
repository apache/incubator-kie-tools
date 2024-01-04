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

package jakarta.json;

import java.math.BigDecimal;
import java.math.BigInteger;

import elemental2.core.JsBoolean;
import elemental2.core.JsNumber;
import jakarta.json.stream.gwt.JsonObjectImpl;
import jakarta.json.stream.gwt.JsonValueImpl;
import jsinterop.base.Js;

public class JsonValueDecorator {

  JreDecorator decorator = new JreDecorator();

  public JsonValueDecorator(Object object) {
    decorator.setDelegate(object);
  }

  public Integer asInteger() {
    return decorator.getInteger();
  }

  public Double asDouble() {
    return decorator.getDouble();
  }

  public Float asFloat() {
    return decorator.getFloat();
  }

  public Long asLong() {
    return decorator.getLong();
  }

  public Byte asByte() {
    return decorator.getByte();
  }

  public Short asShort() {
    return decorator.getShort();
  }

  public Character asCharacter() {
    return decorator.getCharacter();
  }

  public String asString() {
    return decorator.getString();
  }

  public Boolean asBoolean() {
    return decorator.getBoolean();
  }

  public BigInteger asBigInteger() {
    return decorator.getBigInteger();
  }

  public BigDecimal asBigDecimal() {
    return decorator.getBigDecimal();
  }

  private static class GWTDecorator {

    private JsonObjectImpl delegate;

    public GWTDecorator setDelegate(Object delegate) {
      if (delegate instanceof jakarta.json.stream.gwt.JsonValueImpl) {
        this.delegate = (JsonObjectImpl) ((JsonValueImpl) delegate).asJsonObject();
      } else {
        this.delegate = (JsonObjectImpl) delegate;
      }
      return this;
    };

    public Integer getInteger() {
      return Double.valueOf(Js.<JsNumber>uncheckedCast(delegate).valueOf()).intValue();
    }

    public Double getDouble() {
      return new JsNumber(delegate).valueOf();
    }

    public Float getFloat() {
      return Double.valueOf(new JsNumber(delegate).valueOf()).floatValue();
    }

    public Long getLong() {
      return Double.valueOf(new JsNumber(delegate).valueOf()).longValue();
    }

    public Byte getByte() {
      return Double.valueOf(new JsNumber(delegate).valueOf()).byteValue();
    }

    public Short getShort() {
      return (short) Double.valueOf(new JsNumber(delegate).valueOf()).intValue();
    }

    public Character getCharacter() {
      return (char) Double.valueOf(new JsNumber(delegate).valueOf()).intValue();
    }

    public String getString() {
      if (delegate == null) {
        return null;
      }
      return Js.asString(delegate.__holder__);
    }

    public Boolean getBoolean() {
      return new JsBoolean(delegate.__holder__).valueOf();
    }

    public BigInteger getBigInteger() {
      return BigDecimal.valueOf(new JsNumber(delegate).valueOf()).toBigInteger();
    }

    public BigDecimal getBigDecimal() {
      return BigDecimal.valueOf(new JsNumber(delegate).valueOf());
    }
  }

  private static class JreDecorator extends GWTDecorator {

    @GwtIncompatible private JsonValue delegate;

    @Override
    @GwtIncompatible
    public GWTDecorator setDelegate(Object delegate) {
      this.delegate = (JsonValue) delegate;
      return this;
    };

    @Override
    @GwtIncompatible
    public Integer getInteger() {
      return ((JsonNumber) delegate).intValue();
    }

    @Override
    @GwtIncompatible
    public Double getDouble() {
      return ((JsonNumber) delegate).doubleValue();
    }

    @Override
    @GwtIncompatible
    public Float getFloat() {
      return ((JsonNumber) delegate).numberValue().floatValue();
    }

    @Override
    @GwtIncompatible
    public Long getLong() {
      return ((JsonNumber) delegate).numberValue().longValue();
    }

    @Override
    @GwtIncompatible
    public Byte getByte() {
      return ((JsonNumber) delegate).numberValue().byteValue();
    }

    @Override
    @GwtIncompatible
    public Short getShort() {
      return (short) ((JsonNumber) delegate).intValue();
    }

    @Override
    @GwtIncompatible
    public Character getCharacter() {
      return (char) ((JsonNumber) delegate).intValue();
    }

    @Override
    @GwtIncompatible
    public String getString() {
      if (delegate == null) {
        return null;
      }
      return ((JsonString) delegate).getString();
    }

    @Override
    @GwtIncompatible
    public Boolean getBoolean() {
      return delegate.getValueType().equals(JsonValue.ValueType.TRUE) ? true : false;
    }

    @Override
    @GwtIncompatible
    public BigInteger getBigInteger() {
      return ((JsonNumber) delegate).bigIntegerValue();
    }

    @Override
    @GwtIncompatible
    public BigDecimal getBigDecimal() {
      return ((JsonNumber) delegate).bigDecimalValue();
    }
  }
}
