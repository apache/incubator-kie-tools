/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/**
 * This is the place for API experiments and proposals.
 * These API are NOT stable and subject to change. They are only available in the Insiders
 * distribution and CANNOT be used in published extensions.
 *
 * To test these API in local environment:
 * - Use Insiders release of VS Code.
 * - Add `"enableProposedApi": true` to your package.json.
 * - Copy this file to your project.
 */

declare module "vscode" {
  //#region Custom editor https://github.com/microsoft/vscode/issues/77131

  /**
   * Implements the editing functionality of a custom editor.
   *
   * This delegate is how custom editors hook into standard VS Code operations such as save and undo. The delegate
   * is also how custom editors notify VS Code that an edit has taken place.
   *
   * @param EditType Type of edits used for the documents this delegate handles.
   */
  interface CustomEditorEditingDelegate<EditType = unknown> {
    /**
     * Signal that an edit has occurred inside a custom editor.
     *
     * This event must be fired by your extension whenever an edit happens in a custom editor. An edit can be
     * anything from changing some text, to cropping an image, to reordering a list.  Your extension is free to
     * define what an edit is and what data is stored on each edit.
     *
     * VS Code uses edits to determine if a custom editor is dirty or not. VS Code also passes the edit objects back
     * to your extension when triggers undo, redo, or revert (using the `undoEdits`, `applyEdits`, and `revert`
     * methods of `CustomEditorEditingDelegate`)
     */
    readonly onDidEdit: Event<CustomDocumentEditEvent<EditType>>;

    /**
     * Save the resource for a custom editor.
     *
     * This method is invoked by VS Code when the user saves a custom editor. This can happen when the user
     * triggers save while the custom editor is active, by commands such as `save all`, or by auto save if enabled.
     *
     * To implement `save`, the delegate must persist the custom editor. This usually means writing the
     * file data for the custom document to disk. After `save` completes, any associated editor instances will
     * no longer be marked as dirty.
     *
     * @param document Document to save.
     * @param cancellation Token that signals the save is no longer required (for example, if another save was triggered).
     *
     * @return Thenable signaling that saving has completed.
     */
    save(document: CustomDocument<EditType>, cancellation: CancellationToken): Thenable<void>;

    /**
     * Save the resource for a custom editor to a different location.
     *
     * This method is invoked by VS Code when the user triggers `save as` on a custom editor.
     *
     * To implement `saveAs`, the delegate must persist the custom editor to `targetResource`. The
     * existing editor will remain open after `saveAs` completes.
     *
     * @param document Document to save.
     * @param targetResource Location to save to.
     * @param cancellation Token that signals the save is no longer required.
     *
     * @return Thenable signaling that saving has completed.
     */
    saveAs(document: CustomDocument<EditType>, targetResource: Uri, cancellation: CancellationToken): Thenable<void>;

    /**
     * Apply a list of edits to a custom editor.
     *
     * This method is invoked by VS Code when the user triggers `redo` in a custom editor.
     *
     * To implement `applyEdits`, the delegate must make sure all editor instances (webviews) for `document`
     * are updated to render the document's new state (that is, every webview must be updated to show the document
     * after applying `edits` to it).
     *
     * Note that `applyEdits` not invoked when `onDidEdit` is fired by your extension because `onDidEdit` implies
     * that your extension has also updated its editor instances (webviews) to reflect the edit that just occurred.
     *
     * @param document Document to apply edits to.
     * @param redoneEdits Array of edits that were redone. Sorted from oldest to most recent. Use [`document.appliedEdits`](#CustomDocument.appliedEdits)
     * to get the full set of edits applied to the file (when `applyEdits` is called `appliedEdits` will already include
     * the newly applied edit at the end).
     *
     * @return Thenable signaling that the change has completed.
     */
    applyEdits(document: CustomDocument<EditType>, redoneEdits: ReadonlyArray<EditType>): Thenable<void>;

    /**
     * Undo a list of edits to a custom editor.
     *
     * This method is invoked by VS Code when the user triggers `undo` in a custom editor.
     *
     * To implement `undoEdits`, the delegate must make sure all editor instances (webviews) for `document`
     * are updated to render the document's new state (that is, every webview must be updated to show the document
     * after undoing `edits` from it).
     *
     * @param document Document to undo edits from.
     * @param undoneEdits Array of undone edits. Sorted from most recent to oldest. Use [`document.appliedEdits`](#CustomDocument.appliedEdits)
     * to get the full set of edits applied to the file (when `undoEdits` is called, `appliedEdits` will already include
     * have the undone edits removed).
     *
     * @return Thenable signaling that the change has completed.
     */
    undoEdits(document: CustomDocument<EditType>, undoneEdits: ReadonlyArray<EditType>): Thenable<void>;

