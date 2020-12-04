package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.widgets.client.menu.RepositoryMenu;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;

public class Type_factory__o_k_w_c_w_c_m_RepositoryMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<RepositoryMenu> { private class Type_factory__o_k_w_c_w_c_m_RepositoryMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends RepositoryMenu implements Proxy<RepositoryMenu> {
    private final ProxyHelper<RepositoryMenu> proxyHelper = new ProxyHelperImpl<RepositoryMenu>("Type_factory__o_k_w_c_w_c_m_RepositoryMenu__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final RepositoryMenu instance) {

    }

    public RepositoryMenu asBeanType() {
      return this;
    }

    public void setInstance(final RepositoryMenu instance) {
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

    @Override public List getMenuItems() {
      if (proxyHelper != null) {
        final RepositoryMenu proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getMenuItems();
        return retVal;
      } else {
        return super.getMenuItems();
      }
    }

    @Override public void onWorkspaceProjectContextChanged(WorkspaceProjectContextChangeEvent event) {
      if (proxyHelper != null) {
        final RepositoryMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onWorkspaceProjectContextChanged(event);
      } else {
        super.onWorkspaceProjectContextChanged(event);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final RepositoryMenu proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_m_RepositoryMenu__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RepositoryMenu.class, "Type_factory__o_k_w_c_w_c_m_RepositoryMenu__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RepositoryMenu.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent", new AbstractCDIEventCallback<WorkspaceProjectContextChangeEvent>() {
      public void fireEvent(final WorkspaceProjectContextChangeEvent event) {
        final RepositoryMenu instance = Factory.maybeUnwrapProxy((RepositoryMenu) context.getInstance("Type_factory__o_k_w_c_w_c_m_RepositoryMenu__quals__j_e_i_Any_j_e_i_Default"));
        instance.onWorkspaceProjectContextChanged(event);
      }
      public String toString() {
        return "Observer: org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent []";
      }
    });
  }

  public RepositoryMenu createInstance(final ContextManager contextManager) {
    final RepositoryMenu instance = new RepositoryMenu();
    setIncompleteInstance(instance);
    final Caller RepositoryMenu_moduleService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { KieModuleService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, RepositoryMenu_moduleService);
    RepositoryMenu_Caller_moduleService(instance, RepositoryMenu_moduleService);
    final PlaceManagerImpl RepositoryMenu_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    RepositoryMenu_PlaceManager_placeManager(instance, RepositoryMenu_placeManager);
    final WorkspaceProjectContext RepositoryMenu_context = (WorkspaceProjectContext) contextManager.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default");
    RepositoryMenu_WorkspaceProjectContext_context(instance, RepositoryMenu_context);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<RepositoryMenu> proxyImpl = new Type_factory__o_k_w_c_w_c_m_RepositoryMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static PlaceManager RepositoryMenu_PlaceManager_placeManager(RepositoryMenu instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.RepositoryMenu::placeManager;
  }-*/;

  native static void RepositoryMenu_PlaceManager_placeManager(RepositoryMenu instance, PlaceManager value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.RepositoryMenu::placeManager = value;
  }-*/;

  native static WorkspaceProjectContext RepositoryMenu_WorkspaceProjectContext_context(RepositoryMenu instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.RepositoryMenu::context;
  }-*/;

  native static void RepositoryMenu_WorkspaceProjectContext_context(RepositoryMenu instance, WorkspaceProjectContext value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.RepositoryMenu::context = value;
  }-*/;

  native static Caller RepositoryMenu_Caller_moduleService(RepositoryMenu instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.RepositoryMenu::moduleService;
  }-*/;

  native static void RepositoryMenu_Caller_moduleService(RepositoryMenu instance, Caller<KieModuleService> value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.RepositoryMenu::moduleService = value;
  }-*/;
}