// src/components/dashboard/HeroSection.tsx
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

export default function HeroSection() {
  return (
    <Card>
      <CardContent className="pt-6 flex flex-col gap-4">
        <div className="space-y-1">
          <h1 className="text-2xl font-semibold">SpectraTest Console</h1>
          <p className="text-sm text-muted-foreground">
            Create, run and observe automated test cases across Web, Mobile and Desktop platforms.
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          <Button asChild>
            <Link href="/testcases/new">New Test Case</Link>
          </Button>
          <Button variant="outline" asChild>
            <Link href="/testcases">View Test Cases</Link>
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}