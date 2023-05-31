/*
 * Copyright (c) 2018, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.snakeyaml.engine.v2.representer;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import elemental2.core.JsString;
import elemental2.core.Uint8Array;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.snakeyaml.engine.v2.GwtIncompatible;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.RepresentToNode;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.NonPrintableStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.scanner.StreamReader;

/** Represent standard Java classes */
public class StandardRepresenter extends BaseRepresenter {

  /** all chars that represent a new line */
  public static final Pattern MULTILINE_PATTERN = Pattern.compile("\n|\u0085|\u2028|\u2029");
  /** Connect classes to their tags */
  protected Map<Class<? extends Object>, Tag> classTags;
  /** keep the options */
  protected DumpSettings settings;

  /**
   * Create
   *
   * @param settings - configuration options
   */
  public StandardRepresenter(DumpSettings settings) {
    this.defaultFlowStyle = settings.getDefaultFlowStyle();
    this.defaultScalarStyle = settings.getDefaultScalarStyle();

    this.nullRepresenter = new RepresentNull();
    this.representers.put(String.class, new RepresentString());
    this.representers.put(String[].class, new RepresentArray());
    this.representers.put(Boolean.class, new RepresentBoolean());
    this.representers.put(Character.class, new RepresentString());
    this.representers.put(UUID.class, new RepresentUuid());
    this.representers.put(Optional.class, new RepresentOptional());
    this.representers.put(byte[].class, new RepresentByteArray());

    RepresentToNode primitiveArray = new RepresentPrimitiveArray();
    representers.put(short[].class, primitiveArray);
    representers.put(int[].class, primitiveArray);
    representers.put(long[].class, primitiveArray);
    representers.put(float[].class, primitiveArray);
    representers.put(double[].class, primitiveArray);
    representers.put(char[].class, primitiveArray);
    representers.put(boolean[].class, primitiveArray);

    this.parentClassRepresenters.put(Number.class, new RepresentNumber());

    this.parentClassRepresenters.put(Integer.class, new RepresentNumber());
    this.parentClassRepresenters.put(Short.class, new RepresentNumber());
    this.parentClassRepresenters.put(Byte.class, new RepresentNumber());
    this.parentClassRepresenters.put(Long.class, new RepresentNumber());
    this.parentClassRepresenters.put(Float.class, new RepresentNumber());
    this.parentClassRepresenters.put(Double.class, new RepresentNumber());
    this.parentClassRepresenters.put(BigInteger.class, new RepresentNumber());

    this.parentClassRepresenters.put(List.class, new RepresentList());
    this.parentClassRepresenters.put(ArrayList.class, new RepresentList());
    this.parentClassRepresenters.put(LinkedList.class, new RepresentList());
    this.parentClassRepresenters.put(Map.class, new RepresentMap());
    this.parentClassRepresenters.put(HashMap.class, new RepresentMap());
    this.parentClassRepresenters.put(LinkedHashMap.class, new RepresentMap());
    this.parentClassRepresenters.put(Set.class, new RepresentSet());
    this.parentClassRepresenters.put(HashSet.class, new RepresentSet());
    this.parentClassRepresenters.put(Iterator.class, new RepresentIterator());
    this.parentClassRepresenters.put(new Object[0].getClass(), new RepresentArray());
    this.parentClassRepresenters.put(Enum.class, new RepresentEnum());
    classTags = new HashMap<>();
    this.settings = settings;
  }

  /**
   * Define the way to get the Tag for any class
   *
   * @param clazz - the class to serialise
   * @param defaultTag - the tag to use if there is no explicit configuration
   * @return the Tag for output
   */
  protected Tag getTag(Class<?> clazz, Tag defaultTag) {
    return classTags.getOrDefault(clazz, defaultTag);
  }

  /**
   * Define a tag for the <code>Class</code> to serialize. Should be replaced later with the beans
   * project.
   *
   * @param clazz <code>Class</code> which tag is changed
   * @param tag new tag to be used for every instance of the specified <code>Class</code>
   * @return the previous tag associated with the <code>Class</code>
   * @deprecated should be replaced with the Beans project
   */
  @Deprecated
  public Tag addClassTag(Class<? extends Object> clazz, Tag tag) {
    if (tag == null) {
      throw new NullPointerException("Tag must be provided.");
    }
    return classTags.put(clazz, tag);
  }

  private static class IteratorWrapper implements Iterable<Object> {

    private final Iterator<Object> iter;

    public IteratorWrapper(Iterator<Object> iter) {
      this.iter = iter;
    }

    public Iterator<Object> iterator() {
      return iter;
    }
  }

  /** Create null Node */
  protected class RepresentNull implements RepresentToNode {

