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
package org.uberfire.ext.wires.bpmn.api.service;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsCreate;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.editor.commons.service.support.SupportsUpdate;
import org.uberfire.ext.wires.bpmn.api.model.impl.BpmnEditorContent;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.service.todo.Metadata;

@Remote
public interface BpmnService extends SupportsCreate<ProcessNode>,
                                     SupportsRead<ProcessNode>,
                                     SupportsUpdate<ProcessNode, Metadata>,
                                     SupportsDelete,
                                     SupportsCopy,
                                     SupportsRename {

    List<Path> listFiles();

    BpmnEditorContent loadContent( final Path path );

}
