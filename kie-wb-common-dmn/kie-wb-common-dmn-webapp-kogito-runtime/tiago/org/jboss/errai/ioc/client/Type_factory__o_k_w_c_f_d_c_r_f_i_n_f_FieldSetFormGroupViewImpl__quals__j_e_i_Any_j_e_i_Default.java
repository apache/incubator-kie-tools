package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
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
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelp;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldSetFormGroupViewImpl> { public interface o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroupViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/fieldSet/FieldSetFormGroupViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldSetFormGroupViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldSetFormGroupViewImpl.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, FieldSetFormGroupView.class, FormGroupView.class });
  }

  public FieldSetFormGroupViewImpl createInstance(final ContextManager contextManager) {
    final FieldSetFormGroupViewImpl instance = new FieldSetFormGroupViewImpl();
    setIncompleteInstance(instance);
    final Span FieldSetFormGroupViewImpl_legendText = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FieldSetFormGroupViewImpl_legendText);
    FieldSetFormGroupViewImpl_Span_legendText(instance, FieldSetFormGroupViewImpl_legendText);
    final Div FieldSetFormGroupViewImpl_formGroup = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FieldSetFormGroupViewImpl_formGroup);
    FieldSetFormGroupViewImpl_Div_formGroup(instance, FieldSetFormGroupViewImpl_formGroup);
    final FieldRequired FieldSetFormGroupViewImpl_fieldRequired = (FieldRequired) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequired__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, FieldSetFormGroupViewImpl_fieldRequired);
    FieldSetFormGroupViewImpl_FieldRequired_fieldRequired(instance, FieldSetFormGroupViewImpl_fieldRequired);
    final Div FieldSetFormGroupViewImpl_helpBlock = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FieldSetFormGroupViewImpl_helpBlock);
    FieldSetFormGroupViewImpl_Div_helpBlock(instance, FieldSetFormGroupViewImpl_helpBlock);
    final HTMLElement FieldSetFormGroupViewImpl_legend = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=legend)";
        }
        public String value() {
          return "legend";
        }
    } });
    registerDependentScopedReference(instance, FieldSetFormGroupViewImpl_legend);
    FieldSetFormGroupViewImpl_HTMLElement_legend(instance, FieldSetFormGroupViewImpl_legend);
    final FormsElementWrapperWidgetUtilImpl FieldSetFormGroupViewImpl_wrapperWidgetUtil = (FormsElementWrapperWidgetUtilImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default");
    FieldSetFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(instance, FieldSetFormGroupViewImpl_wrapperWidgetUtil);
    final FieldHelp FieldSetFormGroupViewImpl_fieldHelp = (FieldHelp) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelp__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, FieldSetFormGroupViewImpl_fieldHelp);
    FieldSetFormGroupViewImpl_FieldHelp_fieldHelp(instance, FieldSetFormGroupViewImpl_fieldHelp);
    o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroupViewImplTemplateResource templateForFieldSetFormGroupViewImpl = GWT.create(o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroupViewImplTemplateResource.class);
    Element parentElementForTemplateOfFieldSetFormGroupViewImpl = TemplateUtil.getRootTemplateParentElement(templateForFieldSetFormGroupViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/fieldSet/FieldSetFormGroupViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/fieldSet/FieldSetFormGroupViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldSetFormGroupViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldSetFormGroupViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("legend", new DataFieldMeta());
    dataFieldMetas.put("legendText", new DataFieldMeta());
    dataFieldMetas.put("fieldContainer", new DataFieldMeta());
    dataFieldMetas.put("formGroup", new DataFieldMeta());
    dataFieldMetas.put("helpBlock", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/fieldSet/FieldSetFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldSetFormGroupViewImpl_HTMLElement_legend(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "legend");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/fieldSet/FieldSetFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldSetFormGroupViewImpl_Span_legendText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "legendText");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/fieldSet/FieldSetFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return FieldSetFormGroupViewImpl_SimplePanel_fieldContainer(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "fieldContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/fieldSet/FieldSetFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldSetFormGroupViewImpl_Div_formGroup(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "formGroup");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/fieldSet/FieldSetFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldSetFormGroupViewImpl_Div_helpBlock(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "helpBlock");
    templateFieldsMap.put("legend", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldSetFormGroupViewImpl_HTMLElement_legend(instance))));
    templateFieldsMap.put("legendText", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldSetFormGroupViewImpl_Span_legendText(instance))));
    templateFieldsMap.put("fieldContainer", FieldSetFormGroupViewImpl_SimplePanel_fieldContainer(instance).asWidget());
    templateFieldsMap.put("formGroup", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldSetFormGroupViewImpl_Div_formGroup(instance))));
    templateFieldsMap.put("helpBlock", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldSetFormGroupViewImpl_Div_helpBlock(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldSetFormGroupViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FieldSetFormGroupViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final FieldSetFormGroupViewImpl instance, final ContextManager contextManager) {
    instance.clear();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static FormsElementWrapperWidgetUtil FieldSetFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(FieldSetFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::wrapperWidgetUtil;
  }-*/;

  native static void FieldSetFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(FieldSetFormGroupViewImpl instance, FormsElementWrapperWidgetUtil value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::wrapperWidgetUtil = value;
  }-*/;

  native static Span FieldSetFormGroupViewImpl_Span_legendText(FieldSetFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::legendText;
  }-*/;

  native static void FieldSetFormGroupViewImpl_Span_legendText(FieldSetFormGroupViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::legendText = value;
  }-*/;

  native static FieldRequired FieldSetFormGroupViewImpl_FieldRequired_fieldRequired(FieldSetFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::fieldRequired;
  }-*/;

  native static void FieldSetFormGroupViewImpl_FieldRequired_fieldRequired(FieldSetFormGroupViewImpl instance, FieldRequired value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::fieldRequired = value;
  }-*/;

  native static FieldHelp FieldSetFormGroupViewImpl_FieldHelp_fieldHelp(FieldSetFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::fieldHelp;
  }-*/;

  native static void FieldSetFormGroupViewImpl_FieldHelp_fieldHelp(FieldSetFormGroupViewImpl instance, FieldHelp value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::fieldHelp = value;
  }-*/;

  native static Div FieldSetFormGroupViewImpl_Div_helpBlock(FieldSetFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::helpBlock;
  }-*/;

  native static void FieldSetFormGroupViewImpl_Div_helpBlock(FieldSetFormGroupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::helpBlock = value;
  }-*/;

  native static HTMLElement FieldSetFormGroupViewImpl_HTMLElement_legend(FieldSetFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::legend;
  }-*/;

  native static void FieldSetFormGroupViewImpl_HTMLElement_legend(FieldSetFormGroupViewImpl instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::legend = value;
  }-*/;

  native static Div FieldSetFormGroupViewImpl_Div_formGroup(FieldSetFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::formGroup;
  }-*/;

  native static void FieldSetFormGroupViewImpl_Div_formGroup(FieldSetFormGroupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::formGroup = value;
  }-*/;

  native static SimplePanel FieldSetFormGroupViewImpl_SimplePanel_fieldContainer(FieldSetFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::fieldContainer;
  }-*/;

  native static void FieldSetFormGroupViewImpl_SimplePanel_fieldContainer(FieldSetFormGroupViewImpl instance, SimplePanel value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl::fieldContainer = value;
  }-*/;
}