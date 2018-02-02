/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.widgets.client.handlers.NewWorkspaceProjectHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

@Dependent
public class ResourceUtils {

    private Classifier classifier;

    private ManagedInstance<NewResourceHandler> newResourceHandlers;

    @Inject
    public ResourceUtils(final Classifier classifier,
                         final ManagedInstance<NewResourceHandler> newResourceHandlers) {
        this.classifier = classifier;
        this.newResourceHandlers = newResourceHandlers;
    }

    public String getBaseFileName(final Path path) {
        final ClientResourceType resourceType = classifier.findResourceType(path);
        final String baseName = Utils.getBaseFileName(path.getFileName(),
                                                      resourceType.getSuffix());

        return baseName;
    }

    public static boolean isProjectHandler(final NewResourceHandler handler) {
        return handler instanceof NewWorkspaceProjectHandler;
    }

    public static boolean isDefaultProjectHandler(final NewResourceHandler handler) {
        return handler.getClass().getName().contains("org.kie.workbench.common.screens.projecteditor.client.handlers.NewWorkspaceProjectHandler");
    }

    public static boolean isPackageHandler(final NewResourceHandler handler) {
        return handler.getClass().getName().contains("NewPackageHandler");
    }

    public static boolean isUploadHandler(final NewResourceHandler handler) {
        return handler.getClass().getName().contains("NewFileUploader");
    }

    public List<NewResourceHandler> getOrderedNewResourceHandlers() {
        return getNewResourceHandlers(NEW_RESOURCE_HANDLER_COMPARATOR_BY_ORDER);
    }

    public List<NewResourceHandler> getAlphabeticallyOrderedNewResourceHandlers() {
        return getNewResourceHandlers(NEW_RESOURCE_HANDLER_COMPARATOR_BY_ALPHABETICAL_ORDER);
    }

    private List<NewResourceHandler> getNewResourceHandlers(final Comparator<NewResourceHandler> sortComparator) {
        final List<NewResourceHandler> sortedNewResourceHandlers = new ArrayList<>();
        getNewResourceHandlers().forEach(sortedNewResourceHandlers::add);
        Collections.sort(sortedNewResourceHandlers,
                         sortComparator);

        return sortedNewResourceHandlers;
    }

    public static final Comparator<NewResourceHandler> NEW_RESOURCE_HANDLER_COMPARATOR_BY_ORDER = (o1, o2) -> {
        if (o1.order() < o2.order()) {
            return -1;
        } else if (o1.order() > o2.order()) {
            return 1;
        } else {
            return o1.getDescription().compareToIgnoreCase(o2.getDescription());
        }
    };

    public static final Comparator<NewResourceHandler> NEW_RESOURCE_HANDLER_COMPARATOR_BY_ALPHABETICAL_ORDER = (o1, o2) -> o1.getDescription().compareToIgnoreCase(o2.getDescription());

    Iterable<NewResourceHandler> getNewResourceHandlers() {
        return newResourceHandlers;
    }
}