    public Node representData(Object data) {
      return representScalar(Tag.NULL, "null");
    }
  }

  /** Create Node for String */
  public class RepresentString implements RepresentToNode {

    public Node representData(Object data) {
      Tag tag = Tag.STR;
      ScalarStyle style = ScalarStyle.PLAIN;
      String value = data.toString();
      if (settings.getNonPrintableStyle() == NonPrintableStyle.BINARY
          && !StreamReader.isPrintable(value)) {
        tag = Tag.BINARY;
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        // sometimes above will just silently fail - it will return incomplete data
        // it happens when String has invalid code points
        // (for example half surrogate character without other half)
        final String checkValue = new String(bytes, StandardCharsets.UTF_8);
        if (!checkValue.equals(value)) {
          throw new YamlEngineException("invalid string value has occurred");
        }
        value = new JREBase64().encodeToString(bytes);
        style = ScalarStyle.LITERAL;
      }
      // if no other scalar style is explicitly set, use literal style for
      // multiline scalars
      if (defaultScalarStyle == ScalarStyle.PLAIN && MULTILINE_PATTERN.matcher(value).find()) {
        style = ScalarStyle.LITERAL;
      }
      return representScalar(tag, value, style);
    }
  }

  /** Create Node for Boolean */
  public class RepresentBoolean implements RepresentToNode {

    public Node representData(Object data) {
      String value;
      if (Boolean.TRUE.equals(data)) {
        value = "true";
      } else {
        value = "false";
      }
      return representScalar(Tag.BOOL, value);
    }
  }

  /** Create Node for Byte, Short, Integer, Long, BigInteger */
  public class RepresentNumber implements RepresentToNode {

    public Node representData(Object data) {
      Tag tag;
      String value;
      if (data instanceof Byte
          || data instanceof Short
          || data instanceof Integer
          || data instanceof Long
          || data instanceof BigInteger) {
        tag = Tag.INT;
        value = data.toString();
      } else {
        Number number = (Number) data;
        tag = Tag.FLOAT;
        if (number.equals(Double.NaN) || number.equals(Float.NaN)) {
          value = ".nan";
        } else if (number.equals(Double.POSITIVE_INFINITY)
            || number.equals(Float.POSITIVE_INFINITY)) {
          value = ".inf";
        } else if (number.equals(Double.NEGATIVE_INFINITY)
            || number.equals(Float.NEGATIVE_INFINITY)) {
          value = "-.inf";
        } else {
          value = number.toString();
        }
      }
      return representScalar(getTag(data.getClass(), tag), value);
    }
  }

  /** Create Node for List */
  public class RepresentList implements RepresentToNode {

    @SuppressWarnings("unchecked")
    public Node representData(Object data) {
      return representSequence(
          getTag(data.getClass(), Tag.SEQ), (List<Object>) data, settings.getDefaultFlowStyle());
    }
  }

  /** Create Node for Iterator */
  public class RepresentIterator implements RepresentToNode {

    @SuppressWarnings("unchecked")
    public Node representData(Object data) {
      Iterator<Object> iter = (Iterator<Object>) data;
      return representSequence(
          getTag(data.getClass(), Tag.SEQ),
          new IteratorWrapper(iter),
          settings.getDefaultFlowStyle());
    }
  }

  /** Create Node for Object[] */
  public class RepresentArray implements RepresentToNode {

    public Node representData(Object data) {
      Object[] array = (Object[]) data;
      List<Object> list = Arrays.asList(array);
      return representSequence(Tag.SEQ, list, settings.getDefaultFlowStyle());
    }
  }

  /**
   * Represents primitive arrays, such as short[] and float[], by converting them into equivalent
   * {@link List} using the appropriate autoboxing type.
   */
  public class RepresentPrimitiveArray implements RepresentToNode {

    public Node representData(Object data) {
      Class<?> type = data.getClass().getComponentType();

      FlowStyle style = settings.getDefaultFlowStyle();
      if (byte.class == type) {
        return representSequence(Tag.SEQ, asByteList(data), style);
      } else if (short.class == type) {
        return representSequence(Tag.SEQ, asShortList(data), style);
      } else if (int.class == type) {
        return representSequence(Tag.SEQ, asIntList(data), style);
      } else if (long.class == type) {
        return representSequence(Tag.SEQ, asLongList(data), style);
      } else if (float.class == type) {
        return representSequence(Tag.SEQ, asFloatList(data), style);
      } else if (double.class == type) {
        return representSequence(Tag.SEQ, asDoubleList(data), style);
      } else if (char.class == type) {
        return representSequence(Tag.SEQ, asCharList(data), style);
      } else if (boolean.class == type) {
        return representSequence(Tag.SEQ, asBooleanList(data), style);
      }

      throw new YamlEngineException("Unexpected primitive '" + type.getCanonicalName() + "'");
    }

