import FileSection, { Section } from "./FileSection";

const SessionFiles = () => {
  const sections: Section[] = [
    {
      name: "Van-Dung Hoang",
      files: [
        { name: "ContextualSystemsDemoVideo.mp4", type: "video" },
        { name: "ContextualSystemsGlossary.pdf", type: "pdf" },
      ],
    },
    {
      name: "Dinh-Hien Nguyen",
      files: [
        { name: "ComputerVisionIntroduction.mp4", type: "video" },
        { name: "ComputerVisionGlossary.pdf", type: "pdf" },
      ],
    },
    {
      name: "Kang-Hyun Jo",
      files: [
        { name: "IntelligentSystemsResearch.pdf", type: "pdf" },
        { name: "IntelligentSystemsDemoCode.zip", type: "zip" },
        { name: "IntelligentSystemsDemoCode.zip", type: "zip" },
        { name: "IntelligentSystemsDemoCode.zip", type: "zip" },
        { name: "IntelligentSystemsDemoCode.zip", type: "zip" },
      ],
      canDelete: true,
      canUpload: true,
    },
    {
      name: "Huyen Trang Phan",
      files: [
        { name: "IntroductionVideo.mp4", type: "video" },
        { name: "PresentationSlides.pptx", type: "pptx" },
      ],
    },
  ];

  return (
    <div className="flex flex-col sm:flex-row gap-4 p-4">
      {sections.map((section) => (
        <FileSection key={section.name} section={section} />
      ))}
    </div>
  );
};

export default SessionFiles;