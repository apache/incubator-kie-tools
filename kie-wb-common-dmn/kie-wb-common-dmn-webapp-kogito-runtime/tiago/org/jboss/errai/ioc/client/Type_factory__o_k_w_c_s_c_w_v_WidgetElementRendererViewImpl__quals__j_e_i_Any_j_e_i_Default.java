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
import org.kie.workbench.common.stunner.client.widgets.views.WidgetElementRendererViewImpl;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;

public class Type_factory__o_k_w_c_s_c_w_v_WidgetElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WidgetElementRendererViewImpl> { public interface o_k_w_c_s_c_w_v_WidgetElementRendererViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/views/WidgetElementRendererViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_c_w_v_WidgetElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WidgetElementRendererViewImpl.class, "Type_factory__o_k_w_c_s_c_w_v_WidgetElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WidgetElementRendererViewImpl.class, Object.class, WidgetElementRendererView.class, IsElement.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public WidgetElementRendererViewImpl createInstance(final ContextManager contextManager) {
    final WidgetElementRendererViewImpl instance = new WidgetElementRendererViewImpl();
    setIncompleteInstance(instance);
    final Div WidgetElementRendererViewImpl_content = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, WidgetElementRendererViewImpl_content);
    WidgetElementRendererViewImpl_Div_content(instance, WidgetElementRendererViewImpl_content);
    o_k_w_c_s_c_w_v_WidgetElementRendererViewImplTemplateResource templateForWidgetElementRendererViewImpl = GWT.create(o_k_w_c_s_c_w_v_WidgetElementRendererViewImplTemplateResource.class);
    Element parentElementForTemplateOfWidgetElementRendererViewImpl = TemplateUtil.getRootTemplateParentElement(templateForWidgetElementRendererViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/views/WidgetElementRendererViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/views/WidgetElementRendererViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfWidgetElementRendererViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfWidgetElementRendererViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.views.WidgetElementRendererViewImpl", "org/kie/workbench/common/stunner/client/widgets/views/WidgetElementRendererViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WidgetElementRendererViewImpl_Div_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WidgetElementRendererViewImpl_Div_content(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfWidgetElementRendererViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((WidgetElementRendererViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final WidgetElementRendererViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div WidgetElementRendererViewImpl_Div_content(WidgetElementRendererViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.views.WidgetElementRendererViewImpl::content;
  }-*/;

  native static void WidgetElementRendererViewImpl_Div_content(WidgetElementRendererViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.views.WidgetElementRendererViewImpl::content = value;
  }-*/;
}