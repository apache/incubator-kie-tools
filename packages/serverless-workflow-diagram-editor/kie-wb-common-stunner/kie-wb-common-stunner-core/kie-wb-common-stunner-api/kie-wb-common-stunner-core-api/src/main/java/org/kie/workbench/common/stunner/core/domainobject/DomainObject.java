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

package org.kie.workbench.common.stunner.core.domainobject;

import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;

/**
 * A DomainObject belongs to the underlying model represented by the Graph but is not represented by a Node.
 * It is ordinarily itself a property of a Node Content that can have its properties displayed by the Properties Panel.
 * A DomainObject must adhere to the same requirements as Nodes bound to the Properties Panel; namely:
 * 3) It must be a {@link Definition}
 * 7) It must be included in a {@link DefinitionSet} declaration.
 * 8) It can have one or more {@link Property}
 */
public interface DomainObject {

    String getDomainObjectUUID();

    String getDomainObjectNameTranslationKey();
}
