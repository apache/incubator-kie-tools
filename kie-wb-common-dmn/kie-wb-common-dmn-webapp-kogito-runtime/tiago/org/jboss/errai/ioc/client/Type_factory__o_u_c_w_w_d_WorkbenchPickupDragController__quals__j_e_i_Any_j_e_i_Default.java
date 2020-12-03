package org.jboss.errai.ioc.client;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.DragController;
import com.allen_sauer.gwt.dnd.client.FiresDragEvents;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.BoundaryDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;

public class Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPickupDragController> { private class Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkbenchPickupDragController implements Proxy<WorkbenchPickupDragController> {
    private final ProxyHelper<WorkbenchPickupDragController> proxyHelper = new ProxyHelperImpl<WorkbenchPickupDragController>("Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkbenchPickupDragController instance) {

    }

    public WorkbenchPickupDragController asBeanType() {
      return this;
    }

    public void setInstance(final WorkbenchPickupDragController instance) {
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

    @Override public void dragStart() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.dragStart();
      } else {
        super.dragStart();
      }
    }

    @Override public void dragMove() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.dragMove();
      } else {
        super.dragMove();
      }
    }

    @Override protected Widget newDragProxy(DragContext context) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = WorkbenchPickupDragController_newDragProxy_DragContext(proxiedInstance, context);
        return retVal;
      } else {
        return super.newDragProxy(context);
      }
    }

    @Override public void dragEnd() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.dragEnd();
      } else {
        super.dragEnd();
      }
    }

    @Override public boolean getBehaviorBoundaryPanelDrop() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.getBehaviorBoundaryPanelDrop();
        return retVal;
      } else {
        return super.getBehaviorBoundaryPanelDrop();
      }
    }

    @Override public boolean getBehaviorDragProxy() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.getBehaviorDragProxy();
        return retVal;
      } else {
        return super.getBehaviorDragProxy();
      }
    }

    @Override public Iterable getSelectedWidgets() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final Iterable retVal = proxiedInstance.getSelectedWidgets();
        return retVal;
      } else {
        return super.getSelectedWidgets();
      }
    }

    @Override public void previewDragEnd() throws VetoDragException {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.previewDragEnd();
      } else {
        super.previewDragEnd();
      }
    }

    @Override public void registerDropController(DropController dropController) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerDropController(dropController);
      } else {
        super.registerDropController(dropController);
      }
    }

    @Override public void resetCache() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.resetCache();
      } else {
        super.resetCache();
      }
    }

    @Override public void setBehaviorBoundaryPanelDrop(boolean allowDroppingOnBoundaryPanel) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setBehaviorBoundaryPanelDrop(allowDroppingOnBoundaryPanel);
      } else {
        super.setBehaviorBoundaryPanelDrop(allowDroppingOnBoundaryPanel);
      }
    }

    @Override public void setBehaviorDragProxy(boolean dragProxyEnabled) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setBehaviorDragProxy(dragProxyEnabled);
      } else {
        super.setBehaviorDragProxy(dragProxyEnabled);
      }
    }

    @Override public void unregisterDropController(DropController dropController) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unregisterDropController(dropController);
      } else {
        super.unregisterDropController(dropController);
      }
    }

    @Override public void unregisterDropControllers() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unregisterDropControllers();
      } else {
        super.unregisterDropControllers();
      }
    }

    @Override protected BoundaryDropController newBoundaryDropController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final BoundaryDropController retVal = PickupDragController_newBoundaryDropController_AbsolutePanel_boolean(proxiedInstance, boundaryPanel, allowDroppingOnBoundaryPanel);
        return retVal;
      } else {
        return super.newBoundaryDropController(boundaryPanel, allowDroppingOnBoundaryPanel);
      }
    }

    @Override protected void restoreSelectedWidgetsLocation() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        PickupDragController_restoreSelectedWidgetsLocation(proxiedInstance);
      } else {
        super.restoreSelectedWidgetsLocation();
      }
    }

    @Override protected void restoreSelectedWidgetsStyle() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        PickupDragController_restoreSelectedWidgetsStyle(proxiedInstance);
      } else {
        super.restoreSelectedWidgetsStyle();
      }
    }

    @Override protected void saveSelectedWidgetsLocationAndStyle() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        PickupDragController_saveSelectedWidgetsLocationAndStyle(proxiedInstance);
      } else {
        super.saveSelectedWidgetsLocationAndStyle();
      }
    }

    @Override public void clearSelection() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clearSelection();
      } else {
        super.clearSelection();
      }
    }

    @Override public boolean getBehaviorCancelDocumentSelections() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.getBehaviorCancelDocumentSelections();
        return retVal;
      } else {
        return super.getBehaviorCancelDocumentSelections();
      }
    }

    @Override public boolean getBehaviorConstrainedToBoundaryPanel() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.getBehaviorConstrainedToBoundaryPanel();
        return retVal;
      } else {
        return super.getBehaviorConstrainedToBoundaryPanel();
      }
    }

    @Override public int getBehaviorDragStartSensitivity() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getBehaviorDragStartSensitivity();
        return retVal;
      } else {
        return super.getBehaviorDragStartSensitivity();
      }
    }

    @Override public boolean getBehaviorMultipleSelection() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.getBehaviorMultipleSelection();
        return retVal;
      } else {
        return super.getBehaviorMultipleSelection();
      }
    }

    @Override public boolean getBehaviorScrollIntoView() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.getBehaviorScrollIntoView();
        return retVal;
      } else {
        return super.getBehaviorScrollIntoView();
      }
    }

    @Override public void makeDraggable(Widget draggable) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.makeDraggable(draggable);
      } else {
        super.makeDraggable(draggable);
      }
    }

    @Override public void makeDraggable(Widget draggable, Widget dragHandle) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.makeDraggable(draggable, dragHandle);
      } else {
        super.makeDraggable(draggable, dragHandle);
      }
    }

    @Override public void makeNotDraggable(Widget draggable) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.makeNotDraggable(draggable);
      } else {
        super.makeNotDraggable(draggable);
      }
    }

    @Override public void previewDragStart() throws VetoDragException {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.previewDragStart();
      } else {
        super.previewDragStart();
      }
    }

    @Override public void setBehaviorCancelDocumentSelections(boolean cancelDocumentSelections) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setBehaviorCancelDocumentSelections(cancelDocumentSelections);
      } else {
        super.setBehaviorCancelDocumentSelections(cancelDocumentSelections);
      }
    }

    @Override public void setBehaviorConstrainedToBoundaryPanel(boolean constrainedToBoundaryPanel) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setBehaviorConstrainedToBoundaryPanel(constrainedToBoundaryPanel);
      } else {
        super.setBehaviorConstrainedToBoundaryPanel(constrainedToBoundaryPanel);
      }
    }

    @Override public void setBehaviorDragStartSensitivity(int pixels) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setBehaviorDragStartSensitivity(pixels);
      } else {
        super.setBehaviorDragStartSensitivity(pixels);
      }
    }

    @Override public void setBehaviorMultipleSelection(boolean multipleSelectionAllowed) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setBehaviorMultipleSelection(multipleSelectionAllowed);
      } else {
        super.setBehaviorMultipleSelection(multipleSelectionAllowed);
      }
    }

    @Override public void setBehaviorScrollIntoView(boolean scrollIntoView) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setBehaviorScrollIntoView(scrollIntoView);
      } else {
        super.setBehaviorScrollIntoView(scrollIntoView);
      }
    }

    @Override public void setConstrainWidgetToBoundaryPanel(boolean constrainWidgetToBoundaryPanel) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setConstrainWidgetToBoundaryPanel(constrainWidgetToBoundaryPanel);
      } else {
        super.setConstrainWidgetToBoundaryPanel(constrainWidgetToBoundaryPanel);
      }
    }

    @Override public void toggleSelection(Widget draggable) {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.toggleSelection(draggable);
      } else {
        super.toggleSelection(draggable);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkbenchPickupDragController proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchPickupDragController.class, "Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchPickupDragController.class, PickupDragController.class, AbstractDragController.class, Object.class, DragController.class, FiresDragEvents.class });
  }

  public WorkbenchPickupDragController createInstance(final ContextManager contextManager) {
    final WorkbenchPickupDragController instance = new WorkbenchPickupDragController();
    setIncompleteInstance(instance);
    final WorkbenchDragAndDropManager WorkbenchPickupDragController_dndManager = (WorkbenchDragAndDropManager) contextManager.getInstance("Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_Default");
    WorkbenchPickupDragController_WorkbenchDragAndDropManager_dndManager(instance, WorkbenchPickupDragController_dndManager);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkbenchPickupDragController> proxyImpl = new Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static WorkbenchDragAndDropManager WorkbenchPickupDragController_WorkbenchDragAndDropManager_dndManager(WorkbenchPickupDragController instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController::dndManager;
  }-*/;

  native static void WorkbenchPickupDragController_WorkbenchDragAndDropManager_dndManager(WorkbenchPickupDragController instance, WorkbenchDragAndDropManager value) /*-{
    instance.@org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController::dndManager = value;
  }-*/;

  public native static BoundaryDropController PickupDragController_newBoundaryDropController_AbsolutePanel_boolean(PickupDragController instance, AbsolutePanel a0, boolean a1) /*-{
    return instance.@com.allen_sauer.gwt.dnd.client.PickupDragController::newBoundaryDropController(Lcom/google/gwt/user/client/ui/AbsolutePanel;Z)(a0, a1);
  }-*/;

  public native static void PickupDragController_saveSelectedWidgetsLocationAndStyle(PickupDragController instance) /*-{
    instance.@com.allen_sauer.gwt.dnd.client.PickupDragController::saveSelectedWidgetsLocationAndStyle()();
  }-*/;

  public native static void PickupDragController_restoreSelectedWidgetsStyle(PickupDragController instance) /*-{
    instance.@com.allen_sauer.gwt.dnd.client.PickupDragController::restoreSelectedWidgetsStyle()();
  }-*/;

  public native static void PickupDragController_restoreSelectedWidgetsLocation(PickupDragController instance) /*-{
    instance.@com.allen_sauer.gwt.dnd.client.PickupDragController::restoreSelectedWidgetsLocation()();
  }-*/;

  public native static Widget WorkbenchPickupDragController_newDragProxy_DragContext(WorkbenchPickupDragController instance, DragContext a0) /*-{
    return instance.@org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController::newDragProxy(Lcom/allen_sauer/gwt/dnd/client/DragContext;)(a0);
  }-*/;
}