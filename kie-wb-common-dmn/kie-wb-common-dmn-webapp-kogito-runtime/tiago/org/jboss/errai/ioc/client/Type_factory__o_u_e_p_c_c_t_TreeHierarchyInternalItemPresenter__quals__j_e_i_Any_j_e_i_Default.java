package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyInternalItemPresenter;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyItemPresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemPresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemPresenter.View;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemPresenter;
import org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent;

public class Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyInternalItemPresenter> { public Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeHierarchyInternalItemPresenter.class, "Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeHierarchyInternalItemPresenter.class, Object.class, HierarchyInternalItemPresenter.class, HierarchyItemPresenter.class });
  }

  public TreeHierarchyInternalItemPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (TreeHierarchyInternalItemView) contextManager.getInstance("Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default");
    final Event<HierarchyItemSelectedEvent> _hierarchyItemSelectedEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { HierarchyItemSelectedEvent.class }, new Annotation[] { });
    final ManagedInstance<TreeHierarchyLeafItemPresenter> _treeHierarchyLeafItemPresenterProvider_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { TreeHierarchyLeafItemPresenter.class }, new Annotation[] { });
    final ManagedInstance<TreeHierarchyInternalItemPresenter> _treeHierarchyInternalItemPresenterProvider_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { TreeHierarchyInternalItemPresenter.class }, new Annotation[] { });
    final TreeHierarchyInternalItemPresenter instance = new TreeHierarchyInternalItemPresenter(_view_0, _treeHierarchyInternalItemPresenterProvider_1, _treeHierarchyLeafItemPresenterProvider_2, _hierarchyItemSelectedEvent_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _hierarchyItemSelectedEvent_3);
    registerDependentScopedReference(instance, _treeHierarchyLeafItemPresenterProvider_2);
    registerDependentScopedReference(instance, _treeHierarchyInternalItemPresenterProvider_1);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "hierarchyItemSelectedEventSubscription", CDI.subscribeLocal("org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent", new AbstractCDIEventCallback<HierarchyItemSelectedEvent>() {
      public void fireEvent(final HierarchyItemSelectedEvent event) {
        instance.hierarchyItemSelectedEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeHierarchyInternalItemPresenter) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeHierarchyInternalItemPresenter instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "hierarchyItemSelectedEventSubscription", Subscription.class)).remove();
  }
}