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


package org.jboss.errai.ioc.rebind.ioc.extension.builtin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import jsinterop.annotations.JsType;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.ioc.client.api.IOCExtension;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessor;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.injector.api.ExtensionTypeCallback;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

/**
 * This extension tracks non-native {@link JsType} interfaces that are implemented by 0 or 1 classes. Why? Because in
 * order for use of {@link JsType} instances between scripts to work, the compiler must not prune or inline method calls
 * to a {@link JsType} interface.
 *
 * If the compiler finds only a single implementation of an interface, it will replace all method calls on the interface
 * with method calls on the implementing class.
 *
 * If the compiler finds no implementations of an interface, it will generate method calls on null!
 *
 * By generating dummy implementations, we ensure the the compiler is never able to "optimize" calls on {@link JsType}
 * interfaces in these ways.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@IOCExtension
public class JsTypeAntiInliningExtension implements IOCExtensionConfigurator {

  private static Multiset<MetaClass> requiringDummyImpls = null;

  public static boolean requiresAntiInliningDummy(final MetaClass type) {
    return requiringDummyImpls.contains(type);
  }

  public static int numberOfRequiredAntiInliningDummies(final MetaClass type) {
    return requiringDummyImpls.count(type);
  }

  @Override
  public void configure(final IOCProcessingContext context, final InjectionContext injectionContext) {
  }

  @Override
  public void afterInitialization(final IOCProcessingContext context, final InjectionContext injectionContext) {
    if (IOCProcessor.isJsInteropSupportEnabled()) {
      injectionContext.registerExtensionTypeCallback(new ExtensionTypeCallback() {
        final Multimap<MetaClass, MetaClass> jsTypeIfaceImpls = HashMultimap.create();
        final Set<MetaClass> jsTypeIfaces = new HashSet<>();
        @Override
        public void init() {
          requiringDummyImpls = null;
        }

        @Override
        public void callback(final MetaClass type) {
          if (!type.getFullyQualifiedName().startsWith("java.util")) {
            if (type.isInterface()) {
              jsTypeIfaces.add(type);
            }
            else if (type.isConcrete() && type.isPublic()) {
              findJsTypeIfaces(type)
              .forEach(iface -> {
                jsTypeIfaces.add(iface);
                jsTypeIfaceImpls.put(iface, type);
              });
            }
          }
        }

        @Override
        public void finish() {
          final Multiset<MetaClass> noOrSingleImplJsTypeIfaces =
                  jsTypeIfaces
                  .stream()
                  .flatMap(iface -> {
                    final Collection<MetaClass> impls = jsTypeIfaceImpls.get(iface);
                    if (impls.isEmpty()) {
                      return stream(new MetaClass[] { iface, iface });
                    }
                    else if (impls.size() == 1) {
                      return stream(new MetaClass[] { iface });
                    }
                    else {
                      return stream(new MetaClass[0]);
                    }
                  })
                  .collect(toCollection(() -> HashMultiset.create()));
          requiringDummyImpls = noOrSingleImplJsTypeIfaces;
        }
      });
    }
  }

  private Stream<MetaClass> findJsTypeIfaces(final MetaClass type) {
    return stream(type.getInterfaces())
      .flatMap(iface -> stream(iface.getInterfaces()))
      .distinct()
      .filter(iface -> !iface.getFullyQualifiedName().startsWith("java.util"))
      .filter(iface -> iface.isAnnotationPresent(JsType.class) && !iface.getAnnotation(JsType.class).isNative());
  }

}
