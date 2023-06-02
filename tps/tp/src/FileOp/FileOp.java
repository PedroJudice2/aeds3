package FileOp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.Iterator;

import BPlusTree.BPlusTree;
import DataStruct.DataStruct;
import ExtensibleHashing.HashTable;
import ExternalSorting.ExternalSorting;
import Filme.Filme;

/**
 * Class that implements the operations of the db file
 * 
 * @version 1.0
 * @since 2023-02-24
 * @author Pedro
 */
public class FileOp {

    public DataStruct data;

    public static String csvPath;
    public static String dbPath;
    public static RandomAccessFile arq;
    private int algorithm;

    /**
     * Constructor for FileOp class that receives the path of the db file and the
     * csv file and the algorithm to be used
     * 
     * @param dbPath
     * @param csvPath
     * @param op
     */
    public FileOp(String dbPath, String csvPath, int op) {
        setCsvPath(csvPath);
        setDbPath(dbPath);
        try {
            setFile(dbPath);
            setData(getAlgorithm(op));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for FileOp class that receives the path of the db file and the
     * csv file
     * 
     * @param dbPath
     * @param csvPath
     */
    public FileOp(String dbPath, String csvPath) {
        setCsvPath(csvPath);
        setDbPath(dbPath);
        try {
            setFile(dbPath);
            setData(getAlgorithm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * setter for csvPath
     * 
     * @param csvPath
     */
    private void setCsvPath(String csvPath) {
        FileOp.csvPath = csvPath;
    }

    /**
     * setter for dbPath
     * 
     * @param dbPath
     */
    private void setDbPath(String dbPath) {
        FileOp.dbPath = dbPath;
    }

    /**
     * setter for data struct interface
     * 
     * @param data
     */
    private void setData(DataStruct data) {
        this.data = data;
    }

    /**
     * setter for RandomAccessFile
     * 
     * @param dbPath
     * @throws FileNotFoundException
     */
    private void setFile(String dbPath) throws FileNotFoundException {
        FileOp.arq = new RandomAccessFile(dbPath,
                "rw");
    }

    /**
     * reads all the data from de db file
     * 
     * @throws IOException
     */
    public static void readAll() throws IOException {
        arq.seek(16);
        System.out.println("Numero de filmes: " + arq.readInt());
        System.out.println();

        long currentPosition = arq.getFilePointer();
        long endPosition = arq.length();
        int len;
        byte[] ba;
        Filme filme;
        while (currentPosition < endPosition) {
            arq.seek(arq.getFilePointer() + 1);
            len = arq.readInt();
            ba = new byte[len];
            arq.read(ba);
            filme = new Filme(ba);
            System.out.println(filme);
            System.out.println();
            currentPosition = arq.getFilePointer();
        }

    }

    /**
     * get the number of movies in the db file
     * 
     * @return
     * @throws IOException
     */
    public static int getIntCabecalho() throws IOException {
        arq.seek(16);
        int valor = arq.readInt();
        return valor;
    }

    /**
     * get algorithm used in the db file
     * 
     * @return
     * @throws IOException
     */
    public DataStruct getAlgorithm() throws IOException {
        arq.seek(4);
        int op = arq.readInt();
        return getAlgorithm(op);
    }

    /**
     * get algorithm used in the db file
     * 
     * @param op
     * @return
     */
    public DataStruct getAlgorithm(int op) {
        if (op == 1) {
            return new BPlusTree(8);
        } else if (op == 2) {
            return new HashTable();
        } else if (op == 3) {
            return new ExternalSorting(dbPath);
        }
        algorithm = op;
        return null;
    }

    /**
     * create the db file based on a csv file
     * 
     * @throws IOException
     */
    public void loadFile() throws IOException {
        // open db file
        arq.setLength(0);
        // write innformation to the db file, read the README.md file to understand
        arq.writeInt(0); // is not compressed
        arq.writeInt(algorithm); // is using extensible hashing
        arq.writeLong(0); // the id of the last movie
        arq.writeInt(0); // the number of movies
        // open csv file
        try (BufferedReader br = new BufferedReader(
                new FileReader(csvPath))) {
            br.readLine(); // jump csv header
            String line = br.readLine();

            // In each iteration of the loop, populate/assign a movie to the variable.
            while (line != null) {
                String[] vect = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                Filme filme = new Filme(vect[1], vect[2], vect[3], vect[5], Integer.parseInt(vect[7]), vect[11]);
                // write the movie to the file
                write(filme);
                line = br.readLine();
            }

            br.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * write the header of the db file
     * 
     * @param id
     * @throws IOException
     */
    private void writeHeader(long id) throws IOException {
        // write the last movie id
        arq.seek(8);
        arq.writeLong(id);
        // write number of movies
        int numberMovies = arq.readInt();
        numberMovies++;
        arq.seek(16);
        arq.writeInt(numberMovies);
    }

    /**
     * write a movie in the end of the file and increment the header id
     * 
     * @param filme
     * @throws IOException
     */
    private void write(Filme filme) throws IOException {
        Long pos = arq.length();
        byte[] ba;
        // write header
        writeHeader(filme.getId());
        // write flag
        arq.seek(pos);
        arq.writeBoolean(false);
        // write movie lenght
        ba = filme.toByteArray();
        arq.writeInt(ba.length);
        // write movie
        arq.write(ba);
        // insert movie on the hash table
        data.insert(filme.getId(), pos);

        pos = arq.getFilePointer();
    }

    /**
     * write a movie in a specific position (overwrite film)
     * 
     * @param filme
     * @param filePointer
     * @throws IOException
     */
    private void writeUpdate(Filme filme, Long filePointer) throws IOException {
        Long pos = filePointer;
        byte[] ba;
        // change flag
        arq.seek(pos);
        arq.writeBoolean(false);
        // write movie
        ba = filme.toByteArray();
        arq.readInt(); // jump lenght
        arq.write(ba);
        // update hash table
        data.update(filme.getId(), filePointer);

        pos = arq.getFilePointer();
    }

    /**
     * write a movie in the end of the file without increment the header id
     * 
     * @param filme
     * @throws IOException
     */
    private void writeUpdate(Filme filme) throws IOException {
        long pos = arq.length();
        byte[] ba;
        // write flag
        arq.seek(pos);
        arq.writeBoolean(false);
        // write lenght
        ba = filme.toByteArray();
        arq.writeInt(ba.length);
        // wirte movie
        arq.write(ba);
        // update hash
        data.update(filme.getId(), pos);

        pos = arq.getFilePointer();
    }

    /**
     * wirte movie on a specific position
     * 
     * @param filme
     * @param filePointer
     * @throws IOException
     */
    private void writeInclude(Filme filme, Long filePointer) throws IOException {
        Long pos = filePointer;
        byte[] ba;
        // write header
        writeHeader(filme.getId());
        // change flag
        arq.seek(pos);
        arq.writeBoolean(false);
        // write movie
        ba = filme.toByteArray();
        arq.readInt(); // jump movie lenght
        arq.write(ba);
        // insert the movie on the hash table
        data.insert(filme.getId(), filePointer);

        pos = arq.getFilePointer();
    }

    /**
     * read a movie from the db file by the movie id
     * 
     * @param id
     * @return
     * @throws IOException
     */
    public Filme read(long id) throws IOException {
        if (id > data.getLastId()) {
            return null;
        }
        long cursor = data.search(id);
        if (cursor == -1) {

            return null;
        }
        arq.seek(cursor + 1);
        int len = arq.readInt();
        byte[] ba = new byte[len];
        arq.read(ba);
        Filme filme = new Filme(ba);
        return filme;

    }

    /**
     * Serialize the data structure and close the db file
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void close() throws FileNotFoundException, IOException {
        ObjectOutputStream objectOutput = new ObjectOutputStream(
                new FileOutputStream("src/resources/dataStructure/Data.db"));
        objectOutput.writeObject(data);
        objectOutput.close();
        arq.close();
    }

    /**
     * load the data structure and the db file
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadData() throws FileNotFoundException, IOException, ClassNotFoundException {
        arq.seek(8);
        long id = arq.readLong();
        Filme.setCounter(id);
        ObjectInputStream objectInput = new ObjectInputStream(
                new FileInputStream("src/resources/dataStructure/Data.db"));
        this.data = (DataStruct) objectInput.readObject();
        objectInput.close();
    }

    /**
     * delete a movie from the db file
     * 
     * @param id
     * @return
     * @throws IOException
     */
    public boolean delete(long id) throws IOException {
        if (id > data.getLastId()) {
            return false;
        }
        long cursor = data.delete(id);
        if (cursor == -1) {
            return false;
        }

        // decrement number of movies
        arq.seek(16);
        int movies = arq.readInt();
        movies--;
        arq.seek(16);
        arq.writeInt(movies);
        // change movie flag
        arq.seek(cursor);
        arq.writeBoolean(true);
        return true;
    }

    /**
     * update a movie from the db file
     * 
     * @param novoFilme
     * @return
     * @throws IOException
     */
    public boolean update(Filme novoFilme) throws IOException {
        if (novoFilme.getId() > data.getLastId()) {

            return false;
        }
        int len;
        int flag = 0;
        byte[] ba;
        Boolean achou = false;

        long cursor = data.search(novoFilme.getId());
        if (cursor == -1) {

            return false;
        }
        arq.seek(cursor + 1);
        len = arq.readInt();
        ba = new byte[len];
        arq.read(ba);
        Filme filme = new Filme(ba);
        // alterar campo do titulo
        if (!(novoFilme.getTitle().isBlank())) {
            if (!(novoFilme.getTitle().equals(filme.getTitle()))) {
                if (novoFilme.getTitle().length() > filme.getTitle().length()) {
                    flag = 1;
                }
            }
            filme.setTitle(novoFilme.getTitle());
        }
        // alterar campo do tipo
        if (!(novoFilme.getType().isBlank())) {
            if (!(novoFilme.getType().equals(filme.getType()))) {
                if (novoFilme.getType().length() > filme.getType().length()) {
                    flag = 1;
                }
            }
            filme.setType(novoFilme.getType());
        }
        // alterar campo do diretor
        if (!(novoFilme.getDirector().isBlank())) {
            if (!(novoFilme.getDirector().equals(filme.getDirector()))) {
                if (novoFilme.getDirector().length() > filme.getDirector().length()) {
                    flag = 1;
                }
            }
            filme.setDirector(novoFilme.getDirector());
        }
        // alterar compo do pais
        if (!(novoFilme.getCountry().isBlank())) {
            if (!(novoFilme.getCountry().equals(filme.getCountry()))) {
                if (novoFilme.getCountry().length() > filme.getCountry().length()) {
                    flag = 1;
                }
            }
            filme.setCountry(novoFilme.getCountry());
        }
        // alterar campo do ano de lançamento
        if (novoFilme.getReleaseYear() != 0) {
            filme.setReleaseYear(novoFilme.getReleaseYear());
        }
        // alterar campo de descrição
        if (!(novoFilme.getDescription().isBlank())) {
            if (!(novoFilme.getDescription().equals(filme.getDescription()))) {
                if (novoFilme.getDescription().length() > filme.getDescription().length()) {
                    flag = 1;
                }
            }
            filme.setDescription(novoFilme.getDescription());
        }
        if (flag == 1) {
            arq.seek(cursor);
            arq.writeBoolean(true);
            writeUpdate(filme);
            achou = true;
        } else {
            writeUpdate(filme, cursor);
            achou = true;
        }
        return achou;
    }

    /**
     * write a movie in the db file
     * 
     * @param filme
     * @throws IOException
     */
    public void include(Filme filme) throws IOException {
        byte[] ba;
        boolean achou = false;

        Iterator<Long> iterator = data.getList().iterator();

        while (iterator.hasNext()) {
            long cursor = iterator.next();
            arq.seek(cursor + 1);
            int tamanho = arq.readInt();
            ba = filme.toByteArray();
            if (ba.length <= tamanho) {
                iterator.remove();
                writeInclude(filme, cursor);
                achou = true;
                break;
            }
        }
        if (achou == false) {
            write(filme); // ponteiro direcionado para ultima posição do arquivo
        }
    }

    /**
     * get the header of the db file
     * 
     * @return
     */
    public static byte[] getHeader() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(arq.readInt());
            dos.writeInt(arq.readInt());
            dos.writeLong(arq.readLong());
            dos.writeInt(arq.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}
