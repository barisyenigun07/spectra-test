"use client";

import { useState } from "react";
import { api } from "@/lib/api";
import { TestCaseResultDTO } from "@/types/models";
import StatusBadge from "@/components/StatusBadge";
import StepResultsTable from "@/components/testcases/StepResultsTable";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

export default function RunResultsPanel(props: {
  testCaseId: number;
  results: TestCaseResultDTO[] | null;
  latest: TestCaseResultDTO | null;
  isRunning: boolean;
  reload: () => Promise<void> | void;
}) {
  const { testCaseId, results, latest, isRunning, reload } = props;
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const onRun = async () => {
    try {
      setErr(null);
      setBusy(true);
      await api.runTestCase(testCaseId);
      // run tetikleyince hemen yenile (ve polling zaten devreye girer)
      await Promise.resolve(reload());
    } catch (e: any) {
      setErr(e?.message ?? "Failed to run testcase");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="space-y-4">
      <Card>
        <CardContent className="pt-6 flex flex-col gap-3">
          {err && <div className="text-sm text-red-600">{err}</div>}

          <div className="flex items-start justify-between gap-4">
            <div>
              <div className="text-sm font-medium">Latest Run</div>
              <div className="text-xs text-muted-foreground mt-1">
                {latest ? `runId=${latest.runId}` : "No runs yet."}
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

          {latest && (
            <div className="flex flex-wrap items-center gap-3 text-sm">
              <StatusBadge status={latest.status} />
              <span className="text-muted-foreground">
                Duration: {latest.durationMillis} ms
              </span>
              <span className="text-muted-foreground">
                Started: {new Date(latest.startedAt).toLocaleString()}
              </span>
              {!isRunning && latest.finishedAt && (
                <span className="text-muted-foreground">
                  Finished: {new Date(latest.finishedAt).toLocaleString()}
                </span>
              )}
            </div>
          )}
        </CardContent>
      </Card>

      {latest?.stepResults ? (
        <StepResultsTable steps={latest.stepResults} />
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
              Showing {results.length} runs (latest first). (We can add a dedicated run page later.)
            </div>

            <div className="mt-3 space-y-2">
              {results.map((r) => (
                <div key={r.runId} className="flex items-center justify-between rounded-md border p-3">
                  <div className="flex items-center gap-3">
                    <div className="text-sm font-medium">#{r.runId}</div>
                    <StatusBadge status={r.status} />
                    <div className="text-xs text-muted-foreground">
                      {new Date(r.startedAt).toLocaleString()}
                    </div>
                  </div>
                  <div className="text-xs text-muted-foreground">{r.durationMillis} ms</div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}