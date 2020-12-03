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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.crud.client.component.CrudComponent;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.ColumnGeneratorManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormWidget;
import org.kie.workbench.common.forms.processing.engine.handling.IsNestedModel;

public class Type_factory__o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSubFormWidget> { public interface o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormWidgetTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/relations/multipleSubform/MultipleSubFormWidget.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleSubFormWidget.class, "Type_factory__o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleSubFormWidget.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, TakesValue.class, IsNestedModel.class });
  }

  public MultipleSubFormWidget createInstance(final ContextManager contextManager) {
    final DynamicFormRenderer _formRenderer_1 = (DynamicFormRenderer) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_DynamicFormRenderer__quals__j_e_i_Any_j_e_i_Default");
    final ColumnGeneratorManager _columnGeneratorManager_0 = (ColumnGeneratorManager) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_r_r_m_ColumnGeneratorManager__quals__j_e_i_Any_j_e_i_Default");
    final CrudComponent _crudComponent_2 = (CrudComponent) contextManager.getInstance("Type_factory__o_k_w_c_f_c_c_c_CrudComponent__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_3 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final MultipleSubFormWidget instance = new MultipleSubFormWidget(_columnGeneratorManager_0, _formRenderer_1, _crudComponent_2, _translationService_3);
    registerDependentScopedReference(instance, _formRenderer_1);
    registerDependentScopedReference(instance, _crudComponent_2);
    registerDependentScopedReference(instance, _translationService_3);
    setIncompleteInstance(instance);
    final FlowPanel MultipleSubFormWidget_content = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MultipleSubFormWidget_content);
    MultipleSubFormWidget_FlowPanel_content(instance, MultipleSubFormWidget_content);
    o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormWidgetTemplateResource templateForMultipleSubFormWidget = GWT.create(o_k_w_c_f_d_c_r_r_r_m_MultipleSubFormWidgetTemplateResource.class);
    Element parentElementForTemplateOfMultipleSubFormWidget = TemplateUtil.getRootTemplateParentElement(templateForMultipleSubFormWidget.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/relations/multipleSubform/MultipleSubFormWidget.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/relations/multipleSubform/MultipleSubFormWidget.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleSubFormWidget));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleSubFormWidget));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormWidget", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/relations/multipleSubform/MultipleSubFormWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return MultipleSubFormWidget_FlowPanel_content(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("content", MultipleSubFormWidget_FlowPanel_content(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleSubFormWidget), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MultipleSubFormWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final MultipleSubFormWidget instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static FlowPanel MultipleSubFormWidget_FlowPanel_content(MultipleSubFormWidget instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormWidget::content;
  }-*/;

  native static void MultipleSubFormWidget_FlowPanel_content(MultipleSubFormWidget instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormWidget::content = value;
  }-*/;
}