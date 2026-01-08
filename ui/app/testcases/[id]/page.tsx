import TestCaseDetailClient from "@/components/testcases/TestCaseDetailClient";

export default function TestCaseDetailPage({params, searchParams}: {params: {id: string}; searchParams: {run?:string};}) {
  const testCaseId = Number(params.id);
  
  const parsed = searchParams?.run ? Number(searchParams.run) : undefined;
  const runId = Number.isFinite(parsed) ? parsed : undefined; 

  return (
    <TestCaseDetailClient testCaseId={testCaseId} activeRunId={runId}/>
  );
}