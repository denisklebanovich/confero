import {useState, useEffect} from "react";
import {useApi} from "@/api/useApi.ts";
import {ApplicationPreviewResponse, ApplicationStatus} from "@/generated";

const useFilteredApplications = (year, status, order) => {
    const {apiClient, useApiQuery} = useApi();
    const [filteredApplications, setFilteredApplications] = useState([]);
    const [years, setYears] = useState([]);

    const {data: applications, isLoading} = useApiQuery<ApplicationPreviewResponse[]>(
        ["applications"],
        () => apiClient.application.getApplications()
    );

    useEffect(() => {
        if (!applications) return;
        const years = Array.from(
            new Set(applications.map((application) => new Date(application.createdAt).getFullYear()))
        ).sort((a, b) => b - a);
        setYears([...years]);
    }, [applications]);

    useEffect(() => {
        console.log("useFilteredApplications", applications);
        if (!applications) return;

        const filtered = applications
            .filter((application) => {
                if (year !== "All") {
                    const applicationYear = new Date(application.createdAt).getFullYear();
                    if (applicationYear !== parseInt(year)) {
                        return false;
                    }
                }
                if (status !== "All" && application.status !== status) {
                    return false;
                }
                return true;
            })
            .sort((a, b) => {
                const dateA = new Date(a.createdAt).getTime();
                const dateB = new Date(b.createdAt).getTime();
                return order === "Descending" ? dateB - dateA : dateA - dateB;
            });

        setFilteredApplications(filtered);
    }, [applications, year, status, order]);

    return {filteredApplications, years, isLoading};
};

export default useFilteredApplications;
