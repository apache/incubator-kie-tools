package org.jboss.errai.ioc.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeHandler;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;

public class Type_factory__o_k_w_c_w_c_h_NewResourcesMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<NewResourcesMenu> { private class Type_factory__o_k_w_c_w_c_h_NewResourcesMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends NewResourcesMenu implements Proxy<NewResourcesMenu> {
    private final ProxyHelper<NewResourcesMenu> proxyHelper = new ProxyHelperImpl<NewResourcesMenu>("Type_factory__o_k_w_c_w_c_h_NewResourcesMenu__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final NewResourcesMenu instance) {

    }

    public NewResourcesMenu asBeanType() {
      return this;
    }

    public void setInstance(final NewResourcesMenu instance) {
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

    @Override public void setup() {
      if (proxyHelper != null) {
        final NewResourcesMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setup();
      } else {
        super.setup();
      }
    }

    @Override public List getMenuItems() {
      if (proxyHelper != null) {
        final NewResourcesMenu proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getMenuItems();
        return retVal;
      } else {
        return super.getMenuItems();
      }
    }

    @Override public List getMenuItemsWithoutProject() {
      if (proxyHelper != null) {
        final NewResourcesMenu proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getMenuItemsWithoutProject();
        return retVal;
      } else {
        return super.getMenuItemsWithoutProject();
      }
    }

    @Override public void onChange(WorkspaceProjectContextChangeEvent previous, WorkspaceProjectContextChangeEvent current) {
      if (proxyHelper != null) {
        final NewResourcesMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onChange(previous, current);
      } else {
        super.onChange(previous, current);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final NewResourcesMenu proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_h_NewResourcesMenu__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NewResourcesMenu.class, "Type_factory__o_k_w_c_w_c_h_NewResourcesMenu__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NewResourcesMenu.class, Object.class, WorkspaceProjectContextChangeHandler.class });
  }

  public NewResourcesMenu createInstance(final ContextManager contextManager) {
    final SyncBeanManager _iocBeanManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final WorkspaceProjectContext _projectContext_2 = (WorkspaceProjectContext) contextManager.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default");
    final NewResourcePresenter _newResourcePresenter_1 = (NewResourcePresenter) contextManager.getInstance("Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_Default");
    final NewResourcesMenu instance = new NewResourcesMenu(_iocBeanManager_0, _newResourcePresenter_1, _projectContext_2);
    registerDependentScopedReference(instance, _iocBeanManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final NewResourcesMenu instance) {
    instance.setup();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<NewResourcesMenu> proxyImpl = new Type_factory__o_k_w_c_w_c_h_NewResourcesMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}