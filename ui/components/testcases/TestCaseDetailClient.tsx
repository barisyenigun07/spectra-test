"use client";

import Link from "next/link";
import PageHeader from "@/components/PageHeader";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { useTestCase } from "@/hooks/useTestCase";
import { useRunResultsPolling } from "@/hooks/useRunResultsPolling";
import TestCaseDefinitionCard from "@/components/testcases/TestCaseDefinitionCard";
import RunResultsPanel from "@/components/testcases/RunResultsPanel";

export default function TestCaseDetailClient({ testCaseId, activeRunId }: { testCaseId: number; activeRunId?: number; }) {
  const { data: tc, loading: tcLoading, error: tcError, reload: reloadTc } = useTestCase(testCaseId);

  const {
    results,
    latest,
    isRunning,
    loading: resLoading,
    error: resError,
    reload: reloadResults,
  } = useRunResultsPolling(testCaseId, {
    runningIntervalMs: 1500,
    idleIntervalMs: 0, // koşu bitince polling durur
    activeRunId,
  });

  const selectedLatest = activeRunId && results?.length ? results.find((r) => r.runId === activeRunId) ?? latest : latest;

  const selectedIsRunning = selectedLatest?.status === "RUNNING" ? true : isRunning;

  if (!testCaseId || Number.isNaN(testCaseId)) {
    return (
      <div className="p-6">
        <Card><CardContent className="pt-6">Invalid testCaseId.</CardContent></Card>
      </div>
    );
  }

  return (
    <div className="p-6 space-y-6">
      <PageHeader
        title={`Test Case #${testCaseId}`}
        subtitle={tc ? `Platform: ${tc.targetPlatform}` : "Loading…"}
        primaryAction={{ label: "Back to list", href: "/testcases" }}
      />

      {(tcError || resError) && (
        <Card>
          <CardContent className="pt-6 text-sm text-red-600 space-y-2">
            {tcError && <div>TestCase error: {tcError}</div>}
            {resError && <div>Results error: {resError}</div>}
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => reloadTc()}>Reload TestCase</Button>
              <Button variant="outline" onClick={() => reloadResults()}>Reload Results</Button>
            </div>
          </CardContent>
        </Card>
      )}

      {tcLoading || !tc ? (
        <Card><CardContent className="pt-6 text-sm text-muted-foreground">Loading testcase…</CardContent></Card>
      ) : (
        <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
          <div className="space-y-6">
            <TestCaseDefinitionCard tc={tc} />
          </div>

          <div className="space-y-6">
            <RunResultsPanel
              testCaseId={testCaseId}
              results={results}
              latest={selectedLatest}
              isRunning={selectedIsRunning}
              reload={reloadResults}
              activeRunId={activeRunId}
            />

            {resLoading && (
              <div className="text-xs text-muted-foreground">
                Loading results…
              </div>
            )}

            <div className="text-xs text-muted-foreground">
              Tip: When a run is RUNNING, results poll every 1.5s. When finished, polling stops (MVP behavior).
            </div>
          </div>
        </div>
      )}
    </div>
  );
}