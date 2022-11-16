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

package jakarta.json.bind.serializer;

import java.io.StringReader;
import java.lang.reflect.Type;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserImpl;
import jakarta.json.stream.gwt.JsonObjectImpl;
import org.kie.workbench.common.stunner.client.json.mapper.annotation.GwtIncompatible;

public class JsonDeserializationContext implements DeserializationContext {

  public JsonParser createParser(String json) {
    return new JREParserFactory().createParser(json);
  }

  @Override
  public <T> T deserialize(Class<T> clazz, JsonParser parser) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> T deserialize(Type type, JsonParser parser) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private static class GWTParserFactory {
    public JsonParser createParser(String json) {
      return new JsonParserImpl(new JsonObjectImpl(json));
    }
  }

  private static class JREParserFactory extends GWTParserFactory {
    @GwtIncompatible
    @Override
    public JsonParser createParser(String json) {
      return Json.createParser(new StringReader(json));
    }
  }
}
