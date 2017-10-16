/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.refactoring.client.usages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.client.resources.i18n.RefactoringConstants;
import org.kie.workbench.common.services.refactoring.service.AssetsUsageService;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * This component searches on the index the usages for a given asset or asset part and displays them in a modal.
 */
@Dependent
public class ShowAssetUsagesDisplayer implements ShowAssetUsagesDisplayerView.Presenter {

    public static String UNKNOWN_ASSET_TYPE = "-";

    private ShowAssetUsagesDisplayerView view;

    private TranslationService translationService;

    private boolean isOkButtonPressed = false;

    private Command okCommand;

    private Command cancelCommand;

    private Caller<AssetsUsageService> assetsUsageServiceCaller;

    private Map<String, ResourceTypeDefinition> existingResourceTypes = new HashMap<>();

    @Inject
    public ShowAssetUsagesDisplayer(ShowAssetUsagesDisplayerView view,
                                    TranslationService translationService,
                                    Caller<AssetsUsageService> assetsUsageServiceCaller) {
        this.view = view;
        this.translationService = translationService;
        this.assetsUsageServiceCaller = assetsUsageServiceCaller;

        view.init(this);
    }

    @PostConstruct
    public void initialize() {
        IOC.getBeanManager().lookupBeans(ResourceTypeDefinition.class).forEach(resourceTypeDefinitionSyncBeanDef -> {
            registerResourceTypeDefinition(resourceTypeDefinitionSyncBeanDef.getInstance());
        });
    }

    protected void registerResourceTypeDefinition(ResourceTypeDefinition resourceTypeDefinition) {
        existingResourceTypes.put(resourceTypeDefinition.getSuffix(),
                                  resourceTypeDefinition);
    }

    /**
     * Displays the usages for the asset identified by the resourceFQN
     *
     * @param path          Path for the given asset
     * @param resourceFQN   Fully Qualified Name of the asset
     * @param resourceType  The type of asset.
     * @param okCommand     {@link Command} that is going to run when the user presses ok button. If there are no usages on the index the command will be automatically executed without opening the modal
     * @param cancelCommand {@link Command} that is going to run when the user presses Cancel button.
     */
    public void showAssetUsages(final Path path,
                                final String resourceFQN,
                                final ResourceType resourceType,
                                final Command okCommand,
                                final Command cancelCommand) {

        showAssetUsages(translationService.format(RefactoringConstants.ShowAssetUsagesDisplayerViewViewImplAssetUsages,
                                                  resourceFQN),
                        path,
                        resourceFQN,
                        resourceType,
                        okCommand,
                        cancelCommand);
    }

    /**
     * Displays the usages for the asset identified by the resourceFQN using a specific message instead of the default message.
     *
     * @param headerMessage The message that will be shown instead of the default message
     * @param path          Path for the given asset
     * @param resourceFQN   Fully Qualified Name of the asset
     * @param resourceType  The type of asset.
     * @param okCommand     {@link Command} that is going to run when the user presses ok button. If there are no usages on the index the command will be automatically executed without opening the modal
     * @param cancelCommand {@link Command} that is going to run when the user presses Cancel button.
     */
    public void showAssetUsages(final String headerMessage,
                                final Path path,
                                final String resourceFQN,
                                final ResourceType resourceType,
                                final Command okCommand,
                                final Command cancelCommand) {
        PortablePreconditions.checkNotNull("headerMessage",
                                           headerMessage);

        HTMLElement messageContainer = view.getDefaultMessageContainer();

        messageContainer.setInnerHTML(headerMessage);

        showAssetUsages(messageContainer,
                        path,
                        resourceFQN,
                        resourceType,
                        okCommand,
                        cancelCommand);
    }

    /**
     * Displays the usages for the asset identified by the resourceFQN using a specific {@link HTMLElement} on the header instead of the default message.
     *
     * @param headerElement The element that will be shown instead of the default message
     * @param path          Path for the given asset
     * @param resourceFQN   Fully Qualified Name of the asset
     * @param resourceType  The type of asset.
     * @param okCommand     {@link Command} that is going to run when the user presses ok button. If there are no usages on the index the command will be automatically executed without opening the modal
     * @param cancelCommand {@link Command} that is going to run when the user presses Cancel button.
     */
    public void showAssetUsages(final HTMLElement headerElement,
                                final Path path,
                                final String resourceFQN,
                                final ResourceType resourceType,
                                final Command okCommand,
                                final Command cancelCommand) {
        PortablePreconditions.checkNotNull("headerElement",
                                           headerElement);

        PortablePreconditions.checkNotNull("path",
                                           path);
        PortablePreconditions.checkNotNull("resourceFQN",
                                           resourceFQN);
        PortablePreconditions.checkNotNull("resourceType",
                                           resourceType);
        PortablePreconditions.checkNotNull("okCommand",
                                           okCommand);
        PortablePreconditions.checkNotNull("cancelCommand",
                                           cancelCommand);

        isOkButtonPressed = false;

        this.okCommand = okCommand;
        this.cancelCommand = cancelCommand;

        assetsUsageServiceCaller.call(getCallback(okCommand,
                                                  cancelCommand,
                                                  headerElement)).getAssetUsages(resourceFQN,
                                                                                 resourceType,
                                                                                 path);
    }

