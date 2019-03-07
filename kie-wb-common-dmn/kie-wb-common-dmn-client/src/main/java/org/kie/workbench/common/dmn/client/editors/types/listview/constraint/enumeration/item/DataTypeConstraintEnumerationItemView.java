/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.MenuInitializer;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueComponentSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem.NULL;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintEnumerationItemView_None;

@Templated
@Dependent
public class DataTypeConstraintEnumerationItemView implements DataTypeConstraintEnumerationItem.View {

    static final String HIGHLIGHTED_CSS_CLASS = "highlighted";

    static final String NONE_CSS_CLASS = "none";

    private final TypedValueComponentSelector componentSelector;

    private TypedValueSelector typedValueSelector;

    @DataField("value-text")
    private final HTMLElement valueText;

    @DataField("value-input-container")
    private final HTMLDivElement valueInputContainer;

    @DataField("save-anchor")
    private final HTMLAnchorElement saveAnchor;

    @DataField("edit-anchor")
    private final HTMLAnchorElement editAnchor;

    @DataField("remove-anchor")
    private final HTMLAnchorElement removeAnchor;

    @DataField("move-up-anchor")
    private final HTMLAnchorElement moveUpAnchor;

    @DataField("move-down-anchor")
    private final HTMLAnchorElement moveDownAnchor;

    @DataField("kebab-menu")
    private final HTMLDivElement kebabMenu;

    private final TranslationService translationService;

    private DataTypeConstraintEnumerationItem presenter;

    @Inject
    public DataTypeConstraintEnumerationItemView(final @Named("span") HTMLElement valueText,
                                                 final HTMLDivElement valueInput,
                                                 final HTMLAnchorElement saveAnchor,
                                                 final HTMLAnchorElement editAnchor,
                                                 final HTMLAnchorElement removeAnchor,
                                                 final HTMLAnchorElement moveUpAnchor,
                                                 final HTMLAnchorElement moveDownAnchor,
                                                 final HTMLDivElement kebabMenu,
                                                 final TranslationService translationService,
                                                 final TypedValueComponentSelector valueComponentSelector) {
        this.valueText = valueText;
        this.valueInputContainer = valueInput;
        this.saveAnchor = saveAnchor;
        this.editAnchor = editAnchor;
        this.removeAnchor = removeAnchor;
        this.moveUpAnchor = moveUpAnchor;
        this.moveDownAnchor = moveDownAnchor;
        this.kebabMenu = kebabMenu;
        this.translationService = translationService;
        this.componentSelector = valueComponentSelector;
    }

    @Override
    public void init(final DataTypeConstraintEnumerationItem presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void setupKebabElement() {
        makeMenuInitializer(kebabMenu, ".dropdown").init();
    }

    @Override
    public void showValueText() {
        show(valueText);
        hide(valueInputContainer);
    }

    @Override
    public void showValueInput() {
        show(valueInputContainer);
        hide(valueText);
    }

    @Override
    public void focusValueInput() {
        typedValueSelector.select();
    }

    @Override
    public void showSaveButton() {
        show(saveAnchor);
    }

    @Override
    public void hideSaveButton() {
        hide(saveAnchor);
    }

    @Override
    public void enableHighlight() {
        getElement().classList.add(HIGHLIGHTED_CSS_CLASS);
    }

    @Override
    public void disableHighlight() {
        getElement().classList.remove(HIGHLIGHTED_CSS_CLASS);
    }

    @EventHandler("value-text")
    public void onValueTextClick(final ClickEvent e) {
        presenter.enableEditMode();
    }

    @EventHandler("save-anchor")
    public void onSaveAnchorClick(final ClickEvent e) {
        presenter.save(typedValueSelector.getValue());
    }

    @EventHandler("edit-anchor")
    public void onEditAnchorClick(final ClickEvent e) {
        presenter.enableEditMode();
    }

    @EventHandler("remove-anchor")
    public void onRemoveAnchorClick(final ClickEvent e) {
        presenter.remove();
    }

    @EventHandler("move-up-anchor")
    public void onMoveUpAnchorClick(final ClickEvent e) {
        presenter.moveUp();
    }

    @EventHandler("move-down-anchor")
    public void onMoveDownAnchorClick(final ClickEvent e) {
        presenter.moveDown();
    }

    public void onValueInputBlur(final BlurEvent blurEvent) {

        final boolean isNotSaveButtonClick = !Objects.equals(getEventTarget(blurEvent), getSaveAnchorTarget());

        if (isNotSaveButtonClick) {
            presenter.discardEditMode();
        }
    }

    @Override
    public void setValue(final String value) {
        setText(value);
        setInput(value);
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        typedValueSelector.setPlaceholder(placeholder);
    }

    @Override
    public void setComponentSelector(final String type) {
        typedValueSelector = this.componentSelector.makeSelectorForType(type);
        valueInputContainer.innerHTML = "";
        valueInputContainer.appendChild(typedValueSelector.getElement());
        typedValueSelector.onValueInputBlur(this::onValueInputBlur);
    }

    private void setText(final String value) {
        if (isNULL(value)) {
            valueText.classList.add(NONE_CSS_CLASS);
            valueText.textContent = none();
        } else {
            valueText.classList.remove(NONE_CSS_CLASS);
            valueText.textContent = typedValueSelector.toDisplay(value);
        }
    }

    private void setInput(final String value) {
        if (isNULL(value)) {
            typedValueSelector.setValue("");
        } else {
            typedValueSelector.setValue(value);
        }
    }

    private boolean isNULL(final String value) {
        return Objects.equals(value, NULL);
    }

    private String none() {
        return translationService.format(DataTypeConstraintEnumerationItemView_None);
    }

    Object getEventTarget(final BlurEvent blurEvent) {
        return blurEvent.getNativeEvent().getRelatedEventTarget();
    }

    Object getSaveAnchorTarget() {
        return saveAnchor;
    }

    MenuInitializer makeMenuInitializer(final HTMLDivElement kebabMenu,
                                        final String dropDownClass) {
        return new MenuInitializer(kebabMenu, dropDownClass);
    }
}
