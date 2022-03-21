import * as vscode from "vscode";

export class RhhccAuthenticationStore {
  private _session: vscode.AuthenticationSession | undefined;
  private subscriptions = new Set<(session: vscode.AuthenticationSession | undefined) => void>();

  public get session() {
    return this._session;
  }

  public setSession(session: vscode.AuthenticationSession | undefined) {
    this._session = session;
    this.subscriptions.forEach((subscription) => subscription(session));
  }

  public subscribeToSessionChange(subscription: (session: vscode.AuthenticationSession | undefined) => any) {
    this.subscriptions.add(subscription);
    return subscription;
  }

  public unsubscribeToSessionChange(subscription: (session: vscode.AuthenticationSession | undefined) => any) {
    this.subscriptions.delete(subscription);
  }
}
