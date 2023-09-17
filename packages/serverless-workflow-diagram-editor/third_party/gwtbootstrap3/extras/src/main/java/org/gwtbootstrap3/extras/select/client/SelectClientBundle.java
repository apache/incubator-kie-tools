package org.gwtbootstrap3.extras.select.client;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2016 GwtBootstrap3
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

import org.treblereel.j2cl.processors.annotations.GWT3Resource;
import org.treblereel.j2cl.processors.common.resources.ClientBundle;
import org.treblereel.j2cl.processors.common.resources.TextResource;

/**
 * @author godi
 */
@GWT3Resource
public interface SelectClientBundle extends ClientBundle {

    static final SelectClientBundle INSTANCE = SelectClientBundleImpl.INSTANCE;

    static final String VERSION = "1.12.4";
    static final String I18N_DIR = "resource/js/i18n.cache." + VERSION + "/";

    @Source("resource/js/bootstrap-select-" + VERSION + ".min.cache.js.back")
    TextResource select();

    //@Source("resource/css/bootstrap-select-" + VERSION + ".min.cache.css")
    //TextResource select_css();


}
