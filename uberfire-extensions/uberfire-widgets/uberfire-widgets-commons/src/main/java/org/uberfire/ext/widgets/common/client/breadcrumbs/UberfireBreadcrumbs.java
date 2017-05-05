/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.breadcrumbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HasWidgets;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainer;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbsPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

/**
 * A container for breadcrumbs and a toolbar area.
 * Allows the applications to add breadcrumbs to
 * any displayable thing: a {@link WorkbenchPerspective},
 * a {@link WorkbenchScreen} a {@link WorkbenchEditor},
 * or the editor associated with a VFS file located at a
 * particular {@link Path} through a {@link PathPlaceRequest}.
 * Also has a toolbar area, placed in the right side of bread, that allows
 * applications to add {@link Element} to Uberfire Breadcrumbs.
 */

@EntryPoint
public class UberfireBreadcrumbs {

    final Map<String, List<BreadcrumbsPresenter>> breadcrumbsPerPerspective = new HashMap<>();
    final Map<String, Element> breadcrumbsToolBarPerPerspective = new HashMap<>();
    private final UberfireBreadcrumbsContainer uberfireBreadcrumbsContainer;
    private final View view;
    String currentPerspective;
    private ManagedInstance<BreadcrumbsPresenter> breadcrumbsPresenters;
    private PlaceManager placeManager;

    @Inject
    public UberfireBreadcrumbs(UberfireBreadcrumbsContainer uberfireBreadcrumbsContainer,
                               ManagedInstance<BreadcrumbsPresenter> breadcrumbsPresenters,
                               PlaceManager placeManager,
                               View view) {
        this.uberfireBreadcrumbsContainer = uberfireBreadcrumbsContainer;
        this.breadcrumbsPresenters = breadcrumbsPresenters;
        this.placeManager = placeManager;
        this.view = view;
    }

    void perspectiveChangeEvent(@Observes PerspectiveChange perspectiveChange) {
        currentPerspective = perspectiveChange.getIdentifier();
        updateView();
    }

    @AfterInitialization
    public void createBreadcrumbs() {
        uberfireBreadcrumbsContainer.init(getView().getElement());
    }

    /**
     * Clears the breadcrumbs associated with a perspective.
     * @param associatedPerspective perspective associated with the breadcrumb
     */
    public void clearBreadcrumbs(final String associatedPerspective) {
        breadcrumbsPerPerspective.put(associatedPerspective,
                                      new ArrayList<>());
    }

    /**
     * Clears the breadcrumbs and toolbars associated with a perspective.
     * @param associatedPerspective perspective associated with the breadcrumb
     */
    public void clearBreadcrumbsAndToolBars(final String associatedPerspective) {
        breadcrumbsPerPerspective.put(associatedPerspective,
                                      new ArrayList<>());
        breadcrumbsToolBarPerPerspective.remove(associatedPerspective);
    }

    /**
     * Creates a breadcrumb associated with a perspective.
     * @param associatedPerspective perspective associated with the breadcrumb
     * @param breadCrumbLabel label of the breadcrumb
     * @param command command to be executed after the associated place request is accessed
     */
    public void addBreadCrumb(final String associatedPerspective,
                              final String breadCrumbLabel,
                              final Command command) {
        addBreadCrumb(associatedPerspective,
                      breadCrumbLabel,
                      null,
                      null,
                      command);
    }

    /**
     * Creates a breadcrumb associated with a perspective
     * and Place Request.
     * @param associatedPerspective perspective associated with the breadcrumb
     * @param breadCrumbLabel label of the breadcrumb
     * @param associatedPlaceRequest place request associated with the breadcrumb
     */
    public void addBreadCrumb(final String associatedPerspective,
                              final String breadCrumbLabel,
                              final PlaceRequest associatedPlaceRequest) {
        addBreadCrumb(associatedPerspective,
                      breadCrumbLabel,
                      associatedPlaceRequest,
                      null,
                      null);
    }

    /**
     * Creates a breadcrumb associated with a perspective
     * a Place Request and a target panel.
     * @param associatedPerspective perspective associated with the breadcrumb
     * @param breadCrumbLabel label of the breadcrumb
     * @param associatedPlaceRequest place request associated with the breadcrumb
     * @param addTo target content panel of the place request
     */
    public void addBreadCrumb(final String associatedPerspective,
                              final String breadCrumbLabel,
                              final PlaceRequest associatedPlaceRequest,
                              final HasWidgets addTo) {
        addBreadCrumb(associatedPerspective,
                      breadCrumbLabel,
                      associatedPlaceRequest,
                      addTo,
                      null);
    }

    /**
     * Creates a breadcrumb associated with a perspective
     * and Place Request.
     * @param associatedPerspective perspective associated with the breadcrumb
     * @param breadCrumbLabel label of the breadcrumb
     * @param associatedPlaceRequest place request associated with the breadcrumb
     * @param command command to be executed after the associated place request is accessed
     */
    public void addBreadCrumb(final String associatedPerspective,
                              final String breadCrumbLabel,
                              final PlaceRequest associatedPlaceRequest,
                              final Command command) {
        addBreadCrumb(associatedPerspective,
                      breadCrumbLabel,
                      associatedPlaceRequest,
                      null,
                      command);
    }

