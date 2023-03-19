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

package jakarta.json.stream.gwt;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jsinterop.base.Js;

public class JsonValueImpl implements JsonValue {

  Object __holder__;

  public JsonValueImpl(Object holder) {
    this.__holder__ = holder;
  }

  @Override
  public ValueType getValueType() {
    return new JsonObjectImpl(__holder__).getValueType();
  }

  public JsonObject asJsonObject() {
    if (this instanceof JsonObject) {
      return Js.uncheckedCast(this);
    }
    return new JsonObjectImpl(__holder__);
  }
}
