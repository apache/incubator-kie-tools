package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
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
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CheckBoxFormGroupViewImpl> { public interface o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroupViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/checkbox/CheckBoxFormGroupViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CheckBoxFormGroupViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CheckBoxFormGroupViewImpl.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, CheckBoxFormGroupView.class, ValidableFormGroupView.class, FormGroupView.class });
  }

  public CheckBoxFormGroupViewImpl createInstance(final ContextManager contextManager) {
    final CheckBoxFormGroupViewImpl instance = new CheckBoxFormGroupViewImpl();
    setIncompleteInstance(instance);
    final FormsElementWrapperWidgetUtilImpl CheckBoxFormGroupViewImpl_wrapperWidgetUtil = (FormsElementWrapperWidgetUtilImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default");
    CheckBoxFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(instance, CheckBoxFormGroupViewImpl_wrapperWidgetUtil);
    final FieldLabel CheckBoxFormGroupViewImpl_fieldLabel = (FieldLabel) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CheckBoxFormGroupViewImpl_fieldLabel);
    CheckBoxFormGroupViewImpl_FieldLabel_fieldLabel(instance, CheckBoxFormGroupViewImpl_fieldLabel);
    final Div CheckBoxFormGroupViewImpl_helpBlock = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CheckBoxFormGroupViewImpl_helpBlock);
    CheckBoxFormGroupViewImpl_Div_helpBlock(instance, CheckBoxFormGroupViewImpl_helpBlock);
    o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroupViewImplTemplateResource templateForCheckBoxFormGroupViewImpl = GWT.create(o_k_w_c_f_d_c_r_f_i_c_CheckBoxFormGroupViewImplTemplateResource.class);
    Element parentElementForTemplateOfCheckBoxFormGroupViewImpl = TemplateUtil.getRootTemplateParentElement(templateForCheckBoxFormGroupViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/checkbox/CheckBoxFormGroupViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/checkbox/CheckBoxFormGroupViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCheckBoxFormGroupViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCheckBoxFormGroupViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("fieldLabel", new DataFieldMeta());
    dataFieldMetas.put("helpBlock", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/checkbox/CheckBoxFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(CheckBoxFormGroupViewImpl_FieldLabel_fieldLabel(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "fieldLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/checkbox/CheckBoxFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CheckBoxFormGroupViewImpl_Div_helpBlock(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "helpBlock");
    templateFieldsMap.put("fieldLabel", ElementWrapperWidget.getWidget(CheckBoxFormGroupViewImpl_FieldLabel_fieldLabel(instance).getElement()));
    templateFieldsMap.put("helpBlock", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CheckBoxFormGroupViewImpl_Div_helpBlock(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCheckBoxFormGroupViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CheckBoxFormGroupViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final CheckBoxFormGroupViewImpl instance, final ContextManager contextManager) {
    instance.clear();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static FieldLabel CheckBoxFormGroupViewImpl_FieldLabel_fieldLabel(CheckBoxFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl::fieldLabel;
  }-*/;

  native static void CheckBoxFormGroupViewImpl_FieldLabel_fieldLabel(CheckBoxFormGroupViewImpl instance, FieldLabel value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl::fieldLabel = value;
  }-*/;

  native static Div CheckBoxFormGroupViewImpl_Div_helpBlock(CheckBoxFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl::helpBlock;
  }-*/;

  native static void CheckBoxFormGroupViewImpl_Div_helpBlock(CheckBoxFormGroupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl::helpBlock = value;
  }-*/;

  native static FormsElementWrapperWidgetUtil CheckBoxFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(CheckBoxFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl::wrapperWidgetUtil;
  }-*/;

  native static void CheckBoxFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(CheckBoxFormGroupViewImpl instance, FormsElementWrapperWidgetUtil value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroupViewImpl::wrapperWidgetUtil = value;
  }-*/;
}