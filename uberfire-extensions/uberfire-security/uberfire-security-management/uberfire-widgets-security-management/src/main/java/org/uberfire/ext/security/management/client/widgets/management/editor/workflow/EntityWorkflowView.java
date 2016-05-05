/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.editor.workflow;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * <p>The view for a entity workflow edition component.</p>
 * 
 * @since 0.8.0
 */
public interface EntityWorkflowView extends IsWidget {
    interface Callback {
        void onSave();
        void onCancel();
    }
    EntityWorkflowView setCallback(Callback callback);
    EntityWorkflowView setWidget(IsWidget widget);
    EntityWorkflowView setCancelButtonVisible(boolean isVisible);
    EntityWorkflowView setSaveButtonEnabled(boolean isEnabled);
    EntityWorkflowView setSaveButtonVisible(boolean isVisible);
    EntityWorkflowView setSaveButtonText(String text);
    EntityWorkflowView showNotification(final String text);
    EntityWorkflowView clearNotification();
}