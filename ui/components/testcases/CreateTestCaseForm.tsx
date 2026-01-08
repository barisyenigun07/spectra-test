"use client";

import React, { useMemo, useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

import { api } from "@/lib/api";
import type { LocatorDTO, StepCreateDTO, TestCaseCreateRequest } from "@/types/models";

import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Combobox } from "@/components/ui/combobox";

import SchemaForm from "@/components/forms/SchemaForm";
import { PLATFORM_CONFIG_SCHEMAS } from "@/types/platformSchemas";
import { ACTION_PARAM_SCHEMAS } from "@/types/actionSchemas";

type Platform = "web" | "mobile" | "desktop";

const PLATFORM_OPTIONS = [
  { label: "Web", value: "web", keywords: "selenium browser" },
  { label: "Mobile", value: "mobile", keywords: "appium android ios" },
  { label: "Desktop", value: "desktop", keywords: "appium mac windows ldtp linux" },
] satisfies { label: string; value: string; keywords?: string }[];

const ACTIONS: Record<Platform, string[]> = {
  web: ["openUrl", "click", "doubleClick", "navigateBack", "navigateFront", "type", "assertText", "assertElement"],
  mobile: ["tap", "type", "swipe", "swipeUntilVisible", "assertText", "assertElement", "longPress", "navigateBack", "navigateFront"],
  desktop: [
    "assertElement",
    "assertIsChecked",
    "assertText",
    "click",
    "doubleClick",
    "dragAndDropByOffset",
    "dragAndDropToElement",
    "dragAndDropToLocation",
    "moveMouseByOffset",
    "moveMouseToElement",
    "moveMouseToLocation",
    "sendShortcut",
    "scroll",
    "type",
  ],
};

const LOCATOR_TYPE_OPTIONS: Record<Platform, { label: string; value: string; keywords?: string }[]> = {
  web: [
    { label: "css", value: "cssSelector", keywords: "css selector" },
    { label: "xpath", value: "xpath" },
    { label: "id", value: "id" },
    { label: "name", value: "name" },
    { label: "className", value: "className" },
    { label: "linkText", value: "linkText" },
    { label: "partialLinkText", value: "partialLinkText" },
    { label: "tagName", value: "tagName"},
    { label: "Custom…", value: "custom", keywords: "custom free text" },
  ],
  mobile: [
    { label: "accessibilityId", value: "accessibilityId", keywords: "a11y accessibility id" },
    { label: "id", value: "id" },
    { label: "xpath", value: "xpath" },
    { label: "iOSClassChain", value: "iOSClassChain", keywords: "ios class chain" },
    { label: "iOSNsPredicateString", value: "iOSNsPredicateString", keywords: "ios predicate" },
    { label: "uiAutomator", value: "androidUIAutomator", keywords: "android uiautomator" },
    { label: "Custom…", value: "custom", keywords: "custom free text" },
  ],
  desktop: [
    { label: "xpath", value: "xpath" },
    { label: "automationId", value: "automationId", keywords: "windows automation id" },
    { label: "name", value: "name" },
    { label: "className", value: "className" },
    { label: "Custom…", value: "custom", keywords: "custom free text" },
  ],
};

function safeJsonParse(text: string): { ok: true; value: any } | { ok: false; error: string } {
  try {
    return { ok: true, value: text.trim() ? JSON.parse(text) : {} };
  } catch (e: any) {
    return { ok: false, error: e?.message ?? "Invalid JSON" };
  }
}

function removeKeys(obj: Record<string, any>, keys: string[]) {
  const next = { ...(obj ?? {}) };
  for (const k of keys) delete next[k];
  return next;
}

function setDefaultIfEmpty(obj: Record<string, any>, key: string, value: any) {
  const cur = obj?.[key];
  const empty = cur === undefined || cur === null || cur === "";
  return empty ? { ...(obj ?? {}), [key]: value } : obj;
}

