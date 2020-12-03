package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.FormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.ValidableFormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultFormGroupViewImpl> { public interface o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroupViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/def/DefaultFormGroupViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultFormGroupViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultFormGroupViewImpl.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, DefaultFormGroupView.class, ValidableFormGroupView.class, FormGroupView.class });
  }

  public DefaultFormGroupViewImpl createInstance(final ContextManager contextManager) {
    final DefaultFormGroupViewImpl instance = new DefaultFormGroupViewImpl();
    setIncompleteInstance(instance);
    final Div DefaultFormGroupViewImpl_helpBlock = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefaultFormGroupViewImpl_helpBlock);
    DefaultFormGroupViewImpl_Div_helpBlock(instance, DefaultFormGroupViewImpl_helpBlock);
    final FieldLabel DefaultFormGroupViewImpl_fieldLabel = (FieldLabel) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DefaultFormGroupViewImpl_fieldLabel);
    DefaultFormGroupViewImpl_FieldLabel_fieldLabel(instance, DefaultFormGroupViewImpl_fieldLabel);
    final FormsElementWrapperWidgetUtilImpl DefaultFormGroupViewImpl_wrapperWidgetUtil = (FormsElementWrapperWidgetUtilImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default");
    DefaultFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(instance, DefaultFormGroupViewImpl_wrapperWidgetUtil);
    o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroupViewImplTemplateResource templateForDefaultFormGroupViewImpl = GWT.create(o_k_w_c_f_d_c_r_f_i_d_DefaultFormGroupViewImplTemplateResource.class);
    Element parentElementForTemplateOfDefaultFormGroupViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDefaultFormGroupViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/def/DefaultFormGroupViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/def/DefaultFormGroupViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultFormGroupViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultFormGroupViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("fieldLabel", new DataFieldMeta());
    dataFieldMetas.put("fieldContainer", new DataFieldMeta());
    dataFieldMetas.put("helpBlock", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/def/DefaultFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(DefaultFormGroupViewImpl_FieldLabel_fieldLabel(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "fieldLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/def/DefaultFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return DefaultFormGroupViewImpl_SimplePanel_fieldContainer(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "fieldContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/def/DefaultFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefaultFormGroupViewImpl_Div_helpBlock(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "helpBlock");
    templateFieldsMap.put("fieldLabel", ElementWrapperWidget.getWidget(DefaultFormGroupViewImpl_FieldLabel_fieldLabel(instance).getElement()));
    templateFieldsMap.put("fieldContainer", DefaultFormGroupViewImpl_SimplePanel_fieldContainer(instance).asWidget());
    templateFieldsMap.put("helpBlock", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefaultFormGroupViewImpl_Div_helpBlock(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultFormGroupViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefaultFormGroupViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefaultFormGroupViewImpl instance, final ContextManager contextManager) {
    instance.clear();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static FormsElementWrapperWidgetUtil DefaultFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(DefaultFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl::wrapperWidgetUtil;
  }-*/;

  native static void DefaultFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(DefaultFormGroupViewImpl instance, FormsElementWrapperWidgetUtil value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl::wrapperWidgetUtil = value;
  }-*/;

  native static FieldLabel DefaultFormGroupViewImpl_FieldLabel_fieldLabel(DefaultFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl::fieldLabel;
  }-*/;

  native static void DefaultFormGroupViewImpl_FieldLabel_fieldLabel(DefaultFormGroupViewImpl instance, FieldLabel value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl::fieldLabel = value;
  }-*/;

  native static SimplePanel DefaultFormGroupViewImpl_SimplePanel_fieldContainer(DefaultFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl::fieldContainer;
  }-*/;

  native static void DefaultFormGroupViewImpl_SimplePanel_fieldContainer(DefaultFormGroupViewImpl instance, SimplePanel value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl::fieldContainer = value;
  }-*/;

  native static Div DefaultFormGroupViewImpl_Div_helpBlock(DefaultFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl::helpBlock;
  }-*/;

  native static void DefaultFormGroupViewImpl_Div_helpBlock(DefaultFormGroupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroupViewImpl::helpBlock = value;
  }-*/;
}