/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kie.workbench.common.screens.library.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.ext.widgets.common.client.select.SelectComponent;
import org.uberfire.ext.widgets.common.client.select.SelectOption;
import org.uberfire.ext.widgets.common.client.select.SelectOptionImpl;

@ApplicationScoped
public class CategoryUtils {

    private CategoriesManagerCache categoriesManagerCache;
    private TranslationService ts;

    public CategoryUtils() {
    }

    @Inject
    public CategoryUtils(CategoriesManagerCache categoriesManagerCache,
                         TranslationService ts) {
        this.categoriesManagerCache = categoriesManagerCache;
        this.ts = ts;
    }

    /**
     * Create a list of SelectOptions  with all the categories and the ALL Option, to be used
     * in {@link SelectComponent}
     * @return the list of SelectOptions
     */
    public List<SelectOption> createCategories() {
        List<SelectOption> options = new ArrayList<>();
        options.add(new SelectOptionImpl("ALL",
                                         ts.getTranslation(LibraryConstants.ALL)));
        options.addAll(categoriesManagerCache.getCategories().stream()
                               .map(category -> new SelectOptionImpl(category.getName(),
                                                                     ts.getTranslation(category.getName())))
                               .collect(Collectors.toList()));
        return options;
    }
}