    /**
     * Revert a custom editor to its last saved state.
     *
     * This method is invoked by VS Code when the user triggers `File: Revert File` in a custom editor. (Note that
     * this is only used using VS Code's `File: Revert File` command and not on a `git revert` of the file).
     *
     * To implement `revert`, the delegate must make sure all editor instances (webviews) for `document`
     * are displaying the document in the same state is saved in. This usually means reloading the file from the
     * workspace.
     *
     * During `revert`, your extension should also clear any backups for the custom editor. Backups are only needed
     * when there is a difference between an editor's state in VS Code and its save state on disk.
     *
     * @param document Document to revert.
     * @param revert Object with added or removed edits to get back to the saved state. Use [`document.appliedEdits`](#CustomDocument.appliedEdits)
     * to get the full set of edits applied to the file (when `revet` is called, `appliedEdits` will already have
     * removed any edits undone by the revert and added any edits applied by the revert).
     *
     * @return Thenable signaling that the change has completed.
     */
    revert(document: CustomDocument<EditType>, revert: CustomDocumentRevert<EditType>): Thenable<void>;

    /**
     * Back up the resource in its current state.
     *
     * Backups are used for hot exit and to prevent data loss. Your `backup` method should persist the resource in
     * its current state, i.e. with the edits applied. Most commonly this means saving the resource to disk in
     * the `ExtensionContext.storagePath`. When VS Code reloads and your custom editor is opened for a resource,
     * your extension should first check to see if any backups exist for the resource. If there is a backup, your
     * extension should load the file contents from there instead of from the resource in the workspace.
     *
     * `backup` is triggered whenever an edit it made. Calls to `backup` are debounced so that if multiple edits are
     * made in quick succession, `backup` is only triggered after the last one. `backup` is not invoked when
     * `auto save` is enabled (since auto save already persists resource ).
     *
     * @param document Document to backup.
     * @param cancellation Token that signals the current backup since a new backup is coming in. It is up to your
     * extension to decided how to respond to cancellation. If for example your extension is backing up a large file
     * in an operation that takes time to complete, your extension may decide to finish the ongoing backup rather
     * than cancelling it to ensure that VS Code has some valid backup.
     */
    backup(document: CustomDocument<EditType>, cancellation: CancellationToken): Thenable<void>;
  }

  /**
   * Event triggered by extensions to signal to VS Code that an edit has occurred on a `CustomDocument`.
   *
   * @param EditType Type of edits used for the document.
   */
  interface CustomDocumentEditEvent<EditType = unknown> {
    /**
     * Document the edit is for.
     */
    readonly document: CustomDocument<EditType>;

    /**
     * Object that describes the edit.
     *
     * Edit objects are controlled entirely by your extension. Your extension should store whatever information it
     * needs to on the edit to understand what type of edit was made, how to render that edit, and how to save that
     * edit to disk.
     *
     * Edit objects are passed back to your extension in `CustomEditorEditingDelegate.undoEdits`,
     * `CustomEditorEditingDelegate.applyEdits`, and `CustomEditorEditingDelegate.revert`. They can also be accessed
     * using [`CustomDocument.appliedEdits`](#CustomDocument.appliedEdits) and [`CustomDocument.savedEdits`](#CustomDocument.savedEdits).
     */
    readonly edit: EditType;

    /**
     * Display name describing the edit.
     */
    readonly label?: string;
  }

  /**
   * Delta for edits undone/redone while reverting for a `CustomDocument`.
   *
   * @param EditType Type of edits used for the document being reverted.
   */
  interface CustomDocumentRevert<EditType = unknown> {
    /**
     * List of edits that were undone to get the document back to its on disk state.
     */
    readonly undoneEdits: ReadonlyArray<EditType>;

    /**
     * List of edits that were reapplied to get the document back to its on disk state.
     */
    readonly appliedEdits: ReadonlyArray<EditType>;
  }

  /**
   * Represents a custom document used by a [`CustomEditorProvider`](#CustomEditorProvider).
   *
   * Custom documents are only used within a given `CustomEditorProvider`. The lifecycle of a `CustomDocument` is
   * managed by VS Code. When no more references remain to a `CustomDocument`, it is disposed of.
   *
   * @param EditType Type of edits used in this document.
   */
  class CustomDocument<EditType = unknown> {
    /**
     * The associated uri for this document.
     */
    public readonly uri: Uri;

    /**
     * Is this document representing an untitled file which has never been saved yet.
     */
    public readonly isUntitled: boolean;

    /**
     * The version number of this document (it will strictly increase after each
     * change, including undo/redo).
     */
    public readonly version: number;

    /**
     * `true` if there are unpersisted changes.
     */
    public readonly isDirty: boolean;

