/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
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
 */

package org.kie.workbench.common.screens.examples.client.wizard.pages.organizationalunit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.client.resources.i18n.ExamplesScreenConstants;
import org.kie.workbench.common.screens.examples.client.wizard.pages.BaseExamplesWizardPage;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class OUPage extends BaseExamplesWizardPage implements OUPageView.Presenter {

    private OUPageView view;

    public OUPage() {
        //Zero-argument constructor for CDI proxies
    }

    @Inject
    public OUPage(final OUPageView view,
                  final TranslationService translator,
                  final Caller<ExamplesService> examplesService,
                  final Event<WizardPageStatusChangeEvent> pageStatusChangedEvent) {
        super(translator,
              examplesService,
              pageStatusChangedEvent);
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setTargetRepositoryPlaceHolder(translator.format(ExamplesScreenConstants.OrganizationalUnitPage_WizardTargetRepositoryPlaceHolder));
        view.setOrganizationalUnitsPlaceHolder(translator.format(ExamplesScreenConstants.OrganizationalUnitPage_WizardOrganizationalUnitsPlaceHolder));
    }

    @Override
    public void initialise() {
        view.initialise();
    }

    @Override
    public String getTitle() {
        return translator.format(ExamplesScreenConstants.OrganizationalUnitPage_WizardSelectOrganizationalUnitPageTitle);
    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        final ExampleTargetRepository targetRepository = model.getTargetRepository();
        final ExampleOrganizationalUnit targetOrganizationalUnit = model.getTargetOrganizationalUnit();
        validateOrganizationalUnit(targetRepository,
                                   targetOrganizationalUnit,
                                   new Callback<Boolean>() {
                                       @Override
                                       public void callback(final Boolean result) {
                                           callback.callback(result);
                                       }
                                   });
    }

    @Override
    public void setOrganizationalUnits(final Set<ExampleOrganizationalUnit> organizationalUnits) {
        if (organizationalUnits == null) {
            view.setOrganizationalUnits(Collections.<ExampleOrganizationalUnit>emptyList());
            return;
        }
        final List<ExampleOrganizationalUnit> sortedOrganizationalUnits = sort(organizationalUnits);
        view.setOrganizationalUnits(sortedOrganizationalUnits);
        if (sortedOrganizationalUnits.size() > 0) {
            if (model.getTargetOrganizationalUnit() != null) {
                view.setOrganizationalUnit(model.getTargetOrganizationalUnit());
            } else {
                model.setTargetOrganizationalUnit(sortedOrganizationalUnits.get(0));
                view.setOrganizationalUnit(sortedOrganizationalUnits.get(0));
            }
        }
    }

    private List<ExampleOrganizationalUnit> sort(final Set<ExampleOrganizationalUnit> repositories) {
        final List<ExampleOrganizationalUnit> sortedRepositories = new ArrayList<ExampleOrganizationalUnit>(repositories);
        Collections.sort(sortedRepositories,
                         new Comparator<ExampleOrganizationalUnit>() {
                             @Override
                             public int compare(final ExampleOrganizationalUnit o1,
                                                final ExampleOrganizationalUnit o2) {
                                 return o1.getName().compareTo(o2.getName());
                             }
                         });
        return sortedRepositories;
    }

    @Override
    public void setTargetRepository(final ExampleTargetRepository repository) {
        model.setTargetRepository(repository);
        view.setTargetRepository(repository);
        pageStatusChangedEvent.fire(new WizardPageStatusChangeEvent(this));
    }

    @Override
    public void setTargetOrganizationalUnit(final ExampleOrganizationalUnit organizationalUnit) {
        model.setTargetOrganizationalUnit(organizationalUnit);
        view.setOrganizationalUnit(organizationalUnit);
        pageStatusChangedEvent.fire(new WizardPageStatusChangeEvent(this));
    }

    private void validateOrganizationalUnit(final ExampleTargetRepository targetRepository,
                                            final ExampleOrganizationalUnit targetOrganizationalUnit,
                                            final Callback<Boolean> callback) {
        boolean isValid = true;
        if (targetOrganizationalUnit == null) {
            view.setTargetOrganizationalUnitGroupType(ValidationState.ERROR);
            view.showTargetOrganizationalUnitHelpMessage(translator.format(ExamplesScreenConstants.OrganizationalUnitPage_WizardSelectOrganizationalUnitMandatory));
            isValid = false;
        } else {
            final String targetOrganizationalUnitName = targetOrganizationalUnit.getName();
            if (targetOrganizationalUnitName == null || targetOrganizationalUnitName.trim().isEmpty()) {
                view.setTargetOrganizationalUnitGroupType(ValidationState.ERROR);
                view.showTargetOrganizationalUnitHelpMessage(translator.format(ExamplesScreenConstants.OrganizationalUnitPage_WizardSelectOrganizationalUnitMandatory));
                isValid = false;
            } else {
                view.setTargetOrganizationalUnitGroupType(ValidationState.NONE);
                view.hideTargetOrganizationalUnitHelpMessage();
            }
        }

        if (targetRepository == null) {
            view.setTargetRepositoryGroupType(ValidationState.ERROR);
            view.showTargetRepositoryHelpMessage(translator.format(ExamplesScreenConstants.OrganizationalUnitPage_WizardSelectTargetRepositoryMandatory));
            callback.callback(false);
        } else {
            final String targetRepositoryName = targetRepository.getAlias();
            if (targetRepositoryName == null || targetRepositoryName.trim().isEmpty()) {
                view.setTargetRepositoryGroupType(ValidationState.ERROR);
                view.showTargetRepositoryHelpMessage(translator.format(ExamplesScreenConstants.OrganizationalUnitPage_WizardSelectTargetRepositoryMandatory));
                callback.callback(false);
            } else {
                final boolean _isValid = isValid;
                examplesService.call(new RemoteCallback<Boolean>() {
                    @Override
                    public void callback(final Boolean valid) {
                        if (Boolean.TRUE.equals(valid)) {
                            view.setTargetRepositoryGroupType(ValidationState.NONE);
                            view.hideTargetRepositoryHelpMessage();
                        } else {
                            view.setTargetRepositoryGroupType(ValidationState.ERROR);
                            view.showTargetRepositoryHelpMessage(translator.format(ExamplesScreenConstants.OrganizationalUnitPage_WizardSelectTargetRepositoryInvalid));
                        }
                        callback.callback(valid && _isValid);
                    }
                }).validateRepositoryName(targetRepositoryName);
            }
        }
    }
}
