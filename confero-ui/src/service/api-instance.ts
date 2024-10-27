import {AppClient} from "@/generated";

export const apiInstance = new AppClient({
    HEADERS: {
        "Authorization": `Bearer ${localStorage.getItem("token")}`,
    },
    BASE: "/api",
});
