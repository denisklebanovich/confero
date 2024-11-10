import React, {useEffect, useRef, useState} from "react";
import "dhtmlx-scheduler/codebase/dhtmlxscheduler.css";
import scheduler from "dhtmlx-scheduler";

const Scheduler = () => {
    const schedulerContainer = useRef(null);

    useEffect(() => {
        scheduler.init(schedulerContainer.current, new Date("2024-11-10T10:00:00Z"), "day");
        scheduler.config.header = [
            "day",
            "month",
            "date",
            "prev",
            "today",
            "next"
        ]
        scheduler.config.xml_date = "%Y-%m-%d %H:%M"
        scheduler.config.first_hour = 8;
        scheduler.config.last_hour = 20;
        scheduler.config.time_step = 5;

        scheduler.config.hour_size_px = 80;
        scheduler.templates.event_text = function (start, end, event) {

            let eventHtml = `
                        <div class="w-full h-full items-center flex justify-between align-center">
                            <div class="w-2/3 h-full">
                                <strong>${event.text}</strong><br/>
                                <span>${event.description || ""}</span><br/>
                            </div>`;

            if (event.toShow) {
                eventHtml += `<button class="${window.buttonStyles}" onclick="window.handleEventClick(${event.id})">Update</button>`;
            }

            eventHtml += `  </div>`;

            return eventHtml;
        };

        scheduler.templates.event_class = function(start, end, event){
            return "dhx_event_gray";
        }

        scheduler.parse([
            { id: 1, start_date: "2024-11-10T10:00:00Z", end_date: "2024-11-10T12:30:00Z", text: "Event 1", "description" : "21212", onClick: () => console.log("clicked"), toShow: true},
            { id: 2, start_date: "2024-11-10T12:35:00Z", end_date: "2024-11-10T14:30:00Z", text: "Event 2", "description" : "21212", onClick: () => console.log("clicked"), toShow: false},
            { id: 3, start_date: "2024-11-10T14:35:00Z", end_date: "2024-11-10T16:30:00Z", text: "Event 3", "description" : "21212", onClick: () => console.log("clicked"), toShow: true},
        ], "json");

        window.handleEventClick = (eventId) => {
            const event = scheduler.getEvent(eventId);
            if (event && typeof event.onClick === "function") {
                event.onClick();
            }
        };

        window.buttonStyles = "mr-5 inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2";

        return () => {
            scheduler.clearAll();
        };
    }, []);

    const getAllEvents = () => {
        return scheduler.getEvents();
    };


    return(
        <>
            <style>
                {`
                    .dhx_event_gray {
                        --dhx-scheduler-event-background: #FFFFFF;
                         --dhx-scheduler-event-color: black;
                          box-shadow: 2px 4px 8px rgba(0, 0, 0, 0.2);
                    }
                `}
            </style>
            <div ref={schedulerContainer} style={{ width: "70%", height: "450px", paddingTop: "5px" }} />
        </>
    )

};

export default Scheduler;
