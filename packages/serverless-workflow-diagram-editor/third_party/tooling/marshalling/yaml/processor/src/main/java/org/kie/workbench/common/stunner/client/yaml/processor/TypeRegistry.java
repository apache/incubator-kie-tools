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


package org.kie.workbench.common.stunner.client.yaml.processor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BooleanYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.CharacterYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.EnumYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.StringArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.StringYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.PrimitiveBooleanArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.PrimitiveByteArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.PrimitiveCharacterArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.PrimitiveDoubleArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.PrimitiveFloatArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.PrimitiveIntegerArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.PrimitiveLongArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.PrimitiveShortArrayYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.AbstractCollectionYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.AbstractListYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.AbstractQueueYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.AbstractSequentialListYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.AbstractSetYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.ArrayListYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.CollectionYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.EnumSetYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.HashSetYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.IterableYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.LinkedHashSetYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.LinkedListYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.ListYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.PriorityQueueYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.QueueYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.SetYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.SortedSetYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.StackYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.TreeSetYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection.VectorYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.map.AbstractMapYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.map.EnumMapYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.map.HashMapYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.map.IdentityHashMapYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.map.LinkedHashMapYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.map.MapYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.map.SortedMapYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.map.TreeMapYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BooleanYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.CharacterYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.CollectionYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.EnumYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.IterableYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.StringYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.ArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.PrimitiveBooleanArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.PrimitiveByteArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.PrimitiveCharacterArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.PrimitiveDoubleArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.PrimitiveFloatArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.PrimitiveIntegerArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.PrimitiveLongArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array.PrimitiveShortArrayYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.map.MapYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.processor.context.GenerationContext;

import static java.util.Objects.nonNull;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseDateYAMLDeserializer.DateYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseDateYAMLDeserializer.SqlDateYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseDateYAMLDeserializer.SqlTimeYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseDateYAMLDeserializer.SqlTimestampYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer.BigDecimalYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer.BigIntegerYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer.ByteYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer.DoubleYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer.FloatYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer.IntegerYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer.LongYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.BaseNumberYAMLDeserializer.ShortYAMLDeserializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseDateYAMLSerializer.DateYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseDateYAMLSerializer.SqlDateYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseDateYAMLSerializer.SqlTimeYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseDateYAMLSerializer.SqlTimestampYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer.BigDecimalYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer.BigIntegerYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer.ByteYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer.DoubleYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer.FloatYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer.IntegerYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer.LongYAMLSerializer;
import static org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.BaseNumberYAMLSerializer.ShortYAMLSerializer;
/**
 * TypeRegistry class.
 *
 * @author vegegoku
 * @version $Id: $Id
 */
public final class TypeRegistry {

