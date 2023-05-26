import java.io.IOException;
import java.util.regex.Pattern;
import FileOp.FileOp;
import Filme.Filme;
import MyScanner.MyScanner;

class Programa {

    // COLOQUE O CAMINHO DA DO SEU CSV E DO SEU AQUIVO
    public static String csvPath = "src/resources/csv/NetFlix.csv";
    public static String dbPath = "src/resources/database/filmes.db";

    public static void main(String[] args) {
        String choice;
        FileOp fileOp = null;
        int alg;
        String carrega;
        try {
            do {
                System.out.println("Gostaria de carregar a base? ");
                carrega = MyScanner.sc.nextLine();
                carrega = carrega.toLowerCase();
            } while (!(Pattern.matches("^(?:0|1?|n(?:o)?|n(?:ao)?|n(?:ão)?|t(?:rue)?|y(?:es)?|ok(?:ay)?|s(?:im)?)$",
                    carrega)));
            if (Pattern.matches("^(?:1|t(?:rue)?|y(?:es)?|ok(?:ay)?|s(?:im)?)$", carrega)) {
                do {
                    System.out.println("Opções de algoritimos");
                    System.out.println("Arvore B+ op 1 ");
                    System.out.println("Hash extensivel op 2 ");
                    System.out.println("Ordenação externa op 3 ");
                    System.out.println("Qual algoritimo você deseja usar?");
                    alg = MyScanner.sc.nextInt();
                } while (!(alg == 2 || alg == 1 || alg == 3));
                MyScanner.sc.nextLine(); // clear buffer
                fileOp = new FileOp(dbPath, csvPath, alg);
                System.out.println("Carregando...");
                fileOp.loadFile();
            } else {
                System.out.println("Carregando...");
                fileOp = new FileOp(dbPath, csvPath);
                fileOp.loadData();
            }
            do {
                System.out.print("\033[H\033[2J");
                System.out.println("Opções: ");
                System.out.println("Deletar op 1 ");
                System.out.println("pesquisar op 2 ");
                System.out.println("Atualizar op 3 ");
                System.out.println("Adicionar op 4 ");
                System.out.println("ler todos op 5");
                System.out.println(FileOp.getIntCabecalho());
                int op;

                // limitar as opções 1 a 4
                do {
                    System.out.print("Digite a opção desejada: ");
                    op = MyScanner.sc.nextInt();
                } while (op != 1 && op != 2 && op != 3 && op != 4 && op != 5);

                // Opção para exclusão
                if (op == 1) {
                    System.out.print("Digite o id desejado: ");
                    int idBusca = MyScanner.sc.nextInt();
                    MyScanner.sc.nextLine();
                    System.out.println();
                    if (fileOp.delete(idBusca)) {
                        System.out.println("filme excluido com sucesso!");
                    } else {
                        System.out.println("Id não encontrado!");
                    }

                    // Opção para busca
                } else if (op == 2) {
                    System.out.print("Digite o id desejado: ");
                    int idBusca = MyScanner.sc.nextInt();
                    MyScanner.sc.nextLine();
                    System.out.println();
                    Filme filme = fileOp.read(idBusca);
                    if (filme != null) {
                        System.out.println(filme);
                    } else {
                        System.out.println("Id não encontrado!");
                    }

                    // Opção para atualizar
                } else if (op == 3) {
                    System.out.print("Digite o id desejado: ");
                    long idBusca = MyScanner.sc.nextLong();
                    MyScanner.sc.nextLine();
                    System.out.println();
                    Filme filme = fileOp.read(idBusca);
                    if (filme != null) {
                        fileOp.update(new Filme(idBusca));
                        System.out.println("filme Atualizado com sucesso!");
                    } else {
                        System.out.println("Id não encontrado!");
                    }

                    // Opção para adicionar
                } else if (op == 4) {
                    MyScanner.sc.nextLine();
                    Filme filme = new Filme();
                    fileOp.include(filme);
                    System.out.println(filme);
                } else if (op == 5) {
                    FileOp.readAll();
                    MyScanner.sc.nextLine();
                }
                System.out.println();
                do {
                    System.out.println("Deseja fazer alguma outra operação?");
                    choice = MyScanner.sc.nextLine();
                    choice = choice.toLowerCase();
                } while (!(Pattern.matches("^(?:0|n(?:o)?|n(?:ao)?|n(?:ão)?)$", choice)));
            } while (Pattern.matches("^(?:1|t(?:rue)?|y(?:es)?|ok(?:ay)?|s(?:im)?)$", choice));
            System.out.println("Fechando...");
            fileOp.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        MyScanner.sc.close();
    }

}