/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.backend.service.impl.helpers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.uberfire.ext.editor.commons.backend.service.helper.RenameHelper;
import org.uberfire.io.IOService;

@ApplicationScoped
public class FormDefinitionRenameHelper extends AbstractFormDefinitionHelper implements RenameHelper {

    @Inject
    public FormDefinitionRenameHelper(@Named("ioStrategy") IOService ioService, FormDefinitionSerializer serializer, CommentedOptionFactory commentedOptionFactory) {
        super(serializer, ioService, commentedOptionFactory);
    }
}
