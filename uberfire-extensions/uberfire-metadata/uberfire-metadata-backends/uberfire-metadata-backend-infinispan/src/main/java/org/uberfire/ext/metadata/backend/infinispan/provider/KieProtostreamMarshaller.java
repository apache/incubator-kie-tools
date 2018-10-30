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
import java.util.concurrent.ConcurrentHashMap;

import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.protostream.BaseMarshaller;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.impl.AnnotatedDescriptorImpl;

/**
 * A thread safe protostream marshaller supporting dynamic entities. Workaround until dynamic entities are supported on
 * protostream - https://issues.jboss.org/browse/IPROTO-56
 */
public final class KieProtostreamMarshaller extends ProtoStreamMarshaller {

    /**
     * Stores the type being written in the context
     */
    private final InheritableThreadLocal<String> type = new InheritableThreadLocal<>();

    /**
     * Stores the types a certain dynamic marshaller class
     */
    private final ConcurrentHashMap<String, Class<?>> classByType = new ConcurrentHashMap<>(4);
    private final ConcurrentHashMap<Class<?>, KieMarshallerSupplier<?>> supplierByClass = new ConcurrentHashMap<>(2);

    /**
     * Registers a protobuf file
     * @param fileName The name of the file.
     * @param contents The contents of the file.
     * @param dynamicEntityClass The dynamic entity class.
     * the types in the protobuf.
     * @throws IOException in case the registration fails.
     */
    void registerSchema(String fileName,
                        String contents,
                        Class<?> dynamicEntityClass) throws IOException {

        getSerializationContext().registerProtoFiles(FileDescriptorSource.fromString(fileName,
                                                                                     contents));
        getSerializationContext().getFileDescriptors().entrySet().stream()
                .filter(p -> p.getKey().equals(fileName))
                .flatMap(fd -> fd.getValue().getMessageTypes().stream())
                .map(AnnotatedDescriptorImpl::getFullName)
                .forEach(t -> classByType.put(t,
                                              dynamicEntityClass));
    }

    /**
     * Registers a marshaller from a dynamic entity.
     * @param kieMarshallerSupplier The {@link KieMarshallerSupplier for the entity}
     */
    void registerMarshaller(final KieMarshallerSupplier kieMarshallerSupplier) {
        supplierByClass.put(kieMarshallerSupplier.getJavaClass(),
                            kieMarshallerSupplier);
        getSerializationContext().registerMarshallerProvider(new SerializationContext.MarshallerProvider() {

            @Override
            public BaseMarshaller<?> getMarshaller(String typeName) {
                Class<?> classForType = classByType.get(typeName);
                if (classForType != null && classForType.equals(kieMarshallerSupplier.getJavaClass())) {
                    return kieMarshallerSupplier.getMarshallerForType(typeName);
                }
                return null;
            }

            @Override
            public BaseMarshaller<?> getMarshaller(Class<?> javaClass) {
                if (kieMarshallerSupplier.getJavaClass().isAssignableFrom(javaClass)) {
                    return kieMarshallerSupplier.getMarshallerForType(type.get());
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    protected ByteBuffer objectToBuffer(Object o,
                                        int estimatedSize) throws IOException, InterruptedException {
        try {
            String value = extractType(o);
            if (value != null) {
                type.set(value);
            }
            return super.objectToBuffer(o,
                                        estimatedSize);
        } finally {
            type.set(null);
        }
    }

    private String extractType(Object o) {
        KieMarshallerSupplier<Object> marshallerSupplier = (KieMarshallerSupplier<Object>) lookupSupplier(o.getClass());
        if (marshallerSupplier != null) {
            return marshallerSupplier.extractTypeFromEntity(o);
        }
        return null;
    }

    private KieMarshallerSupplier<?> lookupSupplier(Class<?> clazz) {
        KieMarshallerSupplier<?> supplier = supplierByClass.get(clazz);
        if (supplier != null) {
            return supplier;
        }
        for (Class<?> superInterface : clazz.getInterfaces()) {
            KieMarshallerSupplier<?> altSupplier = supplierByClass.get(superInterface);
            if (altSupplier != null) {
                return altSupplier;
            }
        }
        return null;
    }

    /**
     * A marshaller supplier for Dynamic Entities
     */
    interface KieMarshallerSupplier<E> {

        /**
         * Extract the type for an entity.
         * @param entity The entity being marshalled
         * @return the fully qualified type for the entity
         */
        String extractTypeFromEntity(E entity);

        /**
         * @return the {@link Class} of the dynamic entity.
         */
        Class<E> getJavaClass();

        /**
         * @param typeName The typr name being unmarshalled
         * @return An instance of marshaller to unmarshall the supplied type.
         */
        BaseMarshaller<E> getMarshallerForType(String typeName);
    }
}
