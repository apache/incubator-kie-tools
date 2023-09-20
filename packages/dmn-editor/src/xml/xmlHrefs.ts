export function buildXmlHref({ namespace, id }: { namespace?: string; id: string }) {
  return `${namespace ?? ""}#${id}`;
}

export type XmlHref = {
  namespace: string | undefined;
  id: string;
};

export function parseXmlHref(href: string): XmlHref {
  const split = href.split("#");

  if (split.length <= 1) {
    return { namespace: undefined, id: split[0] };
  }

  if (split.length > 2) {
    throw new Error(`XML URI can't have hashes (#) on neither the namespace or the id. Alledged URI: '${href}'`);
  }

  return { namespace: split[0] ? split[0] : undefined, id: split[1] };
}
