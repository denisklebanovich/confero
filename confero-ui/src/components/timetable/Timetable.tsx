import React, {useEffect, useRef, useState} from "react";
import "dhtmlx-scheduler/codebase/dhtmlxscheduler.css";
import scheduler from "dhtmlx-scheduler";
import TimeTableModal from "@/components/timetable/TimeTableModal.tsx";

const Timetable = ({presentations, setPresentations}) => {
    const schedulerContainer = useRef(null);

    const [open, setOpen] = useState(false)
    const [selectedEvent, setSelectedEvent] = useState({})


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
                                <span class="font-light text-xl">${event.organisers_line || ""}</span><br/>
                                <strong class="font-bold text-xl">${event.title}</strong><br/>
                            </div>`;

            if (event.toShow) {
                eventHtml += `<button class="${window.buttonStyles}" onclick="window.handleEventClick(${event.id})">Update</button>`;
            }

            eventHtml += `  </div>`;

            return eventHtml;
        };

        scheduler.templates.event_class = function(start, end, event){
            if(!event.toShow){
                return "dhx_event_gray dhx_event_no_footer";
            }
            return "dhx_event_gray";
        }

        scheduler.parse(

            presentations.map(
                presentation => {
            return {
            ...presentation,
            onClick: () => onEventChange(presentation)
        }
                }
                ), "json");

        window.handleEventClick = (eventId) => {
            const event = scheduler.getEvent(eventId);
            if (event && typeof event.onClick === "function") {
                event.onClick();
            }
        };

        scheduler.config.drag_create = false;
        scheduler.config.details_on_dblclick = false;
        scheduler.config.dblclick_create = false;

        scheduler.attachEvent("onBeforeDrag", function (id, ev) {
            const event = scheduler.getEvent(id);
            return event.toShow;

        });

        const excludeProperty = (array, propToExclude) =>
            array.map(({ [propToExclude]: _, ...rest }) => rest);

        scheduler.attachEvent("onEventChanged", function(){
            setPresentations(excludeProperty(scheduler.getEvents(), "onClick"));
            return true;
        })


        scheduler.attachEvent("onBeforeEventMenu", function (id, menu) {
            const event = scheduler.getEvent(id);
            if (!event.toShow) {
                menu = menu.filter(item => item.action !== "edit" && item.action !== "delete");
            }
            return menu;
        });

        scheduler.attachEvent("onDblClick", function () {
           return false;
        });

        scheduler.attachEvent("onContextMenu", function () {
            return false;
        });
        scheduler.attachEvent("onClick", function () {
            return false;
        });



        window.buttonStyles = "mr-5 inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2";

        return () => {
            scheduler.clearAll();
        };
    }, []);

    function onEventChange(event){
         setSelectedEvent(event);
        setOpen(true)
    }


    const getAllEvents = () => {
        return scheduler.getEvents();
    };

    useEffect(() => {
        scheduler.clearAll();
        scheduler.parse(presentations.map(presentation => {
            return {
                ...presentation,
                onClick: () => onEventChange(presentation)
            }
        }), "json");
    }, [open]);

    return(
        <>
            <style>
                {`
                    .dhx_event_gray {
                        --dhx-scheduler-event-background: #FFFFFF;
                         --dhx-scheduler-event-color: black;
                          box-shadow: 2px 4px 8px rgba(0, 0, 0, 0.2);
                    }
                     .dhx_event_no_footer .dhx_event_resize.dhx_footer {
                        display: none;
                     }
                `}
            </style>
            <TimeTableModal open={open} setOpen={setOpen} selectedEvent={selectedEvent} setSelectedEvent={setSelectedEvent} setPresentations={setPresentations} />
            <div ref={schedulerContainer} style={{ width: "70%", height: "450px", paddingTop: "5px" }} />
        </>
    )

};

export default Timetable;
