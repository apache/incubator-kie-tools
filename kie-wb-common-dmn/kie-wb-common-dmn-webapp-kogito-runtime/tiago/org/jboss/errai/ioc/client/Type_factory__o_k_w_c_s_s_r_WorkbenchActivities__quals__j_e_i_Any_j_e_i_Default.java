package org.jboss.errai.ioc.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.services.shared.resources.WorkbenchActivities;
import org.uberfire.workbench.model.AppFormerActivities;

public class Type_factory__o_k_w_c_s_s_r_WorkbenchActivities__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchActivities> { private class Type_factory__o_k_w_c_s_s_r_WorkbenchActivities__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkbenchActivities implements Proxy<WorkbenchActivities> {
    private final ProxyHelper<WorkbenchActivities> proxyHelper = new ProxyHelperImpl<WorkbenchActivities>("Type_factory__o_k_w_c_s_s_r_WorkbenchActivities__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkbenchActivities instance) {

    }

    public WorkbenchActivities asBeanType() {
      return this;
    }

    public void setInstance(final WorkbenchActivities instance) {
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

    @Override public List getAllEditorIds() {
      if (proxyHelper != null) {
        final WorkbenchActivities proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAllEditorIds();
        return retVal;
      } else {
        return super.getAllEditorIds();
      }
    }

    @Override public List getAllPerpectivesIds() {
      if (proxyHelper != null) {
        final WorkbenchActivities proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAllPerpectivesIds();
        return retVal;
      } else {
        return super.getAllPerpectivesIds();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkbenchActivities proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_s_r_WorkbenchActivities__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchActivities.class, "Type_factory__o_k_w_c_s_s_r_WorkbenchActivities__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchActivities.class, Object.class, AppFormerActivities.class });
  }

  public WorkbenchActivities createInstance(final ContextManager contextManager) {
    final WorkbenchActivities instance = new WorkbenchActivities();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkbenchActivities> proxyImpl = new Type_factory__o_k_w_c_s_s_r_WorkbenchActivities__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}