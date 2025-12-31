import { TestCaseDTO } from "@/types/models";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

export default function TestCaseDefinitionCard({ tc }: { tc: TestCaseDTO }) {
  return (
    <Card>
      <CardContent className="pt-6 space-y-6">
        <div>
          <div className="text-sm font-medium">Config</div>
          <pre className="mt-2 text-xs p-3 rounded-md bg-muted overflow-auto">
            {JSON.stringify(tc.config ?? {}, null, 2)}
          </pre>
        </div>

        <div>
          <div className="text-sm font-medium">Steps</div>
          <div className="mt-3">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>#</TableHead>
                  <TableHead>Action</TableHead>
                  <TableHead>Locator</TableHead>
                  <TableHead>Params</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {(tc.steps ?? [])
                  .slice()
                  .sort((a, b) => a.orderIndex - b.orderIndex)
                  .map((s) => (
                    <TableRow key={s.stepId}>
                      <TableCell className="font-medium">{s.orderIndex}</TableCell>
                      <TableCell>{s.action}</TableCell>
                      <TableCell className="text-xs text-muted-foreground">
                        {s.locator ? `${s.locator.type}:${s.locator.value}` : "—"}
                      </TableCell>
                      <TableCell>
                        <pre className="text-xs p-2 rounded-md bg-muted overflow-auto max-w-[520px]">
                          {JSON.stringify(s.params ?? {}, null, 2)}
                        </pre>
                      </TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}