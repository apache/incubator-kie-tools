package org.jboss.errai.ioc.client;

import elemental2.promise.Promise;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.security.ProjectPermissionsService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.promise.Promises;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_g_c_s_p_c_s_ProjectController__quals__j_e_i_Any_j_e_i_Default extends Factory<ProjectController> { private class Type_factory__o_g_c_s_p_c_s_ProjectController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ProjectController implements Proxy<ProjectController> {
    private final ProxyHelper<ProjectController> proxyHelper = new ProxyHelperImpl<ProjectController>("Type_factory__o_g_c_s_p_c_s_ProjectController__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_g_c_s_p_c_s_ProjectController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null);
    }

    public void initProxyProperties(final ProjectController instance) {

    }

    public ProjectController asBeanType() {
      return this;
    }

    public void setInstance(final ProjectController instance) {
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

    @Override public boolean canCreateProjects(OrganizationalUnit organizationalUnit) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.canCreateProjects(organizationalUnit);
        return retVal;
      } else {
        return super.canCreateProjects(organizationalUnit);
      }
    }

    @Override public boolean canReadProject(WorkspaceProject workspaceProject) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.canReadProject(workspaceProject);
        return retVal;
      } else {
        return super.canReadProject(workspaceProject);
      }
    }

    @Override public Promise canUpdateProject(WorkspaceProject workspaceProject) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canUpdateProject(workspaceProject);
        return retVal;
      } else {
        return super.canUpdateProject(workspaceProject);
      }
    }

    @Override public Promise canUpdateBranch(WorkspaceProject workspaceProject, Branch branch) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canUpdateBranch(workspaceProject, branch);
        return retVal;
      } else {
        return super.canUpdateBranch(workspaceProject, branch);
      }
    }

    @Override public boolean canDeleteProject(WorkspaceProject workspaceProject) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.canDeleteProject(workspaceProject);
        return retVal;
      } else {
        return super.canDeleteProject(workspaceProject);
      }
    }

    @Override public Promise canBuildProject(WorkspaceProject workspaceProject) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canBuildProject(workspaceProject);
        return retVal;
      } else {
        return super.canBuildProject(workspaceProject);
      }
    }

    @Override public Promise canDeployProject(WorkspaceProject workspaceProject) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canDeployProject(workspaceProject);
        return retVal;
      } else {
        return super.canDeployProject(workspaceProject);
      }
    }

    @Override public Promise canReadBranch(WorkspaceProject project) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canReadBranch(project);
        return retVal;
      } else {
        return super.canReadBranch(project);
      }
    }

    @Override public Promise canReadBranch(WorkspaceProject project, String branch) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canReadBranch(project, branch);
        return retVal;
      } else {
        return super.canReadBranch(project, branch);
      }
    }

    @Override public Promise canDeleteBranch(WorkspaceProject project) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canDeleteBranch(project);
        return retVal;
      } else {
        return super.canDeleteBranch(project);
      }
    }

    @Override public Promise canDeleteBranch(WorkspaceProject project, String branch) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canDeleteBranch(project, branch);
        return retVal;
      } else {
        return super.canDeleteBranch(project, branch);
      }
    }

    @Override public Promise canSubmitChangeRequest(WorkspaceProject project) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canSubmitChangeRequest(project);
        return retVal;
      } else {
        return super.canSubmitChangeRequest(project);
      }
    }

    @Override public Promise canSubmitChangeRequest(WorkspaceProject project, String branch) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canSubmitChangeRequest(project, branch);
        return retVal;
      } else {
        return super.canSubmitChangeRequest(project, branch);
      }
    }

    @Override public Promise canViewDeploymentDetails(String id) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.canViewDeploymentDetails(id);
        return retVal;
      } else {
        return super.canViewDeploymentDetails(id);
      }
    }

    @Override public Promise getBranchPermissionsForUser(WorkspaceProject project, String branch) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getBranchPermissionsForUser(project, branch);
        return retVal;
      } else {
        return super.getBranchPermissionsForUser(project, branch);
      }
    }

    @Override public Optional getBranchPermissionsForUser(WorkspaceProject project, Map permissionsByRole) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getBranchPermissionsForUser(project, permissionsByRole);
        return retVal;
      } else {
        return super.getBranchPermissionsForUser(project, permissionsByRole);
      }
    }

    @Override public Promise getReadableBranches(WorkspaceProject project) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getReadableBranches(project);
        return retVal;
      } else {
        return super.getReadableBranches(project);
      }
    }

    @Override public Promise getUpdatableBranches(WorkspaceProject project) {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getUpdatableBranches(project);
        return retVal;
      } else {
        return super.getUpdatableBranches(project);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ProjectController proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_c_s_p_c_s_ProjectController__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ProjectController.class, "Type_factory__o_g_c_s_p_c_s_ProjectController__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ProjectController.class, Object.class });
  }

  public ProjectController createInstance(final ContextManager contextManager) {
    final AuthorizationManager _authorizationManager_0 = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    final Caller<ProjectPermissionsService> _projectPermissionsService_2 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { ProjectPermissionsService.class }, new Annotation[] { });
    final Promises _promises_3 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final User _user_1 = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    final ProjectController instance = new ProjectController(_authorizationManager_0, _user_1, _projectPermissionsService_2, _promises_3);
    registerDependentScopedReference(instance, _projectPermissionsService_2);
    registerDependentScopedReference(instance, _promises_3);
    registerDependentScopedReference(instance, _user_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_g_c_s_p_c_s_ProjectController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.guvnor.common.services.project.client.security.ProjectController an exception was thrown from this constructor: @javax.inject.Inject()  public org.guvnor.common.services.project.client.security.ProjectController ([org.uberfire.security.authz.AuthorizationManager, org.jboss.errai.security.shared.api.identity.User, org.jboss.errai.common.client.api.Caller, org.uberfire.client.promise.Promises])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ProjectController> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}