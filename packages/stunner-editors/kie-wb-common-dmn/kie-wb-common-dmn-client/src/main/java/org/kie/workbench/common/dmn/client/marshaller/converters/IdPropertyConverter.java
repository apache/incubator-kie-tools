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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.Objects;

import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.stunner.core.util.StringUtils;

/**
 * Client-side marshalling does not pass through Errai's RPC marshaller. Errai uses default constructors when
 * instantiating objects on the client side and hence, although the Object instantiated on the server may have had null
 * values, the Object that is instantiated on the client does not have null values. This is problematic for a specific
 * scenario where a LiteralExpression is added to _empty_ Decision nodes as the Id is null. However the Decision
 * Navigator needs non-null values.
 */
public class IdPropertyConverter {

    public static Id wbFromDMN(final String id) {
        if (Objects.isNull(id)) {
            return new Id();
        } else {
            return new Id(id);
        }
    }

    public static String dmnFromWB(final Id id) {
        if (Objects.isNull(id)) {
            return null;
        } else if (StringUtils.isEmpty(id.getValue())) {
            return null;
        } else {
            return id.getValue();
        }
    }
}
