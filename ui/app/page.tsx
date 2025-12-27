"use client";

import CreateTestCaseForm from "@/components/testcases/CreateTestCaseForm";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import "@xyflow/react/dist/style.css";
import { useState } from "react";

export default function Home() {
  return (
    <div className="flex flex-col gap-5 pt-5 pl-3">
      <CreateTestCaseForm/>
    </div>
  );
}
