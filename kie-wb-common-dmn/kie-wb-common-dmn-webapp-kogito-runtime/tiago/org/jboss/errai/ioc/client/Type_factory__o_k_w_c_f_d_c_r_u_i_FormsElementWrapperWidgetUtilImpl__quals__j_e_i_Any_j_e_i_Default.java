package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;

public class Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsElementWrapperWidgetUtilImpl> { private class Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends FormsElementWrapperWidgetUtilImpl implements Proxy<FormsElementWrapperWidgetUtilImpl> {
    private final ProxyHelper<FormsElementWrapperWidgetUtilImpl> proxyHelper = new ProxyHelperImpl<FormsElementWrapperWidgetUtilImpl>("Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final FormsElementWrapperWidgetUtilImpl instance) {

    }

    public FormsElementWrapperWidgetUtilImpl asBeanType() {
      return this;
    }

    public void setInstance(final FormsElementWrapperWidgetUtilImpl instance) {
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

    @Override public Widget getWidget(Object source, HTMLElement element) {
      if (proxyHelper != null) {
        final FormsElementWrapperWidgetUtilImpl proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.getWidget(source, element);
        return retVal;
      } else {
        return super.getWidget(source, element);
      }
    }

    @Override public Widget getWidget(Object source, elemental2.dom.HTMLElement element) {
      if (proxyHelper != null) {
        final FormsElementWrapperWidgetUtilImpl proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.getWidget(source, element);
        return retVal;
      } else {
        return super.getWidget(source, element);
      }
    }

    @Override public void clear(Object source) {
      if (proxyHelper != null) {
        final FormsElementWrapperWidgetUtilImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear(source);
      } else {
        super.clear(source);
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final FormsElementWrapperWidgetUtilImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        super.clear();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final FormsElementWrapperWidgetUtilImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormsElementWrapperWidgetUtilImpl.class, "Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormsElementWrapperWidgetUtilImpl.class, Object.class, FormsElementWrapperWidgetUtil.class });
  }

  public FormsElementWrapperWidgetUtilImpl createInstance(final ContextManager contextManager) {
    final FormsElementWrapperWidgetUtilImpl instance = new FormsElementWrapperWidgetUtilImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FormsElementWrapperWidgetUtilImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final FormsElementWrapperWidgetUtilImpl instance, final ContextManager contextManager) {
    instance.clear();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<FormsElementWrapperWidgetUtilImpl> proxyImpl = new Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}