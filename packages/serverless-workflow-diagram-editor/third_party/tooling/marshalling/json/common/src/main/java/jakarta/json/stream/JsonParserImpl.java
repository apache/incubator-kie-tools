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

package jakarta.json.stream;

import java.math.BigDecimal;

import jakarta.json.JsonObject;
import jsinterop.base.Js;

public class JsonParserImpl implements JsonParser {

  private JsonObject holder;

  public JsonParserImpl(JsonObject holder) {
    this.holder = holder;
  }

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public Event next() {
    if (holder != null && Js.typeof(holder).equals("object")) {
      return Event.START_OBJECT;
    }
    return null;
  }

  public JsonObject getObject() {
    return holder;
  }

  @Override
  public String getString() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isIntegralNumber() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getInt() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getLong() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BigDecimal getBigDecimal() {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonLocation getLocation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() {}
}