    private List<Byte> asByteList(Object in) {
      byte[] array = (byte[]) in;
      List<Byte> list = new ArrayList<>(array.length);
      for (int i = 0; i < array.length; ++i) {
        list.add(array[i]);
      }
      return list;
    }

    private List<Short> asShortList(Object in) {
      short[] array = (short[]) in;
      List<Short> list = new ArrayList<>(array.length);
      for (int i = 0; i < array.length; ++i) {
        list.add(array[i]);
      }
      return list;
    }

    private List<Integer> asIntList(Object in) {
      int[] array = (int[]) in;
      List<Integer> list = new ArrayList<>(array.length);
      for (int i = 0; i < array.length; ++i) {
        list.add(array[i]);
      }
      return list;
    }

    private List<Long> asLongList(Object in) {
      long[] array = (long[]) in;
      List<Long> list = new ArrayList<>(array.length);
      for (int i = 0; i < array.length; ++i) {
        list.add(array[i]);
      }
      return list;
    }

    private List<Float> asFloatList(Object in) {
      float[] array = (float[]) in;
      List<Float> list = new ArrayList<>(array.length);
      for (int i = 0; i < array.length; ++i) {
        list.add(array[i]);
      }
      return list;
    }

    private List<Double> asDoubleList(Object in) {
      double[] array = (double[]) in;
      List<Double> list = new ArrayList<>(array.length);
      for (int i = 0; i < array.length; ++i) {
        list.add(array[i]);
      }
      return list;
    }

    private List<Character> asCharList(Object in) {
      char[] array = (char[]) in;
      List<Character> list = new ArrayList<>(array.length);
      for (int i = 0; i < array.length; ++i) {
        list.add(array[i]);
      }
      return list;
    }

    private List<Boolean> asBooleanList(Object in) {
      boolean[] array = (boolean[]) in;
      List<Boolean> list = new ArrayList<>(array.length);
      for (int i = 0; i < array.length; ++i) {
        list.add(array[i]);
      }
      return list;
    }
  }

  /** Create Node for Map instance */
  public class RepresentMap implements RepresentToNode {

    @SuppressWarnings("unchecked")
    public Node representData(Object data) {
      return representMapping(
          getTag(data.getClass(), Tag.MAP),
          (Map<Object, Object>) data,
          settings.getDefaultFlowStyle());
    }
  }

  /** Create Node for Set instances */
  public class RepresentSet implements RepresentToNode {

    @SuppressWarnings("unchecked")
    public Node representData(Object data) {
      Map<Object, Object> value = new LinkedHashMap<>();
      Set<Object> set = (Set<Object>) data;
      for (Object key : set) {
        value.put(key, null);
      }
      return representMapping(
          getTag(data.getClass(), Tag.SET), value, settings.getDefaultFlowStyle());
    }
  }

  /** Create eNode for Enums */
  public class RepresentEnum implements RepresentToNode {

    public Node representData(Object data) {
      Tag tag = new Tag(data.getClass());
      return representScalar(getTag(data.getClass(), tag), ((Enum<?>) data).name());
    }
  }

  /** Create Node for byte[] */
  public class RepresentByteArray implements RepresentToNode {

    public Node representData(Object data) {
      return representScalar(
          Tag.BINARY, new JREBase64().encodeToString((byte[]) data), ScalarStyle.LITERAL);
    }
  }

  /** Create Node for UUID */
  public class RepresentUuid implements RepresentToNode {

    public Node representData(Object data) {
      return representScalar(getTag(data.getClass(), new Tag(UUID.class)), data.toString());
    }
  }

  /** Create Node for Optional instance (the value of null) */
  public class RepresentOptional implements RepresentToNode {

    public Node representData(Object data) {
      Optional<?> opt = (Optional<?>) data;
      if (opt.isPresent()) {
        Node node = represent(opt.get());
        node.setTag(new Tag(Optional.class));
        return node;
      } else {
        return representScalar(Tag.NULL, "null");
      }
    }
  }

  private static class JREBase64 extends GWTBase64 {

    @GwtIncompatible
    String encodeToString(byte[] src) {
      return Base64.getEncoder().encodeToString(src);
    }
  }

  private static class GWTBase64 {

    String encodeToString(byte[] source) {
      StringBuffer sb = new StringBuffer();
      Uint8Array bytes = Uint8Array.from(Js.<JsArrayLike<Double>>uncheckedCast(source));
      int len = bytes.byteLength;
      for (int i = 0; i < len; i++) {
        sb.append(JsString.fromCharCode(bytes.getAt(i).intValue()));
      }

      return DomGlobal.btoa(sb.toString());
    }
  }
}
