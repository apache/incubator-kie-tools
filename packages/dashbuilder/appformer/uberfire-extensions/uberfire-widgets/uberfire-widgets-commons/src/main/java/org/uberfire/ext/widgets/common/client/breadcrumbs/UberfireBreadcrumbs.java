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

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainer;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbPresenter;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

@EntryPoint
public class UberfireBreadcrumbs {

    final Map<String, List<BreadcrumbPresenter>> breadcrumbsPerPerspective = new HashMap<>();
    final Map<String, Element> breadcrumbsToolBarPerPerspective = new HashMap<>();
    private final UberfireBreadcrumbsContainer uberfireBreadcrumbsContainer;
    private final View view;
    String currentPerspective;
    private ManagedInstance<DefaultBreadcrumbsPresenter> breadcrumbsPresenters;
    private PlaceManager placeManager;

    @Inject
    public UberfireBreadcrumbs(UberfireBreadcrumbsContainer uberfireBreadcrumbsContainer,
                               ManagedInstance<DefaultBreadcrumbsPresenter> breadcrumbsPresenters,
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

    @PostConstruct
    public void createBreadcrumbs() {
        uberfireBreadcrumbsContainer.init(getView().getElement());
    }

    /**
     * Clears the breadcrumbs associated with a perspective.
     * @param associatedPerspective perspective associated with the breadcrumb
     */
    public void clearBreadcrumbs(final String associatedPerspective) {
        breadcrumbsPerPerspective.put(associatedPerspective, new ArrayList<>());
    }

    /**
     * Clears the breadcrumbs and toolbars associated with a perspective.
     * @param associatedPerspective perspective associated with the breadcrumb
     */
    public void clearBreadcrumbsAndToolBars(final String associatedPerspective) {
        breadcrumbsPerPerspective.put(associatedPerspective, new ArrayList<>());
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
                      command,
                      true);
    }

    /**
     * Creates a breadcrumb associated with a perspective.
     * @param associatedPerspective perspective associated with the breadcrumb
     * @param breadCrumbLabel label of the breadcrumb
     * @param command command to be executed after the associated place request is accessed
     * @param removeDeepLevelBreadcrumbsAfterActivation defines if the deep level breadcrumbs should be removed after the breadcrumb is activated
     */
    public void addBreadCrumb(final String associatedPerspective,
                              final String breadCrumbLabel,
                              final Command command,
                              final boolean removeDeepLevelBreadcrumbsAfterActivation) {
        addBreadCrumb(associatedPerspective,
                      breadCrumbLabel,
                      null,
                      command,
                      removeDeepLevelBreadcrumbsAfterActivation);
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
                      true);
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
                      command,
                      true);
    }

    /**
     * Creates a breadcrumb associated with a perspective
     * a Place Request and a target panel.
     * @param associatedPerspective perspective associated with the breadcrumb
     * @param breadCrumbLabel label of the breadcrumb
     * @param associatedPlaceRequest place request associated with the breadcrumb
     * @param command command to be executed after the associated place request is accessed
     * @param removeDeepLevelBreadcrumbsAfterActivation defines if the deep level breadcrumbs should be removed after the breadcrumb is activated
     */
    public void addBreadCrumb(final String associatedPerspective,
                              final String breadCrumbLabel,
                              final PlaceRequest associatedPlaceRequest,
                              final Command command,
                              final boolean removeDeepLevelBreadcrumbsAfterActivation) {

        final DefaultBreadcrumbsPresenter breadCrumb = breadcrumbsPresenters.get();

        breadCrumb.setup(breadCrumbLabel,
                         removeDeepLevelBreadcrumbsAfterActivation,
                         generateBreadCrumbSelectCommand(associatedPerspective,
                                                         breadCrumb,
                                                         associatedPlaceRequest,
                                                         command));

        addBreadCrumb(associatedPerspective, breadCrumb);
    }

    public void addBreadCrumb(final String associatedPerspective,
                              final BreadcrumbPresenter breadCrumbPresenter) {

        final List<BreadcrumbPresenter> breadcrumbs = getBreadcrumbs(associatedPerspective);

        activateLastBreadcrumb(breadcrumbs);
        breadCrumbPresenter.deactivate();

        breadcrumbs.add(breadCrumbPresenter);
        breadcrumbsPerPerspective.put(associatedPerspective, breadcrumbs);
        
        if (currentPerspective.equals(associatedPerspective)) {
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

        breadcrumbsToolBarPerPerspective.put(associatedPerspective, toolbar);
        if (currentPerspective.equals(associatedPerspective)) {
            updateView();
        }
    }

    private List<BreadcrumbPresenter> getBreadcrumbs(final String perspective) {
        return breadcrumbsPerPerspective.getOrDefault(perspective, new ArrayList<>());
    }

    private void activateLastBreadcrumb(final List<BreadcrumbPresenter> breadcrumbs) {
        if (!breadcrumbs.isEmpty()) {
            breadcrumbs.get(breadcrumbs.size() - 1).activate();
        }
    }

    Command generateBreadCrumbSelectCommand(final String perspective,
                                            final DefaultBreadcrumbsPresenter breadCrumb,
                                            final PlaceRequest placeRequest,
                                            final Command command) {
        if (command == null) {
            return null;
        }

        return () -> {
            removeDeepLevelBreadcrumbsIfNecessary(perspective, breadCrumb);

            breadCrumb.deactivate();

            if (placeRequest != null) {
                placeManager.goTo(placeRequest);
            }

            updateView();
            command.execute();
        };
    }

    void removeDeepLevelBreadcrumbsIfNecessary(final String perspective,
                                               final DefaultBreadcrumbsPresenter breadCrumb) {
        if (breadCrumb.hasToRemoveDeepLevelBreadcrumbsAfterActivation()) {
            final List<BreadcrumbPresenter> breadcrumbs = getBreadcrumbs(perspective);
            breadcrumbs.removeIf(b -> breadcrumbs.indexOf(b) > breadcrumbs.indexOf(breadCrumb));
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
            breadcrumbsPerPerspective.get(currentPerspective).forEach(p -> view.addBreadcrumb(p.getView()));
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

        void addBreadcrumb(UberElemental<? extends BreadcrumbPresenter> view);

        void addBreadcrumbToolbar(Element uberElement);
    }
}
