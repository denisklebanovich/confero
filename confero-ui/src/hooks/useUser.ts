import {useQuery} from "@tanstack/react-query";
import {apiInstance} from "@/service/api-instance.ts";


export function useUser(userId: number) {
    return useQuery({
        queryKey: "userId",
        queryFn: async () => {
            const response = await apiInstance.user.getUser(userId);
            return response.data;
        },
    });
}
