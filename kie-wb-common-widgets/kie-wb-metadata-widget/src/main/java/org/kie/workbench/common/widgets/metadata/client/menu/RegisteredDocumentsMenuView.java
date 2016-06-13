/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.menu;

import com.google.gwt.user.client.ui.HasEnabled;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.widgets.metadata.client.KieDocument;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public interface RegisteredDocumentsMenuView extends UberElement<RegisteredDocumentsMenuBuilder>,
                                                     HasEnabled {

    interface Presenter {

        void onOpenDocument();

        void onSaveDocuments();

        void registerDocument( final KieDocument document );

        void deregisterDocument( final KieDocument document );

        void onActivateDocument( final KieDocument document );

        void onRemoveDocument( final KieDocument document );

        void setOpenDocumentCommand( final Command openDocumentCommand );

        void setSaveDocumentsCommand( final Command saveDocumentsCommand );

        void setActivateDocumentCommand( final ParameterizedCommand<KieDocument> activateDocumentCommand );

        void setRemoveDocumentCommand( final ParameterizedCommand<KieDocument> removeDocumentCommand );

        void activateDocument( final KieDocument document );

        void dispose();

    }

    interface DocumentMenuItem extends IsElement {

        String getName();

        void setName( final String name );

        void setActivateDocumentCommand( final Command activateDocumentCommand );

        void setRemoveDocumentCommand( final Command removeDocumentCommand );

        void setActive( final boolean isActive );

    }

    void clear();

    void addDocument( final DocumentMenuItem document );

    void deleteDocument( final DocumentMenuItem document );

}
