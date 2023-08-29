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


package org.jboss.errai.databinding.rebind;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.jboss.errai.codegen.Cast;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.MethodBlockBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.config.rebind.AbstractAsyncGenerator;
import org.jboss.errai.config.rebind.GenerateAsync;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.BindableProxyLoader;
import org.jboss.errai.databinding.client.BindableProxyProvider;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jboss.errai.databinding.client.api.Convert;
import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.databinding.client.api.DefaultConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates the proxy loader for {@link Bindable}s.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@GenerateAsync(BindableProxyLoader.class)
public class BindableProxyLoaderGenerator extends AbstractAsyncGenerator {
  private final Logger log = LoggerFactory.getLogger(BindableProxyLoaderGenerator.class);
  private final String packageName = BindableProxyLoader.class.getPackage().getName();
  private final String className = BindableProxyLoader.class.getSimpleName() + "Impl";

  @Override
  public String generate(final TreeLogger logger, final GeneratorContext context, final String typeName)
      throws UnableToCompleteException {

    return startAsyncGeneratorsAndWaitFor(BindableProxyLoader.class, context, logger, packageName, className);
  }

  @Override
  protected String generate(final TreeLogger logger, final GeneratorContext context) {
    ClassStructureBuilder<?> classBuilder = ClassBuilder.implement(BindableProxyLoader.class);
    final MethodBlockBuilder<?> loadProxies = classBuilder.publicMethod(void.class, "loadBindableProxies");

    final Set<MetaClass> allBindableTypes = DataBindingUtil.getAllBindableTypes(context);
    addCacheRelevantClasses(allBindableTypes);

    for (final MetaClass bindable : allBindableTypes) {
      if (bindable.isFinal()) {
        throw new RuntimeException("@Bindable type cannot be final: " + bindable.getFullyQualifiedName());
      }

      if (bindable.getDeclaredConstructor(new MetaClass[0]) == null || !bindable.getDeclaredConstructor(new MetaClass[0]).isPublic()) {
        throw new RuntimeException("@Bindable type needs a public default no-arg constructor: "
            + bindable.getFullyQualifiedName());
      }

      final ClassStructureBuilder<?> bindableProxy = new BindableProxyGenerator(bindable, logger).generate();
      loadProxies.append(new InnerClass(bindableProxy.getClassDefinition()));
      final Statement proxyProvider =
          ObjectBuilder.newInstanceOf(BindableProxyProvider.class)
              .extend()
              .publicOverridesMethod("getBindableProxy", Parameter.of(Object.class, "model"))
              .append(Stmt.nestedCall(Stmt.newObject(bindableProxy.getClassDefinition())
                  .withParameters(Cast.to(bindable, Stmt.loadVariable("model")))).returnValue())
              .finish()
              .publicOverridesMethod("getBindableProxy")
              .append(
                  Stmt.nestedCall(
                      Stmt.newObject(bindableProxy.getClassDefinition()))
                      .returnValue())
              .finish()
              .finish();
      loadProxies.append(Stmt.invokeStatic(BindableProxyFactory.class, "addBindableProxy", bindable, proxyProvider));
    }

    generateDefaultConverterRegistrations(loadProxies, context);

    classBuilder = (ClassStructureBuilder<?>) loadProxies.finish();
    return classBuilder.toJavaString();
  }

  /**
   * Scans for and registers global default converters.
   */
  private void generateDefaultConverterRegistrations(final MethodBlockBuilder<?> loadProxies,
      final GeneratorContext context) {

    final Collection<MetaClass> defaultConverters = ClassScanner.getTypesAnnotatedWith(DefaultConverter.class,
            RebindUtils.findTranslatablePackages(context), context);
    addCacheRelevantClasses(defaultConverters);
    for (final MetaClass converter : defaultConverters) {

      Statement registerConverterStatement = null;
      for (final MetaClass iface : converter.getInterfaces()) {
        if (iface.getErased().equals(MetaClassFactory.get(Converter.class))) {
          final MetaParameterizedType parameterizedInterface = iface.getParameterizedType();
          if (parameterizedInterface != null) {
            final MetaType[] typeArgs = parameterizedInterface.getTypeParameters();
            if (typeArgs != null && typeArgs.length == 2) {
              registerConverterStatement = Stmt.invokeStatic(Convert.class, "registerDefaultConverter",
                  typeArgs[0], typeArgs[1], Stmt.newObject(converter));
            }
          }
        }
      }

      if (registerConverterStatement != null) {
        loadProxies.append(registerConverterStatement);
      }
      else {
        log.warn("Ignoring @DefaultConverter: " + converter
            + "! Make sure it implements Converter and specifies type arguments for the model and widget type");
      }
    }
  }

  @Override
  protected boolean isRelevantClass(final MetaClass clazz) {
    for (final Annotation anno : clazz.getAnnotations()) {
      if (anno.annotationType().equals(Bindable.class) || anno.annotationType().equals(DefaultConverter.class)) {
        return true;
      }
    }

    return false;
  }
}
