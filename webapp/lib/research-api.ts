const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8000'; // Default to API Gateway port

// Interface for common API response structure (adjust as needed)
interface ApiResponse<T> {
  data: T;
  message?: string;
  // Add other common fields like status, errors, etc.
}

// --- Research-related API functions ---

/**
 * Fetches a quick summary.
 * @param data The request body for the quick summary.
 * @returns A promise that resolves to the summary data.
 */
export const getQuickSummary = async (data: any): Promise<any> => {
  const response = await fetch(`${API_URL}/api/v1/quick_summary`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      // Add Authorization header if needed: 'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to get quick summary');
  }
  return response.json();
};

/**
 * Generates a report.
 * @param data The request body for report generation.
 * @returns A promise that resolves to the report data.
 */
export const generateReport = async (data: any): Promise<any> => {
  const response = await fetch(`${API_URL}/api/v1/generate_report`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      // Add Authorization header if needed
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to generate report');
  }
  return response.json();
};

/**
 * Analyzes documents.
 * @param data The request body for document analysis.
 * @returns A promise that resolves to the analysis results.
 */
export const analyzeDocuments = async (data: any): Promise<any> => {
  const response = await fetch(`${API_URL}/api/v1/analyze_documents`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      // Add Authorization header if needed
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to analyze documents');
  }
  return response.json();
};

/**
 * Fetches metrics.
 * @returns A promise that resolves to the metrics data.
 */
export const getMetrics = async (): Promise<any> => {
  const response = await fetch(`${API_URL}/api/v1/metrics`);
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to fetch metrics');
  }
  return response.json();
};

/**
 * Fetches available types.
 * @returns A promise that resolves to a list of types.
 */
export const getTypes = async (): Promise<string[]> => {
  const response = await fetch(`${API_URL}/api/v1/get_types`);
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to fetch types');
  }
  return response.json();
};

/**
 * Fetches available categories.
 * @returns A promise that resolves to a list of categories.
 */
export const getCategories = async (): Promise<string[]> => {
  const response = await fetch(`${API_URL}/api/v1/get_categories`);
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to fetch categories');
  }
  return response.json();
};

/**
 * Fetches configuration settings.
 * @returns A promise that resolves to the configuration data.
 */
export const getConfig = async (): Promise<any> => {
  const response = await fetch(`${API_URL}/api/v1/get_config`);
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to fetch config');
  }
  return response.json();
};

/**
 * Fetches a specific setting by key.
 * @param key The key of the setting to fetch.
 * @returns A promise that resolves to the setting value.
 */
export const getSetting = async (key: string): Promise<any> => {
  const response = await fetch(`${API_URL}/api/v1/get_setting/${key}`);
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || `Failed to fetch setting for key: ${key}`);
  }
  return response.json();
};
