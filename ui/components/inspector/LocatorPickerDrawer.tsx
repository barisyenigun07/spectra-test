"use client";

import React, { useState, useEffect, useRef } from "react";
import { Sheet, SheetContent, SheetHeader, SheetTitle } from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { inspectorApi } from "@/lib/inspectorApi";
import { useInspectorSession } from "@/hooks/useInspectorSession";
import { useInspectorSnapshot } from "@/hooks/useInspectorSnapshot";
import { InspectorPlatform, LocatorDTO, UiNodeDTO, LocatorCandidateDTO } from "@/types/models";
import { toast } from "sonner";

// ---- helpers ----
function safePlatformEnum(p?: string): InspectorPlatform {
  const v = (p ?? "").trim().toLowerCase();
  if (v === "web") return "WEB";
  if (v === "mobile") return "MOBILE";
  if (v === "desktop") return "DESKTOP";
  // fallback (component patlamasın)
  return "WEB";
}

function NodeRow(props: {
  node: UiNodeDTO;
  depth: number;
  selectedId?: string | null;
  onSelect: (nodeId: string) => void;
  filter?: string;
}) {
  const { node, depth, selectedId, onSelect } = props;

  const label = `${node.type}${node.name ? ` — ${node.name}` : ""}`;

  return (
    <div
      className={`flex items-center justify-between rounded-md px-2 py-1 cursor-pointer hover:bg-muted ${
        selectedId === node.nodeId ? "bg-muted" : ""
      }`}
      style={{ paddingLeft: 8 + depth * 14 }}
      onClick={() => {
        console.log(node);
        onSelect(node.nodeId)
      }}
      title={node.nodeId}
    >
      <div className="min-w-0">
        <div className="text-xs font-medium truncate">{label || node.nodeId}</div>
        <div className="text-[11px] text-muted-foreground truncate">
          {Object.entries(node.attrs ?? {})
            .slice(0, 4)
            .map(([k, v]) => `${k}=${String(v)}`)
            .join(" · ")}
        </div>
      </div>
      <div className="text-[10px] text-muted-foreground ml-2 shrink-0">{node.nodeId}</div>
    </div>
  );
}

function NodeTree(props: {
  root: UiNodeDTO;
  selectedId?: string | null;
  onSelect: (nodeId: string) => void;
  filter?: string;
  maxRender?: number;
}) {
  const { root, selectedId, onSelect, maxRender = 2000 } = props;

  let counter = 0;
  const renderNode = (n: UiNodeDTO, depth: number): React.ReactNode => {
    if (counter++ > maxRender) return null;
    return (
      <div key={n.nodeId}>
        <pre className="text-[10px] p-2 bg-muted rounded max-h-[120px] overflow-auto">
          {JSON.stringify(root, null, 2)}
        </pre>
        <NodeRow node={n} depth={depth} selectedId={selectedId} onSelect={onSelect} filter={props.filter} />
        {(n.children ?? []).map((c) => renderNode(c, depth + 1))}
      </div>
    );
  };

  return <div className="space-y-1">{renderNode(root, 0)}</div>;
}

export default function LocatorPickerDrawer(props: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  platform: "web" | "mobile" | "desktop";
  onPick: (loc: LocatorDTO) => void;

  sessionConfig?: Record<string, any>;
  initialUrl?: string;
}) {
  const platformEnum = safePlatformEnum(props.platform);

  const { session, error: sessErr, ensureSession, close } = useInspectorSession(platformEnum);
  const { data: snap, loading: snapLoading, err: snapErr, reload: reloadSnap } =
    useInspectorSnapshot(platformEnum, session?.sessionId, { autoRefreshMs: 0 });

  const [filter, setFilter] = useState("");
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);
  const [busySug, setBusySug] = useState(false);
  const [cands, setCands] = useState<LocatorCandidateDTO[]>([]);

  const openedOnceRef = useRef(false);
  const sessionIdRef = useRef<string | null>(null);

  const root = snap?.root;
  const effectiveError = sessErr || snapErr;

  const openAndWarmup = async () => {
    const s = await ensureSession({
      config: props.sessionConfig ?? {},
      initialUrl: props.initialUrl,
    });

    if (!s) return;
    sessionIdRef.current = s.sessionId;
    await reloadSnap();
  };

  const onClose = async () => {
    await close();
    setSelectedNodeId(null);
    setCands([]);
    setFilter("");
  };

  const loadSuggestions = async (sessionId: string, nodeId: string) => {
    if (!nodeId || !nodeId.trim()) return;
    try {
      setBusySug(true);
      const res = await inspectorApi.suggestions(platformEnum, sessionId, nodeId);
      setCands(res.candidates ?? []);
    } catch (e: any) {
      toast.error("Failed to load suggestions", { description: e?.message ?? String(e) });
      setCands([]);
    } finally {
      setBusySug(false);
    }
};

