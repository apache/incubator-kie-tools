/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.screens.guided.rule.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.rule.client.type.GuidedRuleDRLResourceType;
import org.drools.workbench.screens.guided.rule.client.type.GuidedRuleDSLRResourceType;
import org.guvnor.common.services.shared.version.VersionService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.screens.socialscreen.client.OverviewScreenPresenter;
import org.kie.workbench.common.screens.socialscreen.client.OverviewScreenView;
import org.kie.workbench.common.screens.socialscreen.client.discussion.VersionRecordManager;
import org.kie.workbench.common.screens.socialscreen.model.Overview;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.workbench.type.ClientTypeRegistry;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
//@WorkbenchEditor(identifier = "GuidedRuleEditor", supportedTypes = {GuidedRuleDRLResourceType.class, GuidedRuleDSLRResourceType.class}, priority = 102)
public class GuidedRuleEditorOverviewPresenter
        extends OverviewScreenPresenter
        implements OverviewScreenView.Presenter {

    private final ClientTypeRegistry clientTypeRegistry;

    @Inject
    private GuidedRuleDRLResourceType resourceTypeDRL;

    @Inject
    private GuidedRuleDSLRResourceType resourceTypeDSL;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<VersionService> versionService;

    private Menus menus;
    private VersionRecordManager versionRecordManager;
    private Overview overview;

    @Inject
    public GuidedRuleEditorOverviewPresenter(
            final ClientTypeRegistry clientTypeRegistry,
            final VersionRecordManager versionRecordManager,
            final OverviewScreenView view) {
        super(view);

        view.setPresenter(this);

        this.clientTypeRegistry = clientTypeRegistry;
        this.versionRecordManager = versionRecordManager;

        versionRecordManager.addVersionSelectionCallback(
                new Callback<VersionRecord>() {
                    @Override
                    public void callback(VersionRecord versionRecord) {

                        ObservablePath wrap = IOC.getBeanManager().lookupBean(ObservablePath.class).getInstance().wrap(
                                PathFactory.newPathBasedOn(path.getFileName(), versionRecord.uri(), path));

                        version = versionRecord.id();

                        isReadOnly = !versionRecordManager.isLatest(versionRecord);

//                        setContent(wrap, place);
                    }
                });
    }

    public void setContent(ObservablePath path, Overview overview) {

        this.path = path;
        this.overview = overview;

        view.setPreview(overview.getPreview());

        view.setResourceType(clientTypeRegistry.resolve(path));

        view.setProject(overview.getProjectName());
        view.setMetadata(overview.getMetadata(), isReadOnly);

        versionRecordManager.setVersions(version, overview.getMetadata().getVersion());

        view.setDescription(overview.getMetadata().getDescription());
        view.setLastModified(overview.getMetadata().getLastContributor(), overview.getMetadata().getLastModified());
        view.setCreated(overview.getMetadata().getCreator(), overview.getMetadata().getDateCreated());

        view.hideBusyIndicator();

    }

    @Override
    public void onDescriptionEdited(String description) {
        overview.getMetadata().setDescription(description);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
