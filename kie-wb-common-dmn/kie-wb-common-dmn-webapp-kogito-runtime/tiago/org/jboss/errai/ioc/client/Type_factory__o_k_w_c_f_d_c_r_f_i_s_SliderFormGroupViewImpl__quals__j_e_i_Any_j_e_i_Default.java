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
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SliderFormGroupViewImpl> { public interface o_k_w_c_f_d_c_r_f_i_s_SliderFormGroupViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/slider/SliderFormGroupViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SliderFormGroupViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SliderFormGroupViewImpl.class, Object.class, SliderFormGroupView.class, ValidableFormGroupView.class, FormGroupView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public SliderFormGroupViewImpl createInstance(final ContextManager contextManager) {
    final SliderFormGroupViewImpl instance = new SliderFormGroupViewImpl();
    setIncompleteInstance(instance);
    final Div SliderFormGroupViewImpl_helpBlock = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, SliderFormGroupViewImpl_helpBlock);
    SliderFormGroupViewImpl_Div_helpBlock(instance, SliderFormGroupViewImpl_helpBlock);
    final FormsElementWrapperWidgetUtilImpl SliderFormGroupViewImpl_wrapperWidgetUtil = (FormsElementWrapperWidgetUtilImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default");
    SliderFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(instance, SliderFormGroupViewImpl_wrapperWidgetUtil);
    final Div SliderFormGroupViewImpl_fieldContainer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, SliderFormGroupViewImpl_fieldContainer);
    SliderFormGroupViewImpl_Div_fieldContainer(instance, SliderFormGroupViewImpl_fieldContainer);
    final FieldLabel SliderFormGroupViewImpl_fieldLabel = (FieldLabel) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SliderFormGroupViewImpl_fieldLabel);
    SliderFormGroupViewImpl_FieldLabel_fieldLabel(instance, SliderFormGroupViewImpl_fieldLabel);
    o_k_w_c_f_d_c_r_f_i_s_SliderFormGroupViewImplTemplateResource templateForSliderFormGroupViewImpl = GWT.create(o_k_w_c_f_d_c_r_f_i_s_SliderFormGroupViewImplTemplateResource.class);
    Element parentElementForTemplateOfSliderFormGroupViewImpl = TemplateUtil.getRootTemplateParentElement(templateForSliderFormGroupViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/slider/SliderFormGroupViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/slider/SliderFormGroupViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSliderFormGroupViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSliderFormGroupViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("fieldLabel", new DataFieldMeta());
    dataFieldMetas.put("fieldContainer", new DataFieldMeta());
    dataFieldMetas.put("helpBlock", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/slider/SliderFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(SliderFormGroupViewImpl_FieldLabel_fieldLabel(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "fieldLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/slider/SliderFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SliderFormGroupViewImpl_Div_fieldContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "fieldContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/slider/SliderFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SliderFormGroupViewImpl_Div_helpBlock(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "helpBlock");
    templateFieldsMap.put("fieldLabel", ElementWrapperWidget.getWidget(SliderFormGroupViewImpl_FieldLabel_fieldLabel(instance).getElement()));
    templateFieldsMap.put("fieldContainer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SliderFormGroupViewImpl_Div_fieldContainer(instance))));
    templateFieldsMap.put("helpBlock", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SliderFormGroupViewImpl_Div_helpBlock(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSliderFormGroupViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SliderFormGroupViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final SliderFormGroupViewImpl instance, final ContextManager contextManager) {
    instance.clear();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div SliderFormGroupViewImpl_Div_fieldContainer(SliderFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl::fieldContainer;
  }-*/;

  native static void SliderFormGroupViewImpl_Div_fieldContainer(SliderFormGroupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl::fieldContainer = value;
  }-*/;

  native static Div SliderFormGroupViewImpl_Div_helpBlock(SliderFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl::helpBlock;
  }-*/;

  native static void SliderFormGroupViewImpl_Div_helpBlock(SliderFormGroupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl::helpBlock = value;
  }-*/;

  native static FieldLabel SliderFormGroupViewImpl_FieldLabel_fieldLabel(SliderFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl::fieldLabel;
  }-*/;

  native static void SliderFormGroupViewImpl_FieldLabel_fieldLabel(SliderFormGroupViewImpl instance, FieldLabel value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl::fieldLabel = value;
  }-*/;

  native static FormsElementWrapperWidgetUtil SliderFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(SliderFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl::wrapperWidgetUtil;
  }-*/;

  native static void SliderFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(SliderFormGroupViewImpl instance, FormsElementWrapperWidgetUtil value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl::wrapperWidgetUtil = value;
  }-*/;
}