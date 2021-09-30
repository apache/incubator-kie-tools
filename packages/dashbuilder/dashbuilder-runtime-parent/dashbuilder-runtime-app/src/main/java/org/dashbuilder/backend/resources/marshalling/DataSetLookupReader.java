/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.backend.resources.marshalling;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.json.DataSetLookupJSONMarshaller;

@Provider
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
public class DataSetLookupReader implements MessageBodyReader<DataSetLookup> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return DataSetLookup.class.isAssignableFrom(type);
    }

    @Override
    public DataSetLookup readFrom(Class<DataSetLookup> type,
                                  Type genericType,
                                  Annotation[] annotations,
                                  MediaType mediaType,
                                  MultivaluedMap<String, String> httpHeaders,
                                  InputStream entityStream) throws IOException, WebApplicationException {
        var content = IOUtils.toString(entityStream, StandardCharsets.UTF_8);
        return DataSetLookupJSONMarshaller.get().fromJson(content);
    }

}
