package in.recordingclub.models;

public class BookModel {
    private int id;
    private String name;

    public BookModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
