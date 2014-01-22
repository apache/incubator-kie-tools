package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.security.annotations.Roles;

@WorkbenchPopup(identifier = "test2")
@Roles({"ADMIN", "SUDO"})
public class WorkbenchPopupTest2 {

}
