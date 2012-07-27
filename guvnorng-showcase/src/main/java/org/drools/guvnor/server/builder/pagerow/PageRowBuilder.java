package org.drools.guvnor.server.builder.pagerow;

import java.util.List;

import org.drools.guvnor.client.rpc.AbstractPageRow;
import org.drools.guvnor.client.rpc.PageRequest;
//import org.jboss.seam.security.Identity;

public interface PageRowBuilder<REQUEST extends PageRequest, CONTENT> {
    public List< ? extends AbstractPageRow> build();

    public void validate();
   
    public PageRowBuilder<REQUEST, CONTENT> withPageRequest(final REQUEST pageRequest);

    public PageRowBuilder<REQUEST, CONTENT> withIdentity(/*final Identity identity*/);

    public PageRowBuilder<REQUEST, CONTENT> withContent(final CONTENT pageRequest);

}
