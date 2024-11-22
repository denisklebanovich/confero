import React, { createContext, useContext, useState } from "react";

const FilterContext = createContext(null);

export const FilterProvider = ({ children }) => {
    const [year, setYear] = useState("All");
    const [status, setStatus] = useState("All");
    const [order, setOrder] = useState("Descending");
    const [filteredApplications, setFilteredApplications] = useState([]);

    return (
        <FilterContext.Provider
            value={{
                year,
                setYear,
                status,
                setStatus,
                order,
                setOrder,
                filteredApplications,
                setFilteredApplications,
            }}
        >
            {children}
        </FilterContext.Provider>
    );
};

export const useFilterContext = () => useContext(FilterContext);
