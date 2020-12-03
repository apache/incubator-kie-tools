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
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainerView;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainerViewImpl;

public class Type_factory__o_k_w_c_s_f_c_w_c_FormsContainerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsContainerViewImpl> { public interface o_k_w_c_s_f_c_w_c_FormsContainerViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/forms/client/widgets/container/FormsContainerViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_f_c_w_c_FormsContainerViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormsContainerViewImpl.class, "Type_factory__o_k_w_c_s_f_c_w_c_FormsContainerViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormsContainerViewImpl.class, Object.class, FormsContainerView.class, IsElement.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public FormsContainerViewImpl createInstance(final ContextManager contextManager) {
    final FormsContainerViewImpl instance = new FormsContainerViewImpl();
    setIncompleteInstance(instance);
    final Div FormsContainerViewImpl_content = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FormsContainerViewImpl_content);
    FormsContainerViewImpl_Div_content(instance, FormsContainerViewImpl_content);
    o_k_w_c_s_f_c_w_c_FormsContainerViewImplTemplateResource templateForFormsContainerViewImpl = GWT.create(o_k_w_c_s_f_c_w_c_FormsContainerViewImplTemplateResource.class);
    Element parentElementForTemplateOfFormsContainerViewImpl = TemplateUtil.getRootTemplateParentElement(templateForFormsContainerViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/forms/client/widgets/container/FormsContainerViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/forms/client/widgets/container/FormsContainerViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFormsContainerViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFormsContainerViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainerViewImpl", "org/kie/workbench/common/stunner/forms/client/widgets/container/FormsContainerViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FormsContainerViewImpl_Div_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FormsContainerViewImpl_Div_content(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFormsContainerViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FormsContainerViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final FormsContainerViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div FormsContainerViewImpl_Div_content(FormsContainerViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainerViewImpl::content;
  }-*/;

  native static void FormsContainerViewImpl_Div_content(FormsContainerViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainerViewImpl::content = value;
  }-*/;
}