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

package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;

public interface Navigator extends IsWidget {

    void setOptions( final NavigatorOptions options );

    void loadContent( final FolderListing content );

    void loadContent( final FolderListing content,
                      final Map<FolderItem, List<FolderItem>> siblings );

    boolean isAttached();

    void clear();

    void setPresenter( final BaseViewPresenter presenter );

    interface NavigatorItem {

        void addDirectory( final FolderItem child );

        void addFile( final FolderItem child );

        void cleanup();
    }

}
