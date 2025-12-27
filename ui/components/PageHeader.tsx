import Link from "next/link";
import { Button } from "@/components/ui/button";

export default function PageHeader(props: {
  title: string;
  subtitle?: string;
  primaryAction?: { label: string; href: string };
}) {
  return (
    <div className="flex items-start justify-between gap-4">
      <div>
        <h1 className="text-2xl font-semibold">{props.title}</h1>
        {props.subtitle && (
          <p className="text-sm text-muted-foreground mt-1">{props.subtitle}</p>
        )}
      </div>

      {props.primaryAction && (
        <Button asChild>
          <Link href={props.primaryAction.href}>{props.primaryAction.label}</Link>
        </Button>
      )}
    </div>
  );
}