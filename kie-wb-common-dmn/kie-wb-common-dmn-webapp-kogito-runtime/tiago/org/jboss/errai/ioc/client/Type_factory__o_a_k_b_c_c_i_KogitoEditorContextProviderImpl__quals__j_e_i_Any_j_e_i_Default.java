package org.jboss.errai.ioc.client;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import org.appformer.client.context.Channel;
import org.appformer.client.context.EditorContextProvider;
import org.appformer.kogito.bridge.client.context.impl.KogitoEditorContextProviderImpl;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoEditorContextProviderImpl> { private class Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends KogitoEditorContextProviderImpl implements Proxy<KogitoEditorContextProviderImpl> {
    private final ProxyHelper<KogitoEditorContextProviderImpl> proxyHelper = new ProxyHelperImpl<KogitoEditorContextProviderImpl>("Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final KogitoEditorContextProviderImpl instance) {

    }

    public KogitoEditorContextProviderImpl asBeanType() {
      return this;
    }

    public void setInstance(final KogitoEditorContextProviderImpl instance) {
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

    @Override public Channel getChannel() {
      if (proxyHelper != null) {
        final KogitoEditorContextProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final Channel retVal = proxiedInstance.getChannel();
        return retVal;
      } else {
        return super.getChannel();
      }
    }

    @Override public Optional getOperatingSystem() {
      if (proxyHelper != null) {
        final KogitoEditorContextProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getOperatingSystem();
        return retVal;
      } else {
        return super.getOperatingSystem();
      }
    }

    @Override public boolean isReadOnly() {
      if (proxyHelper != null) {
        final KogitoEditorContextProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isReadOnly();
        return retVal;
      } else {
        return super.isReadOnly();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final KogitoEditorContextProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoEditorContextProviderImpl.class, "Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KogitoEditorContextProviderImpl.class, Object.class, EditorContextProvider.class });
  }

  public KogitoEditorContextProviderImpl createInstance(final ContextManager contextManager) {
    final KogitoEditorContextProviderImpl instance = new KogitoEditorContextProviderImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<KogitoEditorContextProviderImpl> proxyImpl = new Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}