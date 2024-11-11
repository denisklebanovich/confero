declare module "dhtmlx-scheduler" {
    const scheduler: {
        init: (container: HTMLElement, date: Date, view: string) => void;
        config: {
            xml_date: string;
            first_hour: number;
            last_hour: number;
            header?: any[];
            time_step?: number;
            hour_size_px?: number;
            drag_create?: boolean;
            details_on_dblclick?: boolean;
            dblclick_create?: boolean;
        };
        templates: {
            event_text?: (start: Date, end: Date, event: any) => string;
            event_class?: (start: Date, end: Date, event: any) => string;
        };
        parse: (data: any, json: string) => void;
        clearAll: () => void;
        attachEvent: (name: string, handler: (...args: any[]) => any) => void;
        getEvent: (id: string | number) => any;
        getEvents: () => any[];
    };

    export default scheduler;
}
