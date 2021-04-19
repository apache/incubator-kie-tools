/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.marshalling.server;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.marshalling.client.MarshallingSessionProviderFactory;
import org.jboss.errai.marshalling.client.api.Marshaller;
import org.jboss.errai.marshalling.client.api.MarshallerFactory;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.Parser;
import org.jboss.errai.marshalling.client.api.ParserFactory;
import org.jboss.errai.marshalling.client.api.annotations.AlwaysQualify;
import org.jboss.errai.marshalling.client.api.exceptions.MarshallingException;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.ObjectMarshaller;
import org.jboss.errai.marshalling.client.marshallers.QualifyingMarshallerWrapper;
import org.jboss.errai.marshalling.client.protocols.MarshallingSessionProvider;
import org.jboss.errai.marshalling.client.util.EncDecUtil;
import org.jboss.errai.marshalling.rebind.DefinitionsFactory;
import org.jboss.errai.marshalling.rebind.DefinitionsFactorySingleton;
import org.jboss.errai.marshalling.rebind.api.model.MappingDefinition;
import org.jboss.errai.marshalling.rebind.api.model.MemberMapping;
import org.jboss.errai.marshalling.rebind.util.MarshallingGenUtil;
import org.jboss.errai.marshalling.server.marshallers.DefaultArrayMarshaller;
import org.jboss.errai.marshalling.server.util.ServerMarshallUtil;
import org.slf4j.Logger;

/**
 * @author Mike Brock
 */
public class MappingContextSingleton {
  private static final ServerMappingContext context;
  private static final Logger log = getLogger("ErraiMarshalling");

