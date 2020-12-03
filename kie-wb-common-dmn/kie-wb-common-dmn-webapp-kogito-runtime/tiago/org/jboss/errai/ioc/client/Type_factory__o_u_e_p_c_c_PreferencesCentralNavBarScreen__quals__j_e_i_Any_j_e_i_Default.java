package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.preferences.client.central.PreferencesCentralNavBarScreen;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyStructurePresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructurePresenter;
import org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent;

public class Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesCentralNavBarScreen> { public Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferencesCentralNavBarScreen.class, "Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreen__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferencesCentralNavBarScreen.class, Object.class });
  }

  public PreferencesCentralNavBarScreen createInstance(final ContextManager contextManager) {
    final HierarchyStructurePresenter _treePresenter_0 = (TreeHierarchyStructurePresenter) contextManager.getInstance("Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructurePresenter__quals__j_e_i_Any_o_u_e_p_c_c_t_TreeView");
    final PreferencesCentralNavBarScreen instance = new PreferencesCentralNavBarScreen(_treePresenter_0);
    registerDependentScopedReference(instance, _treePresenter_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "initSubscription", CDI.subscribeLocal("org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent", new AbstractCDIEventCallback<PreferencesCentralInitializationEvent>() {
      public void fireEvent(final PreferencesCentralInitializationEvent event) {
        instance.init(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PreferencesCentralNavBarScreen) instance, contextManager);
  }

  public void destroyInstanceHelper(final PreferencesCentralNavBarScreen instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "initSubscription", Subscription.class)).remove();
  }
}