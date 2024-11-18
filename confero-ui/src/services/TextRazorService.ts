export const fetchTextRazorAnalysis = async (text: string, extractors: string[] = ['entities', 'topics']): Promise<any> => {
    const url = import.meta.env.VITE_TEXT_RAZOR_API_URL as string;
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            'X-TextRazor-Key': import.meta.env.VITE_TEXT_RAZOR_API_KEY as string,
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept-encoding': 'gzip'
        },
        body: new URLSearchParams({
            text,
            extractors: extractors.join(',')
        }).toString(),
    });

    if (!response.ok) {
        throw new Error(`Error: ${response.status} ${response.statusText}`);
    }

    return response.json();
};