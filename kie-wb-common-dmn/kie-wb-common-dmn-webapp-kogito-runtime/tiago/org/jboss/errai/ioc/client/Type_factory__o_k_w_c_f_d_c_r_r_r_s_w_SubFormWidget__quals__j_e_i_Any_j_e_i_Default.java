package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget;
import org.kie.workbench.common.forms.processing.engine.handling.IsNestedModel;
import org.kie.workbench.common.forms.processing.engine.handling.NeedsFlush;

public class Type_factory__o_k_w_c_f_d_c_r_r_r_s_w_SubFormWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<SubFormWidget> { public interface o_k_w_c_f_d_c_r_r_r_s_w_SubFormWidgetTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/relations/subform/widget/SubFormWidget.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_r_r_s_w_SubFormWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SubFormWidget.class, "Type_factory__o_k_w_c_f_d_c_r_r_r_s_w_SubFormWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SubFormWidget.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, TakesValue.class, IsNestedModel.class, NeedsFlush.class });
  }

  public SubFormWidget createInstance(final ContextManager contextManager) {
    final SubFormWidget instance = new SubFormWidget();
    setIncompleteInstance(instance);
    final FlowPanel SubFormWidget_formContent = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SubFormWidget_formContent);
    SubFormWidget_FlowPanel_formContent(instance, SubFormWidget_formContent);
    final DynamicFormRenderer SubFormWidget_formRenderer = (DynamicFormRenderer) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_DynamicFormRenderer__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SubFormWidget_formRenderer);
    SubFormWidget_DynamicFormRenderer_formRenderer(instance, SubFormWidget_formRenderer);
    o_k_w_c_f_d_c_r_r_r_s_w_SubFormWidgetTemplateResource templateForSubFormWidget = GWT.create(o_k_w_c_f_d_c_r_r_r_s_w_SubFormWidgetTemplateResource.class);
    Element parentElementForTemplateOfSubFormWidget = TemplateUtil.getRootTemplateParentElement(templateForSubFormWidget.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/relations/subform/widget/SubFormWidget.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/relations/subform/widget/SubFormWidget.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSubFormWidget));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSubFormWidget));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("formContent", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/relations/subform/widget/SubFormWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return SubFormWidget_FlowPanel_formContent(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "formContent");
    templateFieldsMap.put("formContent", SubFormWidget_FlowPanel_formContent(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSubFormWidget), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SubFormWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final SubFormWidget instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final SubFormWidget instance) {
    SubFormWidget_init(instance);
  }

  native static DynamicFormRenderer SubFormWidget_DynamicFormRenderer_formRenderer(SubFormWidget instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget::formRenderer;
  }-*/;

  native static void SubFormWidget_DynamicFormRenderer_formRenderer(SubFormWidget instance, DynamicFormRenderer value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget::formRenderer = value;
  }-*/;

  native static FlowPanel SubFormWidget_FlowPanel_formContent(SubFormWidget instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget::formContent;
  }-*/;

  native static void SubFormWidget_FlowPanel_formContent(SubFormWidget instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget::formContent = value;
  }-*/;

  public native static void SubFormWidget_init(SubFormWidget instance) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget::init()();
  }-*/;
}