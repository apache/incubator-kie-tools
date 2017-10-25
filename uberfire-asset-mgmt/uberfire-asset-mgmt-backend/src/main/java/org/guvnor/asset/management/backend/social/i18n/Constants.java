/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.backend.social.i18n;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Constants {

    private static ResourceBundle messages = ResourceBundle.getBundle("org.guvnor.asset.management.backend.social.i18n.Constants");

    public static final String CONFIGURE_REPOSITORY = "ConfigureRepository";

    public String getMessage(String key) {
        return key != null ? messages.getString(key) : null;
    }

    public String getMessage(String key,
                             Object... params) {
        final String value = getMessage(key);
        if (value != null) {
            return MessageFormat.format(value,
                                        params);
        }
        return null;
    }

    public String configure_repository_start(String repo) {
        return getMessage("ConfigureRepository_start",
                          repo);
    }

    public String configure_repository_end(String repo) {
        return getMessage("ConfigureRepository_end",
                          repo);
    }

    public String configure_repository_failed(String repo) {
        return getMessage("ConfigureRepository_failed",
                          repo);
    }

    public String configure_repository_branch_created(String branch,
                                                      String repo) {
        return getMessage("ConfigureRepository_branch_created",
                          branch,
                          repo);
    }
}
