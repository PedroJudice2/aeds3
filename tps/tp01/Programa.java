import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.regex.Pattern;

class Programa {

    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        String choice;
        try {
            // Carregar();
            // lerTodos();
            do {
                System.out.print("\033[H\033[2J");
                System.out.println("Opções: ");
                System.out.println("Deletar op 1");
                System.out.println("pesquisar op 2 ");
                System.out.println("Atualizar op 3 ");

                int op;
                do {
                    System.out.print("Digite a opção desejada: ");
                    op = sc.nextInt();
                } while (op != 1 && op != 2 && op != 3);
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

                filePointer = escrever(filme, filePointer, 0);

                line = br.readLine();
            }

            br.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static long escrever(Filme filme, Long filePointer, int type) throws IOException {
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
        } else if (type == 1) {
            arq.seek(pos);
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

    public static void lerTodos() throws IOException {
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

    public static Filme lerId(int id) throws IOException {
        RandomAccessFile arq = new RandomAccessFile("/mnt/d/Documentos/Escola/aeds3/tps/tp01/dados/filmes.db",
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
        RandomAccessFile arq = new RandomAccessFile("/mnt/d/Documentos/Escola/aeds3/tps/tp01/dados/filmes.db",
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
        RandomAccessFile arq = new RandomAccessFile("/mnt/d/Documentos/Escola/aeds3/tps/tp01/dados/filmes.db",
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
                        escrever(filme, arq.length(), 1);
                        // TODO chamar função de ordenação
                        currentPosition = endPosition;
                        achou = true;
                    } else {
                        escrever(filme, cursor, 1);
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

    public static Filme getFilmeInclude() {
        Filme filme = new Filme();
        filme.setId();
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
                filme.setType(sc.nextLine());
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
                filme.setDirector(sc.nextLine());
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
                filme.setCountry(sc.nextLine());
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
                filme.setDescription(sc.nextLine());
            }
        } while (valida);
        return filme;
    }

}