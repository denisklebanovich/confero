import { useRef, useState } from "react";
import { JitsiMeeting } from "@jitsi/react-sdk";
const Room = () => {
    const apiRef = useRef();
    const [logItems, updateLog] = useState([]);
    const [showNew, toggleShowNew] = useState(false);
    const [knockingParticipants, updateKnockingParticipants] = useState([]);

    const generateRoomName = () => `vladz12312`;

    const handleKnockingParticipant = (payload) => {
        updateLog((items) => [...items, JSON.stringify(payload)]);
        updateKnockingParticipants((participants) => [
            ...participants,
            payload?.participant,
        ]);
    };

    const handleAudioStatusChange = (payload, feature) => {
        if (payload.muted) {
            updateLog((items) => [...items, `${feature} off`]);
        } else {
            updateLog((items) => [...items, `${feature} on`]);
        }
    };

    const printEventOutput = (payload) => {
        updateLog((items) => [...items, JSON.stringify(payload)]);
    };

    const handleChatUpdates = (payload) => {
        if (payload.isOpen || !payload.unreadCount) {
            return;
        }
        apiRef.current.executeCommand("toggleChat");
        updateLog((items) => [
            ...items,
            `you have ${payload.unreadCount} unread messages`,
        ]);
    };

    const resolveKnockingParticipants = (condition) => {
        knockingParticipants.forEach((participant) => {
            apiRef.current.executeCommand(
                "answerKnockingParticipant",
                participant?.id,
                condition(participant)
            );
            updateKnockingParticipants((participants) =>
                participants.filter((item) => item.id === participant.id)
            );
        });
    };

    const handleJitsiIFrameRef1 = (iframeRef) => {
        iframeRef.style.border = "5px solid #3d3d3d";
        iframeRef.style.background = "#3d3d3d";
        iframeRef.style.height = "450px";
        iframeRef.style.marginBottom = "20px";
        iframeRef.style.borderRadius = "8px";
    };

    const handleReadyToClose = () => {
        /* eslint-disable-next-line no-alert */
        alert("Ready to close...");
    };

    const handleApiReady = (apiObj) => {
        apiRef.current = apiObj;
        apiRef.current.on("knockingParticipant", handleKnockingParticipant);
        apiRef.current.on("audioMuteStatusChanged", (payload) =>
            handleAudioStatusChange(payload, "audio")
        );
        apiRef.current.on("videoMuteStatusChanged", (payload) =>
            handleAudioStatusChange(payload, "video")
        );
        apiRef.current.on("raiseHandUpdated", printEventOutput);
        apiRef.current.on("titleViewChanged", printEventOutput);
        apiRef.current.on("chatUpdated", handleChatUpdates);
        apiRef.current.on("knockingParticipant", handleKnockingParticipant);
    };

    const renderSpinner = () => (
        <div
            style={{
                fontFamily: "sans-serif",
                textAlign: "center",
            }}
        >
            Loading..
        </div>
    );
    const renderNewInstance = () => {
        if (!showNew) {
            return null;
        }

        return (
            <JitsiMeeting
                roomName={generateRoomName()}
                getIFrameRef={handleJitsiIFrameRef2}
            />
        );
    };

    const handleJitsiIFrameRef2 = (iframeRef) => {
        iframeRef.style.marginTop = "10px";
        iframeRef.style.border = "10px dashed #df486f";
        iframeRef.style.padding = "5px";
        iframeRef.style.height = "400px";
    };

    return (
        <div className={"w-3/5 h-[55vh]"}>
            <JitsiMeeting
                roomName={generateRoomName()}
                spinner={renderSpinner}
                configOverwrite={{
                    subject: "lalalala",
                    hideConferenceSubject: false,
                }}
                userInfo={{ displayName: "Attendee Name" }}
                lang="en"
                onApiReady={(externalApi) => handleApiReady(externalApi)}
                onReadyToClose={handleReadyToClose}
                getIFrameRef={handleJitsiIFrameRef1}
                interfaceConfigOverwrite={{
                    SHOW_JITSI_WATERMARK: false,
                    SHOW_WATERMARK_FOR_GUESTS: false,
                    DISPLAY_WELCOME_PAGE_CONTENT: false,
                    SHOW_CHROME_EXTENSION_BANNER: false,
                    SHOW_POWERED_BY: false,
                    SHOW_PROMOTIONAL_CLOSE_PAGE: true,
                }}
            />
            {renderNewInstance()}

        </div>
    );
};

export default Room;