/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.screen.transfer.export.wizard;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.client.cms.screen.util.DomFactory;
import org.dashbuilder.transfer.DataTransferExportModel;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.client.views.pfly.widgets.HelpIcon;
import org.uberfire.workbench.events.NotificationEvent;

@Templated
@ApplicationScoped
public class ExportSummaryWizardPageView implements ExportSummaryWizardPage.View {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportSummaryWizardPageView.class);

    ContentManagerConstants i18n = ContentManagerConstants.INSTANCE;

    @Inject
    @DataField
    HTMLDivElement exportWizardSummary;

    @Inject
    @DataField
    @Named("span")
    HTMLElement datasetsInformation;

    @Inject
    @DataField
    @Named("span")
    HTMLElement pagesInformation;

    @Inject
    @DataField
    HTMLAnchorElement datasetsInfoAnchor;

    @Inject
    @DataField
    HTMLAnchorElement pagesInfoAnchor;

    @Inject
    @DataField
    HTMLButtonElement downloadExport;

    @Inject
    @DataField
    @Named("h1")
    HTMLHeadingElement exportHeading;

    @Inject
    @DataField
    @Named("span")
    HTMLElement iconSpan;

    @Inject
    @DataField
    HTMLDivElement alertContainer;

    @Inject
    @DataField
    HTMLDivElement navigationSummaryContainer;

    @Inject
    DomFactory domFactory;

    @Inject
    Elemental2DomUtil elementalUtil;

    @Inject
    private Event<NotificationEvent> wbNotification;

    private ExportSummaryWizardPage presenter;

    @Override
    public void init(ExportSummaryWizardPage presenter) {
        this.presenter = presenter;
        alertContainer.hidden = true;

        HelpIcon navigationhelp = new HelpIcon();
        navigationhelp.setHelpContent(i18n.navigationHelpText());
        elementalUtil.appendWidgetToElement(navigationSummaryContainer, navigationhelp);
    }

    @Override
    public HTMLElement getElement() {
        return exportWizardSummary;
    }

    @EventHandler("downloadExport")
    public void downloadAction(ClickEvent click) {
        presenter.confirmDownload();
    }

    @EventHandler("datasetsInfoAnchor")
    public void datasetsInfoAnchorClicked(ClickEvent click) {
        presenter.goToDataSetsPage();
    }

    @EventHandler("pagesInfoAnchor")
    public void pagesInfoAnchorClicked(ClickEvent click) {
        presenter.goToPagesPage();
    }

    @Override
    public void success(DataTransferExportModel dataTransferExportModel) {
        successState();
        showSummary(dataTransferExportModel);
    }

    @Override
    public void validationErrors(DataTransferExportModel dataTransferExportModel,
                                 Map<String, List<String>> pageDependencies) {
        errorState();

        Element errorHeader = domFactory.element("strong");
        errorHeader.textContent = i18n.missingDependencies();
        alertContainer.appendChild(errorHeader);

        Element pageList = domFactory.element("ul");
        pageDependencies.forEach((page, ds) -> {
            Element li = domFactory.listItem(i18n.pageMissingDataSets(page));
            li.appendChild(pageMissingDataSetsList(ds));
            pageList.appendChild(li);
        });
        alertContainer.appendChild(pageList);

        showSummary(dataTransferExportModel);
    }

    @Override
    public void exportError(DataTransferExportModel dataTransferExportModel, String warningMessage) {
        errorState();
        alertContainer.textContent = warningMessage;
        showSummary(dataTransferExportModel);
    }

    private void showSummary(DataTransferExportModel dataTransferExportModel) {
        pagesInformation.textContent = checkPlural(dataTransferExportModel.getPages().size(),
                                                   i18n.pageLabel(),
                                                   i18n.pagesLabel());
        datasetsInformation.textContent = checkPlural(dataTransferExportModel.getDatasetDefinitions().size(),
                                                      i18n.datasetLabel(),
                                                      i18n.datasetsLabel());
    }

    private String checkPlural(int size, String text, String pluralText) {
        String result = size + " ";
        return size == 1 ? result + text : result + pluralText;
    }

    private Element pageMissingDataSetsList(List<String> datasets) {
        Element list = domFactory.element("ul");
        datasets.stream().map(domFactory::listItem).forEach(list::appendChild);
        return list;
    }

    @Override
    public void emptyState() {
        state("pficon-running",
              "",
              true,
              true);
        pagesInformation.textContent = "";
        datasetsInformation.textContent = "";
    }

    @Override
    public void validationError(Throwable throwable) {
        LOGGER.error(throwable.getMessage(), throwable);
        wbNotification.fire(new NotificationEvent(i18n.validationError(),
                                                  NotificationEvent.NotificationType.ERROR));
    }

    private void errorState() {
        state("pficon pficon-error-circle-o",
              i18n.exportWizardHeadingError(),
              false,
              true);
        alertContainer.innerHTML = "";
    }

    private void successState() {
        state("pficon pficon-ok",
              i18n.exportWizardHeadingSuccess(),
              true,
              false);
    }

    private void state(String iconSpanClass,
                       String headingText,
                       boolean hideAlert,
                       boolean hideDownload) {
        iconSpan.className = iconSpanClass;
        exportHeading.textContent = headingText;
        alertContainer.hidden = hideAlert;
        downloadExport.disabled = hideDownload;
    }

}