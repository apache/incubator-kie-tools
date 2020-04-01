import * as React from "react";
import { useEffect, useState } from "react";

interface Props {
  label: string;
}

export function AnimatedTripleDotLabel(props: Props) {
  const [dots, setDots] = useState("");

  useEffect(() => {
    const timeout = setTimeout(() => {
      if (dots.length === 3) {
        setDots("")
      } else {
        setDots(dots + ".")
      }
    }, 1000);
    return () => {
      clearTimeout(timeout);
    };
  }, [dots]);

  return <span>{props.label + dots}</span>;
}
