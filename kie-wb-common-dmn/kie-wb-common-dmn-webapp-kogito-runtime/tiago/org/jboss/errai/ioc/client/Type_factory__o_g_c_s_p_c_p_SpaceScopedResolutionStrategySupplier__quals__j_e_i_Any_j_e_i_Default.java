package org.jboss.errai.ioc.client;

import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.client.preferences.SpaceScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.services.shared.preferences.config.WorkbenchPreferenceScopeResolutionStrategiesImpl;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

public class Type_factory__o_g_c_s_p_c_p_SpaceScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default extends Factory<SpaceScopedResolutionStrategySupplier> { private class Type_factory__o_g_c_s_p_c_p_SpaceScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends SpaceScopedResolutionStrategySupplier implements Proxy<SpaceScopedResolutionStrategySupplier> {
    private final ProxyHelper<SpaceScopedResolutionStrategySupplier> proxyHelper = new ProxyHelperImpl<SpaceScopedResolutionStrategySupplier>("Type_factory__o_g_c_s_p_c_p_SpaceScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final SpaceScopedResolutionStrategySupplier instance) {

    }

    public SpaceScopedResolutionStrategySupplier asBeanType() {
      return this;
    }

    public void setInstance(final SpaceScopedResolutionStrategySupplier instance) {
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

    @Override public void onWorkspaceProjectContextChangeEvent(WorkspaceProjectContextChangeEvent event) {
      if (proxyHelper != null) {
        final SpaceScopedResolutionStrategySupplier proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onWorkspaceProjectContextChangeEvent(event);
      } else {
        super.onWorkspaceProjectContextChangeEvent(event);
      }
    }

    @Override public PreferenceScopeResolutionStrategyInfo get() {
      if (proxyHelper != null) {
        final SpaceScopedResolutionStrategySupplier proxiedInstance = proxyHelper.getInstance(this);
        final PreferenceScopeResolutionStrategyInfo retVal = proxiedInstance.get();
        return retVal;
      } else {
        return super.get();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final SpaceScopedResolutionStrategySupplier proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_c_s_p_c_p_SpaceScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SpaceScopedResolutionStrategySupplier.class, "Type_factory__o_g_c_s_p_c_p_SpaceScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SpaceScopedResolutionStrategySupplier.class, Object.class, Supplier.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent", new AbstractCDIEventCallback<WorkspaceProjectContextChangeEvent>() {
      public void fireEvent(final WorkspaceProjectContextChangeEvent event) {
        final SpaceScopedResolutionStrategySupplier instance = Factory.maybeUnwrapProxy((SpaceScopedResolutionStrategySupplier) context.getInstance("Type_factory__o_g_c_s_p_c_p_SpaceScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default"));
        instance.onWorkspaceProjectContextChangeEvent(event);
      }
      public String toString() {
        return "Observer: org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent []";
      }
    });
  }

  public SpaceScopedResolutionStrategySupplier createInstance(final ContextManager contextManager) {
    final WorkbenchPreferenceScopeResolutionStrategies _scopeResolutionStrategies_0 = (WorkbenchPreferenceScopeResolutionStrategiesImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeResolutionStrategiesImpl__quals__j_e_i_Any_j_e_i_Default");
    final SpaceScopedResolutionStrategySupplier instance = new SpaceScopedResolutionStrategySupplier(_scopeResolutionStrategies_0);
    registerDependentScopedReference(instance, _scopeResolutionStrategies_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<SpaceScopedResolutionStrategySupplier> proxyImpl = new Type_factory__o_g_c_s_p_c_p_SpaceScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}