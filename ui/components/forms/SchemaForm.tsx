"use client";

import React from "react";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";

import type { FieldDef, SchemaDef } from "@/types/schema";
import { Combobox } from "@/components/ui/combobox";

type Props = {
  schema: SchemaDef;
  value: Record<string, any>;
  onChange: (next: Record<string, any>) => void;
};

function setField(obj: Record<string, any>, key: string, v: any) {
  return { ...obj, [key]: v };
}

function shouldShowField(field: FieldDef, values: Record<string, any>) {
  const c = field.condition;
  if (!c) return true;

  const depVal = values?.[c.dependsOn];
  const toArr = (x: any | any[] | undefined) => (Array.isArray(x) ? x : x == null ? [] : [x]);

  if (c.equals !== undefined) {
    const allowed = toArr(c.equals);
    return allowed.some((v) => v === depVal);
  }

  if (c.notEquals !== undefined) {
    const denied = toArr(c.notEquals);
    return !denied.some((v) => v === depVal);
  }

  return true;
}

function pruneToSchema(schema: { fields: { key: string }[] }, obj: Record<string, any>) {
  const allowed = new Set(schema.fields.map((f) => f.key));
  const next: Record<string, any> = {};
  for (const [k, v] of Object.entries(obj ?? {})) {
    if (allowed.has(k)) next[k] = v;
  }
  return next;
}


function pruneToVisibleFields(schema: any, obj: Record<string, any>) {
  const next = { ...(obj ?? {}) };
  for (const f of schema.fields) {
    if (!shouldShowField(f, next)) {
      delete next[f.key];
    }
  }
  return next;
}

export default function SchemaForm({ schema, value, onChange }: Props) {
  const renderField = (f: FieldDef) => {
    // ✅ conditional render burada devreye giriyor
    if (!shouldShowField(f, value)) return null;

    const v = value?.[f.key];

    if (f.type === "boolean") {
      return (
        <label key={f.key} className="flex items-center gap-2 text-sm">
          <input
            type="checkbox"
            checked={Boolean(v ?? f.defaultValue ?? false)}
            onChange={(e) => onChange(setField(value, f.key, e.target.checked))}
          />
          <span>{f.label}</span>
        </label>
      );
    }

    if (f.type === "select") {
      return (
        <div key={f.key} className="space-y-1">
          <div className="text-xs text-muted-foreground">
            {f.label} {f.required ? "*" : ""}
          </div>

          {/* ✅ shadcn combobox */}
          <Combobox
            value={String(v ?? f.defaultValue ?? "")}
            options={(f.options ?? []).map((o) => ({ label: o.label, value: o.value }))}
            placeholder={f.placeholder ?? "Select…"}
            onChange={(next) => onChange(setField(value, f.key, next))}
          />

          {f.help && <div className="text-[11px] text-muted-foreground">{f.help}</div>}
        </div>
      );
    }

    if (f.type === "json") {
      const text = typeof v === "string" ? v : JSON.stringify(v ?? f.defaultValue ?? {}, null, 2);
      return (
        <div key={f.key} className="space-y-1">
          <div className="text-xs text-muted-foreground">
            {f.label} {f.required ? "*" : ""}
          </div>
          <Textarea
            value={text}
            onChange={(e) => onChange(setField(value, f.key, e.target.value))}
            className="min-h-[100px] font-mono text-xs"
            placeholder={f.placeholder}
          />
          {f.help && <div className="text-[11px] text-muted-foreground">{f.help}</div>}
        </div>
      );
    }

    // string / number
    return (
      <div key={f.key} className="space-y-1">
        <div className="text-xs text-muted-foreground">
          {f.label} {f.required ? "*" : ""}
        </div>
        <Input
          type={f.type === "number" ? "number" : "text"}
          value={v ?? f.defaultValue ?? ""}
          placeholder={f.placeholder}
          onChange={(e) => {
            const raw = e.target.value;
            const next = f.type === "number" ? (raw === "" ? "" : Number(raw)) : raw;
            onChange(setField(value, f.key, next));
          }}
        />
        {f.help && <div className="text-[11px] text-muted-foreground">{f.help}</div>}
      </div>
    );
  };

  return <div className="space-y-3">{schema.fields.map(renderField)}</div>;
}