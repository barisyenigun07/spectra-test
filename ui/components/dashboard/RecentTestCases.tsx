// src/components/dashboard/RecentTestCases.tsx
"use client";

import Link from "next/link";
import { TestCaseDTO } from "@/types/models";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

export default function RecentTestCases(props: {
  items: TestCaseDTO[];
  loading: boolean;
  onRefresh: () => void;
}) {
  const { items, loading, onRefresh } = props;

  return (
    <Card>
      <CardContent className="pt-6 space-y-3">
        <div className="flex items-start justify-between gap-4">
          <div>
            <div className="text-sm font-medium">Recent Test Cases</div>
            <div className="text-xs text-muted-foreground">
              Last updated test cases (max 5)
            </div>
          </div>
          <Button variant="outline" size="sm" onClick={onRefresh}>
            Refresh
          </Button>
        </div>

        {loading ? (
          <div className="text-sm text-muted-foreground">Loading…</div>
        ) : items.length === 0 ? (
          <div className="text-sm text-muted-foreground">
            No test cases yet.{" "}
            <Link href="/testcases/new" className="underline">Create your first test case</Link>.
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Platform</TableHead>
                <TableHead>Updated</TableHead>
                <TableHead className="text-right">Open</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {items.map((tc) => (
                <TableRow key={tc.testCaseId}>
                  <TableCell className="font-medium">#{tc.testCaseId}</TableCell>
                  <TableCell>{tc.targetPlatform}</TableCell>
                  <TableCell className="text-xs text-muted-foreground">
                    {new Date(tc.updatedAt).toLocaleString()}
                  </TableCell>
                  <TableCell className="text-right">
                    <Button variant="secondary" size="sm" asChild>
                      <Link href={`/testcases/${tc.testCaseId}`}>View</Link>
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}

        <div className="pt-1">
          <Button variant="ghost" size="sm" asChild>
            <Link href="/testcases">View all →</Link>
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}