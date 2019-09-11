BPMN Editor
--

## Release notes - 0.1.0 (alpha)

- Open and edit BPMN and BPMN2 files using the KIE Group BPMN editor.
- Save changes using built-in `Save` and `Save All` commands.
- Use keyboard shortcuts right on the BPMN editor like `Ctrl+Z` (undo), `Ctrl+C` (copy), `Ctrl+V` (paste). Note that on macOS you have to use `Ctrl` too, and not `Cmd`. We are working on fixing that on the next releases.
- If a BPMN editor is already open but you want to see a file as text, you can right-click on this file and choose "Open to the side". VSCode editor will split in half and the file will open as text.
    
![gif](https://i.imgur.com/MSklVBh.gif)

### Known issues

- [KOGITO-155](https://issues.jboss.org/browse/KOGITO-155) Flicker when opening or restoring editors. 
- [AF-2167](https://issues.jboss.org/browse/AF-2167) Native editor key bindings for macOS. 
- [AF-2113](https://issues.jboss.org/browse/AF-2113) No indication of a modified BPMN diagram.. 
- [AF-2168](https://issues.jboss.org/browse/AF-2168) No confirmation popup when closing a modified BPMN diagram. 
- [KOGITO-157](https://issues.jboss.org/browse/KOGITO-157) Copy/paste between different BPMN diagram. 
- [KOGITO-224](https://issues.jboss.org/browse/KOGITO-224) An error message is displayed if you try to create a new type by pressing `Enter`. 
- [KOGITO-225](https://issues.jboss.org/browse/KOGITO-225) Custom type definitions aren’t re-used within the diagram. 
- [KOGITO-226](https://issues.jboss.org/browse/KOGITO-226) Not all BPMN constructs (elements) are supported. If a non-supported construct is used, an error message will be displayed.
- [KOGITO-272](https://issues.jboss.org/browse/KOGITO-272) Editor doesn't close when file is deleted. 