package org.jboss.errai.ioc.client;

import elemental2.promise.Promise;
import javax.enterprise.context.ApplicationScoped;
import org.appformer.kogito.bridge.client.i18n.I18nApi;
import org.appformer.kogito.bridge.client.i18n.I18nServiceProducer;
import org.appformer.kogito.bridge.client.i18n.LocaleChangeCallback;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Producer_factory__o_a_k_b_c_i_I18nApi__quals__j_e_i_Any_j_e_i_Default extends Factory<I18nApi> { private class Producer_factory__o_a_k_b_c_i_I18nApi__quals__j_e_i_Any_j_e_i_DefaultProxyImpl implements Proxy<I18nApi>, I18nApi {
    private final ProxyHelper<I18nApi> proxyHelper = new ProxyHelperImpl<I18nApi>("Producer_factory__o_a_k_b_c_i_I18nApi__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final I18nApi instance) {

    }

    public I18nApi asBeanType() {
      return this;
    }

    public void setInstance(final I18nApi instance) {
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

    @Override public void onLocaleChange(LocaleChangeCallback callback) {
      if (proxyHelper != null) {
        final I18nApi proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onLocaleChange(callback);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Promise getLocale() {
      if (proxyHelper != null) {
        final I18nApi proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getLocale();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final I18nApi proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }
  }
  public Producer_factory__o_a_k_b_c_i_I18nApi__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(I18nApi.class, "Producer_factory__o_a_k_b_c_i_I18nApi__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { I18nApi.class });
  }

  public I18nApi createInstance(final ContextManager contextManager) {
    I18nServiceProducer producerInstance = contextManager.getInstance("Type_factory__o_a_k_b_c_i_I18nServiceProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final I18nApi instance = producerInstance.produce();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    registerDependentScopedReference(instance, producerInstance);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<I18nApi> proxyImpl = new Producer_factory__o_a_k_b_c_i_I18nApi__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}