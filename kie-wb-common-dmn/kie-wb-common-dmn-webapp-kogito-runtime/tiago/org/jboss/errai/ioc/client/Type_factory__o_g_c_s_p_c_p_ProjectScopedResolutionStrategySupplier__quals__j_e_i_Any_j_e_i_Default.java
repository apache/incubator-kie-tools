package org.jboss.errai.ioc.client;

import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
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

public class Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default extends Factory<ProjectScopedResolutionStrategySupplier> { private class Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ProjectScopedResolutionStrategySupplier implements Proxy<ProjectScopedResolutionStrategySupplier> {
    private final ProxyHelper<ProjectScopedResolutionStrategySupplier> proxyHelper = new ProxyHelperImpl<ProjectScopedResolutionStrategySupplier>("Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ProjectScopedResolutionStrategySupplier instance) {

    }

    public ProjectScopedResolutionStrategySupplier asBeanType() {
      return this;
    }

    public void setInstance(final ProjectScopedResolutionStrategySupplier instance) {
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

    @Override public void selectedProjectChanged(WorkspaceProjectContextChangeEvent event) {
      if (proxyHelper != null) {
        final ProjectScopedResolutionStrategySupplier proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.selectedProjectChanged(event);
      } else {
        super.selectedProjectChanged(event);
      }
    }

    @Override public PreferenceScopeResolutionStrategyInfo get() {
      if (proxyHelper != null) {
        final ProjectScopedResolutionStrategySupplier proxiedInstance = proxyHelper.getInstance(this);
        final PreferenceScopeResolutionStrategyInfo retVal = proxiedInstance.get();
        return retVal;
      } else {
        return super.get();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ProjectScopedResolutionStrategySupplier proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ProjectScopedResolutionStrategySupplier.class, "Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ProjectScopedResolutionStrategySupplier.class, Object.class, Supplier.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent", new AbstractCDIEventCallback<WorkspaceProjectContextChangeEvent>() {
      public void fireEvent(final WorkspaceProjectContextChangeEvent event) {
        final ProjectScopedResolutionStrategySupplier instance = Factory.maybeUnwrapProxy((ProjectScopedResolutionStrategySupplier) context.getInstance("Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default"));
        instance.selectedProjectChanged(event);
      }
      public String toString() {
        return "Observer: org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent []";
      }
    });
  }

  public ProjectScopedResolutionStrategySupplier createInstance(final ContextManager contextManager) {
    final WorkbenchPreferenceScopeResolutionStrategies _scopeResolutionStrategies_0 = (WorkbenchPreferenceScopeResolutionStrategiesImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeResolutionStrategiesImpl__quals__j_e_i_Any_j_e_i_Default");
    final ProjectScopedResolutionStrategySupplier instance = new ProjectScopedResolutionStrategySupplier(_scopeResolutionStrategies_0);
    registerDependentScopedReference(instance, _scopeResolutionStrategies_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ProjectScopedResolutionStrategySupplier> proxyImpl = new Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}