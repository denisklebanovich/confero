declare module "dhtmlx-scheduler" {
    const scheduler: {
        init: (container: HTMLElement, date: Date, view: string) => void;
        config: {
            xml_date: string;
            first_hour: number;
            last_hour: number;
        };
        parse: (data: any, json: string) => void;
        clearAll: () => void;
    };
    export default scheduler;
}