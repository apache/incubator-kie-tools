package org.uberfire.ext.layout.editor.client.infra.experimental;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponent;
import org.uberfire.experimental.client.resources.i18n.UberfireExperimentalConstants;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

@Dependent
public class DisabledExperimentalLayoutComponent implements LayoutDragComponent {

    private String featureId;

    private DisabledFeatureComponent component;
    private TranslationService translationService;

    @Inject
    public DisabledExperimentalLayoutComponent(DisabledFeatureComponent component, TranslationService translationService) {
        this.component = component;
        this.translationService = translationService;
    }

    @Override
    public String getDragComponentTitle() {
        return translationService.getTranslation(UberfireExperimentalConstants.disabledExperimentalFeature);
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext ctx) {
        return render();
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        return render();
    }

    private IsWidget render() {
        component.show(featureId);
        return ElementWrapperWidget.getWidget(component.getElement());
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }
}
