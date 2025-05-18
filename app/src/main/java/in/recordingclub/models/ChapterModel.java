package in.recordingclub.models;

public class ChapterModel {
    private String title;
    private String fileLink;

    public ChapterModel(String title, String fileLink) {
        this.title = title;
        this.fileLink = fileLink;
    }

    public String getTitle() { return title; }
    public String getFileLink() { return fileLink; }
}
