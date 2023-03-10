import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

class Programa {

    // COLOQUE O CAMINHO DA DO SEU CSV E DO SEU AQUIVO
    public static String csvPath = "/mnt/d/Documentos/Escola/aeds3/Base de Dados/Pasta1.csv";
    public static String dbPath = "/mnt/d/Documentos/Escola/aeds3/tps/tp01/dados/filmes.db";

    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        String choice;
        try {
            System.out.println("Gostaria de carregar a base? ");
            String carrega = sc.nextLine();
            if (Pattern.matches("^(?:1|t(?:rue)?|y(?:es)?|ok(?:ay)?|s(?:im)?)$", carrega)) {
                System.out.println("Carregando...");
                Carregar();
            }
            // lerTodos();
            do {
                System.out.print("\033[H\033[2J");
                System.out.println("Opções: ");
                System.out.println("Deletar op 1 ");
                System.out.println("pesquisar op 2 ");
                System.out.println("Atualizar op 3 ");
                System.out.println("Adicionar op 4 ");
                System.out.println("Ordenar op 5 ");
                System.out.println("ler todos op 6");
                System.out.println(getIntCabecalho());
                int op;

                // limitar as opções 1 a 4
                do {
                    System.out.print("Digite a opção desejada: ");
                    op = sc.nextInt();
                } while (op != 1 && op != 2 && op != 3 && op != 4 && op != 5 && op != 6);

                // Opção para exclusão
                if (op == 1) {
                    System.out.print("Digite o id desejado: ");
                    int idBusca = sc.nextInt();
                    sc.nextLine();
                    System.out.println();
                    if (delete(idBusca)) {
                        System.out.println("filme excluido com sucesso!");
                    } else {
                        System.out.println("Id não encontrado!");
                    }

                    // Opção para busca
                } else if (op == 2) {
                    System.out.print("Digite o id desejado: ");
                    int idBusca = sc.nextInt();
                    sc.nextLine();
                    System.out.println();
                    Filme filme = lerId(idBusca);
                    if (filme != null) {
                        System.out.println(filme);
                    } else {
                        System.out.println("Id não encontrado!");
                    }

                    // Opção para atualizar
                } else if (op == 3) {
                    System.out.print("Digite o id desejado: ");
                    int idBusca = sc.nextInt();
                    sc.nextLine();
                    System.out.println();
                    Filme filme = lerId(idBusca);
                    if (filme != null) {
                        update(getFilmeUpdate(idBusca));
                        System.out.println("filme Atualizado com sucesso!");
                    } else {
                        System.out.println("Id não encontrado!");
                    }

                    // Opção para adicionar
                } else if (op == 4) {
                    sc.nextLine();
                    Filme filme = getFilmeInclude();
                    System.out.println(filme);
                    chargeFilmeNovo(filme);
                } else if (op == 5) {
                    sc.nextLine();
                    testeIntercala();
                } else if (op == 6) {
                    sc.nextLine();
                    lerTodos();
                }
                System.out.println();
                System.out.println("Deseja fazer alguma outra operação?");
                choice = sc.nextLine();
                choice.toLowerCase();
            } while (Pattern.matches("^(?:1|t(?:rue)?|y(?:es)?|ok(?:ay)?|s(?:im)?)$", choice));

        } catch (IOException e) {
            e.printStackTrace();
        }

