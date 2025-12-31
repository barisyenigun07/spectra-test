// src/components/dashboard/DashboardClient.tsx
"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { api } from "@/lib/api";
import { TestCaseDTO } from "@/types/models";

import HeroSection from "@/components/dashboard/HeroSection";
import QuickStats from "@/components/dashboard/QuickStats";
import RecentTestCases from "@/components/dashboard/RecentTestCases";
import GettingStarted from "@/components/dashboard/GettingStarted";

import { Card, CardContent } from "@/components/ui/card";

export default function DashboardClient() {
  const [items, setItems] = useState<TestCaseDTO[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    try {
      setError(null);
      const data = await api.getTestCases(); // sende bu var
      setItems(data);
    } catch (e: any) {
      setError(e?.message ?? "Failed to load dashboard data");
      setItems([]);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const sorted = useMemo(() => {
    const list = (items ?? []).slice();
    list.sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
    return list;
  }, [items]);

  const recent = useMemo(() => sorted.slice(0, 5), [sorted]);

  return (
    <div className="p-6 space-y-6">
      <HeroSection />

      {error && (
        <Card>
          <CardContent className="pt-6 text-sm text-red-600">
            {error}
          </CardContent>
        </Card>
      )}

      <QuickStats items={items ?? []} />

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        <RecentTestCases items={recent} loading={items === null} onRefresh={load} />
        <GettingStarted />
      </div>

      <div className="text-xs text-muted-foreground">
        Tip: You can manage everything from{" "}
        <Link href="/testcases" className="underline">Test Cases</Link>.
      </div>
    </div>
  );
}