import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

class Programa {

    public static void Carregar() {
        try (BufferedReader br = new BufferedReader(
                new FileReader("/mnt/d/Documentos/Escola/aeds3/Base de Dados/NetFlix.csv"))) {
            Filme filme = new Filme();
            br.readLine(); // a primeira linha é o reader e não grava, pular linha
            String line = br.readLine(); // lê a segunda linha 
            Long filePointer = (long) 0; // Onde quer que iniciar o arquivo

            while (line != null) {
                String[] vect = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // quebra as linhas
                filme.setId();  // não precisa de parametro, criar seu propio ID de 1 em 1
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

                filePointer = escrever(filme, filePointer); // passar o 1º filme

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
            arq.writeBoolean(false); // lápide
            ba = filme.toByteArray(); // transforma o filme em um vetor de byte
            arq.writeInt(ba.length);  // tamanho
            arq.write(ba); // informações 
        } else {
            arq.seek(0); // posicionar o ponteiro (seek),posição 0
            int id = arq.readInt();
            id++;
            arq.seek(0);
            arq.writeInt(id); // sobrescrever para indicar quantos IDs tem no arquivo
            arq.seek(pos); // pra voltar para a posição que o ultimo ID transcreveu
            arq.writeBoolean(false);
            ba = filme.toByteArray();
            arq.writeInt(ba.length);
            arq.write(ba);
        }

        pos = arq.getFilePointer(); //onde parou, quantos bytes passaram
        arq.close();

        return pos; // retorna e grava no filePointer
    }

    public static void ler() throws IOException {
        RandomAccessFile arq = new RandomAccessFile("/mnt/d/Documentos/Escola/aeds3/tps/tp01/dados/filmes.db",
                "rw");
        arq.seek(0);
        System.out.println("Numero de filmes: " + arq.readInt()); // ler cabeçalho
        System.out.println();

        long currentPosition = arq.getFilePointer(); // Onde está o ponteiro
        long endPosition = arq.length(); // onde o final do arquivo acaba
        int len;
        byte[] ba;
        Filme filme = new Filme();
        while (currentPosition < endPosition) { // percorrer tudo 
            arq.seek(arq.getFilePointer() + 1); // pular a lápide
            len = arq.readInt(); // tamanho de bytes
            ba = new byte[len]; // cria o vetor do tamanho de bytes
            arq.read(ba); //  e por fim le
            filme.fromByteArray(ba); // transforma o vetor de bytes em um filme
            System.out.println(filme); // printa o filme
            System.out.println();
            currentPosition = arq.getFilePointer(); // posição que o filme terminou
        } // ate o final

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