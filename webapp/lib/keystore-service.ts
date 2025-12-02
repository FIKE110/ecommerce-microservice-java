export function saveKey(keyName: string, keyValue: string): void {
    localStorage.setItem(keyName, keyValue);
}

export function getKey(keyName: string): string | null {
    return localStorage.getItem(keyName);
}

export function deleteKey(keyName: string): void {
    localStorage.removeItem(keyName);
}

export function listKeys(): string[] {
    return Object.keys(localStorage);
}