export type Locator = {
    type: string | null,
    value: string | null
}

export type Step = {
    orderIndex: number,
    action: string,
    locator: Locator,
    inputValue: string | null
}

export type Job = {
    id?: number | null,
    targetPlatform: string,
    steps: Step[],
    config: Record<string, string>
}