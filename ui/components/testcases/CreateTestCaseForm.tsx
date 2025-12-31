"use client";

import { api } from '@/lib/api';
import { StepCreateDTO, TestCaseCreateRequest } from '@/types/models';
import React, { useMemo, useState } from 'react'
import { Card, CardContent } from '../ui/card';
import { Input } from '../ui/input';
import { Textarea } from '../ui/textarea';
import { Button } from '../ui/button';

const ACTION_SUGGESTIONS = ["click", "doubleClick", "type", "wait", "assertText", "longPress", "hideKeyboard"];

function safeJsonParse(text: string): {ok: true; value: any} | {ok: false; error: string} {
    try {
        return { ok: true, value: text.trim() ? JSON.parse(text) : {} };
    } catch (e: any) {
        return { ok: false, error: e?.message ?? "Invalid JSON" };
    }
}

function CreateTestCaseForm() {
    const [targetPlatform, setTargetPlatform] = useState("web");
    const [configText, setConfigText] = useState("{}");
    const [error, setError] = useState<string | null>(null);
    const [busy, setBusy] = useState(false);

    const [steps, setSteps] = useState<Array<{
        orderIndex: number;
        action: string;
        locatorType: string;
        locatorValue: string;
        paramsText: string;
    }>>([
        { orderIndex: 1, action: "click", locatorType: "id", locatorValue: "", paramsText: "{}" },
    ])

    const requestPreview: TestCaseCreateRequest | null = useMemo(() => {
        const cfg = safeJsonParse(configText);
        if (!cfg.ok) return null;

        const stepDtos: StepCreateDTO[] = [];
        for (const s of steps) {
            const p = safeJsonParse(s.paramsText);
            if (!p.ok) return null;

            stepDtos.push({
                orderIndex: s.orderIndex,
                action: s.action,
                locator: s.locatorValue ? { type: s.locatorType, value: s.locatorValue }
                : null,
                params: p.value ?? {}
            });
        }

        return {
            targetPlatform,
            config: cfg.value ??  {},
            steps: stepDtos
        }


    }, [configText, steps, targetPlatform]);

    const addStep = () => {
        setSteps((prev) => [
            ...prev,
            {
                orderIndex: prev.length + 1,
                action: "click",
                locatorType: "id",
                locatorValue: "",
                paramsText: "{}"
            }
        ])
    };

    const removeStep = (orderIndex: number) => {
        setSteps((prev) => 
            prev
                .filter((s) => s.orderIndex !== orderIndex)
                .map((s, idx) => ({...s, orderIndex: idx + 1}))
        );
    };

    const updateStep = (orderIndex: number, patch: Partial<(typeof steps)[number]>) => {
        setSteps((prev) => prev.map((s) => (s.orderIndex === orderIndex ? {...s, ...patch} : s)));
    };

    const onSubmit = async () => {
        setError(null);
        const cfg = safeJsonParse(configText);
        if (!cfg.ok) return setError(`Config JSON error: ${cfg.error}`);

        const stepDtos: StepCreateDTO[] = [];
        for (const s of steps) {
            const p = safeJsonParse(s.paramsText);
            if (!p.ok) return setError(`Step #${s.orderIndex} params JSON error: ${p.error}`);

            stepDtos.push({
                orderIndex: s.orderIndex,
                action: s.action,
                locator: s.locatorValue ? { type: s.locatorType, value: s.locatorValue } : null,
                params: p.value ?? {}
            });
        }

        const body: TestCaseCreateRequest = {
            targetPlatform,
            config: cfg.value ?? {},
            steps: stepDtos
        };

        try {
            setBusy(true);
            const created = await api.createTestCase(body);
            setError(`Created TestCase #${created.testCaseId}`);
        } catch (e: any) {
            setError(e?.message ?? "Failed to create test case");
        } finally {
            setBusy(false);
        }
    }
    return (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <Card className="lg:col-span-2">
                <CardContent className="pt-6 space-y-6">
                    {error && <div className="text-sm text-red-600">{error}</div>}
                    <div className="space-y-2">
                        <div className="text-sm font-medium">Target Platform</div>
                        <Input value={targetPlatform} onChange={(e) => setTargetPlatform(e.target.value)}/>
                        <div className="text-xs text-muted-foreground">
                            Example: WEB / MOBILE / DESKTOP
                        </div>
                    </div>

                    <div className="space-y-2">
                        <div className="text-sm font-medium">Config (JSON)</div>
                        <Textarea value={configText} onChange={(e) => setConfigText(e.target.value)} className="min-h-[120px]"/>
                    </div>

                    <div className="space-y-3">
                        <div className="flex items-center justify-between">
                            <div className="text-sm font-medium">Steps</div>
                            <Button variant={"secondary"} onClick={addStep}>Add step</Button>
                        </div>

                        <div className="space-y-4">
                            {steps.map((s) => (
                                <Card key={s.orderIndex}>
                                    <CardContent className="pt-4 space-y-3">
                                        <div className="flex items-center justify-between">
                                            <div className="text-sm font-medium">Step #{s.orderIndex}</div>
                                            <Button variant={"destructive"} onClick={() => removeStep(s.orderIndex)}>
                                                Remove
                                            </Button>
                                        </div>

                                        <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                                            <div className="space-y-1">
                                                <div className="text-xs text-muted-foreground">action</div>
                                                <Input
                                                    value={s.action}
                                                    onChange={(e) => updateStep(s.orderIndex, { action: e.target.value })}
                                                    list="action-suggestions"
                                                />
                                            </div>

                                            <div className="space-y-1">
                                                <div className="text-xs text-muted-foreground">locator.type</div>
                                                <Input
                                                value={s.locatorType}
                                                onChange={(e) => updateStep(s.orderIndex, { locatorType: e.target.value })}
                                                />
                                            </div>

                                            <div className="space-y-1">
                                                <div className="text-xs text-muted-foreground">locator.value</div>
                                                <Input
                                                value={s.locatorValue}
                                                onChange={(e) => updateStep(s.orderIndex, { locatorValue: e.target.value })}
                                                />
                                            </div>
                                        </div>
                                        <div className="space-y-1">
                                            <div className="text-xs text-muted-foreground">params (JSON)</div>
                                            <Textarea
                                                value={s.paramsText}
                                                onChange={(e) => updateStep(s.orderIndex, { paramsText: e.target.value })}
                                                className="min-h-[90px]"
                                            />
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                        <datalist id="action-suggestions">
                            {ACTION_SUGGESTIONS.map((a) => <option key={a} value={a} />)}
                        </datalist>
                    </div>
                    <div className="flex justify-end">
                        <Button onClick={onSubmit} disabled={busy}>
                        {busy ? "Creating…" : "Create Test Case"}
                        </Button>
                    </div>
                </CardContent>
            </Card>
            <Card className="lg:col-span-1">
                <CardContent className="pt-6 space-y-2">
                <div className="text-sm font-medium">Request Preview</div>
                <pre className="text-xs p-3 rounded-md bg-muted overflow-auto min-h-[200px]">
                    {requestPreview ? JSON.stringify(requestPreview, null, 2) : "Invalid JSON in config/params"}
                </pre>
                <div className="text-xs text-muted-foreground">
                    This is exactly what will be sent to <code>/testcases</code>.
                </div>
                </CardContent>
            </Card>
        </div>
    )
}

export default CreateTestCaseForm