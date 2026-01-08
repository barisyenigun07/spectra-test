"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { inspectorApi } from "@/lib/inspectorApi";
import { InspectorPlatform, InspectorSessionDTO } from "@/types/models";

type EnsureSessionArgs = {
  config?: Record<string, any>;
  initialUrl?: string;
};

export function useInspectorSession(platform: InspectorPlatform) {
  const [session, setSession] = useState<InspectorSessionDTO | null>(null);
  const [error, setError] = useState<string | null>(null);

  const ensureSession = async (args?: EnsureSessionArgs) => {
    if (session) return session;

    try {
      setError(null);
      const created = await inspectorApi.createSession({
        platform,
        config: args?.config ?? {},
        initialUrl: args?.initialUrl,
      });
      setSession(created);
      return created;
    } catch (e: any) {
      setError(e?.message ?? "Failed to create session");
      return null;
    }
  };

  const close = async () => {
    if (!session) return;
    try {
      await inspectorApi.closeSession(platform, session.sessionId);
    } finally {
      setSession(null);
    }
  };

  return { session, error, ensureSession, close };
}