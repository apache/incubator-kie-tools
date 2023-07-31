package org.dashbuilder.client.editor.external;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.displayer.client.widgets.ExternalComponentPresenter;
import org.dashbuilder.displayer.external.ExternalComponentMessageHelper;
import org.dashbuilder.patternfly.label.Label;
import org.dashbuilder.patternfly.label.LabelColor;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

import static java.util.stream.Collectors.toMap;

@Dependent
public class ExternalDragComponent implements LayoutDragComponent {

    public static final String COMPONENT_ID_KEY = "componentId";
    public static final String COMPONENT_PARTITION_KEY = "componentPartition";
    public static final String COMPONENT_BASE_URL_KEY = "baseUrl";

    @Inject
    SyncBeanManager beanManager;
    @Inject
    ExternalComponentPresenter externalComponentPresenter;
    @Inject
    ExternalComponentMessageHelper messageHelper;
    @Inject
    Label label;

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        var ltProps = ctx.getComponent().getProperties();
        var storedComponentId = ltProps.get(COMPONENT_ID_KEY);
        var partition = ltProps.get(COMPONENT_PARTITION_KEY);
        var baseUrl = ltProps.get(COMPONENT_BASE_URL_KEY);
        if (storedComponentId == null) {
            label.setLabelColor(LabelColor.RED);
            label.setShowIcon(true);
            label.setText("Component not found.");
            return ElementWrapperWidget.getWidget(label.getElement());
        }

        externalComponentPresenter.withComponentBaseUrlIdAndPartition(baseUrl, storedComponentId, partition);

        var componentProperties = new HashMap<String, Object>(retrieveComponentProperties(storedComponentId, ltProps));
        var message = messageHelper.newInitMessage(componentProperties);
        externalComponentPresenter.sendMessage(message);

        return externalComponentPresenter.getView();
    }

    private Map<String, String> retrieveComponentProperties(String componentId,
                                                            Map<String, String> componentProperties) {
        String prefix = getComponentPrefix(componentId);
        return componentProperties.entrySet()
                .stream().filter(e -> e.getKey().startsWith(prefix))
                .collect(toMap(e -> removeComponentPrefix(
                        componentId, e.getKey()),
                        Map.Entry::getValue));
    }

    private String getComponentPrefix(String componentId) {
        return componentId + ".";
    }

    private String removeComponentPrefix(String componentId, String key) {
        return key.replaceFirst(componentId + ".", "");
    }
}
