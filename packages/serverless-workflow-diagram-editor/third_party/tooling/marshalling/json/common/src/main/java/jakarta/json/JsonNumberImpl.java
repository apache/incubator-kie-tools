/*
 * Copyright © 2022 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jakarta.json;

import java.math.BigDecimal;
import java.math.BigInteger;

import elemental2.core.JsNumber;

public class JsonNumberImpl implements JsonNumber {

  private JsNumber holder;

  public JsonNumberImpl() {
    this(new JsNumber());
  }

  public JsonNumberImpl(Object holder) {
    this(new JsNumber(holder));
  }

  public JsonNumberImpl(JsNumber holder) {
    this.holder = holder;
  }

  @Override
  public boolean isIntegral() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int intValue() {
    return Integer.valueOf(holder.toString());
  }

  @Override
  public int intValueExact() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long longValue() {
    return Long.valueOf(holder.toString());
  }

  @Override
  public long longValueExact() {
    throw new UnsupportedOperationException();
  }

  public Number numberValue() {
    return Double.valueOf(new JsNumber(holder).valueOf());
  }

  @Override
  public BigInteger bigIntegerValue() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BigInteger bigIntegerValueExact() {
    throw new UnsupportedOperationException();
  }

  @Override
  public double doubleValue() {
    return holder.valueOf();
  }

  @Override
  public BigDecimal bigDecimalValue() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ValueType getValueType() {
    return ValueType.NUMBER;
  }
}
