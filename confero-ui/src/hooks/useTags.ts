import {useState} from "react";
import {useApi} from "@/api/useApi.ts";

const useTags = () => {
    const {apiClient} = useApi();
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
            const result = await apiClient.tags.getTagsFromText(text);
            setData(result);
            setCachedText(text);
            return generateTags(result);
        } catch (err: any) {
            setError(err.message || 'An error occurred');
        } finally {
            setLoading(false);
        }
    };

    const generateTags = (tags: string[]) => {
        if (tags) {
            return tags
                .sort(() => Math.random() - 0.5)
                .slice(0, 5);
        }
        return [];
    };


    return {error, data, loading, analyzeText, generateTags};
};

export default useTags;
