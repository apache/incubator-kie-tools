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
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidgetView;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidgetViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormPropertiesWidgetViewImpl> { public interface o_k_w_c_s_f_c_w_FormPropertiesWidgetViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/forms/client/widgets/FormPropertiesWidgetViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormPropertiesWidgetViewImpl.class, "Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormPropertiesWidgetViewImpl.class, Object.class, FormPropertiesWidgetView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public FormPropertiesWidgetViewImpl createInstance(final ContextManager contextManager) {
    final FormPropertiesWidgetViewImpl instance = new FormPropertiesWidgetViewImpl();
    setIncompleteInstance(instance);
    final Div FormPropertiesWidgetViewImpl_formContent = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FormPropertiesWidgetViewImpl_formContent);
    FormPropertiesWidgetViewImpl_Div_formContent(instance, FormPropertiesWidgetViewImpl_formContent);
    o_k_w_c_s_f_c_w_FormPropertiesWidgetViewImplTemplateResource templateForFormPropertiesWidgetViewImpl = GWT.create(o_k_w_c_s_f_c_w_FormPropertiesWidgetViewImplTemplateResource.class);
    Element parentElementForTemplateOfFormPropertiesWidgetViewImpl = TemplateUtil.getRootTemplateParentElement(templateForFormPropertiesWidgetViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/forms/client/widgets/FormPropertiesWidgetViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/forms/client/widgets/FormPropertiesWidgetViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFormPropertiesWidgetViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFormPropertiesWidgetViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("formContent", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidgetViewImpl", "org/kie/workbench/common/stunner/forms/client/widgets/FormPropertiesWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FormPropertiesWidgetViewImpl_Div_formContent(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "formContent");
    templateFieldsMap.put("formContent", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FormPropertiesWidgetViewImpl_Div_formContent(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFormPropertiesWidgetViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FormPropertiesWidgetViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final FormPropertiesWidgetViewImpl instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div FormPropertiesWidgetViewImpl_Div_formContent(FormPropertiesWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidgetViewImpl::formContent;
  }-*/;

  native static void FormPropertiesWidgetViewImpl_Div_formContent(FormPropertiesWidgetViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidgetViewImpl::formContent = value;
  }-*/;
}