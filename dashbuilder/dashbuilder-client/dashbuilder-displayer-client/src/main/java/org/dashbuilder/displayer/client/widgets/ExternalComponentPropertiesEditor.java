/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.displayer.client.widgets;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.external.model.ComponentParameter;
import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.external.service.ExternalComponentService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

@Dependent
public class ExternalComponentPropertiesEditor implements IsWidget {
    
    CommonConstants i18n = CommonConstants.INSTANCE;

    private static final String DEFAULT_CATEGORY = "Component Properties";

    public interface View extends UberView<ExternalComponentPropertiesEditor> {

        void componentNotFound();

        void addCategories(Collection<PropertyEditorCategory> categories);

        void noPropertiesComponent();

    }

    @Inject
    View view;

    @Inject
    Caller<ExternalComponentService> externalComponentService;

    @Inject
    BusyIndicatorView loading;

    private ExternalComponent currentComp;

    Map<String, String> settings;

    Consumer<Map<String, String>> onPropertiesChange;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void init(String componentId,
                     Map<String, String> settings,
                     Consumer<Map<String, String>> onPropertiesChange) {
        this.settings = new HashMap<>(settings);
        this.onPropertiesChange = onPropertiesChange;
        if (componentId == null) {
            view.componentNotFound();
        } else {
            loading.showBusyIndicator(i18n.loadingComponent());
            externalComponentService.call((Optional<ExternalComponent> comp) -> this.loadProperties(comp))
                                    .byId(componentId);
        }

    }

    private void loadProperties(Optional<ExternalComponent> compOp) {
        loading.hideBusyIndicator();
        if (compOp.isPresent()) {
            currentComp = compOp.get();
            loadProperties();
        } else {
            view.componentNotFound();
        }
    }

    private void loadProperties() {
        List<ComponentParameter> parameters = currentComp.getParameters();
        if (parameters == null || parameters.isEmpty()) {
            view.noPropertiesComponent();
        } else {
            Map<String, PropertyEditorCategory> categories = new HashMap<>();
            categories.put(DEFAULT_CATEGORY, new PropertyEditorCategory(DEFAULT_CATEGORY));
            parameters.forEach(this::initializeSetting);
            notifyPropertyChange();
            for (ComponentParameter param : parameters) {
                PropertyEditorCategory category = categories.get(DEFAULT_CATEGORY);
                if (param.getCategory() != null) {
                    String catName = param.getCategory();
                    category = categories.computeIfAbsent(catName, PropertyEditorCategory::new);
                }
                category.withField(buildField(param));
            }
            view.addCategories(categories.values());
        }
    }

    public void onPropertyChange(String key, String value) {
        settings.put(key, value);
        notifyPropertyChange();
    }

    private void notifyPropertyChange() {
        if (onPropertiesChange != null) {
            onPropertiesChange.accept(settings);
        }
    }

    private PropertyEditorFieldInfo buildField(ComponentParameter p) {
        String fieldKey = p.getName();
        String currentValue = settings.get(fieldKey);
        PropertyEditorType type = getType(p.getType());
        PropertyEditorFieldInfo field = new PropertyEditorFieldInfo(p.getLabel(), p.getDefaultValue(), type);
        field.withKey(fieldKey);
        if (field.getType() == PropertyEditorType.COMBO) {
            field.withComboValues(p.getComboValues());
        }
        if (currentValue != null) {
            field.setCurrentStringValue(currentValue);
        }
        return field;
    }

    private PropertyEditorType getType(String type) {
        return Stream.of(PropertyEditorType.values())
                     .filter(t -> t.name().equalsIgnoreCase(type))
                     .findFirst().orElse(PropertyEditorType.TEXT);
    }

    private void initializeSetting(ComponentParameter p) {
        String componentProperty = settings.get(p.getName());
        if (componentProperty == null) {
            settings.put(p.getName(), p.getDefaultValue());
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    void setExternalComponentService(Caller<ExternalComponentService> externalComponentService) {
        this.externalComponentService = externalComponentService;
    }
    

}