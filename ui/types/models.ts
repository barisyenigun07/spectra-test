export type LocatorDTO = {
    type?: string | null,
    value?: string | null
};

export type UiNode = {
  id: string;
  role?: string;
  name?: string;
  value?: string;
  attrs?: Record<string, any>;
  children?: UiNode[];
};

export type LocatorSuggestion = {
  strategy: string; // accessibilityId, xpath, css, id...
  value: string;
  score?: number;
  note?: string;
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

export type InspectorPlatform = "WEB" | "MOBILE" | "DESKTOP";

/* ---------- Session ---------- */

export interface InspectorSessionDTO {
  sessionId: string;
  platform: InspectorPlatform;
  createdAt: string;
}

export interface InspectorCreateSessionRequest {
  platform: InspectorPlatform;
  config: Record<string, any>;      // driver / agent config
  initialUrl?: string; // WEB için opsiyonel
}

/* ---------- Snapshot ---------- */

export interface InspectorSnapshotRequest {
  maxDepth: number;
  maxChildrenPerNode: number;
  maxNodes: number;
}

export interface UiNodeDTO {
  nodeId: string;
  type: string;                // tag / role
  name: string;                // textContent (shortened)
  attrs: Record<string, any>;  // id, class, aria-label, automationId...
  children: UiNodeDTO[];
}

export interface InspectorSnapshotDTO {
  sessionId: string;
  capturedAt: string;
  pageTitle?: string;
  pageUrl?: string;
  root: UiNodeDTO;
}

/* ---------- Suggestions ---------- */

export interface LocatorSuggestionDTO {
  strategy: string; // "id" | "css" | "xpath" | "accessibilityId" | "ldtp"
  value: string;
  score: number;
  note?: string;
}

export interface InspectorSuggestionsDTO {
  sessionId: string;
  nodeId: string;
  candidates: LocatorCandidateDTO[];
}

export interface LocatorCandidateDTO {
  locator: LocatorDTO;
  score: number;
  note?: string;
}

/* ---------- Pick ---------- */

export interface InspectorPickRequest {
  nodeId: string;
  preferredStrategy?: string; // "id" | "css" | "xpath" | ...
}

export interface InspectorPickResponse {
  sessionId: string;
  nodeId: string;
  picked: LocatorDTO;
  allSuggestions: LocatorSuggestionDTO[];
}