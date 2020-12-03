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
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayerView;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayerViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormDisplayerViewImpl> { public interface o_k_w_c_s_f_c_w_c_d_FormDisplayerViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/forms/client/widgets/container/displayer/FormDisplayerViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormDisplayerViewImpl.class, "Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormDisplayerViewImpl.class, Object.class, FormDisplayerView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public FormDisplayerViewImpl createInstance(final ContextManager contextManager) {
    final FormDisplayerViewImpl instance = new FormDisplayerViewImpl();
    setIncompleteInstance(instance);
    final Div FormDisplayerViewImpl_content = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FormDisplayerViewImpl_content);
    FormDisplayerViewImpl_Div_content(instance, FormDisplayerViewImpl_content);
    o_k_w_c_s_f_c_w_c_d_FormDisplayerViewImplTemplateResource templateForFormDisplayerViewImpl = GWT.create(o_k_w_c_s_f_c_w_c_d_FormDisplayerViewImplTemplateResource.class);
    Element parentElementForTemplateOfFormDisplayerViewImpl = TemplateUtil.getRootTemplateParentElement(templateForFormDisplayerViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/forms/client/widgets/container/displayer/FormDisplayerViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/forms/client/widgets/container/displayer/FormDisplayerViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFormDisplayerViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFormDisplayerViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayerViewImpl", "org/kie/workbench/common/stunner/forms/client/widgets/container/displayer/FormDisplayerViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FormDisplayerViewImpl_Div_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FormDisplayerViewImpl_Div_content(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFormDisplayerViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FormDisplayerViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final FormDisplayerViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div FormDisplayerViewImpl_Div_content(FormDisplayerViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayerViewImpl::content;
  }-*/;

  native static void FormDisplayerViewImpl_Div_content(FormDisplayerViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayerViewImpl::content = value;
  }-*/;
}