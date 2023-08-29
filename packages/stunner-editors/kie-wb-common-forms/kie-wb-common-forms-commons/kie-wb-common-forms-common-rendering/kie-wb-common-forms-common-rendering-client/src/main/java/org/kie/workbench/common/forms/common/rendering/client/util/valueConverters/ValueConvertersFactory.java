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


package org.kie.workbench.common.forms.common.rendering.client.util.valueConverters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.databinding.client.api.Converter;

public class ValueConvertersFactory {

    static Map<String, Converter> converters = new HashMap<>();

    static {
        converters.put(Character.class.getName(), new CharacterToStringConverter());
        converters.put(char.class.getName(), new CharacterToStringConverter());
        converters.put(BigInteger.class.getName(), new BigIntegerToLongConverter());
        converters.put(Integer.class.getName(), new IntegerToLongConverter());
        converters.put(int.class.getName(), new IntegerToLongConverter());
        converters.put(Short.class.getName(), new ShortToLongConverter());
        converters.put(short.class.getName(), new ShortToLongConverter());
        converters.put(Byte.class.getName(), new ByteToLongConverter());
        converters.put(byte.class.getName(), new ByteToLongConverter());
        converters.put(Float.class.getName(), new FloatToDoubleConverter());
        converters.put(float.class.getName(), new FloatToDoubleConverter());
        converters.put(BigDecimal.class.getName(), new BigDecimalToDoubleConverter());
        converters.put(Object.class.getName(), new ObjectToStringConverter());
    }

    public static Converter getConverterForType(String type) {
        return converters.get(type);
    }
}
