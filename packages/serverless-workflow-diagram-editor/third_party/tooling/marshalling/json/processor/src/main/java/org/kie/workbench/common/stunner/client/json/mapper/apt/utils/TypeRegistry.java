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

package org.kie.workbench.common.stunner.client.json.mapper.apt.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.apt.exception.TypeDeserializerNotFoundException;
import org.kie.workbench.common.stunner.client.json.mapper.apt.exception.TypeMapperNotFoundException;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BooleanJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.CharacterJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.EnumJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.StringJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.PrimitiveBooleanArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.PrimitiveByteArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.PrimitiveCharacterArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.PrimitiveDoubleArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.PrimitiveFloatArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.PrimitiveIntegerArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.PrimitiveLongArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.PrimitiveShortArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array.StringArrayJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.collection.ArrayListDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.collection.HashSetDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.collection.LinkedHashSetDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.collection.LinkedListDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.collection.ListDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.collection.SortedSetDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BooleanJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.CharacterJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.EnumJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.StringJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.PrimitiveBooleanArrayJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.PrimitiveByteArrayJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.PrimitiveCharacterArrayJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.PrimitiveDoubleArrayJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.PrimitiveFloatArrayJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.PrimitiveIntegerArrayJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.PrimitiveLongArrayLongSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array.PrimitiveShortArrayJsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.collection.CollectionJsonSerializer;

import static org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer.BigDecimalJsonDeserializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer.BigIntegerJsonDeserializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer.ByteJsonDeserializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer.DoubleJsonDeserializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer.FloatJsonDeserializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer.IntegerJsonDeserializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer.LongJsonDeserializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer.ShortJsonDeserializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BaseNumberJsonSerializer.BigDecimalJsonSerializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BaseNumberJsonSerializer.BigIntegerJsonSerializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BaseNumberJsonSerializer.ByteJsonSerializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BaseNumberJsonSerializer.DoubleJsonSerializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BaseNumberJsonSerializer.FloatJsonSerializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BaseNumberJsonSerializer.IntegerJsonSerializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BaseNumberJsonSerializer.LongJsonSerializer;
import static org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.BaseNumberJsonSerializer.ShortJsonSerializer;

public class TypeRegistry {
  private final ClassMapperFactory MAPPER = new ClassMapperFactory();

  private Map<String, ClassMapper> buildIn = new HashMap<>();
  private final Types types;
  private final Elements elements;
  private final GenerationContext context;

  public TypeRegistry(GenerationContext context) {
    this.types = context.getProcessingEnv().getTypeUtils();
    this.elements = context.getProcessingEnv().getElementUtils();
    this.context = context;

    initBasicMappers();
    initCommonMappers();
    initNumberMappers();
    initPrimitiveArraysMappers();
    initCollections();
  }

  public TypeElement getSerializer(TypeMirror typeMirror) {
    return getSerializer(typeMirror.toString());
  }

  public TypeElement getSerializer(String typeName) {
    if (buildIn.containsKey(typeName)) {
      return get(typeName).serializer;
    }
    throw new TypeDeserializerNotFoundException(typeName);
  }

  public TypeElement getDeserializer(TypeMirror typeMirror) {
    return getDeserializer(typeMirror.toString());
  }

  public TypeElement getDeserializer(String typeName) {
    if (buildIn.containsKey(typeName)) {
      return get(typeName).deserializer;
    }
    throw new TypeDeserializerNotFoundException(typeName);
  }

  public boolean has(TypeMirror typeMirror) {
    return buildIn.containsKey(typeMirror.toString());
  }

  public ClassMapper get(String typeName) {
    if (buildIn.containsKey(typeName)) {
      return buildIn.get(typeName);
    }
    throw new TypeMapperNotFoundException(typeName);
  }

