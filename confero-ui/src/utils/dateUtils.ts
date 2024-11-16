export const getFormattedTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
}

export const getFormattedDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString() + " " + date.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
}

export const getInputDate = (dateString: string) => {
    console.log(dateString, 101)
    const date = new Date(dateString);
    console.log(date.toISOString().split("T")[0], 1234)
    return date.toISOString().split("T")[0];
}