const onSelectNode = async (nodeId: string) => {
  if (!nodeId || !nodeId.trim()) return;
  setSelectedNodeId(nodeId);
  const sid = sessionIdRef.current ?? session?.sessionId;
  if (!sid) {
    toast.error("No session yet", { description: "Wait a moment and try again." });
    return;
  }
  await loadSuggestions(sid, nodeId);
};

  useEffect(() => {
    if (props.open) {
      // drawer açıldı
      openedOnceRef.current = true;
      openAndWarmup();
    } else {
      // drawer kapandı (ve daha önce açıldıysa)
      if (openedOnceRef.current) onClose();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [props.open]);

  return (
    <Sheet
      open={props.open}
      onOpenChange={(v) => {
        props.onOpenChange(v);

        // ⚠️ IMPORTANT: async handler'ı swallow etme, try/catch ile sar
        (async () => {
          try {
            if (v) await openAndWarmup();
            else await onClose();
          } catch (e: any) {
            console.error("LocatorPickerDrawer open/close error:", e);
            toast.error("Inspector error", { description: e?.message ?? String(e) });
          }
        })();
      }}
    >
      <SheetContent side="right" className="w-full sm:max-w-[900px]">
        <SheetHeader>
          <SheetTitle>Inspector — {platformEnum}</SheetTitle>
        </SheetHeader>

        <div className="mt-4 grid grid-cols-1 lg:grid-cols-2 gap-4">
          {/* LEFT */}
          <Card className="min-h-[560px]">
            <CardContent className="pt-4 space-y-3">
              <div className="flex items-center gap-2">
                <Input
                  placeholder="Filter (tag, text, attrs)…"
                  value={filter}
                  onChange={(e) => setFilter(e.target.value)}
                />
                <Button variant="outline" onClick={reloadSnap} disabled={!session || snapLoading}>
                  {snapLoading ? "Refreshing…" : "Refresh"}
                </Button>
              </div>

              {effectiveError && <div className="text-sm text-red-600">{effectiveError}</div>}

              {!root ? (
                <div className="text-sm text-muted-foreground">
                  {session ? "No snapshot yet." : "No session. Open drawer to create session."}
                </div>
              ) : (
                <div className="h-[480px] overflow-auto rounded-md border p-2">
                  <div className="text-[11px] text-muted-foreground mb-2">
                    {snap?.pageTitle ? `Title: ${snap.pageTitle}` : ""}{" "}
                    {snap?.pageUrl ? `• URL: ${snap.pageUrl}` : ""}
                  </div>
                  <NodeTree root={root} selectedId={selectedNodeId} onSelect={onSelectNode} filter={filter} />
                </div>
              )}
            </CardContent>
          </Card>

          {/* RIGHT */}
          <Card className="min-h-[560px]">
            <CardContent className="pt-4 space-y-3">
              <div className="text-sm font-medium">Locator Suggestions</div>

              <div className="text-xs text-muted-foreground">
                Selected node: <span className="font-mono">{selectedNodeId ?? "—"}</span>
              </div>

              {busySug ? (
                <div className="text-sm text-muted-foreground">Loading suggestions…</div>
              ) : cands.length === 0 ? (
                <div className="text-sm text-muted-foreground">Select a node to see locator candidates.</div>
              ) : (
                <div className="space-y-2">
                  {cands.map((c, idx) => (
                    <div key={idx} className="rounded-md border p-3">
                      <div className="flex items-start justify-between gap-3">
                        <div className="min-w-0">
                          <div className="text-xs font-medium">
                            {c.locator.type} <span className="text-muted-foreground">score={c.score}</span>
                          </div>
                          <div className="mt-1 text-xs font-mono break-all">{c.locator.value}</div>
                          {c.note && <div className="mt-1 text-[11px] text-muted-foreground">{c.note}</div>}
                        </div>

                        <Button
                          size="sm"
                          onClick={async () => {
                            try {
                              props.onPick(c.locator);
                              props.onOpenChange(false);
                              await onClose(); // session’ı kapat (senin MVP akışın)
                            } catch (e: any) {
                              toast.error("Pick failed", { description: e?.message ?? String(e) });
                            }
                          }}
                        >
                          Use
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </SheetContent>
    </Sheet>
  );
}