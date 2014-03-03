package org.uberfire.client.workbench.events;

import org.uberfire.workbench.events.UberFireEvent;

/**
 * Fired by the framework when all Workbench startup blockers have cleared, and just before the workbench starts to
 * build its components. Observers receive this event at the last possible opportunity to make changes before the UI
 * shows up for the first time.
 */
public class ApplicationReadyEvent extends UberFireEvent {

}
