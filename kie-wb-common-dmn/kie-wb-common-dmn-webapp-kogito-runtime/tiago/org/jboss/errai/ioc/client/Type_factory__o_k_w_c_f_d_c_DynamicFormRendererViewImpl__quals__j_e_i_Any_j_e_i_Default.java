package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
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
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer.DynamicFormRendererView;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormLayoutGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;

public class Type_factory__o_k_w_c_f_d_c_DynamicFormRendererViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicFormRendererViewImpl> { public interface o_k_w_c_f_d_c_DynamicFormRendererViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/DynamicFormRendererViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_DynamicFormRendererViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DynamicFormRendererViewImpl.class, "Type_factory__o_k_w_c_f_d_c_DynamicFormRendererViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DynamicFormRendererViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, DynamicFormRendererView.class });
  }

  public DynamicFormRendererViewImpl createInstance(final ContextManager contextManager) {
    final DynamicFormRendererViewImpl instance = new DynamicFormRendererViewImpl();
    setIncompleteInstance(instance);
    final FlowPanel DynamicFormRendererViewImpl_formContent = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DynamicFormRendererViewImpl_formContent);
    DynamicFormRendererViewImpl_FlowPanel_formContent(instance, DynamicFormRendererViewImpl_formContent);
    final FormLayoutGenerator DynamicFormRendererViewImpl_layoutGenerator = (FormLayoutGenerator) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_FormLayoutGenerator__quals__j_e_i_Any");
    registerDependentScopedReference(instance, DynamicFormRendererViewImpl_layoutGenerator);
    DynamicFormRendererViewImpl_FormLayoutGenerator_layoutGenerator(instance, DynamicFormRendererViewImpl_layoutGenerator);
    final FormsElementWrapperWidgetUtilImpl DynamicFormRendererViewImpl_wrapperWidgetUtil = (FormsElementWrapperWidgetUtilImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default");
    DynamicFormRendererViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(instance, DynamicFormRendererViewImpl_wrapperWidgetUtil);
    o_k_w_c_f_d_c_DynamicFormRendererViewImplTemplateResource templateForDynamicFormRendererViewImpl = GWT.create(o_k_w_c_f_d_c_DynamicFormRendererViewImplTemplateResource.class);
    Element parentElementForTemplateOfDynamicFormRendererViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDynamicFormRendererViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/DynamicFormRendererViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/DynamicFormRendererViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDynamicFormRendererViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDynamicFormRendererViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("formContent", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl", "org/kie/workbench/common/forms/dynamic/client/DynamicFormRendererViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return DynamicFormRendererViewImpl_FlowPanel_formContent(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "formContent");
    templateFieldsMap.put("formContent", DynamicFormRendererViewImpl_FlowPanel_formContent(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDynamicFormRendererViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DynamicFormRendererViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DynamicFormRendererViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static FlowPanel DynamicFormRendererViewImpl_FlowPanel_formContent(DynamicFormRendererViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl::formContent;
  }-*/;

  native static void DynamicFormRendererViewImpl_FlowPanel_formContent(DynamicFormRendererViewImpl instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl::formContent = value;
  }-*/;

  native static FormsElementWrapperWidgetUtil DynamicFormRendererViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(DynamicFormRendererViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl::wrapperWidgetUtil;
  }-*/;

  native static void DynamicFormRendererViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(DynamicFormRendererViewImpl instance, FormsElementWrapperWidgetUtil value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl::wrapperWidgetUtil = value;
  }-*/;

  native static FormLayoutGenerator DynamicFormRendererViewImpl_FormLayoutGenerator_layoutGenerator(DynamicFormRendererViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl::layoutGenerator;
  }-*/;

  native static void DynamicFormRendererViewImpl_FormLayoutGenerator_layoutGenerator(DynamicFormRendererViewImpl instance, FormLayoutGenerator value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl::layoutGenerator = value;
  }-*/;
}