package org.gwtbootstrap3.extras.notify.client;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2015 GwtBootstrap3
 * %%
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
 * #L%
 */

import org.kie.j2cl.tools.processors.annotations.GWT3Resource;
import org.kie.j2cl.tools.processors.common.resources.ClientBundle;
import org.kie.j2cl.tools.processors.common.resources.TextResource;

@GWT3Resource
public interface NotifyClientBundle extends ClientBundle {

    static final NotifyClientBundle INSTANCE = NotifyClientBundleImpl.INSTANCE;

    @Source("resource/js/bootstrap-notify-3.1.3.min.cache")
    TextResource notifyJS();
}
