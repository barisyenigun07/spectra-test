import TestCasesTable from "@/components/testcases/TestCasesTable";
import PageHeader from "@/components/PageHeader";

export default function TestCasesPage() {
  return (
    <div className="p-6 space-y-6">
      <PageHeader
        title="Test Cases"
        subtitle="Create, run, and manage SpectraTest test cases."
        primaryAction={{ label: "New Test Case", href: "/testcases/new" }}
      />
      <TestCasesTable />
    </div>
  );
}