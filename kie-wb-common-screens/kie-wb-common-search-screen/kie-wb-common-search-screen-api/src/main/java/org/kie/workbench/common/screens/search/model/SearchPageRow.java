package org.kie.workbench.common.screens.search.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.tables.AbstractPathPageRow;

/**
 *
 */
@Portable
public class SearchPageRow extends AbstractPathPageRow {

    private String  description;
    private String  abbreviatedDescription;
    private String  creator;
    private Date    createdDate;
    private String  lastContributor;
    private Date    lastModified;
    private boolean disabled;

    public SearchPageRow() {
        super();
    }

    public SearchPageRow( final Path path ) {
        super( path );
    }

    public String getAbbreviatedDescription() {
        return abbreviatedDescription;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setAbbreviatedDescription( String abbreviatedDescription ) {
        this.abbreviatedDescription = abbreviatedDescription;
    }

    public void setCreatedDate( Date createdDate ) {
        this.createdDate = createdDate;
    }

    public void setCreator( String creator ) {
        this.creator = creator;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public void setLastContributor( String lastContributor ) {
        this.lastContributor = lastContributor;
    }

    public void setLastModified( Date lastModified ) {
        this.lastModified = lastModified;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled( final boolean disabled ) {
        this.disabled = disabled;
    }

}
