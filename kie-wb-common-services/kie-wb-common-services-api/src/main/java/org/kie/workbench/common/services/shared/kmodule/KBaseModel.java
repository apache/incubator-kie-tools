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
}
