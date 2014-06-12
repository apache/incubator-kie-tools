package org.uberfire.client.workbench.panels.support;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/06/14
 */
@ApplicationScoped
public class PartManager {

    private final Map<PartDefinition, Widget> widgets = new HashMap<PartDefinition, Widget>();
    private Pair<PartDefinition, Widget> activePart;

    public Pair<PartDefinition, Widget> getActivePart() {
        return activePart;
    }

    public boolean hasActivePart() {
        return activePart!=null;
    }

    public void registerPart(PartDefinition partDef, Widget w)
    {
        if(widgets.containsKey(partDef))
            throw new IllegalArgumentException("Part already registered: "+ partDef.getPlace().getIdentifier());

        widgets.put(partDef, w);
    }

    public void removePart(PartDefinition partDef)
    {
        /*
        TODO (hbraun): revisit panel managers with single parts
        if(partDef.equals(activePart.getK1()))
            throw new IllegalArgumentException("Cannot remove active part: "+ partDef.getPlace().getIdentifier());
            */
        if(partDef.equals(activePart))
            activePart = null;

        widgets.remove(partDef);
    }

    public void clearParts() {
        widgets.clear();
        activePart = null;
    }
    public boolean hasPart(PartDefinition partDef)
    {
        return widgets.containsKey(partDef);
    }

    public Widget selectPart(PartDefinition partDef)
    {
        if(!hasPart(partDef))
            throw new IllegalArgumentException("Unknown part: "+ partDef.getPlace().getIdentifier());

        final Widget w = widgets.get(partDef);
        activePart = new Pair<PartDefinition, Widget>(partDef, w);

        return activePart.getK2();
    }
}
