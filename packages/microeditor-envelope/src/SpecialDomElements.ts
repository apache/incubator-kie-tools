export class SpecialDomElements {
  public readonly loadingScreenContainer: HTMLElement;

  constructor() {
    const loadingScreenContainer = document.getElementById("loading-screen");
    if (!loadingScreenContainer) {
      throw new Error("LoadingScreen container was not found");
    }

    this.loadingScreenContainer = loadingScreenContainer!;
  }
}
