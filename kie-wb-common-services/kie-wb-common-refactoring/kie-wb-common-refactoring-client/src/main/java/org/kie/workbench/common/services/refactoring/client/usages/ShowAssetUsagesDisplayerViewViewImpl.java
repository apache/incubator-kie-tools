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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.services.refactoring.client.resources.i18n.RefactoringConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterYesNoCancelButtons;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

@Templated
public class ShowAssetUsagesDisplayerViewViewImpl implements IsElement,
                                                             ShowAssetUsagesDisplayerView {

    @Inject
    private Document document;

    @Inject
    @DataField
    private Div labelContainer;

    @DataField
    private PagedTable<Path> usedByTable = new PagedTable();

    @Inject
    private TranslationService translationService;

    private ListDataProvider<Path> usedByFilesProvider = new ListDataProvider<>();

    private Presenter presenter;

    private BaseModal modal;

    private ModalFooterYesNoCancelButtons footer;

    @PostConstruct
    public void init() {
        modal = new BaseModal();

        modal.setTitle(translationService.getTranslation(RefactoringConstants.ShowAssetUsagesDisplayerViewViewImplTitle));

        modal.setBody(ElementWrapperWidget.getWidget(this.getElement()));

        modal.addHideHandler(event -> presenter.onClose());

        footer = new ModalFooterYesNoCancelButtons(modal,
                                                   null,
                                                   null,
                                                   null,
                                                   null,
                                                   this::onProceed,
                                                   translationService.getTranslation(RefactoringConstants.ShowAssetUsagesDisplayerViewViewImplProceed),
                                                   null,
                                                   null,
                                                   this::onCancel,
                                                   translationService.getTranslation(RefactoringConstants.ShowAssetUsagesDisplayerViewViewImplCancel),
                                                   null,
                                                   null);

        modal.add(footer);

        initTable();
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(HTMLElement labelElement,
                     List<Path> paths) {

        if (paths != null && !paths.isEmpty()) {
            DOMUtil.removeAllChildren(labelContainer);
            labelContainer.appendChild(labelElement);

            usedByFilesProvider.getList().clear();
            usedByFilesProvider.getList().addAll(paths);
        }
        modal.show();
    }

    @Override
    public HTMLElement getDefaultMessageContainer() {
        return document.createElement("P");
    }

    private void initTable() {
        usedByTable.columnPickerButton.setVisible(true);

        usedByFilesProvider.addDataDisplay(usedByTable);

        Column<Path, String> nameColumn = new TextColumn<Path>() {
            @Override
            public String getValue(Path row) {
                return row != null ? row.getFileName() : null;
            }

            @Override
            public void render(Cell.Context context,
                               Path object,
                               SafeHtmlBuilder sb) {
                final String currentValue = getValue(object);
                if (currentValue != null) {
                    sb.append(SafeHtmlUtils.fromTrustedString("<div title=\""));
                    sb.append(SafeHtmlUtils.fromString(currentValue));
                    sb.append(SafeHtmlUtils.fromTrustedString("\">"));
                }
                super.render(context,
                             object,
                             sb);
                if (currentValue != null) {
                    sb.append(SafeHtmlUtils.fromTrustedString("</div>"));
                }
            }
        };

        usedByTable.addColumn(nameColumn,
                              translationService.getTranslation(RefactoringConstants.ShowAssetUsagesDisplayerViewViewImplName));

        Column<Path, String> assetType = new TextColumn<Path>() {
            @Override
            public String getValue(Path row) {
                return row != null ? presenter.getAssetType(row) : null;
            }

            @Override
            public void render(Cell.Context context,
                               Path object,
                               SafeHtmlBuilder sb) {
                final String currentValue = getValue(object);
                if (currentValue != null) {
                    sb.append(SafeHtmlUtils.fromTrustedString("<div title=\""));
                    sb.append(SafeHtmlUtils.fromString(currentValue));
                    sb.append(SafeHtmlUtils.fromTrustedString("\">"));
                }
                super.render(context,
                             object,
                             sb);
                if (currentValue != null) {
                    sb.append(SafeHtmlUtils.fromTrustedString("</div>"));
                }
            }
        };

        usedByTable.addColumn(assetType,
                              translationService.getTranslation(RefactoringConstants.ShowAssetUsagesDisplayerViewViewImplAssetType));

        Column<Path, String> pathColumn = new TextColumn<Path>() {
            @Override
            public String getValue(Path row) {
                String pathStr = null;
                if (row != null && row.getFileName() != null) {
                    pathStr = row.toURI().substring(0,
                                                    row.toURI().lastIndexOf('/'));
                }
                return pathStr;
            }

            @Override
            public void render(Cell.Context context,
                               Path object,
                               SafeHtmlBuilder sb) {
                final String currentValue = getValue(object);
                if (currentValue != null) {
                    sb.append(SafeHtmlUtils.fromTrustedString("<div title=\""));
                    sb.append(SafeHtmlUtils.fromString(currentValue));
                    sb.append(SafeHtmlUtils.fromTrustedString("\">"));
                }
                super.render(context,
                             object,
                             sb);
                if (currentValue != null) {
                    sb.append(SafeHtmlUtils.fromTrustedString("</div>"));
                }
            }
        };

        usedByTable.addColumn(pathColumn,
                              translationService.getTranslation(RefactoringConstants.ShowAssetUsagesDisplayerViewViewImplPath));
    }

    protected void onProceed() {
        presenter.onOk();
        modal.hide();
    }

    protected void onCancel() {
        presenter.onCancel();
        modal.hide();
    }
}
