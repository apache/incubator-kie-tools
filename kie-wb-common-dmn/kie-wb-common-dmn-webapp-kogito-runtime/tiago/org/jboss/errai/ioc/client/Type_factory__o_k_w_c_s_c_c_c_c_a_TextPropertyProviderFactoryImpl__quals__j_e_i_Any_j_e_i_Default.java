package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Element;

public class Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<TextPropertyProviderFactoryImpl> { private class Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends TextPropertyProviderFactoryImpl implements Proxy<TextPropertyProviderFactoryImpl> {
    private final ProxyHelper<TextPropertyProviderFactoryImpl> proxyHelper = new ProxyHelperImpl<TextPropertyProviderFactoryImpl>("Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final TextPropertyProviderFactoryImpl instance) {

    }

    public TextPropertyProviderFactoryImpl asBeanType() {
      return this;
    }

    public void setInstance(final TextPropertyProviderFactoryImpl instance) {
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

    @Override public TextPropertyProvider getProvider(Element element) {
      if (proxyHelper != null) {
        final TextPropertyProviderFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final TextPropertyProvider retVal = proxiedInstance.getProvider(element);
        return retVal;
      } else {
        return super.getProvider(element);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final TextPropertyProviderFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TextPropertyProviderFactoryImpl.class, "Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TextPropertyProviderFactoryImpl.class, Object.class, TextPropertyProviderFactory.class });
  }

  public TextPropertyProviderFactoryImpl createInstance(final ContextManager contextManager) {
    final ManagedInstance<TextPropertyProvider> _providers_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { TextPropertyProvider.class }, new Annotation[] { });
    final TextPropertyProviderFactoryImpl instance = new TextPropertyProviderFactoryImpl(_providers_0);
    registerDependentScopedReference(instance, _providers_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<TextPropertyProviderFactoryImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}