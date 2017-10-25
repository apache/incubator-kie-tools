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

package org.guvnor.asset.management.client.editors.repository.wizard.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.editors.repository.wizard.CreateRepositoryWizardModel;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.structure.client.editors.repository.RepositoryPreferences;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

public class RepositoryInfoPage extends RepositoryWizardPage
        implements
        RepositoryInfoPageView.Presenter {

    public interface RepositoryInfoPageHandler {

        void managedRepositoryStatusChanged(boolean status);
    }

    private RepositoryInfoPageView view;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private Caller<RepositoryService> repositoryService;

    private boolean isNameValid = false;

    private boolean isOUValid = false;

    private boolean isRepositoryStructurePageValid = false;

    private Map<String, OrganizationalUnit> availableOrganizationalUnits = new HashMap<String, OrganizationalUnit>();
    private boolean mandatoryOU = true;

    private boolean isManagedRepository = false;

    private RepositoryInfoPageHandler handler;

    @Inject
    public RepositoryInfoPage(RepositoryInfoPageView view,
                              Caller<OrganizationalUnitService> organizationalUnitService,
                              Caller<RepositoryService> repositoryService) {
        this.view = view;
        view.init(this);
        this.organizationalUnitService = organizationalUnitService;
        this.repositoryService = repositoryService;
    }

    @Override
    public String getTitle() {
        return Constants.INSTANCE.RepositoryInfoPage();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        boolean completed = mandatoryOU ? isNameValid && isOUValid : isNameValid;
        callback.callback(completed);
    }

    @Override
    public void initialise() {
        //no additional processing required
    }

    @Override
    public void prepareView() {
        init();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setHandler(RepositoryInfoPageHandler handler) {
        this.handler = handler;
    }

    public void onNameChange() {
        String name = view.getName().trim();
        model.setRepositoryName(name);
        if (!name.equals(view.getName())) {
            view.setName(name);
        }

        repositoryService.call(new RemoteCallback<Boolean>() {
                                   @Override
                                   public void callback(Boolean isValid) {
                                       if (isValid) {
                                           view.clearNameErrorMessage();
                                       } else {
                                           view.setNameErrorMessage(Constants.INSTANCE.InvalidRepositoryName());
                                       }
                                       if (isValid != isNameValid) {
                                           isNameValid = isValid;
                                           refreshRepositoryStructurePageStatus();
                                       }
                                   }
                               },
                               new DefaultErrorCallback()).validateRepositoryName(model.getRepositoryName());
    }

    public void onOUChange() {

        final String selectedOU = view.getOrganizationalUnitName();
        boolean newIsOUValid = selectedOU != null && !RepositoryInfoPageView.NOT_SELECTED.equals(selectedOU);
        if (mandatoryOU) {
            view.setValidOU(newIsOUValid);
        }
        model.setOrganizationalUnit(selectedOU != null ? availableOrganizationalUnits.get(selectedOU) : null);

        if (isOUValid != newIsOUValid) {
            isOUValid = newIsOUValid;
            refreshRepositoryStructurePageStatus();
        }
    }

    public void onManagedRepositoryChange() {

        model.setManged(view.isManagedRepository());
        if (isManagedRepository != view.isManagedRepository()) {
            isManagedRepository = view.isManagedRepository();
            refreshRepositoryStructurePageStatus();
        }
    }

    private void refreshRepositoryStructurePageStatus() {
        boolean newIsRepositoryStructurePageValid = isManagedRepository && isNameValid && isOUValid;
        if (newIsRepositoryStructurePageValid != isRepositoryStructurePageValid) {
            isRepositoryStructurePageValid = newIsRepositoryStructurePageValid;
            if (handler != null) {
                handler.managedRepositoryStatusChanged(isRepositoryStructurePageValid);
            }
        }
        fireEvent();
    }

    @PostConstruct
    protected void init() {

        mandatoryOU = isOUMandatory();

        if (!mandatoryOU) {
            view.setVisibleOU(false);
        }

        //populate Organizational Units list box
        organizationalUnitService.call(new RemoteCallback<Collection<OrganizationalUnit>>() {

                                           @Override
                                           public void callback(Collection<OrganizationalUnit> organizationalUnits) {
                                               initOrganizationalUnits(organizationalUnits);
                                           }
                                       },
                                       new ErrorCallback<Message>() {
                                           @Override
                                           public boolean error(final Message message,
                                                                final Throwable throwable) {
                                               view.alert(CoreConstants.INSTANCE.CantLoadOrganizationalUnits() + " \n" + message.toString());

                                               return false;
                                           }
                                       }
        ).getOrganizationalUnits();
    }

    @Override
    public void setModel(CreateRepositoryWizardModel model) {
        super.setModel(model);
        model.setMandatoryOU(mandatoryOU);
        model.setManged(view.isManagedRepository());
    }

    public void enableManagedRepoCreation(boolean enable) {
        view.enabledManagedRepositoryCreation(enable);
    }

    protected boolean isOUMandatory() {
        try {
            final SyncBeanDef<RepositoryPreferences> beanDef = IOC.getBeanManager().lookupBean(RepositoryPreferences.class);
            return beanDef == null || beanDef.getInstance().isOUMandatory();
        } catch (IOCResolutionException exception) {
        }
        return true;
    }

    protected void initOrganizationalUnits(Collection<OrganizationalUnit> organizationalUnits) {
        List<Pair<String, String>> organizationalUnitsInfo = new ArrayList<Pair<String, String>>();
        if (organizationalUnits != null && !organizationalUnits.isEmpty()) {
            for (OrganizationalUnit organizationalUnit : organizationalUnits) {
                availableOrganizationalUnits.put(organizationalUnit.getName(),
                                                 organizationalUnit);
                organizationalUnitsInfo.add(new Pair(organizationalUnit.getName(),
                                                     organizationalUnit.getName()));
            }
        }
        view.initOrganizationalUnits(organizationalUnitsInfo);
    }
}
