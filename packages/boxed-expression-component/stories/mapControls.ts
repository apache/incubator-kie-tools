export function objToMap<K extends string, V>(obj: Record<K, V>): Map<K, V> {
  const n = new Map<K, V>();
  for (const p in obj) {
    n.set(p, obj[p]);
  }
  return n;
}

export function mapToObj<K extends string, V>(map: Map<K, V>) {
  const n: Record<K, V> = {} as any;
  map.forEach((value, key) => {
    n[key] = value;
  });
  return n;
}
