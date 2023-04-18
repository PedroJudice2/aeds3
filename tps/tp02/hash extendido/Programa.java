import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

class Programa {

    // COLOQUE O CAMINHO DA DO SEU CSV E DO SEU AQUIVO
    public static String csvPath = "/mnt/d/Documentos/Escola/aeds3/Base de Dados/NetFlix.csv";
    public static String dbPath = "/mnt/d/Documentos/Escola/aeds3/tps/tp01/dados/filmes.db";

    public static Scanner sc = new Scanner(System.in);

    public static BPlusTree tree = new BPlusTree(8);

    public static ArrayList<Long> disponivel = new ArrayList<Long>();

    public static void main(String[] args) {
        String choice;
        try {
            System.out.println("Gostaria de carregar a base? ");
            String carrega = sc.nextLine();
            carrega = carrega.toLowerCase();
            if (Pattern.matches("^(?:1|t(?:rue)?|y(?:es)?|ok(?:ay)?|s(?:im)?)$", carrega)) {
                System.out.println("Carregando...");
                Carregar();
            } else {
                System.out.println("Carregando...");
                carregarArvore();
            }
            do {
                System.out.print("\033[H\033[2J");
                System.out.println("Opções: ");
                System.out.println("Deletar op 1 ");
                System.out.println("pesquisar op 2 ");
                System.out.println("Atualizar op 3 ");
                System.out.println("Adicionar op 4 ");
                System.out.println("ler todos op 5");
                System.out.println(getIntCabecalho());
                int op;

                // limitar as opções 1 a 4
                do {
                    System.out.print("Digite a opção desejada: ");
                    op = sc.nextInt();
                } while (op != 1 && op != 2 && op != 3 && op != 4 && op != 5);

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
                    System.out.println(tree.search(idBusca));
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
                    chargeFilmeNovo(filme);
                    System.out.println(filme);
                } else if (op == 6) {
                    sc.nextLine();
                    lerTodos();
                }
                System.out.println();
                System.out.println("Deseja fazer alguma outra operação?");
                choice = sc.nextLine();
                choice = choice.toLowerCase();
            } while (Pattern.matches("^(?:1|t(?:rue)?|y(?:es)?|ok(?:ay)?|s(?:im)?)$", choice));

        } catch (IOException e) {
            e.printStackTrace();
        }

        sc.close();
    }

    public static void Carregar() throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        arq.setLength(0);
        try (BufferedReader br = new BufferedReader(
                new FileReader(csvPath))) {
            Filme filme = new Filme();
            br.readLine();
            String line = br.readLine();
            long filePointer = 0;

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
        // escrever primeiro filme
        if (pos == 0 && type != 1 && type != 3 && type != 4) {
            arq.writeInt(1);
            arq.writeBoolean(false);
            ba = filme.toByteArray();
            arq.writeInt(ba.length);
            arq.write(ba);
            tree.insert(filme.getId(), filePointer);

            // escrever filme sem alterar id cabeçalho (update)
        } else if (type == 1) {
            arq.seek(pos);
            arq.writeBoolean(false);
            ba = filme.toByteArray();
            arq.writeInt(ba.length);
            arq.write(ba);
            tree.update(filme.getId(), filePointer);

            // escrever filme incrementando e sem escrever o tamanho (usado para
            // sobrescrever um filme)
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
            tree.insert(filme.getId(), filePointer);

            // escrever filme sem alterar id cabeçalho e marcando-o como apagado
        } else if (type == 3) {
            arq.seek(pos);
            arq.writeBoolean(true);
            ba = filme.toByteArray();
            arq.writeInt(ba.length);
            arq.write(ba);
            // escrever filme sem alterar id cabeçalho e sem escrever o tamanho (usado para
            // sobrescrever um filme) (update)
        } else if (type == 4) {
            arq.seek(pos);
            arq.writeBoolean(false);
            ba = filme.toByteArray();
            arq.readInt();
            arq.write(ba);
            tree.update(filme.getId(), filePointer);

            // esvrever filme incrementando o id cabeçalho
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
            tree.insert(filme.getId(), filePointer);
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

    public static void carregarArvore() throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        arq.seek(4);

        long currentPosition = arq.getFilePointer();
        long endPosition = arq.length();
        int len;
        byte[] ba;
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
                tree.insert(filme.getId(), currentPosition);
                currentPosition = arq.getFilePointer();
            }
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

    public static Filme lerId(int id) throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");

        if (id > tree.getSize()) {
            arq.close();
            return null;
        }
        long cursor = tree.search(id);
        if (cursor == -1) {
            arq.close();
            return null;
        }
        arq.seek(cursor + 1);
        int len = arq.readInt();
        byte[] ba = new byte[len];
        arq.read(ba);
        Filme filme = new Filme();
        filme.fromByteArray(ba);
        arq.close();
        return filme;

    }

    public static int filmeCount() {
        return tree.getCount();
    }

    public static boolean delete(int id) throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");

        if (id > tree.getSize()) {
            arq.close();
            return false;
        }
        long cursor = tree.delete(id);
        if (cursor == -1) {
            arq.close();
            return false;
        }

        arq.seek(cursor);
        arq.writeBoolean(true);
        disponivel.add(cursor);
        arq.close();
        return true;
    }

    public static boolean update(Filme novoFilme) throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        if (novoFilme.getId() > tree.getSize()) {
            arq.close();
            return false;
        }
        int len;
        int flag = 0;
        byte[] ba;
        Boolean achou = false;
        Filme filme = new Filme();

        long cursor = tree.search(novoFilme.getId());
        if (cursor == -1) {
            arq.close();
            return false;
        }
        arq.seek(cursor + 1);
        len = arq.readInt();
        ba = new byte[len];
        arq.read(ba);
        filme.fromByteArray(ba);
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
            achou = true;
        } else {
            escrever(filme, cursor, 4, arq);
            achou = true;
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
        byte[] ba;
        boolean achou = false;

        for (long cursor : disponivel) {

            arq.seek(cursor + 1);
            int tamanho = arq.readInt();
            ba = filme.toByteArray();
            if (ba.length <= tamanho) {
                escrever(filme, cursor, 2, arq);
                achou = true;
                break;
            }
        }
        if (achou == false) {
            escrever(filme, arq.length(), 0, arq); // ponteiro direcionado para ultima posição do arquivo
        }
        arq.close();
    }

    public static int getIntCabecalho() throws IOException {
        RandomAccessFile arq = new RandomAccessFile(dbPath,
                "rw");
        int valor = arq.readInt();
        arq.close();
        return valor;
    }
}