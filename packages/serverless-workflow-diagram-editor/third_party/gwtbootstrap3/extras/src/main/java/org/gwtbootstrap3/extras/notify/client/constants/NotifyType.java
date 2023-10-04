package org.gwtbootstrap3.extras.notify.client.constants;

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

import org.gwtbootstrap3.client.ui.base.helper.EnumHelper;
import org.gwtbootstrap3.client.ui.constants.Type;
import org.gwtproject.dom.client.Style;

/**
 * Enumeration of Notify's types (CSS class names).
 * <p/>
 * Style name is appended after "alert-", so resulting CSS class name is "alert-[type]".
 *
 * @author Pavel Zlámal
 */
public enum NotifyType implements Type, Style.HasCssName {

    DANGER("danger"),
    INFO("info"),
    SUCCESS("success"),
    WARNING("warning");

    private final String cssClass;

    private NotifyType(final String cssClass) {
        this.cssClass = cssClass;
    }

    public static NotifyType fromStyleName(final String styleName) {
        return EnumHelper.fromStyleName(styleName, NotifyType.class, INFO);
    }

    @Override
    public String getCssName() {
        return cssClass;
    }

}
