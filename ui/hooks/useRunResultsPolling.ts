"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { api } from "@/lib/api";
import { TestCaseResultDTO } from "@/types/models";


export function useRunResultsPolling(testCaseId: number, opts?: {
  runningIntervalMs?: number;
  idleIntervalMs?: number; // null gibi davranmak için 0 verebilirsin
}) {
  const runningIntervalMs = opts?.runningIntervalMs ?? 1500;
  const idleIntervalMs = opts?.idleIntervalMs ?? 0;

  const [results, setResults] = useState<TestCaseResultDTO[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const timerRef = useRef<number | null>(null);

  const latest = useMemo(() => (results && results.length ? results[0] : null), [results]);
  const isRunning = latest?.status === "RUNNING";

  const load = async () => {
    try {
      setError(null);
      if (!results) setLoading(true);
      const data = await api.getTestCaseRunResults(testCaseId); // bu endpoint’i api.ts’e ekleyeceğiz aşağıda
      setResults(data);
    } catch (e: any) {
      setError(e?.message ?? "Failed to load run results");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!testCaseId || Number.isNaN(testCaseId)) return;

    const schedule = (ms: number) => {
      if (timerRef.current) window.clearTimeout(timerRef.current);
      if (ms <= 0) return;
      timerRef.current = window.setTimeout(async () => {
        await load();
      }, ms);
    };

    // ilk load
    load();

    // her results değişiminde polling hızını ayarla
    const ms = isRunning ? runningIntervalMs : idleIntervalMs;
    schedule(ms);

    return () => {
      if (timerRef.current) window.clearTimeout(timerRef.current);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [testCaseId, isRunning, runningIntervalMs, idleIntervalMs]);

  return { results, latest, isRunning, loading, error, reload: load };
}