function defaultsFromSchema(p: Platform) {
  const schema = PLATFORM_CONFIG_SCHEMAS[p];
  const out: Record<string, any> = {};
  for (const f of schema.fields) {
    // boolean default false, number default "", diğerleri ""
    if (f.defaultValue !== undefined) out[f.key] = f.defaultValue;
    else if (f.type === "boolean") out[f.key] = false;
    else out[f.key] = "";
  }
  return out;
}

type StepState = {
  orderIndex: number;
  action: string;
  locatorType: string;
  locatorTypePreset?: string;
  locatorValue: string;
  params: Record<string, any>;
};

export default function CreateTestCaseForm() {
  const router = useRouter();

  const [targetPlatform, setTargetPlatform] = useState<Platform>("web");

  // config artık object olarak tutuluyor
  const [config, setConfig] = useState<Record<string, any>>(() => defaultsFromSchema("web"));

  const [steps, setSteps] = useState<StepState[]>([
    { orderIndex: 1, action: "click", locatorType: "id", locatorTypePreset: "id", locatorValue: "", params: {} },
  ]);

  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // platform değişince: config defaultlarını resetlemek istersen (MVP için iyi)
  const onChangePlatform = (p: Platform) => {
    setTargetPlatform(p);

    setConfig(defaultsFromSchema(p));

    //const nextConfig = pruneToVisibleFields(schema, pruneToSchema(schema, nextDefaults));
    //setConfig(nextConfig);

    setSteps((prev) =>
      prev.map((s) => {
        const allowed = ACTIONS[p];
        const nextAction = allowed.includes(s.action) ? s.action : allowed[0];
        return { ...s, action: nextAction, params: {} };
      })
    );
  };

  const addStep = () => {
    setSteps((prev) => [
      ...prev,
      {
        orderIndex: prev.length + 1,
        action: ACTIONS[targetPlatform][0],
        locatorType: "xpath",
        locatorTypePreset: "xpath",
        locatorValue: "",
        params: {},
      },
    ]);
  };

  const removeStep = (orderIndex: number) => {
    setSteps((prev) =>
      prev
        .filter((s) => s.orderIndex !== orderIndex)
        .map((s, idx) => ({ ...s, orderIndex: idx + 1 }))
    );
  };

  const updateStep = (orderIndex: number, patch: Partial<StepState>) => {
    setSteps((prev) => prev.map((s) => (s.orderIndex === orderIndex ? { ...s, ...patch } : s)));
  };

  const requestPreview: TestCaseCreateRequest | null = useMemo(() => {
    // config içinde json field varsa string kalmış olabilir → submitte parse edeceğiz, preview’de parse etmeye çalışalım
    const cfg: Record<string, any> = { ...config };

    // json field parse denemesi
    for (const f of PLATFORM_CONFIG_SCHEMAS[targetPlatform].fields) {
      if (f.type === "json") {
        const raw = cfg[f.key];
        if (typeof raw === "string") {
          const parsed = safeJsonParse(raw);
          if (!parsed.ok) return null;
          cfg[f.key] = parsed.value;
        }
      }
    }

    const stepDtos: StepCreateDTO[] = steps.map((s) => ({
      orderIndex: s.orderIndex,
      action: s.action,
      locator: s.locatorValue ? ({ type: s.locatorType, value: s.locatorValue } as LocatorDTO) : null,
      params: s.params ?? {},
    }));

    return {
      targetPlatform: targetPlatform,
      config: cfg,
      steps: stepDtos,
    } as any;
  }, [config, steps, targetPlatform]);

  const onSubmit = async () => {
    setError(null);

    // config json field parse + required kontrolü
    const schema = PLATFORM_CONFIG_SCHEMAS[targetPlatform];
    const cfg: Record<string, any> = { ...config };

    for (const f of schema.fields) {
      // required validation
      if (f.required) {
        const v = cfg[f.key];
        const empty = v === undefined || v === null || v === "";
        if (empty) {
          setError(`Config field required: ${f.label}`);
          return;
        }
      }

      if (f.type === "json") {
        const raw = cfg[f.key];
        if (typeof raw === "string") {
          const parsed = safeJsonParse(raw);
          if (!parsed.ok) {
            setError(`Config JSON field error (${f.label}): ${parsed.error}`);
            return;
          }
          cfg[f.key] = parsed.value;
        }
      }
    }

    // step params required validation
    for (const s of steps) {
      const ps = ACTION_PARAM_SCHEMAS[targetPlatform]?.[s.action];
      if (ps) {
        for (const f of ps.fields) {
          if (f.required) {
            const v = s.params?.[f.key];
            const empty = v === undefined || v === null || v === "";
            if (empty) {
              setError(`Step #${s.orderIndex} required param: ${f.label}`);
              return;
            }
          }
          if (f.type === "json") {
            const raw = s.params?.[f.key];
            if (typeof raw === "string") {
              const parsed = safeJsonParse(raw);
              if (!parsed.ok) {
                setError(`Step #${s.orderIndex} param JSON error (${f.label}): ${parsed.error}`);
                return;
              }
              s.params[f.key] = parsed.value;
            }
          }
        }
      }
    }

    const body: TestCaseCreateRequest = {
      targetPlatform: targetPlatform,
      config: cfg,
      steps: steps.map((s) => ({
        orderIndex: s.orderIndex,
        action: s.action,
        locator: s.locatorValue ? { type: s.locatorType, value: s.locatorValue } : null,
        params: s.params ?? {},
      })),
    } as any;

    try {
      setBusy(true);
      const created = await api.createTestCase(body);
      toast.success("Test case created");
      router.push(`/testcases/${created.testCaseId}`);
    } catch (e: any) {
      toast.error("Failed to create test case", { description: e?.message ?? String(e) });
      setError(e?.message ?? "Failed to create test case");
    } finally {
      setBusy(false);
    }
  };

  useEffect(() => {
    if (targetPlatform !== "mobile") return;

    const platformName = String(config?.platformName ?? "").toLowerCase();

    if (platformName === "android") {
      setConfig((prev) => ({
        ...removeKeys(prev, ["bundleId", "app"]),
        appPackage: prev.appPackage ?? "",
        appActivity: prev.appActivity ?? "",
        automationName: "UiAutomator2",
      }));
    }

    if (platformName === "ios") {
      setConfig((prev) => ({
        ...removeKeys(prev, ["appPackage", "appActivity"]),
        bundleId: prev.bundleId ?? "",
        app: prev.app ?? "",
        automationName: "XCUITest",
      }));
    }
  }, [targetPlatform, config?.platformName]);

  useEffect(() => {
    if (targetPlatform !== "desktop") return;

    const os = String(config?.os ?? "").toLowerCase();
    
    if (os === "mac") {
      setConfig((prev) => ({
        ...removeKeys(prev, ["app", "windowName", "appPath"]),
        bundleId: prev.bundleId ?? "",
      }));
    }

    if (os === "windows") {
      setConfig((prev) => ({
        ...removeKeys(prev, ["bundleId", "windowName", "appPath"]),
        app: prev.app ?? "",
      }));
    }

    if (os === "linux") {
      setConfig((prev) => ({
        ...removeKeys(prev, ["bundleId", "app", "appPath"]),
        windowName: prev.windowName ?? "",
      }));
    }
  }, [targetPlatform, config?.os]);

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <Card className="lg:col-span-2">
        <CardContent className="pt-6 space-y-6">
          {error && <div className="text-sm text-red-600">{error}</div>}

          {/* Platform */}
          <div className="space-y-2">
            <div className="text-sm font-medium">Target Platform</div>
            <Combobox
              value={targetPlatform}
              onChange={(v) => onChangePlatform(v as Platform)}
              options={PLATFORM_OPTIONS}
              placeholder="Choose platform…"
              searchPlaceholder="Search platform…"
            />
          </div>

          {/* Config schema */}
          <div className="space-y-2">
            <div className="text-sm font-medium">Config</div>
            <SchemaForm
              schema={PLATFORM_CONFIG_SCHEMAS[targetPlatform]}
              value={config}
              onChange={setConfig}
            />
          </div>

          {/* Steps */}
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <div className="text-sm font-medium">Steps</div>
              <Button variant="secondary" onClick={addStep}>
                Add step
              </Button>
            </div>

            <div className="space-y-4">
              {steps.map((s) => {
                const paramSchema = ACTION_PARAM_SCHEMAS[targetPlatform]?.[s.action];

                return (
                  <Card key={s.orderIndex}>
                    <CardContent className="pt-4 space-y-3">
                      <div className="flex items-center justify-between">
                        <div className="text-sm font-medium">Step #{s.orderIndex}</div>
                        <Button variant="destructive" onClick={() => removeStep(s.orderIndex)}>
                          Remove
                        </Button>
                      </div>

                      {/* action */}
                      <div className="space-y-1">
                        <div className="text-xs text-muted-foreground">action</div>
                        <Combobox
                          value={s.action}
                          onChange={(v) => updateStep(s.orderIndex, { action: v, params: {} })}
                          options={ACTIONS[targetPlatform].map((a) => ({ label: a, value: a }))}
                          placeholder="Choose action…"
                          searchPlaceholder="Search action…"
                        />
                      </div>

                      {/* locator */}
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                        <div className="space-y-1">
                          <div className="text-xs text-muted-foreground">locator.type</div>
                          <Combobox
                            value={s.locatorTypePreset ?? (LOCATOR_TYPE_OPTIONS[targetPlatform].some(o => o.value === s.locatorType) ? s.locatorType : "custom")}
                            onChange={(v) => {
                              const preset = String(v);

                              if (preset === "custom") {
                                // custom’a geçince mevcut locatorType’ı koruyalım (kullanıcı önceden yazmış olabilir)
                                updateStep(s.orderIndex, { locatorTypePreset: "custom" });
                              } else {
                                // preset seçildiyse gerçek locatorType’ı da ona eşitle
                                updateStep(s.orderIndex, { locatorTypePreset: preset, locatorType: preset });
                              }
                            }}
                            options={LOCATOR_TYPE_OPTIONS[targetPlatform]}
                            placeholder="Choose locator type…"
                            searchPlaceholder="Search locator type…"
                          />

                          {(s.locatorTypePreset === "custom") && (
                            <Input
                              value={s.locatorType ?? ""}
                              onChange={(e) => updateStep(s.orderIndex, { locatorType: e.target.value })}
                              placeholder="Enter custom locator type (e.g. accessibilityId, iosClassChain, predicate...)"
                            />
                          )}
                        </div>
                        <div className="space-y-1">
                          <div className="text-xs text-muted-foreground">locator.value</div>
                          <Input
                            value={s.locatorValue}
                            onChange={(e) => updateStep(s.orderIndex, { locatorValue: e.target.value })}
                            placeholder='//XCUIElementTypeStaticText[@value="4"]'
                          />
                        </div>
                      </div>

                      {/* params (only if schema exists) */}
                      {paramSchema ? (
                        <div className="space-y-2">
                          <div className="text-sm font-medium">Params</div>
                          <SchemaForm
                            schema={paramSchema}
                            value={s.params}
                            onChange={(next) => updateStep(s.orderIndex, { params: next })}
                          />
                        </div>
                      ) : (
                        <div className="text-xs text-muted-foreground">This action has no params.</div>
                      )}
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          </div>

          <div className="flex justify-end">
            <Button onClick={onSubmit} disabled={busy}>
              {busy ? "Creating…" : "Create Test Case"}
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Preview */}
      <Card className="lg:col-span-1">
        <CardContent className="pt-6 space-y-2">
          <div className="text-sm font-medium">Request Preview</div>
          <pre className="text-xs p-3 rounded-md bg-muted overflow-auto min-h-[200px]">
            {requestPreview ? JSON.stringify(requestPreview, null, 2) : "Invalid JSON in config/params"}
          </pre>
        </CardContent>
      </Card>
    </div>
  );
}