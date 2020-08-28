package org.dashbuilder.client.editor.external;

import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.displayer.client.widgets.ExternalComponentEditorPopUp;
import org.dashbuilder.displayer.client.widgets.ExternalComponentPresenter;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.mvp.Command;

import static java.util.stream.Collectors.toMap;
import static org.dashbuilder.external.model.ExternalComponent.COMPONENT_ID_KEY;
import static org.dashbuilder.external.model.ExternalComponent.COMPONENT_PARTITION_KEY;

@Dependent
public class ExternalDragComponent implements ExternalComponentDragDef, HasModalConfiguration {

    @Inject
    SyncBeanManager beanManager;
    @Inject
    ExternalComponentPresenter externalComponentPresenter;

    private String componentId;
    private String componentName;
    private String componentIcon;

    @Override
    public IsWidget getPreviewWidget(RenderingContext ctx) {
        return getShowWidget(ctx);
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        Map<String, String> ltProps = ctx.getComponent().getProperties();
        String storedComponentId = ltProps.get(COMPONENT_ID_KEY);
        String partition = ltProps.get(COMPONENT_PARTITION_KEY);
        if (storedComponentId == null) {
            return new Label("Component not found.");
        }
        
        if (partition != null) {
            externalComponentPresenter.withComponent(storedComponentId, partition);
        } else {
            externalComponentPresenter.withComponent(storedComponentId);
        }
        
        Map<String, String> componentProperties = retrieveComponentProperties(storedComponentId, ltProps);
        ExternalComponentMessage message = ExternalComponentMessage.create(componentProperties);
        externalComponentPresenter.sendMessage(message);
        
        return externalComponentPresenter.getView();
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    @Override
    public String getComponentIcon() {
        return componentIcon;
    }

    @Override
    public String getComponentId() {
        return componentId;
    }

    @Override
    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    @Override
    public void setDragInfo(String componentName, String componentIcon) {
        this.componentName = componentName;
        this.componentIcon = componentIcon;
    }

    @Override
    public Modal getConfigurationModal(ModalConfigurationContext ctx) {
        String storedComponentId = ctx.getComponentProperty(COMPONENT_ID_KEY);
        ExternalComponentEditorPopUp editor = beanManager.lookupBean(ExternalComponentEditorPopUp.class).newInstance();
        Map<String, String> existingProps = retrieveComponentProperties(storedComponentId, ctx.getComponentProperties());
        editor.init(storedComponentId,
                    existingProps,
                    getCloseCommand(editor, ctx),
                    getSaveCommand(storedComponentId, editor, ctx));
        return editor;
    }

    protected Command getSaveCommand(String componentId, final ExternalComponentEditorPopUp editor, final ModalConfigurationContext ctx) {
        return () -> {
            editor.getProperties().forEach((k, v) -> {
                String key = appendComponentPrefix(componentId, k);
                ctx.getComponentProperties().put(key, v);
            });
            ctx.configurationFinished();
            beanManager.destroyBean(editor);
        };
    }

    protected Command getCloseCommand(final ExternalComponentEditorPopUp editor, final ModalConfigurationContext ctx) {
        return () -> {
            ctx.configurationCancelled();
            beanManager.destroyBean(editor);
        };
    }

    private Map<String, String> retrieveComponentProperties(String componentId, Map<String, String> componentProperties) {
        String prefix = getComponentPrefix(componentId);
        return componentProperties.entrySet()
                                  .stream().filter(e -> e.getKey().startsWith(prefix))
                                  .collect(toMap(e -> removeComponentPrefix(componentId, e.getKey()),
                                                 Map.Entry::getValue));
    }

    private String getComponentPrefix(String componentId) {
        return componentId + ".";
    }

    private String appendComponentPrefix(String componentId, String key) {
        return componentId + "." + key;
    }

    private String removeComponentPrefix(String componentId, String key) {
        return key.replaceFirst(componentId + ".", "");
    }

}
