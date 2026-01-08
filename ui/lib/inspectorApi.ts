import {
  InspectorCreateSessionRequest,
  InspectorSessionDTO,
  InspectorSnapshotDTO,
  InspectorSnapshotRequest,
  InspectorSuggestionsDTO,
  InspectorPickRequest,
  InspectorPickResponse,
  InspectorPlatform,
} from "@/types/models";

function baseUrlFor(platform: InspectorPlatform) {
  const p = platform.toUpperCase();
  if (p === "WEB") return process.env.NEXT_PUBLIC_WEB_AGENT_URL!;
  if (p === "MOBILE") return process.env.NEXT_PUBLIC_MOBILE_AGENT_URL!;
  return process.env.NEXT_PUBLIC_DESKTOP_AGENT_URL!;
}

async function http<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {}),
    },
    cache: "no-store",
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `HTTP ${res.status}`);
  }
  return res.json();
}

export const inspectorApi = {
  createSession: async (req: InspectorCreateSessionRequest) => {
    const base = baseUrlFor(req.platform);
    return http<InspectorSessionDTO>(`${base}/inspector/sessions`, {
      method: "POST",
      body: JSON.stringify(req),
    });
  },

  closeSession: async (platform: InspectorPlatform, sessionId: string) => {
    const base = baseUrlFor(platform);
    return http<void>(`${base}/inspector/sessions/${sessionId}`, {
      method: "DELETE",
    });
  },

  snapshot: async (
    platform: InspectorPlatform,
    sessionId: string,
    req?: InspectorSnapshotRequest
  ) => {
    const base = baseUrlFor(platform);
    return http<InspectorSnapshotDTO>(`${base}/inspector/sessions/${sessionId}/snapshot`, {
      method: "POST",
      body: JSON.stringify(req ?? { maxDepth: 8, maxChildrenPerNode: 40, maxNodes: 1500 }),
    });
  },

  suggestions: async (platform: InspectorPlatform, sessionId: string, nodeId: string) => {
    const base = baseUrlFor(platform);
    return http<InspectorSuggestionsDTO>(
      `${base}/inspector/sessions/${encodeURIComponent(sessionId)}/nodes/${encodeURIComponent(nodeId)}/suggestions`
    );
  },

  pick: async (platform: InspectorPlatform, sessionId: string, req: InspectorPickRequest) => {
    const base = baseUrlFor(platform);
    return http<InspectorPickResponse>(`${base}/inspector/sessions/${sessionId}/pick`, {
      method: "POST",
      body: JSON.stringify(req),
    });
  },
};