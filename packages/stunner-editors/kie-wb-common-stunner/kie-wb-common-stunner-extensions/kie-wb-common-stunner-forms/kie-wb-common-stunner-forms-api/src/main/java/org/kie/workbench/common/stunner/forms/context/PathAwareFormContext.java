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

package org.kie.workbench.common.stunner.forms.context;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.backend.vfs.Path;

/**
 * <p/>
 * A {@link FormRenderingContext} that is {@link PathAware}.
 * <p/>
 * <p/>
 * Allows backend form providers to resolve the project from a context using the project service.
 */
@Portable
public class PathAwareFormContext<T> implements FormRenderingContext<T>,
                                                PathAware {

    private final FormRenderingContext<T> wrapped;
    private final Path path;

    public PathAwareFormContext(@MapsTo("wrapped") final FormRenderingContext<T> wrapped,
                                @MapsTo("path") final Path path) {
        this.wrapped = wrapped;
        this.path = path;
    }

    @Override
    public String getNamespace() {
        return wrapped.getNamespace();
    }

    @Override
    public FormDefinition getRootForm() {
        return wrapped.getRootForm();
    }

    @Override
    public void setRootForm(final FormDefinition rootForm) {
        wrapped.setRootForm(rootForm);
    }

    @Override
    public void setModel(final T model) {
        wrapped.setModel(model);
    }

    @Override
    public T getModel() {
        return wrapped.getModel();
    }

    @Override
    public void setRenderMode(final RenderMode renderMode) {
        wrapped.setRenderMode(renderMode);
    }

    @Override
    public RenderMode getRenderMode() {
        return wrapped.getRenderMode();
    }

    @Override
    public FormRenderingContext getParentContext() {
        return wrapped.getParentContext();
    }

    @Override
    public void setParentContext(final FormRenderingContext parentContext) {
        wrapped.setParentContext(parentContext);
    }

    @Override
    public Map<String, FormDefinition> getAvailableForms() {
        return wrapped.getAvailableForms();
    }

    @Override
    public FormRenderingContext getCopyFor(final String namespace, final String formKey, final T model) {
        final FormRenderingContext<?> wrappedCopy = wrapped.getCopyFor(namespace, formKey, model);
        return new PathAwareFormContext<>(wrappedCopy, path);
    }

    @Override
    public Path getPath() {
        return path;
    }
}
