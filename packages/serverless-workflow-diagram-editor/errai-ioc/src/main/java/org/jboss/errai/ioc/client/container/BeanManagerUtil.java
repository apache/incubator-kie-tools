/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.client.container;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class BeanManagerUtil {

  public static <T> IOCResolutionException ambiguousResolutionException(Class<T> type, final Collection<IOCBeanDef<T>> resolved,
          Annotation... qualifiers) {
    final StringBuilder builder = new StringBuilder();
    builder.append("Multiple beans matched " + type.getName() + " with qualifiers " + qualifiersToString(qualifiers) + "\n")
           .append("Found:\n");
    for (final IOCBeanDef<T> beanDef : resolved) {
      builder.append("  ")
             .append(beanDef.toString())
             .append("\n");
    }
    IOCResolutionException iocResolutionException = new IOCResolutionException(builder.toString());
    return iocResolutionException;
  }

  public static <T> IOCResolutionException unsatisfiedResolutionException(Class<T> type, Annotation... qualifiers) {
    return new IOCResolutionException("No beans matched " + type.getName() + " with qualifiers " + qualifiersToString(qualifiers));
  }

  public static String beanDeftoString(final FactoryHandle handle) {
    return "[type=" + handle.getActualType().getName() + ", qualifiers=" + handle.getQualifiers() + "]";
  }

  private static String qualifiersToString(final Annotation[] qualifiers) {
    final StringBuilder builder = new StringBuilder().append("{ ");
    for (final Annotation qualifier : qualifiers) {
      builder.append(qualifier.annotationType().getName())
             .append(", ");
    }
    builder.delete(builder.length() - ", ".length(), builder.length());
    builder.append(" }");

    return builder.toString();
  }

}
