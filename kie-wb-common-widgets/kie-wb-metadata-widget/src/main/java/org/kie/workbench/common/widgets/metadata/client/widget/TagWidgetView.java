/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.metadata.client.widget;

import com.google.gwt.user.client.ui.IsWidget;

public interface TagWidgetView extends IsWidget {

    void setPresenter( TagWidget presenter );

    void removeTag( String tag );

    void clear();

    void addTag( String tag, boolean readOnly );

    void setReadOnly( boolean readOnly );
}