        sc.close();
    }

    public static void Carregar() throws FileNotFoundException {
        RandomAccessFile arq = new RandomAccessFile("/mnt/d/Documentos/Escola/aeds3/tps/tp01/dados/filmes.db",
                "rw");
        try (BufferedReader br = new BufferedReader(
                new FileReader(csvPath))) {
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

                filePointer = escrever(filme, filePointer, 0, arq);

                line = br.readLine();
            }

            br.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static long escrever(Filme filme, Long filePointer, int type, RandomAccessFile arq) throws IOException {

        Long pos = filePointer;
        byte[] ba;
        if (pos == 0 && type != 1 && type != 3) {
            arq.writeInt(1);
            arq.writeBoolean(false);
            ba = filme.toByteArray();
            arq.writeInt(ba.length);
            arq.write(ba);
        } else if (type == 1) {
            arq.seek(pos);
            arq.writeBoolean(false);
            ba = filme.toByteArray();
            arq.writeInt(ba.length);
            arq.write(ba);
        } else if (type == 2) {
            arq.seek(0);
            int id = arq.readInt();
            id++;
            arq.seek(0);
            arq.writeInt(id);
            arq.seek(pos);
            arq.writeBoolean(false);
            ba = filme.toByteArray();
            arq.readInt();
            arq.write(ba);
        } else if (type == 3) {
            arq.seek(pos);
            arq.writeBoolean(true);
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

        return pos;
    }

    public static void lerTodos() throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
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

    public static void lerTodos(RandomAccessFile arq) throws IOException {
        arq.seek(0);
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

    }

    public static Filme[] getNFilms(int n, long cursor) throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        arq.seek(cursor);
        int len;
        byte[] ba;
        Filme[] filme = new Filme[n];

        for (int i = 0; i < n; i++) {
            arq.seek(arq.getFilePointer() + 1);
            len = arq.readInt();
            ba = new byte[len];
            arq.read(ba);
            filme[i] = new Filme();
            filme[i].fromByteArray(ba);
        }

        arq.close();

        return filme;
    }

    public static void filmeSort(ArrayList<Filme> filme) {
        quicksort(0, filme.size() - 1, filme);
    }

    public static void quicksort(int esq, int dir, ArrayList<Filme> filme) {
        int i = esq, j = dir;
        int pivo = filme.get((dir + esq) / 2).getId();
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

    public static Filme lerId(int id) throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        if (id > arq.readInt()) {
            arq.close();
            return null;
        }
        long currentPosition = arq.getFilePointer();
        long endPosition = arq.length();
        int len;
        byte[] ba;
        Boolean achou = false;
        Filme filme = new Filme();
        while (currentPosition < endPosition) {
            if (arq.readBoolean()) {
                len = arq.readInt();
                long temp = arq.getFilePointer();
                arq.seek(temp + len);
                currentPosition = arq.getFilePointer();
            } else {
                len = arq.readInt();
                ba = new byte[len];
                arq.read(ba);
                filme.fromByteArray(ba);
                if (filme.getId() == id) {
                    currentPosition = endPosition;
                    achou = true;
                } else {
                    currentPosition = arq.getFilePointer();
                }
            }
        }
        arq.close();
        if (achou) {
            return filme;
        } else {
            return null;
        }
    }

    public static boolean delete(int id) throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        if (id > arq.readInt()) {
            arq.close();
            return false;
        }
        long currentPosition = arq.getFilePointer();
        long endPosition = arq.length();
        int len;
        Boolean achou = false;
        while (currentPosition < endPosition) {
            long cursor = currentPosition;
            if (arq.readBoolean()) {
                len = arq.readInt();
                long temp = arq.getFilePointer();
                arq.seek(temp + len);
                currentPosition = arq.getFilePointer();
            } else {
                len = arq.readInt();
                int tempID = arq.readInt();
                if (id == tempID) {
                    arq.seek(cursor);
                    arq.writeBoolean(true);
                    achou = true;
                    currentPosition = endPosition;
                } else {
                    arq.seek(len + (cursor + 5));
                    currentPosition = arq.getFilePointer();
                }
            }
        }
        arq.close();
        if (achou) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean update(Filme novoFilme) throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        if (novoFilme.getId() > arq.readInt()) {
            arq.close();
            return false;
        }
        long currentPosition = arq.getFilePointer();
        long endPosition = arq.length();
        int len;
        int flag = 0;
        byte[] ba;
        Boolean achou = false;
        Filme filme = new Filme();
        while (currentPosition < endPosition) {
            long cursor = arq.getFilePointer();
            if (arq.readBoolean()) {
                len = arq.readInt();
                long temp = arq.getFilePointer();
                arq.seek(temp + len);
                currentPosition = arq.getFilePointer();
            } else {
                len = arq.readInt();
                ba = new byte[len];
                arq.read(ba);
                filme.fromByteArray(ba);
                if (filme.getId() == novoFilme.getId()) {
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
                        escrever(filme, arq.length(), 1, arq);
                        // TODO chamar função de ordenação
                        currentPosition = endPosition;
                        achou = true;
                    } else {
                        escrever(filme, cursor, 1, arq);
                        currentPosition = endPosition;
                        achou = true;
                    }
                } else {
                    currentPosition = arq.getFilePointer();
                }
            }
        }
        arq.close();
        return achou;
    }

    public static Filme getFilmeUpdate(int id) {

        Filme filme = new Filme();
        filme.setIdManual(id);
        int ano;
        String text;
        boolean dataValida;
        System.out.println("Caso não queira atualizar algum campo especifico, deixa o campo em branco");
        System.out.print("Digite o Titulo do filme: ");
        filme.setTitle(sc.nextLine());
        System.out.print("Digite o Tipo do filme: ");
        filme.setType(sc.nextLine());
        System.out.print("Digite o(s) Diretor(es) do filme: ");
        filme.setDirector(sc.nextLine());
        System.out.print("Digite o Pais de produção do filme: ");
        filme.setCountry(sc.nextLine());
        do {
            dataValida = false;
            System.out.print("Digite o ano de produção do filme: ");
            text = sc.nextLine();
            if (text.isEmpty()) {
                filme.setReleaseYear(0);
            } else {
                try {
                    ano = Integer.parseInt(text);
                    if (ano < 1900 || ano > 2023) {
                        System.out.println("Digite uma data valida!!!");
                        dataValida = true;
                    } else {
                        filme.setReleaseYear(ano);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Digite uma data valida!!!");
                    dataValida = true;
                }
            }
        } while (dataValida);
        System.out.print("Digite a Descrição do filme: ");
        filme.setDescription(sc.nextLine());
        return filme;
    }

    public static Filme getFilmeInclude() throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");

        Filme filme = new Filme();
        filme.setIdManual(arq.readInt() + 1);
        arq.close();
        int ano;
        String text;
        boolean dataValida;
        boolean valida;
        // validar entrada do titulo do filme
        do {
            valida = false;
            System.out.print("Digite o Titulo do filme: ");
            text = sc.nextLine();
            if (text.isEmpty()) {
                System.out.print("Campo não pode ficar em branco: ");
                valida = true;
            } else {
                filme.setTitle(text);
            }
        } while (valida);

        // validar entrada do tipo do filme
        do {
            valida = false;
            System.out.print("Digite o Tipo do filme: ");
            text = sc.nextLine();
            if (text.isEmpty()) {
                System.out.print("Campo não pode ficar em branco: ");
                valida = true;
            } else {
                filme.setType(text);
            }
        } while (valida);

        // validar entrada dos diretores do filme
        do {
            valida = false;
            System.out.print("Digite o(s) Diretor(es) do filme: ");
            text = sc.nextLine();
            if (text.isEmpty()) {
                System.out.print("Campo não pode ficar em branco: ");
                valida = true;
            } else {
                filme.setDirector(text);
            }
        } while (valida);

        // validar entrada do pais do filme
        do {
            valida = false;
            System.out.print("Digite o Pais de produção do filme: ");
            text = sc.nextLine();
            if (text.isEmpty()) {
                System.out.print("Campo não pode ficar em branco: ");
                valida = true;
            } else {
                filme.setCountry(text);
            }
        } while (valida);

        // validar entrada do ano de produção do filme
        do {
            valida = false;
            dataValida = false;
            System.out.print("Digite o ano de produção do filme: ");
            text = sc.nextLine();
            if (text.isEmpty()) {
                System.out.print("Campo não pode ficar em branco: ");
                valida = true;
            } else {
                try {
                    ano = Integer.parseInt(text);
                    if (ano < 1900 || ano > 2023) {
                        System.out.println("Digite uma data valida!!!");
                        dataValida = true;
                    } else {
                        filme.setReleaseYear(ano);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Digite uma data valida!!!");
                    dataValida = true;
                }
            }
        } while (dataValida || valida);

        // validar entrada da descrição do filme
        do {
            valida = false;
            System.out.print("Digite a Descrição do filme: ");
            text = sc.nextLine();
            if (text.isEmpty()) {
                System.out.print("Campo não pode ficar em branco: ");
                valida = true;
            } else {
                filme.setDescription(text);
            }
        } while (valida);
        return filme;
    }

    public static void chargeFilmeNovo(Filme filme) throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        arq.readInt();
        long currentPosition = arq.getFilePointer();
        long endPosition = arq.length();
        byte[] ba;
        boolean achou = false;

        while (currentPosition < endPosition) {
            long cursor = arq.getFilePointer();
            if (arq.readBoolean()) {
                int tamanho = arq.readInt();
                ba = filme.toByteArray();
                if (ba.length <= tamanho) {
                    escrever(filme, cursor, 2, arq);
                    achou = true;
                    currentPosition = endPosition; // Break;
                }
            } else {
                int len = arq.readInt();
                arq.seek(len + (cursor + 5));
                currentPosition = arq.getFilePointer();
            }
        }
        if (achou == false) {
            escrever(filme, arq.length(), 0, arq); // ponteiro direcionado para ultima posição do arquivo
        }
        arq.close();
        // TODO reordenar
    }

    public static int getIntCabecalho() throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        int valor = arq.readInt();
        arq.close();
        return valor;
    }

    public static void intercalaVariosCaminho(RandomAccessFile arq, int caminhos) throws IOException {
        arq.seek(0);
        // testar se caminho é valido
        int nFilmes = arq.readInt();
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
        int blocos = 2;
        for (int i = 0; currentPosition < endPosition; i++) {
            ArrayList<Filme> filme = new ArrayList<Filme>();

            for (int j = 0; j < blocos && currentPosition < endPosition; j++) {
                Filme filmetemp = new Filme();
                arq.seek(arq.getFilePointer());
                if (arq.readBoolean()) {
                    filmetemp.setLapide(true);
                } else {
                    filmetemp.setLapide(false);
                }
                len = arq.readInt();
                ba = new byte[len];
                arq.read(ba);
                filmetemp.fromByteArray(ba);
                filme.add(filmetemp);
                currentPosition = arq.getFilePointer();
            }

            filmeSort(filme);

            for (int j = 0; j < filme.size(); j++) {
                if (filme.get(j).getLapide()) {
                    fitaCursor[i % caminhos] = escrever(filme.get(j), fitaCursor[i % caminhos], 3, raf[i % caminhos]);
                } else {
                    fitaCursor[i % caminhos] = escrever(filme.get(j), fitaCursor[i % caminhos], 1, raf[i % caminhos]);
                }
            }

        }

        // intercalação
        int passadas = (int) Math.ceil((Math.log(nFilmes / blocos) /
                Math.log(caminhos)));

        for (int i = 1; i <= passadas; i++) {
            // zerar o ponteiro de cada grupo de arquivo
            for (int j = 0; j < caminhos * 2; j++)
                raf[j].seek(0);
            // quantidade de segmentos por passagem
            int seg = blocos / i;
            int sizeSeg = blocos * i;

            int[] indexCaminho = new int[caminhos];
            boolean[] acabou = new boolean[caminhos];
            for (int j = 0; j < caminhos; j++) {
                acabou[j] = false;
            }

            // percorrer setor
            for (int j = 1; j <= seg; j++) {
                if (j == 2) {
                    System.out.println();
                }
                boolean flag = true;
                int index;
                do {

                    // achar menor filme
                    Filme menor = new Filme();
                    menor.setIdManual(nFilmes);
                    RandomAccessFile champion = null;
                    Long[] cursor = new Long[caminhos];
                    for (int x = 0; x < caminhos; x++) {
                        if (i % 2 != 0) {
                            index = x % caminhos;
                        } else {
                            index = (x % caminhos) + caminhos;
                        }
                        if (indexCaminho[x] < j * sizeSeg && raf[index].getFilePointer() < raf[index].length()) {

                            Filme temp = new Filme();
                            long teste = raf[index].getFilePointer();
                            cursor[x] = raf[index].getFilePointer();
                            raf[index].seek(teste + 1);
                            len = raf[index].readInt();
                            ba = new byte[len];
                            raf[index].read(ba);
                            temp.fromByteArray(ba);
                            if (temp.getId() <= menor.getId()) {
                                menor = temp;
                                champion = raf[index];
                            }
                        }
                    }
                    // checar qual fita teve o menor numero
                    for (int x = 0; x < caminhos; x++) {
                        if (i % 2 != 0) {
                            index = x % caminhos;
                        } else {
                            index = (x % caminhos) + caminhos;
                        }
                        long current = raf[index].getFilePointer();
                        long end = raf[index].length();
                        if (indexCaminho[x] < j * sizeSeg && current <= end && acabou[x] == false) {
                            if (raf[index].equals(champion)) {
                                if (current == end) {
                                    acabou[x] = true;
                                }
                                indexCaminho[x]++;
                            } else {
                                raf[index].seek(cursor[x]);
                            }
                        }
                    }
                    // escrever menor filme
                    if (i % 2 != 0) {
                        index = ((j - 1) % caminhos) + caminhos;
                    } else {
                        index = (j - 1) % caminhos;
                    }

                    if (menor.getId() <= nFilmes)
                        escrever(menor, raf[index].getFilePointer(), 1, raf[index]);

                    // checar se chegou no fim do segmento
                    for (int x = 0; x < caminhos; x++) {
                        if (i % 2 != 0) {
                            index = x % caminhos;
                        } else {
                            index = (x % caminhos) + caminhos;
                        }
                        long curr = raf[index].getFilePointer();
                        long end = raf[index].length();
                        if ((curr >= end) || indexCaminho[x] >= j * sizeSeg) {
                            flag = false;
                        } else {
                            flag = true;
                            x = caminhos;
                        }
                    }
                } while (flag);
            }

            if (seg == 1) {
                if (i % 2 != 0) {
                    fileClone(arq, raf[((i) % caminhos) + caminhos]);
                } else {
                    fileClone(arq, raf[(i % caminhos)]);
                }
            }
        }

        /*
         * for (int i = 0; i < caminhos * 2; i++) {
         * lerTodos(raf[i]);
         * System.out.println(
         * "-------------------------------------------------------------------------------------------"
         * );
         * }
         */

        // exlcuir fitas
        for (int i = 0; i < (caminhos * 2); i++) {
            raf[i].close();
            fita[i].deleteOnExit();
        }
    }

    public static void fileClone(RandomAccessFile a, RandomAccessFile b) throws IOException {
        a.seek(0);
        int cabecalho = a.readInt();
        a.setLength(0);
        a.writeInt(cabecalho);
        b.seek(0);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = b.read(buffer)) != -1) {
            a.write(buffer, 0, bytesRead);
        }

    }

    public static void testeIntercala() throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");

        intercalaVariosCaminho(arq, 2);
    }
}