    /**
     * Displays the usages for the asset part identified by the resourPart parameter.
     *
     * @param path          Path for the given asset
     * @param resourceFQN   Fully Qualified Name of the asset
     * @param resourcePart  The name of the part to search.
     * @param partType      The {@link PartType} that to search.
     * @param okCommand     {@link Command} that is going to run when the user presses ok button. If there are no usages on the index the command will be automatically executed without opening the modal
     * @param cancelCommand {@link Command} that is going to run when the user presses Cancel button.
     */
    public void showAssetPartUsages(Path path,
                                    String resourceFQN,
                                    String resourcePart,
                                    PartType partType,
                                    Command okCommand,
                                    Command cancelCommand) {

        showAssetPartUsages(translationService.format(RefactoringConstants.ShowAssetUsagesDisplayerViewViewImplPartUsages,
                                                      resourcePart),
                            path,
                            resourceFQN,
                            resourcePart,
                            partType,
                            okCommand,
                            cancelCommand);
    }

    /**
     * Displays the usages for the asset part identified by the resourPart parameter using a specific message instead of the default message..
     *
     * @param headerMessage The message that will be shown instead of the default message
     * @param path          Path for the given asset
     * @param resourceFQN   Fully Qualified Name of the asset
     * @param resourcePart  The name of the part to search.
     * @param partType      The {@link PartType} that to search.
     * @param okCommand     {@link Command} that is going to run when the user presses ok button. If there are no usages on the index the command will be automatically executed without opening the modal
     * @param cancelCommand {@link Command} that is going to run when the user presses Cancel button.
     */
    public void showAssetPartUsages(final String headerMessage,
                                    final Path path,
                                    final String resourceFQN,
                                    final String resourcePart,
                                    final PartType partType,
                                    final Command okCommand,
                                    final Command cancelCommand) {
        PortablePreconditions.checkNotNull("headerMessage",
                                           headerMessage);

        HTMLElement messageContainer = view.getDefaultMessageContainer();

        messageContainer.setInnerHTML(headerMessage);

        showAssetPartUsages(messageContainer,
                            path,
                            resourceFQN,
                            resourcePart,
                            partType,
                            okCommand,
                            cancelCommand);
    }

    /**
     * Displays the usages for the asset part identified by the resourPart parameter using a specific {@link HTMLElement} instead of the default message..
     *
     * @param headerElement The element that will be shown instead of the default message
     * @param path          Path for the given asset
     * @param resourceFQN   Fully Qualified Name of the asset
     * @param resourcePart  The name of the part to search.
     * @param partType      The {@link PartType} that to search.
     * @param okCommand     {@link Command} that is going to run when the user presses ok button. If there are no usages on the index the command will be automatically executed without opening the modal
     * @param cancelCommand {@link Command} that is going to run when the user presses Cancel button.
     */
    public void showAssetPartUsages(final HTMLElement headerElement,
                                    final Path path,
                                    final String resourceFQN,
                                    final String resourcePart,
                                    final PartType partType,
                                    final Command okCommand,
                                    final Command cancelCommand) {
        PortablePreconditions.checkNotNull("headerElement",
                                           headerElement);
        PortablePreconditions.checkNotNull("path",
                                           path);
        PortablePreconditions.checkNotNull("resourceFQN",
                                           resourceFQN);
        PortablePreconditions.checkNotNull("resourcePart",
                                           resourcePart);
        PortablePreconditions.checkNotNull("partType",
                                           partType);
        PortablePreconditions.checkNotNull("okCommand",
                                           okCommand);
        PortablePreconditions.checkNotNull("cancelCommand",
                                           cancelCommand);

        isOkButtonPressed = false;

        this.okCommand = okCommand;
        this.cancelCommand = cancelCommand;

        assetsUsageServiceCaller.call(getCallback(okCommand,
                                                  cancelCommand,
                                                  headerElement)).getAssetPartUsages(resourceFQN,
                                                                                     resourcePart,
                                                                                     partType,
                                                                                     path);
    }

    protected RemoteCallback<List<Path>> getCallback(final Command okCommand,
                                                     final Command cancelCommand,
                                                     final HTMLElement headerElement) {
        return queryResults -> {
            ShowAssetUsagesDisplayer.this.isOkButtonPressed = false;

            ShowAssetUsagesDisplayer.this.okCommand = okCommand;
            ShowAssetUsagesDisplayer.this.cancelCommand = cancelCommand;

            if (queryResults != null && !queryResults.isEmpty()) {
                view.show(headerElement,
                          queryResults);
            } else {
                okCommand.execute();
            }
        };
    }

    @Override
    public String getAssetType(Path path) {
        String extension = "";
        String fileName = path.getFileName();
        if (fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        ResourceTypeDefinition typeDefinition = existingResourceTypes.get(extension);
        if (typeDefinition != null) {
            return typeDefinition.getShortName();
        }
        return UNKNOWN_ASSET_TYPE;
    }

    @Override
    public void onClose() {
        if (isOkButtonPressed) {
            okCommand.execute();
        } else {
            cancelCommand.execute();
        }
    }

    @Override
    public void onOk() {
        isOkButtonPressed = true;
    }

    @Override
    public void onCancel() {
        isOkButtonPressed = false;
    }
}
