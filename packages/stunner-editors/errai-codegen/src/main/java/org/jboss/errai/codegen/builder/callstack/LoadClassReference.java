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


package org.jboss.errai.codegen.builder.callstack;

import java.util.Map;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.RenderCacheStore;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;

/**
 * {@link CallElement} to create a class reference.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class LoadClassReference extends AbstractCallElement {
  private final MetaClass metaClass;

  public LoadClassReference(final MetaClass type) {
    this.metaClass = type;
  }

  @Override
  public void handleCall(final CallWriter writer,
                         final Context context,
                         final Statement statement) {
    writer.reset();

    try {
      nextOrReturn(writer, context, new ClassReference(metaClass));
    }
    catch (final GenerationException e) {
      blameAndRethrow(e);
    }
  }

  public static class ClassReference implements Statement {
    private final MetaClass metaClass;

    public ClassReference(final MetaClass metaClass) {
      this.metaClass = metaClass;
    }

    @Override
    public String generate(final Context context) {
      return getClassReference(metaClass, context);
    }

    @Override
    public MetaClass getType() {
      return metaClass;
    }
  }

  public static String getClassReference(final MetaType metaClass, final Context context) {
    return getClassReference(metaClass, context, true);
  }

  private static final RenderCacheStore<MetaType, String> CLASS_LITERAL_RENDER_CACHE = 
          () -> "CLASS_LITERAL_RENDER_CACHE";

  public static String getClassReference(final MetaType metaClass, final Context context, final boolean typeParms) {
    final Map<MetaType, String> cacheStore = context.getRenderingCache(CLASS_LITERAL_RENDER_CACHE);

    String result = cacheStore.get(metaClass);

    if (result == null) {
      result = _getClassReference(metaClass, context, typeParms);
    }
    return result;
  }

  private static String _getClassReference(final MetaType metaClass, final Context context, final boolean typeParms) {

    final MetaClass erased;
    if (metaClass instanceof MetaClass) {
      erased = ((MetaClass) metaClass).getErased();
    }
    else {
      return "Object";
    }

    String fqcn = erased.getCanonicalName();
    final int idx = fqcn.lastIndexOf('.');
    if (idx != -1) {

      if ((context.isAutoImportActive() || "java.lang".equals(erased.getPackageName()))
              && !context.hasImport(erased)) {
        context.addImport(erased);
      }

      if (context.hasImport(erased)) {
        fqcn = fqcn.substring(idx + 1);
      }
    }

    final StringBuilder buf = new StringBuilder(fqcn);
    if (typeParms) {
      buf.append(getClassReferencesForParameterizedTypes(((MetaClass) metaClass).getParameterizedType(), context));
    }

    return buf.toString();
  }

  private static final RenderCacheStore<MetaParameterizedType, String> PARMTYPE_LITERAL_RENDER_CACHE = 
          () -> "PARMTYPE_LITERAL_RENDER_CACHE";

  private static String getClassReferencesForParameterizedTypes(final MetaParameterizedType parameterizedType,
                                                                final Context context) {
    final Map<MetaParameterizedType, String> cacheStore = context.getRenderingCache(PARMTYPE_LITERAL_RENDER_CACHE);

    String result = cacheStore.get(parameterizedType);

    if (result == null) {

      final StringBuilder buf = new StringBuilder(64);

      if (parameterizedType != null && parameterizedType.getTypeParameters().length != 0) {
        buf.append("<");

        for (int i = 0; i < parameterizedType.getTypeParameters().length; i++) {
          final MetaType typeParameter = parameterizedType.getTypeParameters()[i];

          if (typeParameter instanceof MetaParameterizedType) {
            final MetaParameterizedType parameterizedTypeParemeter = (MetaParameterizedType) typeParameter;
            buf.append(getClassReference(parameterizedTypeParemeter.getRawType(), context));
            buf.append(getClassReferencesForParameterizedTypes(parameterizedTypeParemeter, context));
          }
          else {
            // fix to a weirdness in the GWT deferred bining API;
            final String ref = getClassReference(typeParameter, context);
            if ("Object".equals(ref)) {
              // ignore;
              return "";
            }

            buf.append(ref);
          }

          if (i + 1 < parameterizedType.getTypeParameters().length)
            buf.append(", ");
        }
        buf.append(">");
      }

      result = buf.toString();
      cacheStore.put(parameterizedType, result);
    }

    return result;

  }

  @Override
  public String toString() {
    return "[[LoadClassReference<" + metaClass.getFullyQualifiedName() + ">]" + next + "]";
  }
}
