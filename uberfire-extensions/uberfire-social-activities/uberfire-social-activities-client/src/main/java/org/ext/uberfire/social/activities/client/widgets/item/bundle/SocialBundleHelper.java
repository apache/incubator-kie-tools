/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.ext.uberfire.social.activities.client.widgets.item.bundle;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

public class SocialBundleHelper {

    public static String getItemDescription(final String key) {
        Collection<SyncBeanDef<SocialBundleService>> socialBundleServices = IOC.getBeanManager().lookupBeans(SocialBundleService.class);
        String value = null;

        for (SyncBeanDef<SocialBundleService> serviceBean : socialBundleServices) {
            SocialBundleService service = serviceBean.getInstance();
            try {
                value = getTranslationFromService(key,
                                                  value,
                                                  service);
            } catch (DuplicatedTranslationException e) {
                GWT.log(e.getMessage());
                break;
            }
            IOC.getBeanManager().destroyBean(serviceBean);
        }

        return value != null ? value : key;
    }

    static String getTranslationFromService(final String key,
                                            final String currentValue,
                                            final SocialBundleService service) throws DuplicatedTranslationException {
        final String translation = service.getTranslation(key);
        String value;

        if (translation != null) {
            if (currentValue == null) {
                value = translation;
            } else {
                throw new DuplicatedTranslationException(key);
            }
        } else {
            value = currentValue;
        }

        return value;
    }
}
