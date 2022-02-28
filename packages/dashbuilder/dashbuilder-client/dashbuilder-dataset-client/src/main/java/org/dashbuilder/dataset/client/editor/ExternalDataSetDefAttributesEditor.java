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

package org.dashbuilder.dataset.client.editor;

import com.google.gwt.editor.client.ValueAwareEditor;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.def.ExternalDataSetDef;

/**
 * <p>The GWT editor contract for the specific attributes of type <code>org.dashbuilder.dataset.def.ExternalDataSetDef</code>.</p>
 * <p>Used to to edit the following sub-set of attributes:</p>
 * <ul>
 *     <li>URL</li>
 *     <li>Dynamic</li>
 * </ul>
 * 
 */
public interface ExternalDataSetDefAttributesEditor extends ValueAwareEditor<ExternalDataSetDef> {

    LeafAttributeEditor<String> url();


}