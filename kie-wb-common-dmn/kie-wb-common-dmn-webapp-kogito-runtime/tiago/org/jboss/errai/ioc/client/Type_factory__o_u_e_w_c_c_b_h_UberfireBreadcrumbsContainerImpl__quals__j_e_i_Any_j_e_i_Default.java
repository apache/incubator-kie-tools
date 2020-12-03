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
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.workbench.Header;
import org.uberfire.client.workbench.Orderable;
import org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainer;
import org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainerImpl;

public class Type_factory__o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireBreadcrumbsContainerImpl> { public interface o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/breadcrumbs/header/UberfireBreadcrumbsContainerImpl.html") public TextResource getContents();
  @Source("org/uberfire/ext/widgets/common/client/breadcrumbs/header/UberfireBreadcrumbsContainerImpl.css") @NotStrict public CssResource getStyle(); }
  private class Type_factory__o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends UberfireBreadcrumbsContainerImpl implements Proxy<UberfireBreadcrumbsContainerImpl> {
    private final ProxyHelper<UberfireBreadcrumbsContainerImpl> proxyHelper = new ProxyHelperImpl<UberfireBreadcrumbsContainerImpl>("Type_factory__o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final UberfireBreadcrumbsContainerImpl instance) {

    }

    public UberfireBreadcrumbsContainerImpl asBeanType() {
      return this;
    }

    public void setInstance(final UberfireBreadcrumbsContainerImpl instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void init(HTMLElement child) {
      if (proxyHelper != null) {
        final UberfireBreadcrumbsContainerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(child);
      } else {
        super.init(child);
      }
    }

    @Override public String getId() {
      if (proxyHelper != null) {
        final UberfireBreadcrumbsContainerImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getId();
        return retVal;
      } else {
        return super.getId();
      }
    }

    @Override public int getOrder() {
      if (proxyHelper != null) {
        final UberfireBreadcrumbsContainerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getOrder();
        return retVal;
      } else {
        return super.getOrder();
      }
    }

    @Override public void enable() {
      if (proxyHelper != null) {
        final UberfireBreadcrumbsContainerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.enable();
      } else {
        super.enable();
      }
    }

    @Override public void disable() {
      if (proxyHelper != null) {
        final UberfireBreadcrumbsContainerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.disable();
      } else {
        super.disable();
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final UberfireBreadcrumbsContainerImpl proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final UberfireBreadcrumbsContainerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UberfireBreadcrumbsContainerImpl.class, "Type_factory__o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UberfireBreadcrumbsContainerImpl.class, Object.class, Header.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, Orderable.class, UberfireBreadcrumbsContainer.class });
  }

  public void init(final Context context) {
    ((o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImplTemplateResource) GWT.create(o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public UberfireBreadcrumbsContainerImpl createInstance(final ContextManager contextManager) {
    final UberfireBreadcrumbsContainerImpl instance = new UberfireBreadcrumbsContainerImpl();
    setIncompleteInstance(instance);
    final Div UberfireBreadcrumbsContainerImpl_breadcrumbsContainer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, UberfireBreadcrumbsContainerImpl_breadcrumbsContainer);
    UberfireBreadcrumbsContainerImpl_Div_breadcrumbsContainer(instance, UberfireBreadcrumbsContainerImpl_breadcrumbsContainer);
    o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImplTemplateResource templateForUberfireBreadcrumbsContainerImpl = GWT.create(o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImplTemplateResource.class);
    Element parentElementForTemplateOfUberfireBreadcrumbsContainerImpl = TemplateUtil.getRootTemplateParentElement(templateForUberfireBreadcrumbsContainerImpl.getContents().getText(), "org/uberfire/ext/widgets/common/client/breadcrumbs/header/UberfireBreadcrumbsContainerImpl.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/breadcrumbs/header/UberfireBreadcrumbsContainerImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfUberfireBreadcrumbsContainerImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfUberfireBreadcrumbsContainerImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("breadcrumbs-container", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainerImpl", "org/uberfire/ext/widgets/common/client/breadcrumbs/header/UberfireBreadcrumbsContainerImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(UberfireBreadcrumbsContainerImpl_Div_breadcrumbsContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "breadcrumbs-container");
    templateFieldsMap.put("breadcrumbs-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(UberfireBreadcrumbsContainerImpl_Div_breadcrumbsContainer(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfUberfireBreadcrumbsContainerImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((UberfireBreadcrumbsContainerImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final UberfireBreadcrumbsContainerImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<UberfireBreadcrumbsContainerImpl> proxyImpl = new Type_factory__o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Div UberfireBreadcrumbsContainerImpl_Div_breadcrumbsContainer(UberfireBreadcrumbsContainerImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainerImpl::breadcrumbsContainer;
  }-*/;

  native static void UberfireBreadcrumbsContainerImpl_Div_breadcrumbsContainer(UberfireBreadcrumbsContainerImpl instance, Div value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainerImpl::breadcrumbsContainer = value;
  }-*/;
}