import {saveAs} from "file-saver";

const convertEventToICS = (event) => {
    const { startTime, endTime, title, description, tags } = event;

    const startDate = new Date(startTime).toISOString().replace(/[-:]/g, '').split('.')[0];
    const endDate = new Date(endTime).toISOString().replace(/[-:]/g, '').split('.')[0];

    return `\nBEGIN:VEVENT
UID:${event.id}@yourdomain.com
DTSTAMP:${startDate}
DTSTART:${startDate}
DTEND:${endDate}
SUMMARY:${title}
DESCRIPTION:${description || 'Empty description'}
LOCATION:
STATUS:CONFIRMED
CATEGORIES:${tags.join(', ')}
END:VEVENT\n`;
};

export const generateICSFile = (events) => {
    const icsHeader = 'BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//Your Company//NONSGML v1.0//EN';
    const icsFooter = 'END:VCALENDAR';

    let icsContent = icsHeader;

    events.forEach((event) => {
        icsContent += convertEventToICS(event);
    });

    icsContent += icsFooter;

    const blob = new Blob([icsContent], { type: 'text/calendar' });
    saveAs(blob, 'my_events.ics');
};