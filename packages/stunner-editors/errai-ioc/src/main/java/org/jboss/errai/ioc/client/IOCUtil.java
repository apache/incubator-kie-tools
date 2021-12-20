/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCEnvironment;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

import com.google.gwt.core.client.GWT;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class IOCUtil {

  private IOCUtil() {};

  private static final IOCEnvironment IOC_ENVIRONMENT = GWT.<IOCEnvironment> create(IOCEnvironment.class);

  public static <T> T getInstance(final Class<T> type, final Annotation... qualifiers) {
    return getSyncBean(type, qualifiers).getInstance();
  }

  public static <T> SyncBeanDef<T> getSyncBean(final Class<T> type, final Annotation... qualifiers) {
    try {
      return IOC.getBeanManager().lookupBean(type, qualifiers);
    } catch (final IOCResolutionException ex) {
      if (IOC_ENVIRONMENT.isAsync() && isUnsatisfied(type, qualifiers)) {
        throw new RuntimeException("No bean satisfied " + prettyQualifiersAndType(type, qualifiers)
                + ". Hint: Types loaded via Instance should not be @LoadAsync.", ex);
      }
      else {
        throw ex;
      }
    }
  }

  public static void destroy(final Object instance) {
    IOC.getBeanManager().destroyBean(instance);
  }

  public static boolean isUnsatisfied(final Class<?> type, final Annotation... qualifiers) {
    return IOC.getBeanManager().lookupBeans(type, qualifiers).isEmpty();
  }

  public static boolean isAmbiguous(final Class<?> type, final Annotation... qualifiers) {
    return IOC.getBeanManager().lookupBeans(type, qualifiers).size() > 1;
  }

  public static Annotation[] joinQualifiers(final Annotation[] a1, final Annotation[] a2) {
    final Set<Annotation> annos = new HashSet<>();
    annos.addAll(Arrays.asList(a1));
    annos.addAll(Arrays.asList(a2));

    return annos.toArray(new Annotation[annos.size()]);
  }

  private static String prettyQualifiersAndType(final Class<?> type, final Annotation... qualifiers) {
    final StringBuilder builder = new StringBuilder();
    for (final Annotation qual : qualifiers) {
      builder.append('@').append(qual.annotationType().getSimpleName()).append(' ');
    }
    builder.append(type.getSimpleName());

    return builder.toString();
  }

}
