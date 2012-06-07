package org.drools.guvnor.shared.common.vo.asset;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AssetData  {

    private String  uuid;
    private String  name;
    private String  description;
    private Date    lastModified;
    private String  lastContributor;
    private String  state      = "";
    private Date    dateCreated;
    private String  checkinComment;
    private long    versionNumber;
    private boolean isReadOnly = false;
    private boolean isArchived = false;
    private String  format     = "";

    public String getUuid() {
        return uuid;
    }

    public AssetData setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getName() {
        return name;
    }

    public AssetData setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AssetData setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public AssetData setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public AssetData setLastContributor(String lastContributor) {
        this.lastContributor = lastContributor;
        return this;
    }

    public String getState() {
        return state;
    }

    public AssetData setState(String state) {
        this.state = state;
        return this;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public AssetData setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public String getCheckinComment() {
        return checkinComment;
    }

    public AssetData setCheckinComment(String checkinComment) {
        this.checkinComment = checkinComment;
        return this;
    }

    public long getVersionNumber() {
        return versionNumber;
    }

    public AssetData setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
        return this;
    }

    public boolean isReadonly() {
        return isReadOnly;
    }

    public AssetData setReadonly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public AssetData setFormat(String format) {
        this.format = format;
        return this;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public AssetData setArchived(boolean isArchived) {
        this.isArchived = isArchived;
        return this;
    }

}
