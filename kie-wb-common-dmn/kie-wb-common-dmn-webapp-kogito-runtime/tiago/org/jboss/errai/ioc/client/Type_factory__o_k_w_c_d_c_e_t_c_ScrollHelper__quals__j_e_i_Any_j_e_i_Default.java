package org.jboss.errai.ioc.client;

import elemental2.dom.Element;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;

public class Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<ScrollHelper> { private class Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ScrollHelper implements Proxy<ScrollHelper> {
    private final ProxyHelper<ScrollHelper> proxyHelper = new ProxyHelperImpl<ScrollHelper>("Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ScrollHelper instance) {

    }

    public ScrollHelper asBeanType() {
      return this;
    }

    public void setInstance(final ScrollHelper instance) {
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

    @Override public void animatedScrollToBottom(Element element) {
      if (proxyHelper != null) {
        final ScrollHelper proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.animatedScrollToBottom(element);
      } else {
        super.animatedScrollToBottom(element);
      }
    }

    @Override public void scrollToBottom(Element element) {
      if (proxyHelper != null) {
        final ScrollHelper proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.scrollToBottom(element);
      } else {
        super.scrollToBottom(element);
      }
    }

    @Override public void scrollTo(Element target, Element container, int padding) {
      if (proxyHelper != null) {
        final ScrollHelper proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.scrollTo(target, container, padding);
      } else {
        super.scrollTo(target, container, padding);
      }
    }

    @Override public void scrollTo(Element target, Element container) {
      if (proxyHelper != null) {
        final ScrollHelper proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.scrollTo(target, container);
      } else {
        super.scrollTo(target, container);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ScrollHelper proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ScrollHelper.class, "Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ScrollHelper.class, Object.class });
  }

  public ScrollHelper createInstance(final ContextManager contextManager) {
    final ScrollHelper instance = new ScrollHelper();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ScrollHelper> proxyImpl = new Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}