import { TestCaseCreateRequest, TestCaseDTO, TestCaseResultDTO } from "./models";

const BASE_URL = process.env.NEXT_PUBLIC_CONTROL_API_BASE_URL!;

async function http<T>(path: string, init?: RequestInit): Promise<T> {
    const res = await fetch(`${BASE_URL}${path}`, {
        ...init,
        headers: {
            "Content-Type": "application/json",
            ...(init?.headers ?? {}),
        },
        cache: "no-store",
    });

    if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(`HTTP ${res.status} ${res.statusText}: ${text}`);
    }

    return res.json() as Promise<T>;
}

export const api = {
    getTestCase: (id: number) => http<TestCaseDTO>(`/testcases/${id}`),
    getTestCases: () => http<TestCaseDTO[]>("/testcases"),
    getTestCaseRunResults: (id: number) => http<TestCaseResultDTO[]>(`/testcases/${id}/run/results`),
    createTestCase: (body: TestCaseCreateRequest) => http<TestCaseDTO>("/testcases", {method: "POST", body: JSON.stringify(body)}),
    runTestCase: (id: number) => http<{runId: number}>(`/testcases/${id}/run`, {method: "POST"}),
    deleteTestCase: (id: number) => http<{deletedTestCaseId: number}>(`/testcases/${id}`, {method: "DELETE"})
}