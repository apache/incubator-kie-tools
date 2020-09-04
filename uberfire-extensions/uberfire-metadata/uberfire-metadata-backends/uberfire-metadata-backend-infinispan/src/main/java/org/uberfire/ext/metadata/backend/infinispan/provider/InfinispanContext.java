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
import java.util.Arrays;
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
import org.infinispan.client.hotrod.configuration.SaslQop;
import org.infinispan.client.hotrod.exceptions.HotRodClientException;
import org.infinispan.client.hotrod.impl.RemoteCacheImpl;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.CacheConfigurationException;
import org.infinispan.protostream.BaseMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.ext.metadata.backend.infinispan.exceptions.InfinispanException;
import org.uberfire.ext.metadata.backend.infinispan.proto.KObjectMarshaller;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Schema;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.SchemaGenerator;
import org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil;
import org.uberfire.ext.metadata.backend.infinispan.utils.Retry;
import org.uberfire.ext.metadata.model.KObject;

import static java.util.stream.Collectors.toList;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

public class InfinispanContext implements Disposable {

    private static final String PORT = "org.appformer.ext.metadata.infinispan.port";
    private static final String HOST = "org.appformer.ext.metadata.infinispan.host";
    private static final String TIMEOUT = "org.appformer.ext.metadata.infinispan.timeout";
    private static final String RETRIES = "org.appformer.ext.metadata.infinispan.retries";
    private static final String USERNAME = "org.appformer.ext.metadata.infinispan.username";
    private static final String PASSWORD = "org.appformer.ext.metadata.infinispan.password";
    private static final String REALM = "org.appformer.ext.metadata.infinispan.realm";
    private static final String SERVER_NAME = "org.appformer.ext.metadata.infinispan.server.name";
    private static final String SASL_QOP = "org.appformer.ext.metadata.infinispan.sasl.qop";

    private static final String TYPES_CACHE = "types";
    private static final String SCHEMAS_CACHE = "schemas";
    private static final String PROTO_EXTENSION = ".proto";
    private static final String SCHEMA_PROTO = "schema.proto";
    private static final String ORG_KIE = "org.kie.";
    public static final String SASL_MECHANISM = "DIGEST-MD5";
    private static final String CACHE_PREFIX = "appformer_";
    private final InfinispanPingService pingService;
    private RemoteCacheManager cacheManager;
    private KieProtostreamMarshaller marshaller = new KieProtostreamMarshaller();
    private SchemaGenerator schemaGenerator;

    private InfinispanConfiguration infinispanConfiguration;

    private Logger logger = LoggerFactory.getLogger(InfinispanContext.class);
    private Optional<Runnable> initializationObserver = Optional.empty();

    private static final class LazyHolder {

        static final Map<String, String> PROPERTIES = new HashMap<String, String>() {{
            put(HOST,
                System.getProperty(HOST,
                                   "127.0.0.1"));
            put(PORT,
                System.getProperty(PORT,
                                   "11222"));
            put(TIMEOUT,
                System.getProperty(TIMEOUT,
                                   "30000"));
            put(RETRIES,
                System.getProperty(RETRIES,
                                   "5"));
            put(USERNAME,
                System.getProperty(USERNAME,
                                   ""));
            put(PASSWORD,
                System.getProperty(PASSWORD,
                                   ""));
            put(REALM,
                System.getProperty(REALM,
                                   "ApplicationRealm"));
            put(SERVER_NAME,
                System.getProperty(SERVER_NAME,
                                   ""));
            put(SASL_QOP,
                System.getProperty(SASL_QOP,
                                   ""));
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

        this.pingService = new InfinispanPingService((RemoteCacheImpl) this.cacheManager.getCache());

        createBaseIndex();

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
                         addCachePrefix(SCHEMA_PROTO),
                         Schema.class);

        retrieveProbufSchemas();
    }

    private void createBaseIndex() {
        if (!this.getIndices().contains(SCHEMAS_CACHE)) {
            this.initializationObserver.orElse(() -> {
            }).run();
            cacheManager.administration().createCache(getSchemaCacheName(),
                                                      this.infinispanConfiguration.getConfiguration(getSchemaCacheName()));
        }
    }

    public void retrieveProbufSchemas() {
        this.loadProtobufSchema(getProtobufCache());
    }

    private String getSchemaCacheName() {
        return addCachePrefix(SCHEMAS_CACHE);
    }

    private String getTypesCacheName() {
        return addCachePrefix(TYPES_CACHE);
    }

