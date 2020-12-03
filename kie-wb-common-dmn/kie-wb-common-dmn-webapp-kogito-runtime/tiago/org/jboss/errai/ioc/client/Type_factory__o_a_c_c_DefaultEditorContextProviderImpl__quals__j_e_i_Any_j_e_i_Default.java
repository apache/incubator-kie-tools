package org.jboss.errai.ioc.client;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import org.appformer.client.context.Channel;
import org.appformer.client.context.DefaultEditorContextProviderImpl;
import org.appformer.client.context.EditorContextProvider;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Type_factory__o_a_c_c_DefaultEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultEditorContextProviderImpl> { private class Type_factory__o_a_c_c_DefaultEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultEditorContextProviderImpl implements Proxy<DefaultEditorContextProviderImpl> {
    private final ProxyHelper<DefaultEditorContextProviderImpl> proxyHelper = new ProxyHelperImpl<DefaultEditorContextProviderImpl>("Type_factory__o_a_c_c_DefaultEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefaultEditorContextProviderImpl instance) {

    }

    public DefaultEditorContextProviderImpl asBeanType() {
      return this;
    }

    public void setInstance(final DefaultEditorContextProviderImpl instance) {
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
        final DefaultEditorContextProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final Channel retVal = proxiedInstance.getChannel();
        return retVal;
      } else {
        return super.getChannel();
      }
    }

    @Override public Optional getOperatingSystem() {
      if (proxyHelper != null) {
        final DefaultEditorContextProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getOperatingSystem();
        return retVal;
      } else {
        return super.getOperatingSystem();
      }
    }

    @Override public boolean isReadOnly() {
      if (proxyHelper != null) {
        final DefaultEditorContextProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isReadOnly();
        return retVal;
      } else {
        return super.isReadOnly();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultEditorContextProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_a_c_c_DefaultEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultEditorContextProviderImpl.class, "Type_factory__o_a_c_c_DefaultEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultEditorContextProviderImpl.class, Object.class, EditorContextProvider.class });
  }

  public DefaultEditorContextProviderImpl createInstance(final ContextManager contextManager) {
    final DefaultEditorContextProviderImpl instance = new DefaultEditorContextProviderImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultEditorContextProviderImpl> proxyImpl = new Type_factory__o_a_c_c_DefaultEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}