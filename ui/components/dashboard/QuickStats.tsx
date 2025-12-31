// src/components/dashboard/QuickStats.tsx
import { TestCaseDTO } from "@/types/models";
import { Card, CardContent } from "@/components/ui/card";

function uniquePlatforms(items: TestCaseDTO[]) {
  const set = new Set(items.map((x) => x.targetPlatform).filter(Boolean));
  return Array.from(set);
}

export default function QuickStats({ items }: { items: TestCaseDTO[] }) {
  const total = items.length;
  const platforms = uniquePlatforms(items);
  const lastUpdated = items
    .slice()
    .sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())[0];

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <Card>
        <CardContent className="pt-6">
          <div className="text-xs text-muted-foreground">Total Test Cases</div>
          <div className="mt-1 text-2xl font-semibold">{total}</div>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="pt-6">
          <div className="text-xs text-muted-foreground">Platforms Used</div>
          <div className="mt-1 text-2xl font-semibold">{platforms.length}</div>
          <div className="mt-1 text-xs text-muted-foreground">
            {platforms.length ? platforms.join(", ") : "—"}
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="pt-6">
          <div className="text-xs text-muted-foreground">Last Updated</div>
          <div className="mt-1 text-sm font-medium">
            {lastUpdated ? `#${lastUpdated.testCaseId}` : "—"}
          </div>
          <div className="mt-1 text-xs text-muted-foreground">
            {lastUpdated ? new Date(lastUpdated.updatedAt).toLocaleString() : "No test cases yet"}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}