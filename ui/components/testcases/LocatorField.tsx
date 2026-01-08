"use client";

import React from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import type { LocatorDTO } from "@/types/models";

export default function LocatorField(props: {
  value: LocatorDTO | null;
  onChange: (v: LocatorDTO | null) => void;
  onPickClick: () => void;
  disabled?: boolean;
  showLabels?: boolean; // default true
}) {
  const { value, onChange, onPickClick, disabled, showLabels = true } = props;

  const typeVal = value?.type ?? "";
  const valueVal = value?.value ?? "";

  return (
    <>
      {/* locator.type */}
      <div className="space-y-1">
        {showLabels && (
          <div className="text-xs text-muted-foreground">locator.type</div>
        )}
        <Input
          value={typeVal}
          onChange={(e) => onChange({ type: e.target.value, value: valueVal })}
          disabled={disabled}
          placeholder="id / css / xpath / accessibilityId ..."
        />
      </div>

      {/* locator.value + Pick */}
      <div className="space-y-1">
        {showLabels && (
          <div className="text-xs text-muted-foreground">locator.value</div>
        )}
        <div className="flex gap-2">
          <Input
            value={valueVal}
            onChange={(e) => onChange({ type: typeVal, value: e.target.value })}
            disabled={disabled}
            placeholder="..."
          />
          <Button
            type="button"
            variant="outline"
            onClick={onPickClick}
            disabled={disabled}
            className="shrink-0"
          >
            Pick
          </Button>
        </div>
      </div>
    </>
  );
}