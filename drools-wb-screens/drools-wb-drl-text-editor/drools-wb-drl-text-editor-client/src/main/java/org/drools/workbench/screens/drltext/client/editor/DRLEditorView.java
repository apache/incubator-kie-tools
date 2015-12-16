/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.drltext.client.editor;

import java.util.List;

import com.google.gwt.user.client.ui.RequiresResize;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.client.mvp.UberView;

public interface DRLEditorView extends KieEditorView,
                                       RequiresResize,
                                       UberView<DRLEditorPresenter> {

    void setContent( final String drl,
                     final List<String> fullyQualifiedClassNames );

    void setContent( final String dslr,
                     final List<String> fullyQualifiedClassNames,
                     final List<DSLSentence> dslConditions,
                     final List<DSLSentence> dslActions );

    String getContent();

    void setReadOnly( final boolean readOnly );

}
