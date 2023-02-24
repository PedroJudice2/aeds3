import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

class Programa {

    public static void Carregar() {
        try (BufferedReader br = new BufferedReader(
                new FileReader("/mnt/d/Documentos/Escola/aeds3/Base de Dados/NetFlix.csv"))) {
            Filme filme = new Filme();
            br.readLine();
            String line = br.readLine();
            Long filePointer = (long) 0;

            while (line != null) {
                String[] vect = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                filme.setId();
                filme.setType(vect[1]);
                filme.setTitle(vect[2]);
                filme.setDirector(vect[3]);
                filme.setCountry(vect[5]);
                filme.setReleaseYear(Integer.parseInt(vect[7]));
                filme.setDescription(vect[11]);

                /*
                 * Escrever filmes
                 * System.out.println(filme);
                 * System.out.println();
                 */

                filePointer = escrever(filme, filePointer);

                line = br.readLine();
            }

            br.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static long escrever(Filme filme, Long filePointer) throws IOException {
        RandomAccessFile arq = new RandomAccessFile("/mnt/d/Documentos/Escola/aeds3/tps/tp01/dados/filmes.db",
                "rw");

        Long pos = filePointer;
        byte[] ba;
        if (pos == 0) {
            arq.writeInt(1);
            arq.writeBoolean(false);
            ba = filme.toByteArray();
            arq.writeInt(ba.length);
            arq.write(ba);
        } else {
            arq.seek(0);
            int id = arq.readInt();
            id++;
            arq.seek(0);
            arq.writeInt(id);
            arq.seek(pos);
            arq.writeBoolean(false);
            ba = filme.toByteArray();
            arq.writeInt(ba.length);
            arq.write(ba);
        }

        pos = arq.getFilePointer();
        arq.close();

        return pos;
    }

    public static void ler() throws IOException {
        RandomAccessFile arq = new RandomAccessFile("/mnt/d/Documentos/Escola/aeds3/tps/tp01/dados/filmes.db",
                "rw");
        arq.seek(0);
        System.out.println("Numero de filmes: " + arq.readInt());
        System.out.println();

        long currentPosition = arq.getFilePointer();
        long endPosition = arq.length();
        int len;
        byte[] ba;
        Filme filme = new Filme();
        while (currentPosition < endPosition) {
            arq.seek(arq.getFilePointer() + 1);
            len = arq.readInt();
            ba = new byte[len];
            arq.read(ba);
            filme.fromByteArray(ba);
            System.out.println(filme);
            System.out.println();
            currentPosition = arq.getFilePointer();
        }

        arq.close();
    }

    public static void main(String[] args) {
        // Carregar();
        try {
            ler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}