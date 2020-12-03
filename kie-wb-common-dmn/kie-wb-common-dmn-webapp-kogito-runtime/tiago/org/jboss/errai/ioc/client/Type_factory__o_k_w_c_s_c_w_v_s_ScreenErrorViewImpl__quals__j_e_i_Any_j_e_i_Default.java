package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorView;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorViewImpl;

public class Type_factory__o_k_w_c_s_c_w_v_s_ScreenErrorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ScreenErrorViewImpl> { public interface o_k_w_c_s_c_w_v_s_ScreenErrorViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/views/session/ScreenErrorViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_c_w_v_s_ScreenErrorViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ScreenErrorViewImpl.class, "Type_factory__o_k_w_c_s_c_w_v_s_ScreenErrorViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ScreenErrorViewImpl.class, Object.class, ScreenErrorView.class, IsWidget.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public ScreenErrorViewImpl createInstance(final ContextManager contextManager) {
    final ScreenErrorViewImpl instance = new ScreenErrorViewImpl();
    setIncompleteInstance(instance);
    final Label ScreenErrorViewImpl_message = (Label) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_Label__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ScreenErrorViewImpl_message);
    ScreenErrorViewImpl_Label_message(instance, ScreenErrorViewImpl_message);
    o_k_w_c_s_c_w_v_s_ScreenErrorViewImplTemplateResource templateForScreenErrorViewImpl = GWT.create(o_k_w_c_s_c_w_v_s_ScreenErrorViewImplTemplateResource.class);
    Element parentElementForTemplateOfScreenErrorViewImpl = TemplateUtil.getRootTemplateParentElement(templateForScreenErrorViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/views/session/ScreenErrorViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/views/session/ScreenErrorViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfScreenErrorViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfScreenErrorViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("message", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorViewImpl", "org/kie/workbench/common/stunner/client/widgets/views/session/ScreenErrorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ScreenErrorViewImpl_Label_message(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "message");
    templateFieldsMap.put("message", ScreenErrorViewImpl_Label_message(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfScreenErrorViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ScreenErrorViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ScreenErrorViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Label ScreenErrorViewImpl_Label_message(ScreenErrorViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorViewImpl::message;
  }-*/;

  native static void ScreenErrorViewImpl_Label_message(ScreenErrorViewImpl instance, Label value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorViewImpl::message = value;
  }-*/;
}