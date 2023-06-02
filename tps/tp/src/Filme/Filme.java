package Filme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import MyScanner.MyScanner;

/**
 * The `Filme` class represents a movie, with attributes such as title, type,
 * director, country, release year, and description.
 * It provides constructors to create a new `Filme` object, either by asking the
 * user to input the values or by passing them as parameters.
 * It also provides methods to get and set the values of its attributes, as well
 * as a static counter to keep track of the number of `Filme` objects created.
 *
 * @version 1.0
 * @since 2023-05-13
 * @author Pedro
 */
public class Filme {
    private static AtomicLong counter = new AtomicLong(0L);
    private boolean lapide;
    private long id;
    private String title;
    private String type;
    private String director;
    private String country;
    private int releaseYear;
    private String description;

    /**
     * constructs a new `Filme` object with the given parameters.
     * 
     * @param title
     * @param type
     * @param director
     * @param country
     * @param releaseYear
     * @param description
     */
    public Filme(String title, String type, String director, String country, int releaseYear, String description) {
        setId();
        setTitle(title);
        setType(type);
        setDirector(director);
        setCountry(country);
        setReleaseYear(releaseYear);
        setDescription(description);
    }

    /**
     * constructs a new `Filme` object by asking the user to input the values of
     * its attributes.
     * 
     * @param id
     */
    public Filme(Long id) {
        setIdManual(id);
        askFilme();
    }

    /**
     * constructs a new `Filme` object by asking the user to input the values of
     * its attributes.
     */
    public Filme() {
        setId();
        askFilme();
    }

    /**
     * asks the user to input the values of the attributes of the `Filme` object.
     */
    private void askFilme() {
        int ano;
        String text;
        boolean dataValida;
        System.out.println("Caso não queira atualizar algum campo especifico, deixa o campo em branco");
        System.out.print("Digite o Titulo do filme: ");
        setTitle(MyScanner.sc.nextLine());
        System.out.print("Digite o Tipo do filme: ");
        setType(MyScanner.sc.nextLine());
        System.out.print("Digite o(s) Diretor(es) do filme: ");
        setDirector(MyScanner.sc.nextLine());
        System.out.print("Digite o Pais de produção do filme: ");
        setCountry(MyScanner.sc.nextLine());
        do {
            dataValida = false;
            System.out.print("Digite o ano de produção do filme: ");
            text = MyScanner.sc.nextLine();
            if (text.isEmpty()) {
                setReleaseYear(0);
            } else {
                try {
                    ano = Integer.parseInt(text);
                    if (ano < 1900 || ano > 2023) {
                        System.out.println("Digite uma data valida!!!");
                        dataValida = true;
                    } else {
                        setReleaseYear(ano);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Digite uma data valida!!!");
                    dataValida = true;
                }
            }
        } while (dataValida);
        System.out.print("Digite a Descrição do filme: ");
        setDescription(MyScanner.sc.nextLine());
    }

    /**
     * return the id of the `Filme` object.
     * 
     * @return
     */
    public long getId() {
        return this.id;
    }

    /**
     * sets the id of the `Filme` object.
     */
    public void setId() {
        this.id = counter.incrementAndGet();
    }

    /**
     * set the counter to the given value.
     * 
     * @param value
     */
    public static void setCounter(long value) {
        counter = new AtomicLong(value);
    }

    /**
     * sets the id of the `Filme` object manually
     * 
     * @param id
     */
    public void setIdManual(long id) {
        this.id = id;
    }

    /**
     * return the title of the `Filme` object.
     * 
     * @return
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * sets the title of the `Filme` object.
     * 
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * return the type of the `Filme` object.
     * 
     * @return
     */
    public String getType() {
        return this.type;
    }

    /**
     * sets the type of the `Filme` object.
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * return the director of the `Filme` object.
     * 
     * @return
     */
    public String getDirector() {
        return this.director;
    }

    /**
     * sets the director of the `Filme` object.
     * 
     * @param director
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * return the country of the `Filme` object.
     * 
     * @return
     */
    public String getCountry() {
        return this.country;
    }

    /**
     * sets the country of the `Filme` object.
     * 
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * return the release year of the `Filme` object.
     * 
     * @return
     */
    public int getReleaseYear() {
        return this.releaseYear;
    }

    /**
     * sets the release year of the `Filme` object.
     * 
     * @param releaseYear
     */
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * return the description of the `Filme` object.
     * 
     * @return
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * sets the description of the `Filme` object.
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * return the lapide of the `Filme` object.
     * 
     * @return
     */
    public boolean getLapide() {
        return this.lapide;
    }

    /**
     * sets the lapide of the `Filme` object.
     * 
     * @param lapide
     */
    public void setLapide(boolean lapide) {
        this.lapide = lapide;
    }

    /**
     * {@inheritDoc}
     * compares the `Filme` object with another object.
     */
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

    /**
     * {@inheritDoc}
     * Converts the `Filme` object to a String.
     */
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

    /**
     * converts the `Filme` object to a byte array.
     * 
     * @return
     * @throws IOException
     */
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // escrever Id
        dos.writeLong(id);

        // escrever titulo
        byte[] ba = title.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(ba.length);
        dos.write(ba);

        // escrever tipo
        ba = type.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(ba.length);
        dos.write(ba);

        // escrever diretor
        ba = director.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(ba.length);
        dos.write(ba);

        // escrever pais
        ba = country.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(ba.length);
        dos.write(ba);

        // escrever ano de lançamento
        dos.writeInt(releaseYear);

        // escrever descrição
        ba = description.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(ba.length);
        dos.write(ba);

        return baos.toByteArray();
    }

    /**
     * converts a byte array to a `Filme` object.
     * 
     * @param ba
     * @throws IOException
     */
    public Filme(byte ba[]) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        // ler id
        id = dis.readLong();

        // ler titulo
        int len = dis.readInt();
        title = new String(dis.readNBytes(len), StandardCharsets.UTF_8);

        // ler tipo
        len = dis.readInt();
        type = new String(dis.readNBytes(len), StandardCharsets.UTF_8);

        // ler diretor
        len = dis.readInt();
        director = new String(dis.readNBytes(len), StandardCharsets.UTF_8);

        // ler pais
        len = dis.readInt();
        country = new String(dis.readNBytes(len), StandardCharsets.UTF_8);

        // ler ano de lançamento
        releaseYear = dis.readInt();

        // ler descrição
        len = dis.readInt();
        description = new String(dis.readNBytes(len), StandardCharsets.UTF_8);

    }

}