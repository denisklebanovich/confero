export const getFormattedTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
}

export const getFormattedDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString();
}

export const getInputDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toISOString().split("T")[0];
}