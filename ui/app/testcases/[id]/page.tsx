import TestCaseDetailClient from "@/components/testcases/TestCaseDetailClient";

export default function TestCaseDetailPage({params}: {params: {id: string}}) {
  const id = Number(params.id);
  return (
    <TestCaseDetailClient testCaseId={id}/>
  );
}