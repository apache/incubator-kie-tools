/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.dataset.client.editor;

import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.dataset.client.editor.DataSetDefEditor;
import org.dashbuilder.kieserver.RemoteDataSetDef;

public interface RemoteDataSetDefEditor extends DataSetDefEditor<RemoteDataSetDef> {

    LeafAttributeEditor<String> queryTarget();
    LeafAttributeEditor<String> serverTemplateId();
    public ValueBoxEditor<String> dataSource();
    LeafAttributeEditor<String> dbSQL();
    
}