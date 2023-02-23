import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Filme {
    private static int size;
    private int id;
    private String title;
    private String type;
    private String director;
    private String country;
    private int releaseYear;
    private String description;

    public Filme() {
        size = 0;
    }

    public Filme(String title, String type, String director, String country, int releaseYear, String description) {
        this.id = size++;
        this.title = title;
        this.type = type;
        this.director = director;
        this.country = country;
        this.releaseYear = releaseYear;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public void setId() {
        this.id = size++;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirector() {
        return this.director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getReleaseYear() {
        return this.releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Filme title(String title) {
        setTitle(title);
        return this;
    }

    public Filme type(String type) {
        setType(type);
        return this;
    }

    public Filme director(String director) {
        setDirector(director);
        return this;
    }

    public Filme country(String country) {
        setCountry(country);
        return this;
    }

    public Filme releaseYear(int releaseYear) {
        setReleaseYear(releaseYear);
        return this;
    }

    public Filme description(String description) {
        setDescription(description);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Filme)) {
            return false;
        }
        Filme filme = (Filme) o;
        return id == filme.id && Objects.equals(title, filme.title) && Objects.equals(type, filme.type)
                && Objects.equals(director, filme.director) && Objects.equals(country, filme.country)
                && releaseYear == filme.releaseYear && Objects.equals(description, filme.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, type, director, country, releaseYear, description);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", title='" + getTitle() + "'" +
                ", type='" + getType() + "'" +
                ", director='" + getDirector() + "'" +
                ", country='" + getCountry() + "'" +
                ", releaseYear='" + getReleaseYear() + "'" +
                ", description='" + getDescription() + "'" +
                "}";
    }

    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeUTF(type);
        dos.writeUTF(director);
        dos.writeUTF(country);
        dos.writeInt(releaseYear);
        dos.writeUTF(description);

        return baos.toByteArray();
    }

}
