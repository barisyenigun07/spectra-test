"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { api } from "@/lib/api";
import { TestCaseResultDTO } from "@/types/models";

export function useRunResultsPolling(
  testCaseId: number,
  opts?: {
    runningIntervalMs?: number;
    idleIntervalMs?: number; // durdurmak için 0
    activeRunId?: number;    // URL'den gelen ?run=...
  }
) {
  const runningIntervalMs = opts?.runningIntervalMs ?? 1500;
  const idleIntervalMs = opts?.idleIntervalMs ?? 0;
  const activeRunId = opts?.activeRunId;

  const [results, setResults] = useState<TestCaseResultDTO[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const timerRef = useRef<number | null>(null);
  const stoppedRef = useRef(false);

  const latest = useMemo(
    () => (results && results.length ? results[0] : null),
    [results]
  );

  const selected = useMemo(() => {
    if (!results || results.length === 0) return null;
    if (!activeRunId) return latest;
    return results.find((r) => r.runId === activeRunId) ?? latest;
  }, [results, latest, activeRunId]);

  const selectedIsRunning = selected?.status === "RUNNING";
  const isRunning = selectedIsRunning; // dışarıya “seçili run running mi” diye döndürelim

  const clearTimer = () => {
    if (timerRef.current) window.clearTimeout(timerRef.current);
    timerRef.current = null;
  };

  const load = useCallback(async () => {
    try {
      setError(null);
      if (!results) setLoading(true);
      const data = await api.getTestCaseRunResults(testCaseId);
      setResults(data);
    } catch (e: any) {
      setError(e?.message ?? "Failed to load run results");
    } finally {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [testCaseId]); // results'ı dependency yapmıyoruz; load stable kalsın

  const scheduleNext = useCallback(
    (ms: number) => {
      clearTimer();
      if (ms <= 0) return;
      timerRef.current = window.setTimeout(() => {
        tick();
      }, ms);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    []
  );

  // tick: load + sonra tekrar schedule (asıl polling loop)
  const tick = useCallback(async () => {
    if (stoppedRef.current) return;
    await load();

    // load sonrası state async update olur; burada “en güncel” status'a ulaşmak için
    // bir sonraki effect çalışacak. Ama polling’in kopmaması için:
    // - running ise runningInterval
    // - değilse idleInterval
    const ms = selectedIsRunning ? runningIntervalMs : idleIntervalMs;
    scheduleNext(ms);
  }, [load, selectedIsRunning, runningIntervalMs, idleIntervalMs, scheduleNext]);

  useEffect(() => {
    if (!testCaseId || Number.isNaN(testCaseId)) return;

    stoppedRef.current = false;
    // ilk tick hemen çalışsın
    tick();

    return () => {
      stoppedRef.current = true;
      clearTimer();
    };
  }, [testCaseId, activeRunId, runningIntervalMs, idleIntervalMs, tick]);

  // activeRunId değişince selected hesaplanır; polling hızını da güncellemek için:
  useEffect(() => {
    if (!testCaseId || Number.isNaN(testCaseId)) return;
    const ms = selectedIsRunning ? runningIntervalMs : idleIntervalMs;
    scheduleNext(ms);
  }, [testCaseId, selectedIsRunning, runningIntervalMs, idleIntervalMs, scheduleNext]);

  return {
    results,
    latest,
    selected,        // panelde direkt kullanabilmen için
    isRunning,       // seçili run running mi
    loading,
    error,
    reload: load,
  };
}