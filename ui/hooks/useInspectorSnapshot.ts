"use client";

import { useCallback, useEffect, useState } from "react";
import { inspectorApi } from "@/lib/inspectorApi";
import { InspectorPlatform, InspectorSnapshotDTO } from "@/types/models";

export function useInspectorSnapshot(platform: InspectorPlatform, sessionId?: string, opts?: {
  autoRefreshMs?: number; // istersen
}) {
  const [data, setData] = useState<InspectorSnapshotDTO | null>(null);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const load = useCallback(async () => {
    if (!sessionId) return;
    try {
      setErr(null);
      setLoading(true);
      const snap = await inspectorApi.snapshot(platform, sessionId);
      setData(snap);
    } catch (e: any) {
      setErr(e?.message ?? "Failed to snapshot");
    } finally {
      setLoading(false);
    }
  }, [platform, sessionId]);

  useEffect(() => {
    if (!sessionId) return;
    load();

    const ms = opts?.autoRefreshMs ?? 0;
    if (ms > 0) {
      const t = window.setInterval(load, ms);
      return () => window.clearInterval(t);
    }
  }, [sessionId, load, opts?.autoRefreshMs]);

  return { data, loading, err, reload: load };
}