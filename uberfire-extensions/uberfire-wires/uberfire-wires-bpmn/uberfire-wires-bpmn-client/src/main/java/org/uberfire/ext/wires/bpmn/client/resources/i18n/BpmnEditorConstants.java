/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface BpmnEditorConstants
        extends
        Messages {

    public static final BpmnEditorConstants INSTANCE = GWT.create( BpmnEditorConstants.class );

    String bpmnResourceTypeDescription();

    String bpmnPerspectiveTitle();

    String bpmnExplorerTitle();

    String bpmnExplorerNoFilesFound();

    String bpmnExplorerNoFilesOpen();

    String bpmnExplorerFileUrl();

    String bpmnEditorTitle();

}