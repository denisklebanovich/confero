export const fetchTextRazorAnalysis = async (text: string, extractors: string[] = ['entities', 'topics']): Promise<any> => {
    const url = "https://api.textrazor.com";
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            'X-TextRazor-Key': "edcfed6fa2e08928dcf9e62563466eb4dea82257dfb35b35603e5645",
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