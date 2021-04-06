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

package org.jboss.errai.marshalling.client.api;

import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.MarshallingSessionProviderFactory;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.api.json.impl.gwt.GWTJSON;
import org.jboss.errai.marshalling.client.protocols.MarshallingSessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class MarshallerFramework implements EntryPoint {
  private static MarshallerFactory marshallerFactory;
  private static Logger logger = LoggerFactory.getLogger(MarshallerFramework.class);

  static {
    logger.debug("Initializing marshalling framework...");
    InitVotes.waitFor(MarshallerFramework.class);
    marshallerFactory = GWT.create(MarshallerFactory.class);

    ParserFactory.registerParser(new Parser() {
      @Override
      public EJValue parse(final String input) {
        return GWTJSON.wrap(JSONParser.parseStrict(input));
      }
    });

    InitVotes.voteFor(MarshallerFramework.class);

    MarshallerFramework.initializeDefaultSessionProvider();
  }

  @Override
  public void onModuleLoad() {
  }

  public static void initializeDefaultSessionProvider() {
    if (!MarshallingSessionProviderFactory.isMarshallingSessionProviderRegistered()) {
      MarshallingSessionProviderFactory.setMarshallingSessionProvider(new MarshallingSessionProvider() {
        @Override
        public MarshallingSession getEncoding() {
          return new MarshallerFramework.JSONMarshallingSession();
        }

        @Override
        public MarshallingSession getDecoding() {
          return new MarshallerFramework.JSONMarshallingSession();
        }

        @Override
        public boolean hasMarshaller(final String fqcn) {
          return MarshallerFramework.getMarshallerFactory().getMarshaller(fqcn) != null;
        }

        @Override
        public Marshaller getMarshaller(final String fqcn) {
          return MarshallerFramework.getMarshallerFactory().getMarshaller(fqcn);
        }

        @Override
        public void registerMarshaller(final String fqcn, final Marshaller m) {
          MarshallerFramework.getMarshallerFactory().registerMarshaller(fqcn, m);
        }
      });
    }
  }

  public static class JSONMarshallingSession extends AbstractMarshallingSession {
    public JSONMarshallingSession() {
      super(new MappingContext() {
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
          return marshallerFactory.getMarshaller(cls) != null;
        }
      });
    }

    @Override
    public String determineTypeFor(final String formatType, final Object o) {
      if (((EJValue) o).isObject() != null) {
        final EJObject jsonObject = ((EJValue) o).isObject();
        if (jsonObject.containsKey(SerializationParts.ENCODED_TYPE)) {
          return jsonObject.get(SerializationParts.ENCODED_TYPE).isString().stringValue();
        }
        else {
          return Map.class.getName();
        }
      }
      else if (((EJValue) o).isString() != null) {
        return String.class.getName();
      }
      else if (((EJValue) o).isNumber() != null) {
        return Double.class.getName();
      }
      else if (((EJValue) o).isBoolean() != null) {
        return Boolean.class.getName();
      }
      else if (((EJValue) o).isArray() != null) {
        return List.class.getName();
      }
      else if (((EJValue) o).isNull()) {
        return null;
      }
      throw new RuntimeException("unknown type: cannot reverse map value to concrete Java type: " + o);
    }
  }

  public static boolean canMarshall(final String type) {
    return marshallerFactory.getMarshaller(type) != null;
  }

  public static MarshallerFactory getMarshallerFactory() {
    return marshallerFactory;
  }
}
