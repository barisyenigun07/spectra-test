"use client";

import Link from "next/link";
import PageHeader from "@/components/PageHeader";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import { TestCaseDTO } from "@/types/models";
import TestCasesTable from "@/components/testcases/TestCasesTable";

export default function TestCasesClient() {
  const [data, setData] = useState<TestCaseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    try {
      setError(null);
      setLoading(true);
      const list = await api.getTestCases();
      setData(list);
    } catch (e: any) {
      setError(e?.message ?? "Failed to load testcases");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div className="p-6 space-y-6">
      <PageHeader
        title="Test Cases"
        subtitle="Manage and run your automated test cases"
        primaryAction={{ label: "New Test Case", href: "/testcases/new" }}
      />

      {error && (
        <Card>
          <CardContent className="pt-6 text-sm text-red-600">
            {error}
          </CardContent>
        </Card>
      )}

      {loading ? (
        <Card>
          <CardContent className="pt-6 text-sm text-muted-foreground">
            Loading test cases…
          </CardContent>
        </Card>
      ) : (
        <TestCasesTable />
      )}
    </div>
  );
}