package mk.tr.coursedy.models;

public class RegisterUser {

    private String id;
    private String nameSurname;
    private String email;
    private String pictureUrl;
    private String type;

    public RegisterUser(){

    }

    public RegisterUser(String id, String nameSurname, String email, String pictureUrl, String type) {
        this.id = id;
        this.nameSurname = nameSurname;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
