// app/job-flow/page.tsx (veya components/JobFlowEditor.tsx)
"use client";

import React, { useCallback, useMemo, useState } from "react";
import  {
    ReactFlow,
  Background,
  Controls,
  MiniMap,
  addEdge,
  useNodesState,
  useEdgesState,
  Connection,
  Edge,
  Node,
  Position,
} from "@xyflow/react";
import "@xyflow/react/dist/style.css";

import type { Step, Job, Locator } from "@/models/Models";

const initialSteps: Step[] = [
  {
    orderIndex: 1,
    action: "openUrl",
    locator: { type: null, value: null },
    inputValue: "https://www.selenium.dev",
  },
  {
    orderIndex: 2,
    action: "click",
    locator: { type: "id", value: "navbarDropdown" },
    inputValue: null,
  },
];

function stepsToNodesAndEdges(steps: Step[]) {
  const sorted = [...steps].sort((a, b) => a.orderIndex - b.orderIndex);

  const nodes: Node[] = sorted.map((step, idx) => ({
    id: `step-${idx + 1}`,
    position: { x: 100, y: idx * 120 },
    sourcePosition: Position.Right,
    targetPosition: Position.Left,
    data: {
      label: `${step.orderIndex}. ${step.action}`,
      step,
    },
    style: {
      padding: 12,
      borderRadius: 8,
      border: "1px solid #e2e8f0",
      background: "#D61A1A",
      minWidth: 220,
      boxShadow: "0 4px 8px rgba(15,23,42,0.06)",
      fontSize: 12,
    },
  }));

  const edges: Edge[] = [];
  for (let i = 0; i < sorted.length - 1; i++) {
    edges.push({
      id: `e-step-${i + 1}-step-${i + 2}`,
      source: `step-${i + 1}`,
      target: `step-${i + 2}`,
      animated: true,
      label: "then",
      style: { strokeWidth: 1.5 },
    });
  }

  return { nodes, edges };
}

export default function JobFlowPage() {
  
  const [job, setJob] = useState<Job>({
    targetPlatform: "web",
    steps: initialSteps,
    config: {
      browser: "chrome",
      headless: "true",
    },
  });

  const { nodes: initialNodes, edges: initialEdges } = useMemo(
    () => stepsToNodesAndEdges(job.steps),
    [job.steps]
  );

  const [nodes, setNodes, onNodesChange] =
    useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);

  // Yeni connection oluştuğunda edge ekleme
  const onConnect = useCallback(
    (connection: Connection) => {
      setEdges((eds) => addEdge({ ...connection, animated: true }, eds));
    },
    [setEdges]
  );

  // Yeni step eklemek için
  const addStep = () => {
    setJob((prev) => {
      const nextOrder = prev.steps.length + 1;
      const newStep: Step = {
        orderIndex: nextOrder,
        action: "click",
        locator: { type: "id", value: "myElement" },
        inputValue: null,
      };

      const updatedSteps = [...prev.steps, newStep];
      const { nodes, edges } = stepsToNodesAndEdges(updatedSteps);

      setNodes(nodes);
      setEdges(edges);

      return { ...prev, steps: updatedSteps };
    });
  };

  // Örneğin job’ı backend’e POST etmek için
  const sendToBackend = async () => {
    // orderIndex’leri node sırasına göre güncellemek istersen
    // burada node.id → step eşlemesini yapıp yeniden sırala.
    const payload = {
      targetPlatform: job.targetPlatform,
      steps: job.steps.map((s) => ({
        orderIndex: s.orderIndex,
        action: s.action,
        locator: s.locator,
        inputValue: s.inputValue ?? null,
      })),
      config: job.config,
    };

    console.log("Sending payload:", payload);

    try {
      const res = await fetch("http://localhost:8080/api/jobs", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        throw new Error(`HTTP ${res.status}`);
      }

      const json = await res.json();
      console.log("Job created:", json);
    } catch (e) {
      console.error("Failed to create job:", e);
    }
  };

  return (
    <div className="flex h-[calc(100vh-80px)]">
      {/* Sol taraf: Flow */}
      <div className="flex-1 border-r border-slate-200">
        <ReactFlow
          nodes={nodes}
          edges={edges}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          onConnect={onConnect}
          fitView
        >
          <Background />
          <Controls />
          <MiniMap />
        </ReactFlow>
      </div>

      {/* Sağ taraf: Basit kontrol paneli */}
      <div className="w-80 p-4 flex flex-col gap-4 bg-slate-50">
        <h2 className="font-semibold text-sm">Job Config</h2>

        <div className="space-y-2 text-xs">
          <div>
            <div className="text-[11px] text-slate-500">Target Platform</div>
            <div className="font-mono text-[11px]">
              {job.targetPlatform.toUpperCase()}
            </div>
          </div>
          <div>
            <div className="text-[11px] text-slate-500">Browser</div>
            <div className="font-mono text-[11px]">
              {job.config.browser ?? "chrome"}
            </div>
          </div>
          <div>
            <div className="text-[11px] text-slate-500">Headless</div>
            <div className="font-mono text-[11px]">
              {job.config.headless ?? "true"}
            </div>
          </div>
          <div>
            <div className="text-[11px] text-slate-500">Steps</div>
            <pre className="text-[10px] bg-white p-2 rounded border border-slate-200 max-h-40 overflow-auto">
              {JSON.stringify(job.steps, null, 2)}
            </pre>
          </div>
        </div>

        <button
          onClick={addStep}
          className="mt-auto text-xs border border-slate-300 rounded px-3 py-1 hover:bg-slate-100"
        >
          + Add Step
        </button>

        <button
          onClick={sendToBackend}
          className="text-xs bg-slate-900 text-white rounded px-3 py-1 hover:bg-slate-800"
        >
          Send Job to Backend
        </button>
      </div>
    </div>
  );
}