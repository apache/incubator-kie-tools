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


package org.jboss.errai.codegen.literal;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.codegen.AnnotationEncoder;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.RenderCacheStore;
import org.jboss.errai.codegen.SnapshotMaker;
import org.jboss.errai.codegen.exception.NotLiteralizableException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

import static org.jboss.errai.codegen.builder.callstack.LoadClassReference.getClassReference;

/**
 * The literal factory provides a LiteralValue for the specified object (if possible).
 *
 * @author Mike Brock <cbrock@redhat.com>
 */
public class LiteralFactory {


  /**
   * Returns a literal value (specialization of Statement) representing the
   * given object in the given context.
   *
   * @param o
   *     The object to create a literal value for.
   *
   * @return a LiteralValue for the given object. Never null.
   *
   * @throws NotLiteralizableException
   *     if {@code o} cannot be literalized
   */
  public static LiteralValue<?> getLiteral(final Object o) {
    return getLiteral(null, o);
  }

  /**
   * Returns a literal value (specialization of Statement) representing the
   * given object in the given context.
   *
   * @param context
   *     The context the literal value will be code-generated in. Contexts
   *     can specify additional literalizable types. See {@link Context#addLiteralizableClass(Class)}.
   * @param o
   *     The object to create a literal value for.
   *
   * @return a LiteralValue for the given object. Never null.
   *
   * @throws NotLiteralizableException
   *     if {@code o} cannot be literalized
   */
  public static LiteralValue<?> getLiteral(final Context context, final Object o) {
    return getLiteral(context, o, true);
  }

  private static final RenderCacheStore<Object, LiteralValue<?>> CLASS_LITERAL_RENDER_CACHE = 
          () -> "LITERAL_CACHE_STORE";

  /**
   * Implementation for the public getLiteral() methods.
   *
   * @param context
   *     The context the literal value will be code-generated in. Contexts
   *     can specify additional literalizable types.
   * @param o
   *     The object to create a literal value for.
   *
   * @return a LiteralValue for the given object. Never null.
   *
   * @throws NotLiteralizableException
   *     if {@code o} cannot be literalized
   */
  private static LiteralValue<?> getLiteral(final Context context,
                                            final Object o,
                                            final boolean throwIfNotLiteralizable) {
    Map<Object, LiteralValue<?>> LITERAL_CACHE = null;
    if (context != null) {
      LITERAL_CACHE = context.getRenderingCache(CLASS_LITERAL_RENDER_CACHE);
    }

    LiteralValue<?> result = LITERAL_CACHE != null ? LITERAL_CACHE.get(o) : null;
    if (result == null) {

      if (o instanceof MetaClass) {
        result = new MetaClassLiteral((MetaClass) o);
      }
      else if (o instanceof Annotation) {
        result = new LiteralValue<Annotation>((Annotation) o) {
          @Override
          public String getCanonicalString(final Context context) {
            return AnnotationEncoder.encode((Annotation) o).generate(context);
          }
        };
      }
      else if (o instanceof Enum) {
        result = new LiteralValue<Enum>((Enum) o) {
          @Override
          public String getCanonicalString(final Context context) {
            return getClassReference(MetaClassFactory.get(o.getClass()), context) + "." + ((Enum) o).name();
          }
        };
      }
      else {
        result = _getLiteral(context, o, throwIfNotLiteralizable);
      }

      // avoid caching the null; we don't want that returned from the cache!
      if (result != null && LITERAL_CACHE != null) {
        LITERAL_CACHE.put(o, result);
      }
    }

    return result;
  }

  private static LiteralValue<?> _getLiteral(final Context context,
                                             final Object o,
                                             final boolean throwIfNotLiteralizable) {
    if (o == null) {
      return NullLiteral.INSTANCE;
    }

    if (o instanceof String) {
      return new StringLiteral((String) o);
    }
    else if (o instanceof Integer) {
      return new IntValue((Integer) o);
    }
    else if (o instanceof Character) {
      return new CharValue((Character) o);
    }
    else if (o instanceof Boolean) {
      return new BooleanValue((Boolean) o);
    }
    else if (o instanceof Short) {
      return new ShortValue((Short) o);
    }
    else if (o instanceof Long) {
      return new LongValue((Long) o);
    }
    else if (o instanceof Double) {
      return new DoubleValue((Double) o);
    }
    else if (o instanceof Float) {
      return new FloatValue((Float) o);
    }
    else if (o instanceof Byte) {
      return new ByteValue((Byte) o);
    }
    else if (o instanceof Class) {
      return new ClassLiteral((Class) o);
    }
    else if (o instanceof MetaClass) {
      return new MetaClassLiteral((MetaClass) o);
    }
    else if (o instanceof Set) {
      return new SetValue((Set) o);
    }
    else if (o instanceof List) {
      return new ListValue((List) o);
    }
    else if (o instanceof Map) {
      return new MapValue((Map) o);
    }
    else if (o.getClass().isArray()) {
      return new ArrayLiteral(o);
    }
    else if (context != null && context.isLiteralizableClass(o.getClass())) {
      // the new instance of LiteralValue here provides surprising (but desirable) caching behaviour.
      // see LiteralTest.testGenerateObjectArrayThenModifyThenGenerateAgain for details.
      return new LiteralValue<Object>(o) {
        @Override
        public String getCanonicalString(final Context context) {
          final Class<?> targetType = context.getLiteralizableTargetType(o.getClass());
          return SnapshotMaker.makeSnapshotAsSubclass(o, targetType, targetType, null).generate(context);
        }
      };
    }
    else {
      if (throwIfNotLiteralizable) {
        throw new NotLiteralizableException(o);
      }
      return null;
    }
  }

  /**
   * Returns a literal value (specialization of Statement) representing the
   * given object in the given context, or null if the value is not
   * literalizable.
   *
   * @param o
   *     The object to create a literal value for.
   *
   * @return a LiteralValue for the given object, or null if the value cannot be
   *         expressed as a literal.
   */
  public static LiteralValue<?> isLiteral(final Object o) {
    return getLiteral(null, o, false);
  }
}
