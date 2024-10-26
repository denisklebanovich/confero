import React, { useEffect, useRef } from "react";
import "dhtmlx-scheduler/codebase/dhtmlxscheduler.css";
import scheduler from "dhtmlx-scheduler";

const Scheduler = () => {
    const schedulerContainer = useRef(null);

    useEffect(() => {
        // Initialize scheduler with "day" view
        scheduler.init(schedulerContainer.current, new Date(), "day");


        // Optional: set default configurations
        scheduler.config.xml_date = "%Y-%m-%d %H:%i";
        scheduler.config.first_hour = 8;
        scheduler.config.last_hour = 20;

        // Define some sample events
        scheduler.parse([
            { id: 1, start_date: "2024-10-25 10:00", end_date: "2024-10-25 12:00", text: "Event 1" },
            { id: 2, start_date: "2024-10-26 14:00", end_date: "2024-10-26 16:00", text: "Event 2" },
        ], "json");

        // Clean up scheduler on component unmount
        return () => {
            scheduler.clearAll();
        };
    }, []);

    return <div ref={schedulerContainer} style={{ width: "60%", height: "400px", paddingTop: "5px" }} />;
};

export default Scheduler;
