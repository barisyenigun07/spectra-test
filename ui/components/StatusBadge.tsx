import React from 'react'
import { cn } from '@/lib/utils'

const StatusBadge = (props: { status: string }) => {
  const s = props.status;

  const cls = s === "PASSED" ?
    "bg-emerald-100 text-emerald-700 border-emerald-200" : 
    s === "FAILED" ? "bg-red-100 text-red-700 border-red-200" :
    s === "RUNNING" ? "bg-blue-100 text-blue-700 border-blue-200" :
    s === "SKIPPED" ? "bg-zinc-100 text-zinc-700 border-zinc-200" :
    "bg-amber-100 text-amber-700 border-amber-200";

  return (
    <span className={cn("inline-flex items-center rounded-md border px-2 py-0.5 text-xs font-medium", cls)}>
      {s}
    </span>
  )
}

export default StatusBadge