package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControlCleaner;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;

public class Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_Default extends Factory<ClipboardControlCleaner> { private class Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClipboardControlCleaner implements Proxy<ClipboardControlCleaner> {
    private final ProxyHelper<ClipboardControlCleaner> proxyHelper = new ProxyHelperImpl<ClipboardControlCleaner>("Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final ClipboardControlCleaner instance) {

    }

    public ClipboardControlCleaner asBeanType() {
      return this;
    }

    public void setInstance(final ClipboardControlCleaner instance) {
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

    @Override public void onPlaceGainFocusEvent(PlaceLostFocusEvent event) {
      if (proxyHelper != null) {
        final ClipboardControlCleaner proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPlaceGainFocusEvent(event);
      } else {
        super.onPlaceGainFocusEvent(event);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClipboardControlCleaner proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClipboardControlCleaner.class, "Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClipboardControlCleaner.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceLostFocusEvent", new AbstractCDIEventCallback<PlaceLostFocusEvent>() {
      public void fireEvent(final PlaceLostFocusEvent event) {
        final ClipboardControlCleaner instance = Factory.maybeUnwrapProxy((ClipboardControlCleaner) context.getInstance("Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_Default"));
        instance.onPlaceGainFocusEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceLostFocusEvent []";
      }
    });
  }

  public ClipboardControlCleaner createInstance(final ContextManager contextManager) {
    final ManagedInstance<ClipboardControl> _clipboardControls_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ClipboardControl.class }, new Annotation[] { });
    final ActivityBeansCache _activityBeansCache_1 = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    final ClipboardControlCleaner instance = new ClipboardControlCleaner(_clipboardControls_0, _activityBeansCache_1);
    registerDependentScopedReference(instance, _clipboardControls_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_c_c_c_c_ClipboardControlCleaner__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControlCleaner an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControlCleaner ([org.jboss.errai.ioc.client.api.ManagedInstance, org.uberfire.client.mvp.ActivityBeansCache])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClipboardControlCleaner> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}