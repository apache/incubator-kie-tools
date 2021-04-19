/*
 * Copyright (C) 2010 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.types;

import org.jboss.errai.common.client.types.handlers.collections.*;
import org.jboss.errai.common.client.types.handlers.numbers.*;

import java.util.*;

public class TypeHandlerFactory {
  private static Map<Class, Map<Class, TypeHandler>> handlers =
      new HashMap<Class, Map<Class, TypeHandler>>();

  private static Map<Class, Class> inheritanceMap = new HashMap<Class, Class>();

  static {
    /**
     * Declare all the default coercion handlers.
     */
    final Map<Class, TypeHandler> collectionHandlers = new HashMap<Class, TypeHandler>();
    collectionHandlers.put(Object[].class, new CollectionToObjArray());
    collectionHandlers.put(String[].class, new CollectionToStringArray());
    collectionHandlers.put(Integer[].class, new CollectionToIntArray());
    collectionHandlers.put(Long[].class, new CollectionToLongArray());
    collectionHandlers.put(Boolean[].class, new CollectionToBooleanArray());
    collectionHandlers.put(Double[].class, new CollectionToDoubleArray());

    collectionHandlers.put(Set.class, new CollectionToSet());
    collectionHandlers.put(List.class, new CollectionToList());
    collectionHandlers.put(Queue.class, new CollectionToQueue());

    handlers.put(Collection.class, collectionHandlers);

    final Map<Class, TypeHandler> numberHandlers = new HashMap<Class, TypeHandler>();
    numberHandlers.put(Integer.class, new NumberToInt());
    numberHandlers.put(Long.class, new NumberToLong());
    numberHandlers.put(Short.class, new NumberToShort());
    numberHandlers.put(Float.class, new NumberToFloat());
    numberHandlers.put(Double.class, new NumberToFloat());
    numberHandlers.put(Byte.class, new NumberToByte());
    numberHandlers.put(java.util.Date.class, new NumberToDate());
    numberHandlers.put(java.sql.Date.class, new NumberToSQLDate());

    handlers.put(Number.class, numberHandlers);

    /**
     * Build an inheretence Map.
     */
    inheritanceMap.put(Integer.class, Number.class);
    inheritanceMap.put(Long.class, Number.class);
    inheritanceMap.put(Short.class, Number.class);
    inheritanceMap.put(Float.class, Number.class);
    inheritanceMap.put(Double.class, Number.class);

    inheritanceMap.put(ArrayList.class, List.class);
    inheritanceMap.put(LinkedList.class, List.class);
    inheritanceMap.put(AbstractList.class, List.class);
    inheritanceMap.put(Stack.class, List.class);

    inheritanceMap.put(HashSet.class, Set.class);
    inheritanceMap.put(AbstractSet.class, Set.class);

    inheritanceMap.put(Set.class, Collection.class);
    inheritanceMap.put(List.class, Collection.class);

    addHandler(String.class, Character.class, new TypeHandler<String, Character>() {
      public Character getConverted(final String in) {
        return in.charAt(0);
      }
    });
  }

  public static Map<Class, TypeHandler> getHandler(final Class from) {
    if (!handlers.containsKey(from) && inheritanceMap.containsKey(from)) {
      return getHandler(inheritanceMap.get(from));
    }
    else {
      return handlers.get(from);
    }
  }

  public static <T> T convert(final Object value, final Class<T> to) {
    if (value == null) return null;
    return convert(value.getClass(), to, value);
  }

  public static <T> T convert(final Class from, final Class<T> to, final Object value) {
    if (value.getClass() == to) return (T) value;
    final Map<Class, TypeHandler> toHandlers = getHandler(from);
    if (toHandlers == null) {
      if (value instanceof String) {
        /**
         * We assume that this may be an enum type.  It may not be, but we try to decode it
         * as such.
         */
        try {
          T val = (T) Enum.valueOf((Class<? extends Enum>) to, (String) value);

          /**
           * If we successfully decoded an enum, then we cache this handler for future
           * use.
           */
          addHandler(from, to, new TypeHandler<String, T>() {
            public T getConverted(final String in) {
              return (T) Enum.valueOf((Class<? extends Enum>) to, in);
            }
          });

          return val;
        }
        catch (Exception e) {
          /**
           * Definitely not an enum, so do nothing.
           */
          return (T) value;
        }
      }

      return (T) value;
    }

    if (toHandlers.containsKey(to)) {
      return (T) toHandlers.get(to).getConverted(value);
    }
    else {
      return (T) value;
    }
  }

  public static void addHandler(final Class from, final Class to, final TypeHandler handler) {
    if (!handlers.containsKey(from)) {
      handlers.put(from, new HashMap<Class, TypeHandler>());
    }
    handlers.get(from).put(to, handler);
  }
}