    /**
     * Creates a breadcrumb associated with a perspective
     * a Place Request and a target panel.
     * @param associatedPerspective perspective associated with the breadcrumb
     * @param breadCrumbLabel label of the breadcrumb
     * @param associatedPlaceRequest place request associated with the breadcrumb
     * @param addTo target content panel of the place request
     * @param command command to be executed after the associated place request is accessed
     */
    public void addBreadCrumb(final String associatedPerspective,
                              final String breadCrumbLabel,
                              final PlaceRequest associatedPlaceRequest,
                              final HasWidgets addTo,
                              final Command command) {
        List<BreadcrumbsPresenter> breadcrumbs = getBreadcrumbs(associatedPerspective);
        deactivateLastBreadcrumb(breadcrumbs);
        breadcrumbs.add(createBreadCrumb(associatedPerspective,
                                         breadCrumbLabel,
                                         associatedPlaceRequest,
                                         addTo,
                                         command));
        breadcrumbsPerPerspective.put(associatedPerspective,
                                      breadcrumbs);
        if (currentPerspective == associatedPerspective) {
            updateView();
        }
    }

    /**
     * Adds a toolbar to a perspective.
     * Toolbar is placed in the right side of the breadcrumbs area.
     * @param associatedPerspective perspective associated with the toolbar
     * @param toolbar toolbar that will be added
     */
    public void addToolbar(final String associatedPerspective,
                           final Element toolbar) {
        breadcrumbsToolBarPerPerspective.put(associatedPerspective,
                                             toolbar);
        if (currentPerspective == associatedPerspective) {
            updateView();
        }
    }

    private List<BreadcrumbsPresenter> getBreadcrumbs(final String perspective) {
        List<BreadcrumbsPresenter> breadcrumbs = breadcrumbsPerPerspective.get(perspective);
        if (breadcrumbs == null) {
            breadcrumbs = new ArrayList<>();
        }
        return breadcrumbs;
    }

    private void deactivateLastBreadcrumb(final List<BreadcrumbsPresenter> breadcrumbs) {
        if (!breadcrumbs.isEmpty()) {
            breadcrumbs.get(breadcrumbs.size() - 1).deactivate();
        }
    }

    private BreadcrumbsPresenter createBreadCrumb(final String perspective,
                                                  final String label,
                                                  final PlaceRequest placeRequest,
                                                  final HasWidgets addTo,
                                                  final Command command) {

        BreadcrumbsPresenter breadCrumb = breadcrumbsPresenters.get();
        breadCrumb.setup(label,
                         placeRequest,
                         generateBreadCrumbSelectCommand(perspective,
                                                         breadCrumb,
                                                         placeRequest,
                                                         addTo,
                                                         command));
        breadCrumb.activate();
        return breadCrumb;
    }

    Command generateBreadCrumbSelectCommand(final String perspective,
                                            final BreadcrumbsPresenter breadCrumb,
                                            final PlaceRequest placeRequest,
                                            final HasWidgets addTo,
                                            final Command command) {
        return () -> {
            removeDeepLevelBreadcrumbs(perspective,
                                       breadCrumb);
            breadCrumb.activate();
            if (placeRequest != null) {
                goToBreadCrumb(placeRequest,
                               addTo);
            }
            updateView();
            if (command != null) {
                command.execute();
            }
        };
    }

    private void goToBreadCrumb(final PlaceRequest placeRequest,
                                final HasWidgets addTo) {
        if (addTo != null) {
            placeManager.goTo(placeRequest,
                              addTo);
        } else {
            placeManager.goTo(placeRequest);
        }
    }

    void removeDeepLevelBreadcrumbs(final String perspective,
                                    final BreadcrumbsPresenter breadCrumb) {
        List<BreadcrumbsPresenter> breadcrumbs = breadcrumbsPerPerspective.get(perspective);
        if (breadcrumbs != null) {
            Predicate<BreadcrumbsPresenter> toRemovePredicate = current ->
                    breadcrumbs.indexOf(current) > breadcrumbs.indexOf(breadCrumb);
            breadcrumbs.removeIf(toRemovePredicate);
        }
    }

    private void updateView() {
        getView();
    }

    View getView() {
        view.clear();
        updateBreadcrumbsContainer();
        updateBreadcrumbs();
        return view;
    }

    private void updateBreadcrumbs() {
        if (thereIsBreadcrumbsFor(currentPerspective)) {
            breadcrumbsPerPerspective.get(currentPerspective)
                    .stream()
                    .forEach(p -> view.addBreadcrumb(p.getView()));
        }
        if (thereIsBreadcrumbToolbarFor(currentPerspective)) {
            view.addBreadcrumbToolbar(breadcrumbsToolBarPerPerspective.get(currentPerspective));
        }
    }

    void updateBreadcrumbsContainer() {
        if (thereIsContentOnBreadcrumbs()) {
            uberfireBreadcrumbsContainer.enable();
        } else {
            uberfireBreadcrumbsContainer.disable();
        }
    }

    private boolean thereIsContentOnBreadcrumbs() {
        return thereIsBreadcrumbsFor(currentPerspective) || thereIsBreadcrumbToolbarFor(currentPerspective);
    }

    private boolean thereIsBreadcrumbsFor(final String perspective) {
        return breadcrumbsPerPerspective.containsKey(perspective);
    }

    private boolean thereIsBreadcrumbToolbarFor(final String perspective) {
        return breadcrumbsToolBarPerPerspective.containsKey(perspective);
    }

    public interface View extends UberElement<UberfireBreadcrumbs> {

        void clear();

        void addBreadcrumb(UberElement<BreadcrumbsPresenter> view);

        void addBreadcrumbToolbar(Element uberElement);
    }
}
