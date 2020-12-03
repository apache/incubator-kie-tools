package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
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
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentView;
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentViewImpl;

public class Type_factory__o_u_e_w_c_c_d_n_NoItemsComponentViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NoItemsComponentViewImpl> { public interface o_u_e_w_c_c_d_n_NoItemsComponentViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/dropdown/noItems/NoItemsComponentViewImpl.html") public TextResource getContents();
  @Source("org/uberfire/ext/widgets/common/client/dropdown/noItems/NoItemsComponentViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_w_c_c_d_n_NoItemsComponentViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NoItemsComponentViewImpl.class, "Type_factory__o_u_e_w_c_c_d_n_NoItemsComponentViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NoItemsComponentViewImpl.class, Object.class, NoItemsComponentView.class, IsElement.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_w_c_c_d_n_NoItemsComponentViewImplTemplateResource) GWT.create(o_u_e_w_c_c_d_n_NoItemsComponentViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public NoItemsComponentViewImpl createInstance(final ContextManager contextManager) {
    final NoItemsComponentViewImpl instance = new NoItemsComponentViewImpl();
    setIncompleteInstance(instance);
    final Div NoItemsComponentViewImpl_container = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, NoItemsComponentViewImpl_container);
    NoItemsComponentViewImpl_Div_container(instance, NoItemsComponentViewImpl_container);
    final Span NoItemsComponentViewImpl_message = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, NoItemsComponentViewImpl_message);
    NoItemsComponentViewImpl_Span_message(instance, NoItemsComponentViewImpl_message);
    o_u_e_w_c_c_d_n_NoItemsComponentViewImplTemplateResource templateForNoItemsComponentViewImpl = GWT.create(o_u_e_w_c_c_d_n_NoItemsComponentViewImplTemplateResource.class);
    Element parentElementForTemplateOfNoItemsComponentViewImpl = TemplateUtil.getRootTemplateParentElement(templateForNoItemsComponentViewImpl.getContents().getText(), "org/uberfire/ext/widgets/common/client/dropdown/noItems/NoItemsComponentViewImpl.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/dropdown/noItems/NoItemsComponentViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfNoItemsComponentViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfNoItemsComponentViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("container", new DataFieldMeta());
    dataFieldMetas.put("message", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/noItems/NoItemsComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(NoItemsComponentViewImpl_Div_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/noItems/NoItemsComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(NoItemsComponentViewImpl_Span_message(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "message");
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(NoItemsComponentViewImpl_Div_container(instance))));
    templateFieldsMap.put("message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(NoItemsComponentViewImpl_Span_message(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfNoItemsComponentViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((NoItemsComponentViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final NoItemsComponentViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div NoItemsComponentViewImpl_Div_container(NoItemsComponentViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentViewImpl::container;
  }-*/;

  native static void NoItemsComponentViewImpl_Div_container(NoItemsComponentViewImpl instance, Div value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentViewImpl::container = value;
  }-*/;

  native static Span NoItemsComponentViewImpl_Span_message(NoItemsComponentViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentViewImpl::message;
  }-*/;

  native static void NoItemsComponentViewImpl_Span_message(NoItemsComponentViewImpl instance, Span value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentViewImpl::message = value;
  }-*/;
}