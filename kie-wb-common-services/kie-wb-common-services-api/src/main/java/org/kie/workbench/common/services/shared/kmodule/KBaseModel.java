package org.kie.workbench.common.services.shared.kmodule;

import org.guvnor.common.services.project.model.HasListFormComboPanelProperties;
import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.List;

@Portable
public class KBaseModel
        implements HasListFormComboPanelProperties {

    private String name;
    private AssertBehaviorOption equalsBehavior = AssertBehaviorOption.IDENTITY;
    private EventProcessingOption eventProcessingMode = EventProcessingOption.STREAM;
    private List<KSessionModel> kSessions = new ArrayList<KSessionModel>();
    private List<String> includes = new ArrayList<String>();

    private boolean theDefault;
    private String scope;
    private List<String> packages=new ArrayList<String>();
    private DeclarativeAgendaOption declarativeAgenda;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEqualsBehavior(AssertBehaviorOption equalsBehavior) {
        this.equalsBehavior = equalsBehavior;
    }

    public AssertBehaviorOption getEqualsBehavior() {
        return equalsBehavior;
    }

    public void setEventProcessingMode(EventProcessingOption eventProcessingMode) {
        this.eventProcessingMode = eventProcessingMode;
    }

    public EventProcessingOption getEventProcessingMode() {
        return eventProcessingMode;
    }

    public List<KSessionModel> getKSessions() {
        return kSessions;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void addInclude(String include) {
        includes.add(include);
    }

    public boolean isDefault() {
        return theDefault;
    }

    public String getScope() {
        return scope;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setDefault(boolean theDefault) {
        this.theDefault = theDefault;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void addPackage(String pkg) {
        packages.add(pkg);
    }

    public void setDeclarativeAgenda(DeclarativeAgendaOption declarativeAgenda) {
        this.declarativeAgenda = declarativeAgenda;
    }

    public DeclarativeAgendaOption getDeclarativeAgenda() {
        return declarativeAgenda;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        KBaseModel that = ( KBaseModel ) o;

        if ( theDefault != that.theDefault ) {
            return false;
        }
        if ( declarativeAgenda != that.declarativeAgenda ) {
            return false;
        }
        if ( equalsBehavior != that.equalsBehavior ) {
            return false;
        }
        if ( eventProcessingMode != that.eventProcessingMode ) {
            return false;
        }
        if ( includes != null ? !includes.equals( that.includes ) : that.includes != null ) {
            return false;
        }
        if ( kSessions != null ? !kSessions.equals( that.kSessions ) : that.kSessions != null ) {
            return false;
        }
        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        if ( packages != null ? !packages.equals( that.packages ) : that.packages != null ) {
            return false;
        }
        if ( scope != null ? !scope.equals( that.scope ) : that.scope != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( equalsBehavior != null ? equalsBehavior.hashCode() : 0 );
        result = 31 * result + ( eventProcessingMode != null ? eventProcessingMode.hashCode() : 0 );
        result = 31 * result + ( kSessions != null ? kSessions.hashCode() : 0 );
        result = 31 * result + ( includes != null ? includes.hashCode() : 0 );
        result = 31 * result + ( theDefault ? 1 : 0 );
        result = 31 * result + ( scope != null ? scope.hashCode() : 0 );
        result = 31 * result + ( packages != null ? packages.hashCode() : 0 );
        result = 31 * result + ( declarativeAgenda != null ? declarativeAgenda.hashCode() : 0 );
        return result;
    }
}
