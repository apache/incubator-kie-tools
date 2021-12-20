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

package org.uberfire.client.views.pfly.sys;

import java.util.Optional;

import com.google.gwt.i18n.client.LocaleInfo;

public class MomentUtils {

    protected static void setMomentLocale(){
        setMomentLocale(getLocaleName(LocaleInfo.getCurrentLocale().getLocaleName()));
    }

    /**
     * Transform GWT locale to moment.js locale
     * See <a href="http://momentjs.com/docs/#/displaying/format/">moment.js</a>
     * @return
     */
    protected static String getLocaleName(final String gwtLocale) {
        final String locale = Optional.of(gwtLocale).get().toLowerCase().replace("_",
                                                                                 "-");

        if (locale.isEmpty() || "default".equals(locale)) {
            return "en";
        } else {
            return locale;
        }
    }

    public static native void setMomentLocale(final String locale) /*-{
        $wnd.moment.locale(locale);
    }-*/;

}
