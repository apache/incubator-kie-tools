package org.kie.workbench.common.screens.server.management.client.widget.card.title;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Templated
@Dependent
public class InfoTitleView extends Composite
        implements InfoTitlePresenter.View {

    @Inject
    @DataField("groupId")
    Span groupId;

    @Inject
    @DataField("artifactId")
    Span artifactId;

    @Override
    public void setup( final String groupId,
                       final String artifactId ) {
        this.groupId.setText( checkNotEmpty( "groupId", groupId ) );
        this.artifactId.setText( checkNotEmpty( "artifactId", artifactId ) );
    }

}
