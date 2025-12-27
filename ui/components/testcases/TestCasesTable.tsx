"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { api } from "@/lib/api";
import { TestCaseDTO } from "@/lib/models";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";

export default function TestCasesTable() {
  const [items, setItems] = useState<TestCaseDTO[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [busyId, setBusyId] = useState<number | null>(null);

  const load = async () => {
    try {
      setError(null);
      const data = await api.getTestCases();
      setItems(data);
    } catch (e: any) {
      setError(e?.message ?? "Failed to load testcases");
    }
  };

  useEffect(() => {
    load();
  }, []);

  const onRun = async (id: number) => {
    try {
      setBusyId(id);
      await api.runTestCase(id);
      // MVP: sadece tetikledik. Detail sayfasında results polling olacak.
    } catch (e: any) {
      setError(e?.message ?? "Failed to run testcase");
    } finally {
      setBusyId(null);
    }
  };

  const onDelete = async (id: number) => {
    try {
      setBusyId(id);
      const res = await api.deleteTestCase(id);
      setItems((prev) => (prev ?? []).filter((x) => x.testCaseId !== res.deletedTestCaseId));
    } catch (e: any) {
      setError(e?.message ?? "Failed to delete testcase");
    } finally {
      setBusyId(null);
    }
  };

  return (
    <Card>
      <CardContent className="pt-6">
        {error && (
          <div className="mb-4 text-sm text-red-600">
            {error}
          </div>
        )}

        {!items ? (
          <div className="text-sm text-muted-foreground">Loading…</div>
        ) : items.length === 0 ? (
          <div className="text-sm text-muted-foreground">No test cases yet.</div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Platform</TableHead>
                <TableHead>Steps</TableHead>
                <TableHead>Updated</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>

            <TableBody>
              {items.map((tc) => (
                <TableRow key={tc.testCaseId}>
                  <TableCell className="font-medium">{tc.testCaseId}</TableCell>
                  <TableCell>{tc.targetPlatform}</TableCell>
                  <TableCell>{tc.steps?.length ?? 0}</TableCell>
                  <TableCell>{new Date(tc.updatedAt).toLocaleString()}</TableCell>
                  <TableCell className="text-right space-x-2">
                    <Button variant="secondary" asChild>
                      <Link href={`/testcases/${tc.testCaseId}`}>View</Link>
                    </Button>

                    <Button
                      onClick={() => onRun(tc.testCaseId)}
                      disabled={busyId === tc.testCaseId}
                    >
                      {busyId === tc.testCaseId ? "Running…" : "Run"}
                    </Button>

                    <Dialog>
                      <DialogTrigger asChild>
                        <Button variant="destructive" disabled={busyId === tc.testCaseId}>
                          Delete
                        </Button>
                      </DialogTrigger>
                      <DialogContent>
                        <DialogHeader>
                          <DialogTitle>Delete test case?</DialogTitle>
                        </DialogHeader>
                        <p className="text-sm text-muted-foreground">
                          This will remove the test case definition. Run history may also be lost depending on your DB cascade settings.
                        </p>
                        <DialogFooter>
                          <Button variant="secondary">Cancel</Button>
                          <Button variant="destructive" onClick={() => onDelete(tc.testCaseId)}>
                            Confirm delete
                          </Button>
                        </DialogFooter>
                      </DialogContent>
                    </Dialog>

                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}

        <div className="mt-4">
          <Button variant="outline" onClick={load}>Refresh</Button>
        </div>
      </CardContent>
    </Card>
  );
}