  static {
    ClassScanner.setReflectionsScanning(true);

    ParserFactory.registerParser(
        new Parser() {
          @Override
          public EJValue parse(final String input) {
            return JSONDecoder.decode(input);
          }
        });

    ServerMappingContext sContext;

    try {
      if (!MarshallingGenUtil.isUseStaticMarshallers()) {
        sContext = loadDynamicMarshallers();
      }
      else {
        try {
          sContext = loadPrecompiledMarshallers();
        }
        catch (Throwable t) {
          log.debug("failed to load static marshallers", t);
          log.warn("static marshallers were not found.");

          if (MarshallingGenUtil.isForceStaticMarshallers()) {
            throw new IOException("Enforcing static marshallers but failed to load generated server marshallers", t);
          }

          sContext = loadDynamicMarshallers();
        }
        finally {
          ClassScanner.setReflectionsScanning(false);
        }
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
      log.error(t.getMessage());
      throw new RuntimeException("critical problem loading the marshallers", t);
    }

    context = sContext;
  }

  private static void dynamicMarshallingWarning() {
    log.warn("using dynamic marshallers. dynamic marshallers are designed" +
        " for development mode testing, and ideally should not be used in production. *");
  }

  public static ServerMappingContext loadPrecompiledMarshallers() throws Exception {

    final Class<? extends MarshallerFactory> cls = ServerMarshallUtil.getGeneratedMarshallerFactoryForServer();

    if (cls == null) {
      return loadDynamicMarshallers();
    }

    final Object o = cls.newInstance();
    final MarshallerFactory marshallerFactory = (MarshallerFactory) o;

    return new ServerMappingContext() {
      {
        MarshallingSessionProviderFactory.setMarshallingSessionProvider(new MarshallingSessionProvider() {
          @Override
          public MarshallingSession getEncoding() {
            return new EncodingSession(get());
          }

          @Override
          public MarshallingSession getDecoding() {
            return new DecodingSession(get());
          }

          @Override
          public boolean hasMarshaller(final String fqcn) {
            return marshallerFactory.getMarshaller(fqcn) != null;
          }

          @Override
          public Marshaller getMarshaller(final String fqcn) {
            return marshallerFactory.getMarshaller(fqcn);
          }

          @Override
          public void registerMarshaller(String fqcn, Marshaller m) {
            marshallerFactory.registerMarshaller(fqcn, m);
          }

        });
      }

      @Override
      public DefinitionsFactory getDefinitionsFactory() {
        return DefinitionsFactorySingleton.get();
      }

      @Override
      public Marshaller<Object> getMarshaller(final String clazz) {
        return marshallerFactory.getMarshaller(clazz);
      }

      @Override
      public boolean hasMarshaller(final String clazzName) {
        return marshallerFactory.getMarshaller(clazzName) != null;
      }

      @Override
      public boolean canMarshal(final String cls) {
        return hasMarshaller(cls);
      }
    };
  }

  private static final Marshaller<Object> NULL_MARSHALLER = new Marshaller<Object>() {
    @Override
    public Object demarshall(EJValue o, MarshallingSession ctx) {
      return null;
    }

    @Override
    public String marshall(Object o, MarshallingSession ctx) {
      return "null";
    }

    @Override
    public Object[] getEmptyArray() {
      return null;
    }
  };

  public static ServerMappingContext loadDynamicMarshallers() {
    dynamicMarshallingWarning();

    return new ServerMappingContext() {
      private final DefinitionsFactory factory = DefinitionsFactorySingleton.newInstance();

      {
        MarshallingSessionProviderFactory.setMarshallingSessionProvider(new MarshallingSessionProvider() {
          @Override
          public MarshallingSession getEncoding() {
            return new EncodingSession(get());
          }

          @Override
          public MarshallingSession getDecoding() {
            return new DecodingSession(get());
          }

          @Override
          public boolean hasMarshaller(final String fqcn) {
            return factory.hasDefinition(fqcn);
          }

          @Override
          public Marshaller getMarshaller(final String fqcn) {
            return factory.getDefinition(fqcn).getMarshallerInstance();
          }

          @Override
          public void registerMarshaller(String fqcn, Marshaller m) {
            throw new UnsupportedOperationException("Not implemented for dynamic marshalling");
          }
        });

        // ensure object marshaller is available before processing all mapping definitions, so we
        // can fall back to it when discovering array types with non-concrete component types.
        factory.getDefinition(Object.class).setMarshallerInstance(new ObjectMarshaller());

        for (final MappingDefinition def : factory.getMappingDefinitions()) {
          if (def.getMarshallerInstance() != null) {
          }
          else if (def.getServerMarshallerClass() != null) {
            try {
              final Marshaller<Object> marshallerInstance =
                  def.getServerMarshallerClass().asSubclass(Marshaller.class).newInstance();

              if (def.getServerMarshallerClass().isAnnotationPresent(AlwaysQualify.class)) {
                def.setMarshallerInstance(new QualifyingMarshallerWrapper<Object>(marshallerInstance,
                    (Class<Object>) def.getMappingClass().asClass()));
              }
              else {
                def.setMarshallerInstance(marshallerInstance);
              }
            }
            catch (InstantiationException e) {
              e.printStackTrace();
            }
            catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }

          addArrayMarshaller(def.getMappingClass().asArrayOf(1));
        }

        for (final MappingDefinition def : factory.getMappingDefinitions()) {
          for (final MemberMapping mapping : def.getMemberMappings()) {
            if (mapping.getType().isArray()) {
              addArrayMarshaller(mapping.getType());
            }
          }
        }

        for (final MetaClass arrayType : MarshallingGenUtil.getDefaultArrayMarshallers()) {
          addArrayMarshaller(arrayType);
        }
      }

      private void addArrayMarshaller(final MetaClass type) {
        MetaClass compType = type.getOuterComponentType().asBoxed();

        if (!factory.hasDefinition(type.getFullyQualifiedName())
            && !factory.hasDefinition(type.getInternalName())) {

          MappingDefinition outerDef = factory.getDefinition(compType);
          Marshaller<Object> marshaller;

          if (outerDef != null && !factory.shouldUseObjectMarshaller(compType)) {
            marshaller = outerDef.getMarshallerInstance();
          }
          else {
            compType = MetaClassFactory.get(Object.class);
            marshaller = factory.getDefinition(Object.class).getMarshallerInstance();
          }

          if (marshaller == null) {
            throw new MarshallingException("Failed to generate array marshaller for " + type.getCanonicalName() +
                " because marshaller for " + compType + " could not be found.");
          }

          MappingDefinition newDef = new MappingDefinition(
              EncDecUtil.qualifyMarshaller(
                  new DefaultArrayMarshaller(type, marshaller),
                  (Class<Object>) type.asClass()
                  ), type, true);

          if (outerDef != null) {
            newDef.setClientMarshallerClass(outerDef.getClientMarshallerClass());
          }

          factory.addDefinition(newDef);
        }
      }

      @Override
      public DefinitionsFactory getDefinitionsFactory() {
        return factory;
      }

      @Override
      public Marshaller<Object> getMarshaller(final String clazz) {
        if (clazz == null) {
          return NULL_MARSHALLER;
        }

        final MappingDefinition def = factory.getDefinition(clazz);

        if (def == null) {
          return null;
        }

        return def.getMarshallerInstance();
      }

      @Override
      public boolean hasMarshaller(final String clazzName) {
        return factory.hasDefinition(clazzName);
      }

      @Override
      public boolean canMarshal(final String cls) {
        return hasMarshaller(cls);
      }
    };
  }

  public static ServerMappingContext get() {
    return context;
  }
}
