import { api } from '@/lib/api'
import React from 'react'

const TestCaseRunDetailed = async () => {
    const testCaseDTO = await api.getTestCase(3);
    
    return (
        <div>
            <h3 className="text-6xl font-bold">Target Platform: {testCaseDTO.targetPlatform.toUpperCase()}</h3>
            <p>{testCaseDTO.testCaseId}</p>
        </div>
    )
}

export default TestCaseRunDetailed