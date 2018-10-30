/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.uberfire.ext.metadata.backend.infinispan.provider;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.AuthenticationConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.BaseMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.ext.metadata.backend.infinispan.exceptions.InfinispanException;
import org.uberfire.ext.metadata.backend.infinispan.proto.KObjectMarshaller;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Schema;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.SchemaGenerator;
import org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil;
import org.uberfire.ext.metadata.model.KObject;

import static java.util.stream.Collectors.toList;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

public class InfinispanContext implements Disposable {

    private static final String PORT = "org.appformer.ext.metadata.infinispan.port";
    private static final String HOST = "org.appformer.ext.metadata.infinispan.host";
    private static final String USERNAME = "org.appformer.ext.metadata.infinispan.username";
    private static final String PASSWORD = "org.appformer.ext.metadata.infinispan.password";
    private static final String REALM = "org.appformer.ext.metadata.infinispan.realm";

    private static final String TYPES_CACHE = "types";
    private static final String SCHEMAS_CACHE = "schemas";
    private static final String PROTO_EXTENSION = ".proto";
    private static final String SCHEMA_PROTO = "schema.proto";
    private static final String ORG_KIE = "org.kie.";
    public static final String SASL_MECHANISM = "DIGEST-MD5";
    private final RemoteCacheManager cacheManager;
    private final KieProtostreamMarshaller marshaller = new KieProtostreamMarshaller();
    private final SchemaGenerator schemaGenerator;

    private final InfinispanConfiguration infinispanConfiguration;

    private static final class LazyHolder {

        static final Map<String, String> PROPERTIES = new HashMap<String, String>() {{
            put(HOST,
                System.getProperty(HOST,
                                   "127.0.0.1"));
            put(PORT,
                System.getProperty(PORT,
                                   "11222"));
            put(USERNAME,
                System.getProperty(USERNAME,
                                   ""));
            put(PASSWORD,
                System.getProperty(PASSWORD,
                                   ""));
            put(REALM,
                System.getProperty(REALM,
                                   "ApplicationRealm"));
        }};
        static final InfinispanContext INSTANCE = new InfinispanContext(PROPERTIES);
    }

    public static InfinispanContext getInstance() {
        return LazyHolder.INSTANCE;
    }

    private InfinispanContext(Map<String, String> properties) {
        this.infinispanConfiguration = new InfinispanConfiguration();
        schemaGenerator = new SchemaGenerator();

        cacheManager = this.createRemoteCache(properties);

        if (!this.getIndices().contains(TYPES_CACHE)) {
            cacheManager.administration().createCache(TYPES_CACHE,
                                                      this.infinispanConfiguration.getConfiguration(TYPES_CACHE));
        }

        if (!this.getIndices().contains(SCHEMAS_CACHE)) {
            cacheManager.administration().createCache(SCHEMAS_CACHE,
                                                      this.infinispanConfiguration.getConfiguration(SCHEMAS_CACHE));
        }

        marshaller.registerMarshaller(new KieProtostreamMarshaller.KieMarshallerSupplier<KObject>() {
            @Override
            public String extractTypeFromEntity(KObject entity) {
                return ORG_KIE + AttributesUtil.toProtobufFormat(entity.getClusterId() + "_" + entity.getType().getName());
            }

            @Override
            public Class<KObject> getJavaClass() {
                return KObject.class;
            }

            @Override
            public BaseMarshaller<KObject> getMarshallerForType(String typeName) {
                return new KObjectMarshaller(typeName);
            }
        });

        SerializationContext serializationContext = ProtoStreamMarshaller.getSerializationContext(cacheManager);

        addProtobufClass(serializationContext,
                         SCHEMA_PROTO,
                         Schema.class);

        this.loadProtobufSchema(getProtobufCache());
    }

    private RemoteCacheManager createRemoteCache(Map<String, String> properties) {
        String username = properties.get(USERNAME);
        String password = properties.get(PASSWORD);
        String realm = properties.get(REALM);
        String host = properties.get(HOST);
        String port = properties.get(PORT);
        try {
            ConfigurationBuilder builder = getMaybeSecurityBuilder(username,
                                                                   password,
                                                                   realm)
                    .addServer()
                    .host(host)
                    .port(Integer.parseInt(port))
                    .marshaller(new ProtoStreamMarshaller())
                    .marshaller(marshaller);
            return new RemoteCacheManager(builder.build());
        } catch (Exception e) {
            throw new InfinispanException(MessageFormat.format("Error trying to connect to server <{0}:{1}>",
                                                               host,
                                                               port),
                                          e);
        }
    }

