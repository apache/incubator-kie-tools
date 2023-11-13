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


package org.kie.workbench.common.stunner.core.definition.clone;

/**
 * <p>This interface represent a type that has been injected in the {@link CloneManagerImpl}</p>
 * <p>All classes that implements {@link IDeepCloneProcess} (e.g. {@link DeepCloneProcess}) can specify a different strategy for cloning </p>
 * <p>At runtime, the correct {@link IDeepCloneProcess} instance will be selected, depending on the class annotated by {@link Alternative} </p>
 * <p>If, for a specific module, no alternative has been specified, the fallback will be {@link DeepCloneProcess}</p>
 */
public interface IDeepCloneProcess extends CloneProcess {

}
