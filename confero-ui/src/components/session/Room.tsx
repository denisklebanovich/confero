import { useRef, useState } from "react";
import { JitsiMeeting } from "@jitsi/react-sdk";
import {useUser} from "@/state/UserContext.tsx";
const Room = ({roomID, title}) => {
    const apiRef = useRef();
    const [logItems, updateLog] = useState([]);
    const [showNew, toggleShowNew] = useState(false);
    const [knockingParticipants, updateKnockingParticipants] = useState([]);
    const {profileData: {firstName, lastName, emails}, isLoading: isLoadingProfile} = useUser();

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
        // @ts-ignore
        apiRef.current.executeCommand("toggleChat");
        updateLog((items) => [
            ...items,
            `you have ${payload.unreadCount} unread messages`,
        ]);
    };

    const resolveKnockingParticipants = (condition) => {
        knockingParticipants.forEach((participant) => {
            // @ts-ignore
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
        alert("Ready to close...");
    };

    const handleApiReady = (apiObj) => {
        // @ts-ignore
        apiRef.current = apiObj;
        // @ts-ignore
        apiRef.current.on("knockingParticipant", handleKnockingParticipant);
        // @ts-ignore
        apiRef.current.on("audioMuteStatusChanged", (payload) =>
            handleAudioStatusChange(payload, "audio")
        );
        // @ts-ignore
        apiRef.current.on("videoMuteStatusChanged", (payload) =>
            handleAudioStatusChange(payload, "video")
        );
        // @ts-ignore
        apiRef.current.on("raiseHandUpdated", printEventOutput);
        // @ts-ignore
        apiRef.current.on("titleViewChanged", printEventOutput);
        // @ts-ignore
        apiRef.current.on("chatUpdated", handleChatUpdates);
        // @ts-ignore
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
                roomName={roomID}
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
        <div className={"w-3/5"}>
            {!isLoadingProfile
                &&
            <>
            <JitsiMeeting
                roomName={roomID}
                spinner={renderSpinner}
                configOverwrite={{
                    subject: title,
                    hideConferenceSubject: false,
                }}
                userInfo={{ displayName: "", email: emails[0].email }}
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
            </>}


        </div>
    );
};

export default Room;