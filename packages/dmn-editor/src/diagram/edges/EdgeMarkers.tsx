import * as React from "react";

export function EdgeMarkers() {
  return (
    <svg style={{ position: "absolute", top: 0, left: 0 }}>
      <defs>
        <marker
          id="closed-circle-at-center"
          viewBox="0 0 10 10"
          refX={5}
          refY={5}
          markerWidth="8"
          markerHeight="8"
          orient="auto-start-reverse"
        >
          <circle cx="5" cy="5" r="5" fill="black" />
        </marker>
        <marker
          id="closed-circle-at-border"
          viewBox="0 0 10 10"
          refX={10}
          refY={5}
          markerWidth="8"
          markerHeight="8"
          orient="auto-start-reverse"
        >
          <circle cx="5" cy="5" r="5" fill="black" />
        </marker>
        <marker
          id="closed-arrow"
          viewBox="0 0 10 10"
          refX={10}
          refY={5}
          markerWidth="8"
          markerHeight="8"
          orient="auto-start-reverse"
        >
          <path d="M 0 0 L 10 5 L 0 10 z" stroke="black" />
        </marker>
        <marker
          id="open-arrow"
          viewBox="0 0 10 10"
          refX={10}
          refY={5}
          markerWidth="8"
          markerHeight="8"
          orient="auto-start-reverse"
        >
          <path d="M 0,0 L 10,5 M 10,5 L 0,10" stroke="black" strokeWidth={1} />
        </marker>
      </defs>
    </svg>
  );
}
