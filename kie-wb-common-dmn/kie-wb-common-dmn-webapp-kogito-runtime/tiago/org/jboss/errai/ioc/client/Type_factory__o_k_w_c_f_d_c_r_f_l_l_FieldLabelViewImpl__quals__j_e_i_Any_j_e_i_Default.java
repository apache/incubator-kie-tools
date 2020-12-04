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
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelp;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabelViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldLabelViewImpl> { public interface o_k_w_c_f_d_c_r_f_l_l_FieldLabelViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/label/FieldLabelViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabelViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldLabelViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabelViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldLabelViewImpl.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, FieldLabelView.class, UberElement.class, HasPresenter.class });
  }

  public FieldLabelViewImpl createInstance(final ContextManager contextManager) {
    final FieldLabelViewImpl instance = new FieldLabelViewImpl();
    setIncompleteInstance(instance);
    final Span FieldLabelViewImpl_labelText = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FieldLabelViewImpl_labelText);
    FieldLabelViewImpl_Span_labelText(instance, FieldLabelViewImpl_labelText);
    final FieldRequired FieldLabelViewImpl_fieldRequired = (FieldRequired) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequired__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, FieldLabelViewImpl_fieldRequired);
    FieldLabelViewImpl_FieldRequired_fieldRequired(instance, FieldLabelViewImpl_fieldRequired);
    final FieldHelp FieldLabelViewImpl_fieldHelp = (FieldHelp) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelp__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, FieldLabelViewImpl_fieldHelp);
    FieldLabelViewImpl_FieldHelp_fieldHelp(instance, FieldLabelViewImpl_fieldHelp);
    o_k_w_c_f_d_c_r_f_l_l_FieldLabelViewImplTemplateResource templateForFieldLabelViewImpl = GWT.create(o_k_w_c_f_d_c_r_f_l_l_FieldLabelViewImplTemplateResource.class);
    Element parentElementForTemplateOfFieldLabelViewImpl = TemplateUtil.getRootTemplateParentElement(templateForFieldLabelViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/label/FieldLabelViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/label/FieldLabelViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldLabelViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldLabelViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("labelText", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/label/FieldLabelViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldLabelViewImpl_Span_labelText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "labelText");
    templateFieldsMap.put("labelText", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldLabelViewImpl_Span_labelText(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldLabelViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FieldLabelViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final FieldLabelViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final FieldLabelViewImpl instance) {
    instance.init();
  }

  native static Span FieldLabelViewImpl_Span_labelText(FieldLabelViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl::labelText;
  }-*/;

  native static void FieldLabelViewImpl_Span_labelText(FieldLabelViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl::labelText = value;
  }-*/;

  native static FieldRequired FieldLabelViewImpl_FieldRequired_fieldRequired(FieldLabelViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl::fieldRequired;
  }-*/;

  native static void FieldLabelViewImpl_FieldRequired_fieldRequired(FieldLabelViewImpl instance, FieldRequired value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl::fieldRequired = value;
  }-*/;

  native static FieldHelp FieldLabelViewImpl_FieldHelp_fieldHelp(FieldLabelViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl::fieldHelp;
  }-*/;

  native static void FieldLabelViewImpl_FieldHelp_fieldHelp(FieldLabelViewImpl instance, FieldHelp value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl::fieldHelp = value;
  }-*/;
}