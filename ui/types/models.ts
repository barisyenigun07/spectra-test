export type LocatorDTO = {
    type?: string | null,
    value?: string | null
};

export type StepCreateDTO = {
    orderIndex: number;
    action: string;
    locator?: LocatorDTO | null;
    params?: Record<string, any> | null;
};

export type StepDTO = StepCreateDTO & { stepId: number }

export type StepStatus = "PASSED" | "FAILED" | "SKIPPED";

export type StepResultDTO = {
    stepId: number;
    runId: number;
    orderIndex: number;
    action: string;
    locator?: LocatorDTO | null;
    status: StepStatus;
    message?: string | null;
    startedAt: string;
    finishedAt: string;
    durationMillis: number;
    errorMessage?: string | null;
    errorType?: string | null;
    extra: Record<string, any>;
};

export type TestCaseCreateRequest = {
    targetPlatform: string;
    steps: StepCreateDTO[];
    config: Record<string, any>;
};

export type TestCaseDTO = {
    testCaseId: number;
    targetPlatform: string;
    createdAt: string;
    updatedAt: string;
    steps: StepDTO[];
    config: Record<string, any>;
}

export type TestCaseStatus = "RUNNING" | "PASSED" | "FAILED";

export type TestCaseRunRequestedEvent = {
    testCaseId: number;
    runId: number;
    targetPlatform: string;
    steps: StepDTO[];
    config: Record<string, any>;
}

export type TestCaseResultDTO = {
    testCaseId: number;
    runId: number;
    targetPlatform: string;
    status: TestCaseStatus;
    startedAt: string;
    finishedAt: string;
    durationMillis: number;
    stepResults: StepResultDTO[];
}