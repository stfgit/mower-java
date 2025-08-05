import { MowerCommandRequest, MowerResponse, BatchMowerRequest } from '@/types/mower';

const API_BASE_URL = `${process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'}/api/mower`;

export class MowerAPI {
  static async executeSingleMower(request: MowerCommandRequest): Promise<MowerResponse> {
    const response = await fetch(`${API_BASE_URL}/execute`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      throw new Error(`API Error: ${response.status} - ${response.statusText}`);
    }

    return await response.json();
  }

  static async executeBatchMowers(request: BatchMowerRequest): Promise<MowerResponse[]> {
    const response = await fetch(`${API_BASE_URL}/batch`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      throw new Error(`API Error: ${response.status} - ${response.statusText}`);
    }

    return await response.json();
  }

  static async healthCheck(): Promise<{ status: string; service: string }> {
    const response = await fetch(`${API_BASE_URL}/health`);
    
    if (!response.ok) {
      throw new Error(`Health check failed: ${response.status}`);
    }

    return await response.json();
  }
}