export function buildXmlHref({ namespace, id }: { namespace?: string; id: string }) {
  return `${namespace ?? ""}#${id}`;
}
