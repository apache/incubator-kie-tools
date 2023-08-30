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


package org.kie.workbench.common.stunner.core.marshaller;

import java.util.function.Function;

public class MarshallingMessageDecorator<T> {

    private final Function<T, String> name;
    private final Function<T, String> type;

    public MarshallingMessageDecorator(Function<T, String> name, Function<T, String> type) {
        this.name = name;
        this.type = type;
    }

    public static <T> MarshallingMessageDecorator<T> of(Function<T, String> name, Function<T, String> type){
        return new MarshallingMessageDecorator<>(name, type);
    }

    public String getName(T object) {
        return name.apply(object);
    }

    public String getType(T object) {
        return type.apply(object);
    }
}
