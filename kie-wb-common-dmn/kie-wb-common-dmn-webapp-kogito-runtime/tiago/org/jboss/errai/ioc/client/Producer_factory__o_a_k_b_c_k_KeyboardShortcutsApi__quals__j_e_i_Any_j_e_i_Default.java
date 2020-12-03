package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts;
import org.appformer.kogito.bridge.client.keyboardshortcuts.KeyboardShortcutsApi;
import org.appformer.kogito.bridge.client.keyboardshortcuts.KeyboardShortcutsApi.Action;
import org.appformer.kogito.bridge.client.keyboardshortcuts.KeyboardShortcutsServiceProducer;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Producer_factory__o_a_k_b_c_k_KeyboardShortcutsApi__quals__j_e_i_Any_j_e_i_Default extends Factory<KeyboardShortcutsApi> { private class Producer_factory__o_a_k_b_c_k_KeyboardShortcutsApi__quals__j_e_i_Any_j_e_i_DefaultProxyImpl implements Proxy<KeyboardShortcutsApi>, KeyboardShortcutsApi {
    private final ProxyHelper<KeyboardShortcutsApi> proxyHelper = new ProxyHelperImpl<KeyboardShortcutsApi>("Producer_factory__o_a_k_b_c_k_KeyboardShortcutsApi__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final KeyboardShortcutsApi instance) {

    }

    public KeyboardShortcutsApi asBeanType() {
      return this;
    }

    public void setInstance(final KeyboardShortcutsApi instance) {
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

    @Override public int registerKeyPress(String combination, String label, Action onKeyDown, KeyboardShortcutsApiOpts opts) {
      if (proxyHelper != null) {
        final KeyboardShortcutsApi proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.registerKeyPress(combination, label, onKeyDown, opts);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int registerKeyDownThenUp(String combination, String label, Action onKeyDown, Action onKeyUp, KeyboardShortcutsApiOpts opts) {
      if (proxyHelper != null) {
        final KeyboardShortcutsApi proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.registerKeyDownThenUp(combination, label, onKeyDown, onKeyUp, opts);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void deregister(int id) {
      if (proxyHelper != null) {
        final KeyboardShortcutsApi proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.deregister(id);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final KeyboardShortcutsApi proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }
  }
  public Producer_factory__o_a_k_b_c_k_KeyboardShortcutsApi__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KeyboardShortcutsApi.class, "Producer_factory__o_a_k_b_c_k_KeyboardShortcutsApi__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KeyboardShortcutsApi.class });
  }

  public KeyboardShortcutsApi createInstance(final ContextManager contextManager) {
    KeyboardShortcutsServiceProducer producerInstance = contextManager.getInstance("Type_factory__o_a_k_b_c_k_KeyboardShortcutsServiceProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final KeyboardShortcutsApi instance = producerInstance.produce();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    registerDependentScopedReference(instance, producerInstance);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<KeyboardShortcutsApi> proxyImpl = new Producer_factory__o_a_k_b_c_k_KeyboardShortcutsApi__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}