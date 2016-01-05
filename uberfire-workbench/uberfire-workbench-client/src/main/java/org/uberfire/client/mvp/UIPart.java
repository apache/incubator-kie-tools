/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.mvp;

import com.google.gwt.user.client.ui.IsWidget;

public class UIPart {

    private final String title;
    private final IsWidget titleDecoration;
    private final IsWidget widget;

    public UIPart( final String title,
                   final IsWidget titleDecoration,
                   final IsWidget widget ) {
        this.title = title;
        this.titleDecoration = titleDecoration;
        this.widget = widget;
    }

    public String getTitle() {
        return this.title;
    }

    public IsWidget getTitleDecoration() {
        return this.titleDecoration;
    }

    public IsWidget getWidget() {
        return this.widget;
    }
}
