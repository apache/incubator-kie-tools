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
 */

package org.kie.workbench.common.screens.library.client.screens.assets.add;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.CategoryUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ResourceHandlerManager;
import org.kie.workbench.common.screens.library.client.widgets.project.NewAssetHandlerCardWidget;
import org.kie.workbench.common.profile.api.preferences.ProfilePreferences;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.select.SelectOption;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Undefined;

@WorkbenchScreen(identifier = LibraryPlaces.ADD_ASSET_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class AddAssetScreen {

    private View view;
    private TranslationService ts;
    private ResourceHandlerManager resourceHandlerManager;
    private CategoriesManagerCache categoriesManagerCache;
    private ManagedInstance<NewAssetHandlerCardWidget> newAssetHandlerCardWidgets;
    private LibraryConstants libraryConstants;
    private CategoryUtils categoryUtils;
    private LibraryPlaces libraryPlaces;

    private String filter;
    private String filterType;
    List<NewResourceHandler> newResourceHandlers;
    
    ProfilePreferences profilesPreferences;

    public AddAssetScreen() {
    }

    @Inject
    public AddAssetScreen(final AddAssetScreen.View view,
                          final TranslationService ts,
                          final ResourceHandlerManager resourceHandlerManager,
                          final CategoriesManagerCache categoriesManagerCache,
                          final ManagedInstance<NewAssetHandlerCardWidget> newAssetHandlerCardWidgets,
                          final LibraryConstants libraryConstants,
                          final CategoryUtils categoryUtils,
                          final LibraryPlaces libraryPlaces,
                          final ProfilePreferences profilesPreferences) {
        this.view = view;
        this.ts = ts;
        this.resourceHandlerManager = resourceHandlerManager;
        this.categoriesManagerCache = categoriesManagerCache;
        this.newAssetHandlerCardWidgets = newAssetHandlerCardWidgets;
        this.libraryConstants = libraryConstants;
        this.categoryUtils = categoryUtils;
        this.libraryPlaces = libraryPlaces;
        this.profilesPreferences = profilesPreferences;
    }

    @PostConstruct
    public void initialize() {
        this.filter = "";
        this.view.init(this);
        this.view.setTitle(this.getTitle());
        this.view.setCategories(this.categoryUtils.createCategories());
    }

    @OnOpen
    public void onOpen() {
        this.filter = "";
        this.view.setCategories(this.categoryUtils.createCategories());
        profilesPreferences.load(this::filterNewResourcesHandlersAndUpdate, RuntimeException::new);
    }

    private Callback<Boolean, Void> acceptContextCallback(NewResourceHandler resourceHandler) {
        return new Callback<Boolean, Void>() {
            @Override
            public void onFailure(Void reason) {
                // Nothing to do there right now.
            }

            @Override
            public void onSuccess(Boolean result) {
                NewAssetHandlerCardWidget widget = newAssetHandlerCardWidgets.get();
                widget.initialize(resourceHandler);
                view.addNewAssetWidget(widget.getView());
            }
        };
    }
    
    protected void filterNewResourcesHandlersAndUpdate(ProfilePreferences loadedProfilePreferences) {
        this.newResourceHandlers = filterNewResourceHandlers(loadedProfilePreferences);
        this.update();
    }

    protected List<NewResourceHandler> filterNewResourceHandlers(ProfilePreferences loadedProfilePreferences) {
        Function<NewResourceHandler, Boolean> newResourceHandlerFilter = 
                asset -> asset.isProjectAsset() &&
                    asset.getProfiles().stream()
                               .filter(p ->  p == loadedProfilePreferences.getProfile())
                               .findFirst().isPresent();
       return this.resourceHandlerManager.getNewResourceHandlers(newResourceHandlerFilter);
    }

    void update() {
        this.view.clear();
        List<NewResourceHandler> filteredHandlers = this.filterAndSortHandlers(this.newResourceHandlers,
                                                                               this.filter,
                                                                               this.categoriesManagerCache.getCategory(filterType));
        filteredHandlers.forEach(handler -> addAssetItem(handler));
    }

    protected List<NewResourceHandler> filterAndSortHandlers(List<NewResourceHandler> handlers,
                                                             String textFilter,
                                                             Category category) {
        Stream<NewResourceHandler> filtered = handlers
                .stream()
                .filter(handler -> handler.getDescription().toLowerCase().contains(textFilter.toLowerCase()));
        if (!category.equals(new Undefined())) {
            filtered = filtered.filter(resourceHandler -> category.equals(resourceHandler.getResourceType().getCategory()));
        }
        return filtered.sorted(Comparator.comparing(r -> r.getDescription().toLowerCase()))
                .collect(Collectors.toList());
    }

    private void addAssetItem(NewResourceHandler resourceHandler) {
        if (resourceHandler.canCreate()) {
            resourceHandler.acceptContext(this.acceptContextCallback(resourceHandler));
        }
    }

    public void setFilter(String filter) {
        this.filter = filter;
        this.update();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation(libraryConstants.AddAsset);
    }

    @WorkbenchPartView
    public AddAssetScreen.View getView() {
        return view;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
        this.update();
    }

    public void cancel() {
        this.libraryPlaces.goToProject(this.libraryPlaces.getActiveWorkspace());
    }

    public interface View extends UberElemental<AddAssetScreen> {

        void addNewAssetWidget(HTMLElement view);

        void setCategories(List<SelectOption> categories);

        void clear();

        void setTitle(String title);
    }
    
}
