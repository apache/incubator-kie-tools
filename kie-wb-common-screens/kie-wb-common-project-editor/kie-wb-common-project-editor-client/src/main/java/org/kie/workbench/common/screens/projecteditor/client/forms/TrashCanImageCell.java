package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;

public class TrashCanImageCell
        extends
        ButtonCell {

    public TrashCanImageCell() {
        super(ButtonSize.SMALL);
        setType(ButtonType.DANGER);
        setIcon(IconType.TRASH);
    }
}
