import {useApi} from "@/api/useApi.ts";
import {ProfileResponse} from "@/generated";

export const useProfile = () => {
    const {apiClient, useApiQuery} = useApi();
    const {data: profileData, isLoading} = useApiQuery<ProfileResponse>(
        ["profile"],
        () => apiClient.user.getUserProfile()
    );
    return {profile: profileData, isLoading};
}