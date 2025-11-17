"use client";

import { Client } from "@stomp/stompjs";
import Image from "next/image";
import "@xyflow/react/dist/style.css";
import { ReactFlow, Position, useNodesState, type Node } from "@xyflow/react";
import { BaseNode, BaseNodeContent, BaseNodeHeader, BaseNodeHeaderTitle } from "@/components/base-node";

type User = {
  id: number,
  name: string,
  age: number,
  job: string
}

const users: User[] = [
  {
    id: 1,
    name: "John Doe",
    age: 35,
    job: "Software Developer"
  },
  {
    id: 2,
    name: "George Wilkinson",
    age: 46,
    job: "Doctor"
  },
  {
    id: 3,
    name: "Alice Warburton",
    age: 27,
    job: "Accountant"
  },
  {
    id: 4,
    name: "Susie Richards",
    age: 34,
    job: "Lawyer"
  }
]

export default function Home() {
  return (
    <div className="flex flex-col gap-5">
      <BaseNode>
        <BaseNodeHeader>
          <BaseNodeHeaderTitle>Base Node</BaseNodeHeaderTitle>
        </BaseNodeHeader>
        <BaseNodeContent>
          This is a base node compnent.
        </BaseNodeContent>
      </BaseNode>
      <BaseNode>
        <BaseNodeHeader>
          <BaseNodeHeaderTitle>Base Node</BaseNodeHeaderTitle>
        </BaseNodeHeader>
        <BaseNodeContent>
          This is a base node compnent.
        </BaseNodeContent>
      </BaseNode>
      <div className="flex justify-center gap-4">
        <div className="bg-orange-600 rounded-2xl p-2 w-1/3">
          <h3 className="text-4xl text-white">Box</h3>
          <p className="text-white">Box paragraph 1</p>
        </div>
        <div className="bg-orange-600 rounded-2xl p-2 w-1/3">
          <h3 className="text-4xl text-white">Box</h3>
          <p className="text-white">Box paragraph 1</p>
        </div>
        <div className="bg-orange-600 hover:bg-purple-600 transition delay-150 duration-150 ease-in-out rounded-2xl p-2 w-1/3">
          <h3 className="text-4xl text-white">Box</h3>
          <p className="text-white">Box paragraph 1</p>
        </div>
      </div>
    </div>
  );
}
