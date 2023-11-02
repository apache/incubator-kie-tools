export function getNsDeclarationPropName({
  namespace,
  atInstanceNs,
  fallingBackToNs,
}: {
  namespace: string;
  atInstanceNs: Map<string, string>;
  fallingBackToNs: Map<string, string>;
}): "@_xmlns" | `@_xmlns:${string}` {
  let instanceNsKey = atInstanceNs.get(namespace);
  if (instanceNsKey === undefined) {
    instanceNsKey = fallingBackToNs.get(namespace);
    if (instanceNsKey === undefined) {
      throw new Error(`DMN MARSHALLER: Can't find namespace declaration for '${namespace}'`);
    }
  }

  if (instanceNsKey === "") {
    return "@_xmlns";
  } else {
    return `@_xmlns:${instanceNsKey.split(":")[0]}`;
  }
}