    private AuthenticationConfigurationBuilder getMaybeSecurityBuilder(String username,
                                                                       String password,
                                                                       String realm) {

        ConfigurationBuilder b = new ConfigurationBuilder();

        if (StringUtils.isNotEmpty(username)) {
            checkNotEmpty("password",
                          password);
            checkNotEmpty("realm",
                          realm);
            return b.security().authentication()
                    .enable()
                    .saslMechanism(SASL_MECHANISM)
                    .callbackHandler(new LoginHandler(username,
                                                      password.toCharArray(),
                                                      realm));
        } else {
            return b.security().authentication().disable();
        }
    }

    private void addProtobufClass(SerializationContext serializationContext,
                                  String protoName,
                                  Class<?> clazz) {
        try {
            ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
            protoSchemaBuilder.fileName(protoName);
            protoSchemaBuilder.addClass(clazz);
            String schemaString = protoSchemaBuilder.build(serializationContext);
            this.getProtobufCache().put(protoName,
                                        schemaString);
        } catch (IOException e) {
            throw new InfinispanException("Can't add protobuf class <" + protoName + "> to cache",
                                          e);
        }
    }

    private RemoteCache<String, String> getProtobufCache() {
        return this.cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
    }

    public RemoteCache<String, KObject> getCache(String index) {
        String i = AttributesUtil.toProtobufFormat(index).toLowerCase();
        if (!this.getIndices().contains(i)) {
            cacheManager
                    .administration()
                    .createCache(i,
                                 this.infinispanConfiguration.getIndexedConfiguration(i));
        }
        return this.cacheManager.getCache(i);
    }

    public List<String> getTypes(String index) {
        return this.getSchema(index)
                .map(schema -> schema.getMessages().stream().map(x -> x.getName()).collect(toList()))
                .orElse(Collections.emptyList());
    }

    public void addProtobufSchema(String clusterId,
                                  Schema schema) {

        try {
            String protoTypeName = AttributesUtil.toProtobufFormat(clusterId);
            RemoteCache<String, String> metadataCache = getProtobufCache();
            String proto = this.schemaGenerator.generate(schema);

            marshaller.registerSchema(protoTypeName,
                                      proto,
                                      KObject.class);
            metadataCache.put(protoTypeName + PROTO_EXTENSION,
                              proto);
        } catch (IOException e) {
            throw new InfinispanException("Can't add protobuf schema <" + schema.getName() + "> to cache",
                                          e);
        }
    }

    public void loadProtobufSchema(RemoteCache<String, String> metadataCache) {
        metadataCache.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(SCHEMA_PROTO))
                .forEach((entry) -> {
                    int index = entry.getKey().lastIndexOf('.');
                    String protoTypeName = entry.getKey().substring(0,
                                                                    index);
                    String proto = entry.getValue();

                    try {
                        marshaller.registerSchema(protoTypeName,
                                                  proto,
                                                  KObject.class);
                    } catch (IOException e) {
                        throw new InfinispanException("Can't add protobuf schema <" + protoTypeName + "> to cache",
                                                      e);
                    }
                });
    }

    @Override
    public void dispose() {
        if (this.cacheManager.isStarted()) {
            this.cacheManager.stop();
        }
    }

    public List<String> getIndices() {
        return new ArrayList<>(this.cacheManager.getCacheNames());
    }

    public Optional<Schema> getSchema(String clusterId) {
        Schema schema = (Schema) this.cacheManager.getCache(SCHEMAS_CACHE).get(clusterId.toLowerCase());
        return Optional.ofNullable(schema);
    }

    public void addSchema(Schema schema) {
        this.cacheManager.getCache(SCHEMAS_CACHE).put(AttributesUtil.toProtobufFormat(schema.getName()).toLowerCase(),
                                                      schema);
    }
}

