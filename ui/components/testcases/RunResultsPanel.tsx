"use client";

import { useState } from "react";
import { api } from "@/lib/api";
import { TestCaseResultDTO } from "@/types/models";
import StatusBadge from "@/components/StatusBadge";
import StepResultsTable from "@/components/testcases/StepResultsTable";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

export default function RunResultsPanel(props: {
  testCaseId: number;
  results: TestCaseResultDTO[] | null;
  latest: TestCaseResultDTO | null;
  isRunning: boolean;
  reload: () => Promise<void> | void;
  activeRunId?: number;
}) {
  const { testCaseId, results, latest, isRunning, reload, activeRunId } = props;
  const router = useRouter();

  const selectedRunId = activeRunId ?? latest?.runId;

  const selected = selectedRunId
  ? results?.find((r) => r.runId === selectedRunId) ?? latest
  : latest;

  const title = activeRunId ? "Selected Run" : "Latest Run";

  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const onRun = async () => {
    try {
      setErr(null);
      setBusy(true);
      const { runId } = await api.runTestCase(testCaseId);
      toast.success("Run started", { description: `runId=${runId}` });
      router.push(`/testcases/${testCaseId}?run=${runId}`);
      await Promise.resolve(reload());
    } catch (e: any) {
      setErr(e?.message ?? "Failed to run testcase");
      toast.error("Failed to run testcase", { description: e?.message });
    } finally {
      setBusy(false);
    }
  };

  const onSelectRun = (runId: number) => {
    router.push(`/testcases/${testCaseId}?run=${runId}`);
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardContent className="pt-6 flex flex-col gap-3">
          {err && <div className="text-sm text-red-600">{err}</div>}

          <div className="flex items-start justify-between gap-4">
            <div>
              <div className="text-sm font-medium">{title}</div>
              <div className="text-xs text-muted-foreground mt-1">
                {selected ? `runId=${selected.runId}` : "No runs yet."}
              </div>
            </div>

            <div className="flex items-center gap-2">
              <Button onClick={onRun} disabled={busy}>
                {busy ? "Triggering…" : "Run"}
              </Button>
              <Button variant="outline" onClick={() => reload()}>
                Refresh
              </Button>
            </div>
          </div>

          {selected && (
            <div className="flex flex-wrap items-center gap-3 text-sm">
              <StatusBadge status={selected.status} />
              <span className="text-muted-foreground">
                Duration: {selected.durationMillis} ms
              </span>
              <span className="text-muted-foreground">
                Started: {new Date(selected.startedAt).toLocaleString()}
              </span>
              {!isRunning && selected.finishedAt && (
                <span className="text-muted-foreground">
                  Finished: {new Date(selected.finishedAt).toLocaleString()}
                </span>
              )}
            </div>
          )}
        </CardContent>
      </Card>

      {selected?.stepResults ? (
        <StepResultsTable steps={selected.stepResults} />
      ) : (
        <Card>
          <CardContent className="pt-6 text-sm text-muted-foreground">
            No step results to show.
          </CardContent>
        </Card>
      )}

      {results && results.length > 1 && (
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm font-medium">Run History</div>
            <div className="mt-2 text-xs text-muted-foreground">
              Showing {results.length} runs (latest first).
            </div>

            <div className="mt-3 space-y-2">
              {results.map((r) => {
                const isSelected = r.runId === selectedRunId;
                return (
                  <button
                    key={r.runId}
                    type="button"
                    onClick={() => onSelectRun(r.runId)}
                    className={[
                      "w-full text-left flex items-center justify-between rounded-md border p-3",
                      isSelected ? "border-primary" : "",
                    ].join(" ")}
                  >
                    <div className="flex items-center gap-3">
                      <div className="text-sm font-medium">#{r.runId}</div>
                      <StatusBadge status={r.status} />
                      <div className="text-xs text-muted-foreground">
                        {new Date(r.startedAt).toLocaleString()}
                      </div>
                    </div>
                    <div className="text-xs text-muted-foreground">{r.durationMillis} ms</div>
                  </button>
                );
              })}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}