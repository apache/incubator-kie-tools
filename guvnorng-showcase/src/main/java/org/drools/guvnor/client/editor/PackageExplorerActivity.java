package org.drools.guvnor.client.editor;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.mvp.PlaceRequest;
import org.drools.guvnor.client.workbench.Position;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@NameToken("Package Explorer")
public class PackageExplorerActivity implements Activity {

    private final PlaceManager placeManager;

    @Inject
    public PackageExplorerActivity(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    @Override
    public void start() {
    }

    @Override
    public boolean mayStop() {
        return true;
    }

    @Override
    public void onStop() {
        //TODO: -Rikkola-
    }

    @Override
    public Position getPreferredPosition() {
        return Position.WEST;
    }

    @Override
    public void revealPlace(AcceptItem acceptPanel) {
        Tree tree = new Tree();

        final TreeItem treeItem = tree.addItem("Editors");
        final TreeItem textEditorTreeItem = treeItem.addItem("Text Editor");
        final TreeItem textEditor2TreeItem = treeItem.addItem("Text Editor2");
        final TreeItem myAdminAreaTreeItem = treeItem.addItem("MyAdminArea");

        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                if (textEditorTreeItem.equals(event.getSelectedItem())) {
                    //placeManager.goTo(new TextEditorPlace());
                    PlaceRequest placeRequest = new PlaceRequest("TextEditor");
                    placeRequest.parameter("uuid", "uuid");
                    placeManager.goTo(placeRequest);
                } else if (textEditor2TreeItem.equals(event.getSelectedItem())) {
                    PlaceRequest placeRequest = new PlaceRequest("TextEditor");
                    placeRequest.parameter("uuid", "uuid2");
                    placeManager.goTo(placeRequest);
                } else if (myAdminAreaTreeItem.equals(event.getSelectedItem())) {
                    PlaceRequest placeRequest = new PlaceRequest("MyAdminArea");
                    placeManager.goTo(placeRequest);
                }
            }
        });

        acceptPanel.add("Package Explorer", tree);
    }

    @Override
    public boolean mayClosePlace() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void closePlace() {
        // TODO Auto-generated method stub        
    }

    @Override
    public void hide() {
        //TODO: -Rikkola-
    }

    @Override
    public void show() {
        //TODO: -Rikkola-
    }
}
