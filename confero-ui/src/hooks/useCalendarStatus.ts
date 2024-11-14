import {useQueryClient} from "@tanstack/react-query";
import type {SessionPreviewResponse} from "@/generated";

export function useCalendarStatus() {
    const queryClient = useQueryClient();

    const changeCalendarStatus = (sessionId: number, isInCalendar: boolean) => {
        queryClient.setQueryData<SessionPreviewResponse[]>(["sessions"], (data) => {
            return data?.map((s) => {
                if (s.id === sessionId) {
                    return {...s, isInCalendar: isInCalendar};
                }
                return s;
            });
        });
        if (isInCalendar) {
            queryClient.invalidateQueries({queryKey: ["user-sessions"]});
        } else {
            queryClient.setQueryData<SessionPreviewResponse[]>(["user-sessions"], (data) => {
                return data?.filter((s) => s.id !== sessionId);
            })
        }
    };

    return {changeCalendarStatus};
}
