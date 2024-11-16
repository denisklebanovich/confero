import { fetchTextRazorAnalysis } from "@/services/TextRazorService";
import { useState } from "react";

const useTags = () => {
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [data, setData] = useState<any>(null);
    const [cachedText, setCachedText] = useState<string | null>(null);

    const analyzeText = async (text: string) => {
        setError(null);
        if (cachedText === text) {
            return generateTags(data);
        }
        setLoading(true);
        try {
            const result = await fetchTextRazorAnalysis(text);
            setData(result);
            setCachedText(text);
            return generateTags(result);
        } catch (err: any) {
            setError(err.message || 'An error occurred');
        } finally {
            setLoading(false);
        }
    };

    const generateTags = (dataToUse = data) => {
        if (dataToUse?.response?.topics) {
            return dataToUse.response.topics
                .filter((topic: any) => topic.score > 0.5)
                .sort(() => Math.random() - 0.5)
                .slice(0, 3)
                .map((topic: any) => topic.label);
        }
        return [];
    };


    return { error, data, loading, analyzeText, generateTags };
};

export default useTags;
