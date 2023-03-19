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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import elemental2.core.JsArray;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonNumberImpl;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonStringImpl;
import jakarta.json.JsonValue;

public class JsonArrayImpl implements JsonArray {

  JsArray array;

  public JsonArrayImpl() {
    this(new JsArray());
  }

  public JsonArrayImpl(JsArray array) {
    this.array = array;
  }

  @Override
  public JsonArray asJsonArray() {
    return this;
  }

  @Override
  public JsonObject getJsonObject(int index) {
    return new JsonObjectImpl(array.getAt(index));
  }

  @Override
  public JsonArray getJsonArray(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonNumber getJsonNumber(int index) {
    return new JsonNumberImpl(array.getAt(index));
  }

  @Override
  public JsonString getJsonString(int index) {
    return new JsonStringImpl(array.getAt(index));
  }

  @Override
  public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getString(int index) {
    return array.getAt(index).toString();
  }

  @Override
  public String getString(int index, String defaultValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getInt(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getInt(int index, int defaultValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean getBoolean(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean getBoolean(int index, boolean defaultValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isNull(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ValueType getValueType() {
    return ValueType.ARRAY;
  }

  @Override
  public int size() {
    return array.length;
  }

  @Override
  public boolean isEmpty() {
    return array == null || array.length == 0;
  }

  @Override
  public boolean contains(Object o) {
    return array.includes(o);
  }

  @Override
  public Iterator<JsonValue> iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(JsonValue jsonValue) {
    array.push(jsonValue);
    return true;
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends JsonValue> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(int index, Collection<? extends JsonValue> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    while (array.length > 0) {
      array.pop();
    }
  }

  @Override
  public JsonValue get(int index) {
    return new JsonValueImpl(array.getAt(index));
  }

  @Override
  public JsonValue set(int index, JsonValue element) {
    JsonValue old = new JsonValueImpl(array.getAt(index));
    array.setAt(index, ((JsonValueImpl) element).__holder__);
    return old;
  }

  @Override
  public void add(int index, JsonValue element) {
    array.setAt(index, ((JsonValueImpl) element).__holder__);
  }

  @Override
  public JsonValue remove(int index) {
    JsonValue old = new JsonValueImpl(array.getAt(index));
    array.delete(index);
    return old;
  }

  @Override
  public int indexOf(Object o) {
    return array.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return array.lastIndexOf(o);
  }

  @Override
  public ListIterator<JsonValue> listIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator<JsonValue> listIterator(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<JsonValue> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }
}
