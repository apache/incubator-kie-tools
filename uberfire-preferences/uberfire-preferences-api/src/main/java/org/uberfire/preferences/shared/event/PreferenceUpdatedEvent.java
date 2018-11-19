package org.uberfire.preferences.shared.event;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Event fired when a preference is saved
 */
@Portable
public class PreferenceUpdatedEvent {
    
    private String key;
    private Object value;
    
    public PreferenceUpdatedEvent() {
    }

    public PreferenceUpdatedEvent(String key, Object value) { 
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}