    private RemoteCacheManager createRemoteCache(Map<String, String> properties) {

        String host = properties.get(HOST);
        String port = properties.get(PORT);
        String timeout = properties.get(TIMEOUT);
        String retries = properties.get(RETRIES);

        try {
            ConfigurationBuilder builder = getMaybeSecurityBuilder(properties)
                    .addServer()
                    .host(host)
                    .port(Integer.parseInt(port))
                    .connectionTimeout(Integer.parseInt(timeout))
                    .maxRetries(Integer.parseInt(retries))
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

    private AuthenticationConfigurationBuilder getMaybeSecurityBuilder(Map<String, String> properties) {

        String username = properties.get(USERNAME);
        String password = properties.get(PASSWORD);
        String realm = properties.get(REALM);
        String saslQop = properties.get(SASL_QOP);
        String serverName = properties.get(SERVER_NAME);

        ConfigurationBuilder b = new ConfigurationBuilder();

        if (StringUtils.isNotEmpty(username)) {
            checkNotEmpty("password",
                          password);
            checkNotEmpty("realm",
                          realm);
            checkNotEmpty("qop",
                          saslQop);
            checkNotEmpty("serverName",
                          serverName);
            return b.security().authentication()
                    .enable()
                    .saslMechanism(SASL_MECHANISM)
                    .saslQop(buildSaslQop(saslQop))
                    .serverName(serverName)
                    .callbackHandler(new LoginHandler(username,
                                                      password.toCharArray(),
                                                      realm));
        } else {
            return b.security().authentication().disable();
        }
    }

    protected static SaslQop[] buildSaslQop(String saslQop) {
        return Arrays.asList(saslQop.split(",")).stream()
                .map(InfinispanContext::toSaslQop)
                .toArray(size -> new SaslQop[size]);
    }

    protected static SaslQop toSaslQop(String value) {
        try {
            return SaslQop.valueOf(value.trim()
                                           .replace('-',
                                                    '_')
                                           .toUpperCase());
        } catch (IllegalArgumentException e) {
            List<String> values = Arrays.asList(SaslQop.values()).stream().map(SaslQop::toString).collect(toList());
            throw new InfinispanException(MessageFormat.format("SaslQoP option <{0}> is not present in one of this possible values {1}",
                                                               value,
                                                               values),
                                          e);
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

    private static String addCachePrefix(String content) {
        return CACHE_PREFIX + content;
    }

    public RemoteCache<String, KObject> getCache(String index) {
        String cacheName = AttributesUtil.toProtobufFormat(index).toLowerCase();

        if (!this.getIndices().contains(cacheName)) {

            String appformerCacheName = addCachePrefix(cacheName);

            try {
                cacheManager
                        .administration()
                        .createCache(appformerCacheName,
                                     this.infinispanConfiguration.getIndexedConfiguration(appformerCacheName));
            } catch (HotRodClientException | CacheConfigurationException ex) {
                logger.warn("Can't create cache with name <{}>",
                            appformerCacheName);
                logger.warn("Cause:",
                            ex);
            }
        }

        return this.cacheManager.getCache(addCachePrefix(cacheName));
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

        new Retry(5, () -> {
            metadataCache.entrySet()
                    .stream()
                    .filter(entry -> !entry.getKey().equals(addCachePrefix(SCHEMA_PROTO)))
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
        }).run();
    }

    @Override
    public void dispose() {
        if (this.cacheManager.isStarted()) {
            this.cacheManager.stop();
            this.pingService.stop();
        }
    }

    public List<String> getIndices() {
        return new ArrayList<>(this.cacheManager.getCacheNames())
                .stream()
                .filter(s -> s.startsWith(CACHE_PREFIX))
                .map(s -> s.substring(CACHE_PREFIX.length()))
                .collect(toList());
    }

    public Optional<Schema> getSchema(String clusterId) {
        Schema schema = (Schema) getSchemaCache().get(clusterId.toLowerCase());
        return Optional.ofNullable(schema);
    }

    public void addSchema(Schema schema) {
        getSchemaCache().put(AttributesUtil.toProtobufFormat(schema.getName()).toLowerCase(),
                             schema);
    }

    private RemoteCache<Object, Object> getSchemaCache() {
        this.createBaseIndex();
        return this.cacheManager.getCache(getSchemaCacheName());
    }

    public boolean isAlive() {

        RemoteCacheImpl remoteCache = (RemoteCacheImpl) this.cacheManager.getCache();

        try {
            boolean isStarted = remoteCache.ping().isSuccess();
            if (logger.isDebugEnabled()) {
                logger.debug("Infinispan server is not started");
            }
            return isStarted;
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("Infinispan server is not started");
            }
            if (logger.isTraceEnabled()) {
                logger.error("Infinispan server is not started", e);
            }
            return false;
        }
    }

    public void deleteCache(String index){
        String cacheName = AttributesUtil.toProtobufFormat(index).toLowerCase();
        this.cacheManager.administration().removeCache(cacheName);
    }

    public void observeInitialization(Runnable runnable) {
        this.initializationObserver = Optional.of(runnable);
    }
}



