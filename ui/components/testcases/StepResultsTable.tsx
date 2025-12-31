"use client";

import { StepResultDTO } from "@/types/models";
import StatusBadge from "@/components/StatusBadge";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";

export default function StepResultsTable(props: { steps: StepResultDTO[] }) {
  const steps = (props.steps ?? []).slice().sort((a, b) => a.orderIndex - b.orderIndex);

  if (!steps.length) {
    return (
      <Card>
        <CardContent className="pt-6 text-sm text-muted-foreground">
          No step results.
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent className="pt-6">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>#</TableHead>
              <TableHead>Action</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right">Duration</TableHead>
              <TableHead className="text-right">Details</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {steps.map((s) => (
              <TableRow key={`${s.runId}-${s.stepId}`}>
                <TableCell className="font-medium">{s.orderIndex}</TableCell>
                <TableCell>
                  <div className="font-medium">{s.action}</div>
                  <div className="text-xs text-muted-foreground">
                    {s.locator ? `${s.locator.type}:${s.locator.value}` : "—"}
                  </div>
                </TableCell>
                <TableCell>
                  <StatusBadge status={s.status} />
                </TableCell>
                <TableCell className="text-right">{s.durationMillis} ms</TableCell>
                <TableCell className="text-right">
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button variant="outline" size="sm">View</Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Step #{s.orderIndex} — {s.action}</DialogTitle>
                      </DialogHeader>

                      <div className="space-y-3 text-sm">
                        <div className="flex items-center gap-2">
                          <span>Status:</span>
                          <StatusBadge status={s.status} />
                        </div>

                        <div>
                          <div className="text-xs text-muted-foreground">Message</div>
                          <div className="mt-1">{s.message ?? "—"}</div>
                        </div>

                        <div>
                          <div className="text-xs text-muted-foreground">Error</div>
                          <div className="mt-1">
                            {s.errorType || s.errorMessage ? (
                              <pre className="text-xs p-3 rounded-md bg-muted overflow-auto">
                                {JSON.stringify(
                                  { errorType: s.errorType, errorMessage: s.errorMessage },
                                  null,
                                  2
                                )}
                              </pre>
                            ) : (
                              "—"
                            )}
                          </div>
                        </div>

                        <div>
                          <div className="text-xs text-muted-foreground">Extra</div>
                          <pre className="mt-1 text-xs p-3 rounded-md bg-muted overflow-auto">
                            {JSON.stringify(s.extra ?? {}, null, 2)}
                          </pre>
                        </div>
                      </div>

                      <DialogFooter>
                        <Button variant="secondary">Close</Button>
                      </DialogFooter>
                    </DialogContent>
                  </Dialog>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}