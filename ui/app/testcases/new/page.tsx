import PageHeader from "@/components/PageHeader";
import CreateTestCaseForm from "@/components/testcases/CreateTestCaseForm";

export default function NewTestCasePage() {
  return (
    <div className="p-6 space-y-6">
      <PageHeader
        title="New Test Case"
        subtitle="Create a test case definition for SpectraTest agents."
      />
      <CreateTestCaseForm />
    </div>
  );
}