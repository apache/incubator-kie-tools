function main() {
  if ("BroadcastChannel" in window) {
    return;
  }

  const subscriptions = new Map<string, Set<any>>();

  (window as any).BroadcastChannel = class BroadcastChannel {
    public onmessage: ((this: BroadcastChannel, ev: MessageEvent) => any) | null;
    public onmessageerror: ((this: BroadcastChannel, ev: MessageEvent) => any) | null;

    constructor(private readonly name: string) {
      const subscription = subscriptions.get(name);
      if (!subscription) {
        subscriptions.set(name, new Set());
      }

      subscriptions.get(name)!.add(this);
    }

    public postMessage(message: any): void {
      //FIXME: This could be one message per `setTimeout`, but it's fine for now.
      setTimeout(() => {
        subscriptions.get(this.name)!.forEach((bc) => {
          if (bc === this) {
            return;
          }

          bc.onmessage?.(message);
        });
      }, 0);
    }

    public close(): void {
      subscriptions.get(this.name)!.delete(this);
    }
  };
}

main();