  private void initBasicMappers() {
    MAPPER
            .forType(boolean.class)
            // .serializer(BooleanJsonSerializer.class)
            .deserializer(BooleanJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(char.class)
            // .serializer(CharacterJsonSerializer.class)
            .deserializer(CharacterJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(byte.class)
            // .serializer(ByteJsonSerializer.class)
            .deserializer(ByteJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(double.class)
            // .serializer(DoubleJsonSerializer.class)
            .deserializer(DoubleJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(float.class)
            // .serializer(FloatJsonSerializer.class)
            .deserializer(FloatJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(int.class)
            // .serializer(IntegerJsonSerializer.class)
            .deserializer(IntegerJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(long.class)
            // .serializer(LongJsonSerializer.class)
            .deserializer(LongJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(short.class)
            // .serializer(ShortJsonSerializer.class)
            .deserializer(ShortJsonDeserializer.class)
            .register(buildIn);
  }

  private void initCommonMappers() {
    // Common mappers
    MAPPER
            .forType(String.class)
            .serializer(StringJsonSerializer.class)
            .deserializer(StringJsonDeserializer.class)
            .register(buildIn);
    MAPPER
            .forType(Boolean.class)
            .serializer(BooleanJsonSerializer.class)
            .deserializer(BooleanJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(Character.class)
            .serializer(CharacterJsonSerializer.class)
            .deserializer(CharacterJsonDeserializer.class)
            .register(buildIn);

    /*    MAPPER
            .forType(UUID.class)
            .serializer(UUIDJsonSerializer.class)
            .deserializer(UUIDJsonDeserializer.class)
            .register(buildIn);
    */
    MAPPER
            .forType(Enum.class)
            .serializer(EnumJsonSerializer.class)
            .deserializer(EnumJsonDeserializer.class)
            .register(buildIn);
  }

  private void initNumberMappers() {
    MAPPER
            .forType(BigDecimal.class)
            .serializer(BigDecimalJsonSerializer.class)
            .deserializer(BigDecimalJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(BigInteger.class)
            .serializer(BigIntegerJsonSerializer.class)
            .deserializer(BigIntegerJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(Byte.class)
            .serializer(ByteJsonSerializer.class)
            .deserializer(ByteJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(Double.class)
            .serializer(DoubleJsonSerializer.class)
            .deserializer(DoubleJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(Float.class)
            .serializer(FloatJsonSerializer.class)
            .deserializer(FloatJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(Integer.class)
            .serializer(IntegerJsonSerializer.class)
            .deserializer(IntegerJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(Long.class)
            .serializer(LongJsonSerializer.class)
            .deserializer(LongJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(Short.class)
            .serializer(ShortJsonSerializer.class)
            .deserializer(ShortJsonDeserializer.class)
            .register(buildIn);
  }

  private void initPrimitiveArraysMappers() {
    MAPPER
            .forType(boolean[].class)
            .serializer(PrimitiveBooleanArrayJsonSerializer.class)
            .deserializer(PrimitiveBooleanArrayJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(byte[].class)
            .serializer(PrimitiveByteArrayJsonSerializer.class)
            .deserializer(PrimitiveByteArrayJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(char[].class)
            .serializer(PrimitiveCharacterArrayJsonSerializer.class)
            .deserializer(PrimitiveCharacterArrayJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(double[].class)
            .serializer(PrimitiveDoubleArrayJsonSerializer.class)
            .deserializer(PrimitiveDoubleArrayJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(float[].class)
            .serializer(PrimitiveFloatArrayJsonSerializer.class)
            .deserializer(PrimitiveFloatArrayJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(int[].class)
            .serializer(PrimitiveIntegerArrayJsonSerializer.class)
            .deserializer(PrimitiveIntegerArrayJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(long[].class)
            .serializer(PrimitiveLongArrayLongSerializer.class)
            .deserializer(PrimitiveLongArrayJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(short[].class)
            .serializer(PrimitiveShortArrayJsonSerializer.class)
            .deserializer(PrimitiveShortArrayJsonDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(String[].class)
            .serializer(PrimitiveShortArrayJsonSerializer.class)
            .deserializer(StringArrayJsonDeserializer.class)
            .register(buildIn);
  }

  private void initCollections() {
    MAPPER
            .forType(List.class)
            .serializer(CollectionJsonSerializer.class)
            .deserializer(ListDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(ArrayList.class)
            .serializer(CollectionJsonSerializer.class)
            .deserializer(ArrayListDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(LinkedList.class)
            .serializer(CollectionJsonSerializer.class)
            .deserializer(LinkedListDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(Set.class)
            .serializer(CollectionJsonSerializer.class)
            .deserializer(HashSetDeserializer.class)
            .register(buildIn);
    MAPPER
            .forType(SortedSet.class)
            .serializer(CollectionJsonSerializer.class)
            .deserializer(SortedSetDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(HashSet.class)
            .serializer(CollectionJsonSerializer.class)
            .deserializer(HashSetDeserializer.class)
            .register(buildIn);

    MAPPER
            .forType(LinkedHashSet.class)
            .serializer(CollectionJsonSerializer.class)
            .deserializer(LinkedHashSetDeserializer.class)
            .register(buildIn);
    MAPPER
            .forType(TreeSet.class)
            .serializer(CollectionJsonSerializer.class)
            .deserializer(SortedSetDeserializer.class)
            .register(buildIn);
  }

  class ClassMapperFactory {

    ClassMapper forType(Class<?> clazz) {
      return new ClassMapper(clazz);
    }
  }

  public class ClassMapper {

    private final String clazz;

    private TypeElement serializer;

    private TypeElement deserializer;

    private ClassMapper(Class clazz) {
      this.clazz = clazz.getCanonicalName();
    }

    private ClassMapper(String type) {
      this.clazz = type;
    }

    private ClassMapper serializer(Class serializer) {
      this.serializer = elements.getTypeElement(serializer.getCanonicalName());
      return this;
    }

    private ClassMapper deserializer(Class deserializer) {
      this.deserializer = elements.getTypeElement(deserializer.getCanonicalName());
      return this;
    }

    private ClassMapper register(Map<String, ClassMapper> registry) {
      registry.put(this.clazz, this);
      return this;
    }

    @Override
    public String toString() {
      return "ClassMapper{" + "clazz='" + clazz + '\'' + ", serializer=" + serializer != null
              ? serializer.toString()
              : "" + ", deserializer=" + deserializer != null ? deserializer.toString() : "" + '}';
    }
  }
}
