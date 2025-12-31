"use client";

import { api } from "@/lib/api";
import { TestCaseDTO } from "@/types/models";
import { useEffect, useState } from "react";

export function useTestCase(id: number) {
    const [data, setData] = useState<TestCaseDTO | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const load = async () => {
        try {
            setError(null);
            setLoading(true);
            const tc = await api.getTestCase(id);
            setData(tc);
        } catch (e: any) {
            setError(e?.message ?? "Failed to load testcase");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        if (!id || Number.isNaN(id)) return;
        load();
    }, [id]);

    return { data, loading, error, reload: load };
}