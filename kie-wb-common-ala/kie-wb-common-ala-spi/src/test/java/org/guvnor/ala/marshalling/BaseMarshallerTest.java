/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.marshalling;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class BaseMarshallerTest<T> {

    protected Marshaller<T> marshaller;

    @Before
    public void setUp() {
        marshaller = createMarshaller();
    }

    public abstract Marshaller<T> createMarshaller();

    public abstract Class<T> getType();

    /**
     * @return a value that will be marshalled/unmarshalled.
     */
    public abstract T getValue();

    /**
     * @return the raw expected returned value by the marshaller.
     * By default we look for a resource with name of the class T.marshallerOutput located in the class path.
     * e.g. if he class is MyMarshaller, we look for a resource MyMarshallerTest.marshallerOutput.
     */
    public String getMarshallerOutput() throws Exception {
        return readResource(".marshallerOutput");
    }

    /**
     * @return a string representing the marshalled value, might be raw value produced by the marshaller but we can also
     * provide a valid marshalled value in cases we want provide the marshalled value in a more human readable fashion.
     * e.g. we can store the marshalled value in a file with formatting.
     * By default we look for a resource with name of the class T.marshalledValue located in the class path.
     * e.g. if he class is MyMarshaller, we look for a resource MyMarshallerTest.marshalledValue.
     */
    public String getMarshalledValue() throws Exception {
        return readResource(".marshalledValue");
    }

    public String readResource(String extension) throws Exception {
        URL resource = this.getClass().getResource(getType().getSimpleName() + extension);
        if (resource == null) {
            throw new Exception("No resource was found for: " + getType().getCanonicalName() + extension);
        }
        return new String(Files.readAllBytes(Paths.get(resource.toURI())));
    }

    @Test
    public void testGetClass() {
        assertEquals(getType(),
                     marshaller.getType());
    }

    @Test
    public void testMarshall() throws Exception {
        String marshalledValue = marshaller.marshal(getValue());
        String marshallerOutput = getMarshallerOutput();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> marshalledValueMap =
                (Map<String, Object>)(objectMapper.readValue(marshalledValue, Map.class));
        Map<String, Object> marshallerOutputMap =
                (Map<String, Object>)(objectMapper.readValue(marshallerOutput, Map.class));
        assertEquals(marshallerOutputMap,
                marshalledValueMap);
    }

    @Test
    public void testUnMarshall() throws Exception {
        T result = marshaller.unmarshal(getMarshalledValue());
        assertEquals(getValue(),
                     result);
    }
}
