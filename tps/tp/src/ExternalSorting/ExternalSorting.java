package ExternalSorting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import DataStruct.DataStruct;
import FileOp.FileOp;
import Filme.Filme;

/**
 * The ExternalSorting class provides methods for sorting large files that
 * cannot fit into memory.
 * It uses the external merge sort algorithm, which involves dividing the file
 * into smaller chunks,
 * sorting them in memory, and then merging them back together. This process is
 * repeated until the entire
 * file is sorted. The class includes methods for sorting files of any type that
 * implement the Serializable
 * interface. It also includes methods for specifying the chunk size, the
 * maximum number of chunks to use,
 * and the comparator to use for sorting. The class assumes that the file is
 * stored on disk and can be accessed
 * using a RandomAccessFile object.
 * 
 * @version 1.0
 * @since 2023-05-13
 * @author Pedro
 * 
 */
public class ExternalSorting extends DataStruct {
    long listSize = 0;
    String dbPath;

    /**
     * Constructs an ExternalSorting object with the given path to the db file.
     * 
     * @param dbPath
     */
    public ExternalSorting(String dbPath) {
        try {
            setDbPath(dbPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * sets the path to the db file
     * 
     * @param dbPath
     * @throws FileNotFoundException
     */
    private void setDbPath(String dbPath) throws FileNotFoundException {
        this.dbPath = dbPath;
    }

    /**
     * search the movie with the given id in the db file
     * 
     * @param id
     * @return
     * @throws IOException
     */
    private long searchById(long id) throws IOException {
        RandomAccessFile arq = FileOp.arq;
        arq.seek(20);
        long currentPosition = arq.getFilePointer();
        long endPosition = arq.length();
        int len;
        long value = -1;
        while (currentPosition < endPosition) {
            long cursor = currentPosition;
            if (arq.readBoolean()) {
                len = arq.readInt();
                long temp = arq.getFilePointer();
                arq.seek(temp + len);
                currentPosition = arq.getFilePointer();
            } else {
                len = arq.readInt();
                long tempID = arq.readLong();
                if (id == tempID) {
                    value = cursor;
                    currentPosition = endPosition; // break
                } else if (id < tempID) {
                    currentPosition = endPosition; // break
                } else {
                    arq.seek(len + (cursor + 5));
                    currentPosition = arq.getFilePointer();
                }
            }
        }
        return value;
    }

    /**
     * {@inheritDoc}
     * deletes the movie with the given id from the db file
     */
    @Override
    public long delete(long id) {
        long value = search(id);
        if (value != -1) {
            addToList(value);
            listSize++;
        }
        return value;
    }

    /**
     * {@inheritDoc}
     * search for the movie with the given id in the db file
     */
    @Override
    public long search(long id) {
        long value = -1;
        try {
            value = searchById(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * {@inheritDoc}
     * update the movie with the given id in the db file
     */
    @Override
    public boolean update(long key, long newValue) {
        boolean answer = true;
        try {
            sort();
            answer = true;
        } catch (IOException e) {
            answer = false;
            e.printStackTrace();
        }
        return answer;
    }

    /**
     * {@inheritDoc}
     * insert the movie with the given id in the db file
     */
    @Override
    public void insert(long key, long value) {
        if (listSize > getListSize()) {
            listSize--;
            try {
                sort();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setLastId(value);
    }

    /**
     * sort the db file using the external merge sort algorithm
     * 
     * @throws IOException
     */
    private void sort() throws IOException {
        intercalaVariosCaminho(3);
    }

    /**
     * sort the db file using the external merge sort algorithm
     * 
     * @param caminhos
     * @throws IOException
     */
    private void intercalaVariosCaminho(int caminhos) throws IOException {
        RandomAccessFile arq = FileOp.arq;
        // pegar numero de filmes
        arq.seek(16);
        int nFilmes = arq.readInt();
        // testar se caminho é valido
        if (caminhos > nFilmes || caminhos < 2) {
            throw new IOException("caminho invalido");
        }

        // Criar fitas
        File[] fita = new File[caminhos * 2];
        for (int i = 0; i < (caminhos * 2); i++) {
            fita[i] = File.createTempFile("temp", ".db");
        }

        // Criar acesso para fitas
        RandomAccessFile[] raf = new RandomAccessFile[caminhos * 2];
        for (int i = 0; i < (caminhos * 2); i++) {
            raf[i] = new RandomAccessFile(fita[i], "rw");
        }

        // distribuição
        long currentPosition = arq.getFilePointer();
        long endPosition = arq.length();
        long[] fitaCursor = new long[caminhos];

        int len;
        byte[] ba;
        int blocos = 2048;
        for (int i = 0; currentPosition < endPosition; i++) {
            ArrayList<Filme> filmes = new ArrayList<Filme>();

            for (int j = 0; j < blocos && currentPosition < endPosition; j++) {
                Filme filmetemp;
                arq.seek(currentPosition + 1);
                len = arq.readInt();
                ba = new byte[len];
                arq.read(ba);
                filmetemp = new Filme(ba);
                long cursor = arq.getFilePointer();
                arq.seek(currentPosition);
                if (arq.readBoolean()) {
                    filmetemp.setLapide(true);
                } else {
                    filmetemp.setLapide(false);
                }
                filmes.add(filmetemp);
                currentPosition = cursor;
            }

            filmeSort(filmes);

            for (int j = 0; j < filmes.size(); j++) {
                if (filmes.get(j).getLapide()) {
                    fitaCursor[i % caminhos] = write(filmes.get(j), fitaCursor[i % caminhos], true, raf[i % caminhos]);
                } else {
                    fitaCursor[i % caminhos] = write(filmes.get(j), fitaCursor[i % caminhos], false, raf[i % caminhos]);
                }
            }

        }

        // intercalação
        boolean exit = true;
        int sizeSeg = 0;
        for (int i = 1; exit; i++) {
            // zerar o ponteiro de cada grupo de arquivo
            for (int j = 0; j < caminhos; j++) {
                if (i % 2 != 0) {
                    raf[(j % caminhos) + caminhos].setLength(0);
                    raf[j % caminhos].seek(0);
                } else {
                    raf[j % caminhos].setLength(0);
                    raf[(j % caminhos) + caminhos].seek(0);
                }
            }
            // quantidade de segmentos por passagem
            int seg = (int) Math.ceil((double) nFilmes / (caminhos * (blocos * i)));
            if (i == 1) {
                sizeSeg = blocos;
            } else {
                sizeSeg = sizeSeg * caminhos;
            }

            // percorrer setor
            int index = 0;
            for (int j = 1; j <= seg; j++) {
                boolean[] acabou = new boolean[caminhos];
                int[] fitaIndex = new int[caminhos];
                for (int x = 0; x < caminhos; x++) {
                    acabou[x] = false;
                    fitaIndex[x] = 0;
                }

                // for representado o numero de comparações
                for (int z = 0; z < caminhos * sizeSeg; z++) {
                    boolean flag = true;
                    MinHeap heap = new MinHeap(caminhos);
                    // compara filmes e achar o menor
                    for (int x = 0; x < caminhos; x++) {
                        if (i % 2 != 0) {
                            index = x % caminhos;
                        } else {
                            index = (x % caminhos) + caminhos;
                        }
                        if (raf[index].getFilePointer() == raf[index].length()) {
                            acabou[index % caminhos] = true;
                        }

                        if (!(acabou[index % caminhos])) {
                            Filme temp;
                            long cursor = raf[index].getFilePointer();
                            raf[index].readBoolean();
                            len = raf[index].readInt();
                            ba = new byte[len];
                            raf[index].read(ba);
                            temp = new Filme(ba);
                            long last = raf[index].getFilePointer();
                            raf[index].seek(cursor);
                            if (raf[index].readBoolean()) {
                                temp.setLapide(true);
                            } else {
                                temp.setLapide(false);
                            }
                            heap.insert(temp, index, last);
                            raf[index].seek(cursor);
                        }
                    }

                    // pegar menor filme
                    Node temp = heap.extractMin();

                    // andar com o ponteiro na fita vencedora
                    raf[temp.index].seek(temp.cursor);
                    fitaIndex[temp.index % caminhos]++;

                    // checar novamente se o segmento na fita acabou
                    if (fitaIndex[temp.index % caminhos] == sizeSeg
                            || raf[temp.index].getFilePointer() == raf[temp.index].length()) {
                        acabou[temp.index % caminhos] = true;
                    }

                    // ver em qual fita escrever menor filme
                    if (i % 2 != 0) {
                        index = ((j - 1) % caminhos) + caminhos;
                    } else {
                        index = (j - 1) % caminhos;
                    }

                    // esvrever menor filme marcado ou não
                    if (temp.filme.getLapide()) {
                        write(temp.filme, raf[index].getFilePointer(), true, raf[index]);
                    } else {
                        write(temp.filme, raf[index].getFilePointer(), false, raf[index]);
                    }

                    // checar se as duas fitas ou acabaram o segmento ou chegaram no fim
                    for (int x = 0; x < caminhos; x++) {
                        if (!(acabou[x])) {
                            flag = false;
                        }
                    }

                    // checar se chegou no fim do segmento
                    if (flag) {
                        z = caminhos * sizeSeg; // break
                    }
                }

            }

            if (seg == 1) {
                exit = false;
                fileClone(arq, raf[index]);
            }
        }

        // exlcuir fitas
        for (int i = 0; i < (caminhos * 2); i++) {
            raf[i].close();
            fita[i].deleteOnExit();
        }
    }

    /**
     * clones the file to the db file
     * 
     * @param a
     * @param b
     * @throws IOException
     */
    private void fileClone(RandomAccessFile a, RandomAccessFile b) throws IOException {
        a.seek(0);
        byte[] header = FileOp.getHeader();
        a.setLength(0);
        a.write(header);
        b.seek(0);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = b.read(buffer)) != -1) {
            a.write(buffer, 0, bytesRead);
        }

    }

    /**
     * sort a movie array
     * 
     * @param filme
     */
    private void filmeSort(ArrayList<Filme> filme) {
        quicksort(0, filme.size() - 1, filme);
    }

    /**
     * quicksort for sorting movies
     * 
     * @param esq
     * @param dir
     * @param filme
     */
    private void quicksort(int esq, int dir, ArrayList<Filme> filme) {
        int i = esq, j = dir;
        long pivo = filme.get((dir + esq) / 2).getId();
        while (i <= j) {
            while (filme.get(i).getId() < pivo)
                i++;
            while (filme.get(j).getId() > pivo)
                j--;
            if (i <= j) {
                // swap
                Filme temp = filme.get(i);
                filme.set(i, filme.get(j));
                filme.set(j, temp);
                i++;
                j--;
            }
        }
        if (esq < j)
            quicksort(esq, j, filme);
        if (i < dir)
            quicksort(i, dir, filme);
    }

    /**
     * write a movie in the file
     * 
     * @param filme
     * @param filePointer
     * @param type
     * @param arq
     * @return
     * @throws IOException
     */
    private long write(Filme filme, Long filePointer, Boolean type, RandomAccessFile arq) throws IOException {
        Long pos = filePointer;
        byte[] ba;
        arq.seek(pos);
        arq.writeBoolean(type);
        ba = filme.toByteArray();
        arq.writeInt(ba.length);
        arq.write(ba);
        pos = arq.getFilePointer();

        return pos;
    }

}