  private Map<String, ClassMapper> simpleTypes = new HashMap<>();
  private Map<String, ClassMapper> basicTypes = new HashMap<>();
  private Map<String, Class> collectionsDeserializers = new HashMap<>();
  private Map<String, Class> mapDeserializers = new HashMap<>();
  private Map<String, ClassMapper> customMappers = new HashMap<>();
  private final ClassMapperFactory MAPPER = new ClassMapperFactory();
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
    initDataMappers();
    initIterableMappers();
    initMapMappers();
    initPrimitiveArraysMappers();
    initCollectionsDeserializersMappers();
    initMapsDeserializersMappers();
  }

  private void initBasicMappers() {
    MAPPER
        .forType(boolean.class)
        .serializer(BooleanYAMLSerializer.class)
        .deserializer(BooleanYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(char.class)
        .serializer(CharacterYAMLSerializer.class)
        .deserializer(CharacterYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(byte.class)
        .serializer(ByteYAMLSerializer.class)
        .deserializer(ByteYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(double.class)
        .serializer(DoubleYAMLSerializer.class)
        .deserializer(DoubleYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(float.class)
        .serializer(FloatYAMLSerializer.class)
        .deserializer(FloatYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(int.class)
        .serializer(IntegerYAMLSerializer.class)
        .deserializer(IntegerYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(long.class)
        .serializer(LongYAMLSerializer.class)
        .deserializer(LongYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(short.class)
        .serializer(ShortYAMLSerializer.class)
        .deserializer(ShortYAMLDeserializer.class)
        .register(basicTypes);

  }

  private void initCommonMappers() {
    // Common mappers
    MAPPER
        .forType(String.class)
        .serializer(StringYAMLSerializer.class)
        .deserializer(StringYAMLDeserializer.class)
        .register(basicTypes);
    MAPPER
        .forType(Boolean.class)
        .serializer(BooleanYAMLSerializer.class)
        .deserializer(BooleanYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Character.class)
        .serializer(CharacterYAMLSerializer.class)
        .deserializer(CharacterYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Enum.class)
        .serializer(EnumYAMLSerializer.class)
        .deserializer(EnumYAMLDeserializer.class)
        .register(basicTypes);
  }

  private void initNumberMappers() {
    MAPPER
        .forType(BigDecimal.class)
        .serializer(BigDecimalYAMLSerializer.class)
        .deserializer(BigDecimalYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(BigInteger.class)
        .serializer(BigIntegerYAMLSerializer.class)
        .deserializer(BigIntegerYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Byte.class)
        .serializer(ByteYAMLSerializer.class)
        .deserializer(ByteYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Double.class)
        .serializer(DoubleYAMLSerializer.class)
        .deserializer(DoubleYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Float.class)
        .serializer(FloatYAMLSerializer.class)
        .deserializer(FloatYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Integer.class)
        .serializer(IntegerYAMLSerializer.class)
        .deserializer(IntegerYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Long.class)
        .serializer(LongYAMLSerializer.class)
        .deserializer(LongYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Short.class)
        .serializer(ShortYAMLSerializer.class)
        .deserializer(ShortYAMLDeserializer.class)
        .register(basicTypes);
  }

  private void initDataMappers() {
    MAPPER
        .forType(Date.class)
        .serializer(DateYAMLSerializer.class)
        .deserializer(DateYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(java.sql.Date.class)
        .serializer(SqlDateYAMLSerializer.class)
        .deserializer(SqlDateYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Time.class)
        .serializer(SqlTimeYAMLSerializer.class)
        .deserializer(SqlTimeYAMLDeserializer.class)
        .register(basicTypes);

    MAPPER
        .forType(Timestamp.class)
        .serializer(SqlTimestampYAMLSerializer.class)
        .deserializer(SqlTimestampYAMLDeserializer.class)
        .register(basicTypes);
  }

  private void initIterableMappers() {
    MAPPER
        .forType(Iterable.class)
        .serializer(IterableYAMLSerializer.class)
        .deserializer(IterableYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(Collection.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(CollectionYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(AbstractCollection.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(AbstractCollectionYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(AbstractList.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(AbstractListYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(AbstractQueue.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(AbstractQueueYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(AbstractSequentialList.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(AbstractSequentialListYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(AbstractSet.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(AbstractSetYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(ArrayList.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(ArrayListYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(EnumSet.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(EnumSetYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(HashSet.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(HashSetYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(LinkedHashSet.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(LinkedHashSetYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(LinkedList.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(LinkedListYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(List.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(ListYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(PriorityQueue.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(PriorityQueueYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(Queue.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(QueueYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(Set.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(SetYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(SortedSet.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(SortedSetYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(Stack.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(StackYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(TreeSet.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(TreeSetYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(Vector.class)
        .serializer(CollectionYAMLSerializer.class)
        .deserializer(VectorYAMLDeserializer.class)
        .register(simpleTypes);
  }

  private void initMapMappers() {
    MAPPER
        .forType(Map.class)
        .serializer(MapYAMLSerializer.class)
        .deserializer(MapYAMLDeserializer.class)
        .register(simpleTypes);
    MAPPER
        .forType(AbstractMap.class)
        .serializer(MapYAMLSerializer.class)
        .deserializer(AbstractMapYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(EnumMap.class)
        .serializer(MapYAMLSerializer.class)
        .deserializer(EnumMapYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(HashMap.class)
        .serializer(MapYAMLSerializer.class)
        .deserializer(HashMapYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(IdentityHashMap.class)
        .serializer(MapYAMLSerializer.class)
        .deserializer(IdentityHashMapYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(LinkedHashMap.class)
        .serializer(MapYAMLSerializer.class)
        .deserializer(LinkedHashMapYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(SortedMap.class)
        .serializer(MapYAMLSerializer.class)
        .deserializer(SortedMapYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(TreeMap.class)
        .serializer(MapYAMLSerializer.class)
        .deserializer(TreeMapYAMLDeserializer.class)
        .register(simpleTypes);
  }

  private void initPrimitiveArraysMappers() {
    MAPPER
        .forType(boolean[].class)
        .serializer(PrimitiveBooleanArrayYAMLSerializer.class)
        .deserializer(PrimitiveBooleanArrayYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(byte[].class)
        .serializer(PrimitiveByteArrayYAMLSerializer.class)
        .deserializer(PrimitiveByteArrayYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(char[].class)
        .serializer(PrimitiveCharacterArrayYAMLSerializer.class)
        .deserializer(PrimitiveCharacterArrayYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(double[].class)
        .serializer(PrimitiveDoubleArrayYAMLSerializer.class)
        .deserializer(PrimitiveDoubleArrayYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(float[].class)
        .serializer(PrimitiveFloatArrayYAMLSerializer.class)
        .deserializer(PrimitiveFloatArrayYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(int[].class)
        .serializer(PrimitiveIntegerArrayYAMLSerializer.class)
        .deserializer(PrimitiveIntegerArrayYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(long[].class)
        .serializer(PrimitiveLongArrayYAMLSerializer.class)
        .deserializer(PrimitiveLongArrayYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
        .forType(short[].class)
        .serializer(PrimitiveShortArrayYAMLSerializer.class)
        .deserializer(PrimitiveShortArrayYAMLDeserializer.class)
        .register(simpleTypes);

    MAPPER
            .forType(String[].class)
            .serializer(ArrayYAMLSerializer.class)
            .deserializer(StringArrayYAMLDeserializer.class)
            .register(simpleTypes);
  }

  private void initCollectionsDeserializersMappers() {
    collectionsDeserializers.put(
        AbstractCollection.class.getCanonicalName(), AbstractCollectionYAMLDeserializer.class);
    collectionsDeserializers.put(
        AbstractList.class.getCanonicalName(), AbstractListYAMLDeserializer.class);
    collectionsDeserializers.put(
        AbstractQueue.class.getCanonicalName(), AbstractQueueYAMLDeserializer.class);
    collectionsDeserializers.put(
        AbstractSequentialList.class.getCanonicalName(),
        AbstractSequentialListYAMLDeserializer.class);
    collectionsDeserializers.put(
        AbstractSet.class.getCanonicalName(), AbstractSetYAMLDeserializer.class);
    collectionsDeserializers.put(
        ArrayList.class.getCanonicalName(), ArrayListYAMLDeserializer.class);
    collectionsDeserializers.put(
        Collection.class.getCanonicalName(), CollectionYAMLDeserializer.class);
    collectionsDeserializers.put(EnumSet.class.getCanonicalName(), EnumSetYAMLDeserializer.class);
    collectionsDeserializers.put(HashSet.class.getCanonicalName(), HashSetYAMLDeserializer.class);
    collectionsDeserializers.put(Iterable.class.getCanonicalName(), IterableYAMLDeserializer.class);
    collectionsDeserializers.put(
        LinkedHashSet.class.getCanonicalName(), LinkedHashSetYAMLDeserializer.class);
    collectionsDeserializers.put(
        LinkedList.class.getCanonicalName(), LinkedListYAMLDeserializer.class);
    collectionsDeserializers.put(List.class.getCanonicalName(), ListYAMLDeserializer.class);
    collectionsDeserializers.put(
        PriorityQueue.class.getCanonicalName(), PriorityQueueYAMLDeserializer.class);
    collectionsDeserializers.put(Queue.class.getCanonicalName(), QueueYAMLDeserializer.class);
    collectionsDeserializers.put(Set.class.getCanonicalName(), SetYAMLDeserializer.class);
    collectionsDeserializers.put(
        SortedSet.class.getCanonicalName(), SortedSetYAMLDeserializer.class);
    collectionsDeserializers.put(Stack.class.getCanonicalName(), StackYAMLDeserializer.class);
    collectionsDeserializers.put(TreeSet.class.getCanonicalName(), TreeSetYAMLDeserializer.class);
    collectionsDeserializers.put(Vector.class.getCanonicalName(), VectorYAMLDeserializer.class);
  }

  private void initMapsDeserializersMappers() {
    mapDeserializers.put(Map.class.getName(), MapYAMLDeserializer.class);
    mapDeserializers.put(AbstractMap.class.getName(), AbstractMapYAMLDeserializer.class);
    mapDeserializers.put(EnumMap.class.getName(), EnumMapYAMLDeserializer.class);
    mapDeserializers.put(HashMap.class.getName(), HashMapYAMLDeserializer.class);
    mapDeserializers.put(IdentityHashMap.class.getName(), IdentityHashMapYAMLDeserializer.class);
    mapDeserializers.put(LinkedHashMap.class.getName(), LinkedHashMapYAMLDeserializer.class);
    mapDeserializers.put(SortedMap.class.getName(), SortedMapYAMLDeserializer.class);
    mapDeserializers.put(TreeMap.class.getName(), TreeMapYAMLDeserializer.class);
  }

  /**
   * resetTypeRegistry
   *
   * <p>Helper method to clean (reset) state of TypeRegistry. This action should be performed on
   * every APT run, since in some environments (such as Eclipse), the processor is instantiated once
   * and used multiple times. Without some cleanup we may end up with some serializer/deserializers
   * not generated due to TypeRegistry internal state saying that they already exists.
   */
  public void resetTypeRegistry() {
    customMappers.clear();
  }

  /**
   * register.
   *
   * @param mapper a {@link TypeRegistry.ClassMapper} object.
   */
  public void register(ClassMapper mapper) {
    mapper.register(simpleTypes);
  }

  /**
   * isBasicType.
   *
   * @param type a {@link String} object.
   * @return a boolean.
   */
  public boolean isBasicType(String type) {
    return basicTypes.containsKey(type);
  }

  /**
   * registerSerializer.
   *
   * @param type a {@link String} object.
   * @param serializer a {@link TypeElement} object.
   */
  public void registerSerializer(String type, TypeElement serializer) {
    if (customMappers.containsKey(type)) {
      customMappers.get(type).serializer = serializer;
    } else {
      ClassMapper classMapper = new ClassMapper(type);
      classMapper.serializer = serializer;
      customMappers.put(type, classMapper);
    }
  }

  /**
   * registerDeserializer.
   *
   * @param type a {@link String} object.
   * @param deserializer a {@link TypeElement} object.
   */
  public void registerDeserializer(String type, TypeElement deserializer) {
    if (customMappers.containsKey(type)) {
      customMappers.get(type).deserializer = deserializer;
    } else {
      ClassMapper classMapper = new ClassMapper(type);
      classMapper.deserializer = deserializer;
      customMappers.put(type, classMapper);
    }
  }

  /**
   * getCustomSerializer.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @return a {@link TypeElement} object.
   */
  public TypeElement getCustomSerializer(TypeMirror typeMirror) {
    return getCustomSerializer(types.erasure(typeMirror).toString());
  }

  /**
   * getCustomSerializer.
   *
   * @param type a {@link String} object.
   * @return a {@link TypeElement} object.
   */
  public TypeElement getCustomSerializer(String type) {
    if (containsSerializer(type)) {
      return customMappers.get(type).serializer;
    }
    throw new TypeSerializerNotFoundException(type);
  }

  /**
   * containsSerializer.
   *
   * @param typeName a {@link String} object.
   * @return a boolean.
   */
  public boolean containsSerializer(String typeName) {
    return nonNull(customMappers.get(typeName)) && nonNull(customMappers.get(typeName).serializer);
  }

  /**
   * getCustomDeserializer.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @return a {@link TypeElement} object.
   */
  public TypeElement getCustomDeserializer(TypeMirror typeMirror) {
    return getCustomDeserializer(types.erasure(typeMirror).toString());
  }

  /**
   * getCustomDeserializer.
   *
   * @param type a {@link String} object.
   * @return a {@link TypeElement} object.
   */
  public TypeElement getCustomDeserializer(String type) {
    if (containsDeserializer(type)) {
      return customMappers.get(type).deserializer;
    }
    throw new TypeDeserializerNotFoundException(type);
  }

  /**
   * containsDeserializer.
   *
   * @param typeName a {@link String} object.
   * @return a boolean.
   */
  public boolean containsDeserializer(String typeName) {
    return nonNull(customMappers.get(typeName))
        && nonNull(customMappers.get(typeName).deserializer);
  }

  /**
   * getSerializer.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @return a {@link TypeElement} object.
   */
  public TypeElement getSerializer(TypeMirror typeMirror) {
    return getSerializer(typeMirror.toString());
  }

  public TypeElement getSerializer(String typeName) {
    if (basicTypes.containsKey(typeName) || simpleTypes.containsKey(typeName)) {
      return get(typeName).serializer;
    }
    throw new TypeSerializerNotFoundException(typeName);
  }

  /**
   * get.
   *
   * @param typeName a {@link String} object.
   * @return a {@link TypeRegistry.ClassMapper} object.
   */
  public ClassMapper get(String typeName) {
    if (isSimpleType(typeName)) {
      return simpleTypes.get(typeName);
    }
    return basicTypes.get(typeName);
  }

  public boolean isSimpleType(String type) {
    return simpleTypes.containsKey(type);
  }

  /**
   * getDeserializer.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @return a {@link TypeElement} object.
   */
  public TypeElement getDeserializer(TypeMirror typeMirror) {
    return getDeserializer(typeMirror.toString());
  }

  public TypeElement getDeserializer(String typeName) {
    if (basicTypes.containsKey(typeName) || simpleTypes.containsKey(typeName)) {
      return get(typeName).deserializer;
    }
    throw new TypeDeserializerNotFoundException(typeName);
  }

  /**
   * getCollectionDeserializer.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @return a {@link Class} object.
   */
  public Class<?> getCollectionDeserializer(TypeMirror typeMirror) {
    return getCollectionDeserializer(asNoneGeneric(typeMirror));
  }

  private Class<?> getCollectionDeserializer(String collectionType) {
    if (collectionsDeserializers.containsKey(collectionType)) {
      return collectionsDeserializers.get(collectionType);
    }
    throw new TypeDeserializerNotFoundException(collectionType);
  }

  private String asNoneGeneric(TypeMirror type) {
    return types.asElement(type).toString().replace("<E>", "");
  }

  /**
   * getMapDeserializer.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @return a {@link Class} object.
   */
  public Class<?> getMapDeserializer(TypeMirror typeMirror) {
    return getMapDeserializer(asNoneGeneric(typeMirror));
  }

  private Class<?> getMapDeserializer(String mapType) {
    if (mapDeserializers.containsKey(mapType)) {
      return mapDeserializers.get(mapType);
    }
    throw new TypeDeserializerNotFoundException(mapType);
  }

  /**
   * containsSerializer.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @return a boolean.
   */
  public boolean containsSerializer(TypeMirror typeMirror) {
    return containsSerializer(context.getTypeUtils().stringifyTypeWithPackage(typeMirror));
  }

  /**
   * containsDeserializer.
   *
   * @param typeMirror a {@link TypeMirror} object.
   * @return a boolean.
   */
  public boolean containsDeserializer(TypeMirror typeMirror) {
    return containsDeserializer(context.getTypeUtils().stringifyTypeWithPackage(typeMirror));
  }

  private static class TypeSerializerNotFoundException extends RuntimeException {

    TypeSerializerNotFoundException(String typeName) {
      super(typeName);
    }
  }

  private static class TypeDeserializerNotFoundException extends RuntimeException {

    TypeDeserializerNotFoundException(String typeName) {
      super(typeName);
    }
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
