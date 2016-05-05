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

package org.uberfire.ext.security.management.client.widgets.popup;

import org.uberfire.ext.widgets.common.client.common.BusyPopup;

/**
 * <p>A loading box view using <code>org.uberfire.ext.widgets.common.client.common.BusyPopup</code>.</p>
 *
 * @since 0.8.0
 */
public class LoadingBoxView implements LoadingBox.View {

    @Override
    public void show(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void close() {
        BusyPopup.close();
    }


}
