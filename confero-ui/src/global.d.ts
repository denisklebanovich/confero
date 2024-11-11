export {};

declare global {
    interface Window {
        buttonStyles: string;
        handleEventClick: (eventId: string | number) => void;
    }
}