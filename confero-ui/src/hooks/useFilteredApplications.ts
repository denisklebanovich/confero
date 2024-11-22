import {useState, useEffect} from "react";
import {useApi} from "@/api/useApi.ts";
import {ApplicationPreviewResponse } from "@/generated";
import { useFilterContext } from "@/state/FilterContext";

const useFilteredApplications = () => {
    const {apiClient, useApiQuery} = useApi();
    const { year, status, order, setFilteredApplications, filteredApplications } = useFilterContext();
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
        if (!applications) return;

        const filtered = applications
            .filter((application) => {
                if (year !== "All") {
                    const applicationYear = new Date(application.createdAt).getFullYear();
                    if (applicationYear !== parseInt(year)) {
                        return false;
                    }
                }
                return !(status !== "All" && application.status !== status);
            })
            .sort((a, b) => {
                const dateA = new Date(a.createdAt).getTime();
                const dateB = new Date(b.createdAt).getTime();
                return order === "Descending" ? dateB - dateA : dateA - dateB;
            });

        setFilteredApplications(filtered);
    }, [applications, setFilteredApplications, year, status, order]);

    return {filteredApplications, years, isLoading};
};

export default useFilteredApplications;