    /**
     * List of edits from document open to the document's current state.
     *
     * `appliedEdits` returns a copy of the edit stack at the current point in time. Your extension should always
     * use `CustomDocument.appliedEdits` to check the edit stack instead of holding onto a reference to `appliedEdits`.
     */
    public readonly appliedEdits: ReadonlyArray<EditType>;

    /**
     * List of edits from document open to the document's last saved point.
     *
     * The save point will be behind `appliedEdits` if the user saves and then continues editing,
     * or in front of the last entry in `appliedEdits` if the user saves and then hits undo.
     *
     * `savedEdits` returns a copy of the edit stack at the current point in time. Your extension should always
     * use `CustomDocument.savedEdits` to check the edit stack instead of holding onto a reference to `savedEdits`.
     */
    public readonly savedEdits: ReadonlyArray<EditType>;

    /**
     * `true` if the document has been closed. A closed document isn't synchronized anymore
     * and won't be reused when the same resource is opened again.
     */
    public readonly isClosed: boolean;

    /**
     * Event fired when there are no more references to the `CustomDocument`.
     *
     * This happens when all custom editors for the document have been closed. Once a `CustomDocument` is disposed,
     * it will not be reused when the same resource is opened again.
     */
    public readonly onDidDispose: Event<void>;

    /**
     * @param uri The associated resource for this document.
     */
    constructor(uri: Uri);
  }

  /**
   * Provider for custom editors that use a custom document model.
   *
   * Custom editors use [`CustomDocument`](#CustomDocument) as their document model instead of a [`TextDocument`](#TextDocument).
   * This gives extensions full control over actions such as edit, save, and backup.
   *
   * You should use this type of custom editor when dealing with binary files or more complex scenarios. For simple
   * text based documents, use [`CustomTextEditorProvider`](#CustomTextEditorProvider) instead.
   *
   * @param EditType Type of edits used by the editors of this provider.
   */
  export interface CustomEditorProvider<EditType = unknown> {
    /**
     * Defines the editing capability of the provider.
     *
     * When not provided, editors for this provider are considered readonly.
     */
    readonly editingDelegate?: CustomEditorEditingDelegate<EditType>;

    /**
     * Create a new document for a given resource.
     *
     * `openCustomDocument` is called when the first editor for a given resource is opened, and the resolve document
     * is passed to `resolveCustomEditor`. The resolved `CustomDocument` is re-used for subsequent editor opens.
     * If all editors for a given resource are closed, the `CustomDocument` is disposed of. Opening an editor at
     * this point will trigger another call to `openCustomDocument`.
     *
     * @param uri Uri of the document to open.
     * @param token A cancellation token that indicates the result is no longer needed.
     *
     * @return The custom document.
     */
    openCustomDocument(
      uri: Uri,
      token: CancellationToken
    ): Thenable<CustomDocument<EditType>> | CustomDocument<EditType>;

    /**
     * Resolve a custom editor for a given resource.
     *
     * This is called whenever the user opens a new editor for this `CustomEditorProvider`.
     *
     * To resolve a custom editor, the provider must fill in its initial html content and hook up all
     * the event listeners it is interested it. The provider can also hold onto the `WebviewPanel` to use later,
     * for example in a command. See [`WebviewPanel`](#WebviewPanel) for additional details.
     *
     * @param document Document for the resource being resolved.
     * @param webviewPanel Webview to resolve.
     * @param token A cancellation token that indicates the result is no longer needed.
     *
     * @return Optional thenable indicating that the custom editor has been resolved.
     */
    resolveCustomEditor(
      document: CustomDocument<EditType>,
      webviewPanel: WebviewPanel,
      token: CancellationToken
    ): Thenable<void> | void;
  }

  namespace window {
    /**
     * Temporary overload for `registerCustomEditorProvider` that takes a `CustomEditorProvider`.
     */
    export function registerCustomEditorProvider2(
      viewType: string,
      provider: CustomEditorProvider,
      options?: {
        readonly webviewOptions?: WebviewPanelOptions;
      }
    ): Disposable;
  }

  // #endregion

  //#region Custom editor move https://github.com/microsoft/vscode/issues/86146

  // TODO: Also for custom editor

  export interface CustomTextEditorProvider {
    /**
     * Handle when the underlying resource for a custom editor is renamed.
     *
     * This allows the webview for the editor be preserved throughout the rename. If this method is not implemented,
     * VS Code will destory the previous custom editor and create a replacement one.
     *
     * @param newDocument New text document to use for the custom editor.
     * @param existingWebviewPanel Webview panel for the custom editor.
     * @param token A cancellation token that indicates the result is no longer needed.
     *
     * @return Thenable indicating that the webview editor has been moved.
     */
    moveCustomTextEditor?(
      newDocument: TextDocument,
      existingWebviewPanel: WebviewPanel,
      token: CancellationToken
    ): Thenable<void>;
  }

  //#endregion
}
