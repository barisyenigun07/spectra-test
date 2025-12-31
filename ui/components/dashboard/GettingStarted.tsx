// src/components/dashboard/GettingStarted.tsx
import Link from "next/link";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

function Step(props: { n: number; title: string; desc: string }) {
  return (
    <div className="flex gap-3">
      <div className="h-7 w-7 rounded-full border flex items-center justify-center text-xs font-medium">
        {props.n}
      </div>
      <div className="space-y-0.5">
        <div className="text-sm font-medium">{props.title}</div>
        <div className="text-xs text-muted-foreground">{props.desc}</div>
      </div>
    </div>
  );
}

export default function GettingStarted() {
  return (
    <Card>
      <CardContent className="pt-6 space-y-4">
        <div>
          <div className="text-sm font-medium">Getting Started</div>
          <div className="text-xs text-muted-foreground">
            A quick path to your first run
          </div>
        </div>

        <div className="space-y-3">
          <Step
            n={1}
            title="Create a test case"
            desc="Choose a platform and define steps with locators & params."
          />
          <Step
            n={2}
            title="Run it"
            desc="Trigger a run to execute steps on your selected agent."
          />
          <Step
            n={3}
            title="Observe results"
            desc="Inspect step-by-step results on the detail page."
          />
        </div>

        <div className="flex flex-wrap gap-2 pt-1">
          <Button asChild>
            <Link href="/testcases/new">Create Test Case</Link>
          </Button>
          <Button variant="outline" asChild>
            <Link href="/testcases">Go to Test Cases</Link>
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}