package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeHandle;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeHandler;
import org.guvnor.common.services.project.events.ModuleUpdatedEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkspaceProjectContext> { private class Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkspaceProjectContext implements Proxy<WorkspaceProjectContext> {
    private final ProxyHelper<WorkspaceProjectContext> proxyHelper = new ProxyHelperImpl<WorkspaceProjectContext>("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkspaceProjectContext instance) {

    }

    public WorkspaceProjectContext asBeanType() {
      return this;
    }

    public void setInstance(final WorkspaceProjectContext instance) {
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

    @Override public void onOrganizationalUnitUpdated(UpdatedOrganizationalUnitEvent event) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onOrganizationalUnitUpdated(event);
      } else {
        super.onOrganizationalUnitUpdated(event);
      }
    }

    @Override public void onModuleUpdated(ModuleUpdatedEvent moduleUpdatedEvent) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onModuleUpdated(moduleUpdatedEvent);
      } else {
        super.onModuleUpdated(moduleUpdatedEvent);
      }
    }

    @Override public void onProjectContextChanged(WorkspaceProjectContextChangeEvent event) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onProjectContextChanged(event);
      } else {
        super.onProjectContextChanged(event);
      }
    }

    @Override public Optional getActiveRepositoryRoot() {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getActiveRepositoryRoot();
        return retVal;
      } else {
        return super.getActiveRepositoryRoot();
      }
    }

    @Override protected void setActiveOrganizationalUnit(OrganizationalUnit activeOrganizationalUnit) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        WorkspaceProjectContext_setActiveOrganizationalUnit_OrganizationalUnit(proxiedInstance, activeOrganizationalUnit);
      } else {
        super.setActiveOrganizationalUnit(activeOrganizationalUnit);
      }
    }

    @Override public Optional getActiveOrganizationalUnit() {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getActiveOrganizationalUnit();
        return retVal;
      } else {
        return super.getActiveOrganizationalUnit();
      }
    }

    @Override protected void setActiveWorkspaceProject(WorkspaceProject activeWorkspaceProject) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        WorkspaceProjectContext_setActiveWorkspaceProject_WorkspaceProject(proxiedInstance, activeWorkspaceProject);
      } else {
        super.setActiveWorkspaceProject(activeWorkspaceProject);
      }
    }

    @Override public Optional getActiveWorkspaceProject() {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getActiveWorkspaceProject();
        return retVal;
      } else {
        return super.getActiveWorkspaceProject();
      }
    }

    @Override public Optional getActiveModule() {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getActiveModule();
        return retVal;
      } else {
        return super.getActiveModule();
      }
    }

    @Override protected void setActiveModule(Module activeModule) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        WorkspaceProjectContext_setActiveModule_Module(proxiedInstance, activeModule);
      } else {
        super.setActiveModule(activeModule);
      }
    }

    @Override public Optional getActivePackage() {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getActivePackage();
        return retVal;
      } else {
        return super.getActivePackage();
      }
    }

    @Override protected void setActivePackage(Package activePackage) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        WorkspaceProjectContext_setActivePackage_Package(proxiedInstance, activePackage);
      } else {
        super.setActivePackage(activePackage);
      }
    }

    @Override public ProjectContextChangeHandle addChangeHandler(WorkspaceProjectContextChangeHandler changeHandler) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        final ProjectContextChangeHandle retVal = proxiedInstance.addChangeHandler(changeHandler);
        return retVal;
      } else {
        return super.addChangeHandler(changeHandler);
      }
    }

    @Override public void removeChangeHandler(ProjectContextChangeHandle projectContextChangeHandle) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeChangeHandler(projectContextChangeHandle);
      } else {
        super.removeChangeHandler(projectContextChangeHandle);
      }
    }

    @Override public void updateProjectModule(Module module) {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.updateProjectModule(module);
      } else {
        super.updateProjectModule(module);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkspaceProjectContext proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkspaceProjectContext.class, "Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkspaceProjectContext.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent", new AbstractCDIEventCallback<UpdatedOrganizationalUnitEvent>() {
      public void fireEvent(final UpdatedOrganizationalUnitEvent event) {
        final WorkspaceProjectContext instance = Factory.maybeUnwrapProxy((WorkspaceProjectContext) context.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default"));
        instance.onOrganizationalUnitUpdated(event);
      }
      public String toString() {
        return "Observer: org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent []";
      }
    });
    CDI.subscribeLocal("org.guvnor.common.services.project.events.ModuleUpdatedEvent", new AbstractCDIEventCallback<ModuleUpdatedEvent>() {
      public void fireEvent(final ModuleUpdatedEvent event) {
        final WorkspaceProjectContext instance = Factory.maybeUnwrapProxy((WorkspaceProjectContext) context.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default"));
        instance.onModuleUpdated(event);
      }
      public String toString() {
        return "Observer: org.guvnor.common.services.project.events.ModuleUpdatedEvent []";
      }
    });
    CDI.subscribeLocal("org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent", new AbstractCDIEventCallback<WorkspaceProjectContextChangeEvent>() {
      public void fireEvent(final WorkspaceProjectContextChangeEvent event) {
        final WorkspaceProjectContext instance = Factory.maybeUnwrapProxy((WorkspaceProjectContext) context.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default"));
        instance.onProjectContextChanged(event);
      }
      public String toString() {
        return "Observer: org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent []";
      }
    });
  }

  public WorkspaceProjectContext createInstance(final ContextManager contextManager) {
    final Event<WorkspaceProjectContextChangeEvent> _contextChangeEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { WorkspaceProjectContextChangeEvent.class }, new Annotation[] { });
    final WorkspaceProjectContext instance = new WorkspaceProjectContext(_contextChangeEvent_0);
    registerDependentScopedReference(instance, _contextChangeEvent_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkspaceProjectContext> proxyImpl = new Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void WorkspaceProjectContext_setActivePackage_Package(WorkspaceProjectContext instance, Package a0) /*-{
    instance.@org.guvnor.common.services.project.client.context.WorkspaceProjectContext::setActivePackage(Lorg/guvnor/common/services/project/model/Package;)(a0);
  }-*/;

  public native static void WorkspaceProjectContext_setActiveModule_Module(WorkspaceProjectContext instance, Module a0) /*-{
    instance.@org.guvnor.common.services.project.client.context.WorkspaceProjectContext::setActiveModule(Lorg/guvnor/common/services/project/model/Module;)(a0);
  }-*/;

  public native static void WorkspaceProjectContext_setActiveWorkspaceProject_WorkspaceProject(WorkspaceProjectContext instance, WorkspaceProject a0) /*-{
    instance.@org.guvnor.common.services.project.client.context.WorkspaceProjectContext::setActiveWorkspaceProject(Lorg/guvnor/common/services/project/model/WorkspaceProject;)(a0);
  }-*/;

  public native static void WorkspaceProjectContext_setActiveOrganizationalUnit_OrganizationalUnit(WorkspaceProjectContext instance, OrganizationalUnit a0) /*-{
    instance.@org.guvnor.common.services.project.client.context.WorkspaceProjectContext::setActiveOrganizationalUnit(Lorg/guvnor/structure/organizationalunit/OrganizationalUnit;)(a0);
  }-*/;
}