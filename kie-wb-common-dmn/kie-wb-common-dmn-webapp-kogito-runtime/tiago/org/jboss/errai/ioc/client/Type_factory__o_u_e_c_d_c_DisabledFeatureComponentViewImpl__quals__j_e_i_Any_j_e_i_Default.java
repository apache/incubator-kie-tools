package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentView;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentViewImpl;

public class Type_factory__o_u_e_c_d_c_DisabledFeatureComponentViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DisabledFeatureComponentViewImpl> { public interface o_u_e_c_d_c_DisabledFeatureComponentViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/experimental/client/disabled/component/DisabledFeatureComponentViewImpl.html") public TextResource getContents();
  @Source("org/uberfire/experimental/client/disabled/component/DisabledFeatureComponentViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_c_d_c_DisabledFeatureComponentViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DisabledFeatureComponentViewImpl.class, "Type_factory__o_u_e_c_d_c_DisabledFeatureComponentViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DisabledFeatureComponentViewImpl.class, Object.class, DisabledFeatureComponentView.class, IsElement.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_c_d_c_DisabledFeatureComponentViewImplTemplateResource) GWT.create(o_u_e_c_d_c_DisabledFeatureComponentViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public DisabledFeatureComponentViewImpl createInstance(final ContextManager contextManager) {
    final DisabledFeatureComponentViewImpl instance = new DisabledFeatureComponentViewImpl();
    setIncompleteInstance(instance);
    final Elemental2DomUtil DisabledFeatureComponentViewImpl_util = (Elemental2DomUtil) contextManager.getInstance("Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DisabledFeatureComponentViewImpl_util);
    DisabledFeatureComponentViewImpl_Elemental2DomUtil_util(instance, DisabledFeatureComponentViewImpl_util);
    final HTMLDivElement DisabledFeatureComponentViewImpl_content = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DisabledFeatureComponentViewImpl_content);
    DisabledFeatureComponentViewImpl_HTMLDivElement_content(instance, DisabledFeatureComponentViewImpl_content);
    o_u_e_c_d_c_DisabledFeatureComponentViewImplTemplateResource templateForDisabledFeatureComponentViewImpl = GWT.create(o_u_e_c_d_c_DisabledFeatureComponentViewImplTemplateResource.class);
    Element parentElementForTemplateOfDisabledFeatureComponentViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDisabledFeatureComponentViewImpl.getContents().getText(), "org/uberfire/experimental/client/disabled/component/DisabledFeatureComponentViewImpl.html", "");
    TemplateUtil.translateTemplate("org/uberfire/experimental/client/disabled/component/DisabledFeatureComponentViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDisabledFeatureComponentViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDisabledFeatureComponentViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentViewImpl", "org/uberfire/experimental/client/disabled/component/DisabledFeatureComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DisabledFeatureComponentViewImpl_HTMLDivElement_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DisabledFeatureComponentViewImpl_HTMLDivElement_content(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDisabledFeatureComponentViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DisabledFeatureComponentViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DisabledFeatureComponentViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement DisabledFeatureComponentViewImpl_HTMLDivElement_content(DisabledFeatureComponentViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentViewImpl::content;
  }-*/;

  native static void DisabledFeatureComponentViewImpl_HTMLDivElement_content(DisabledFeatureComponentViewImpl instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentViewImpl::content = value;
  }-*/;

  native static Elemental2DomUtil DisabledFeatureComponentViewImpl_Elemental2DomUtil_util(DisabledFeatureComponentViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentViewImpl::util;
  }-*/;

  native static void DisabledFeatureComponentViewImpl_Elemental2DomUtil_util(DisabledFeatureComponentViewImpl instance, Elemental2DomUtil value) /*-{
    instance.@org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentViewImpl::util = value;
  }-*/;
}