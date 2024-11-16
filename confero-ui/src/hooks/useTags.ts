import { fetchTextRazorAnalysis } from "@/services/TextRazorService";
import { useState } from "react";

const useTags = () => {
    const [error, setError] = useState<string | null>(null);
    const [data, setData] = useState<any>(null);

    const analyzeText = async (text: string) => {
        setError(null);
        try {
            const result = await fetchTextRazorAnalysis(text);
            setData(result);
        } catch (err: any) {
            setError(err.message || 'An error occurred');
        }
    };

    const generateTags = () => {
        if (data?.response?.topics) {
            return data.response.topics
                .filter((topic: any) => topic.score > 0.5)
                .sort(() => Math.random() - 0.5)
                .slice(0, 3)
                .map((topic: any) => topic.label);
        }
        return [];
    };

    return { error, data, analyzeText, generateTags };
};

export default useTags;
