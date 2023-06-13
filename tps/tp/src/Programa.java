import java.io.IOException;
import java.util.regex.Pattern;

import FileOp.FileOp;
import Filme.Filme;
import KMP.KMP;
import MyScanner.MyScanner;

/**
 * This class is the main class of the program. It contains the main method, and
 * is used to run the program.
 * 
 * @version 1.0
 * @since 2023-05-13
 * @author Pedro
 */
public class Programa {

    // COLOQUE O CAMINHO DA DO SEU CSV E DO SEU AQUIVO
    public static String csvPath = "resources/csv/Pasta1.csv";
    public static String dbPath = "resources/database/filmes.db";

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
                System.out.println("Carregando...");
                fileOp = new FileOp(dbPath, csvPath, alg);
            } else {
                fileOp = new FileOp(dbPath, csvPath);
                System.out.println("Carregando...");
            }
            do {
                System.out.print("\033[H\033[2J");
                System.out.println("Opções: ");
                System.out.println("Deletar op 1 ");
                System.out.println("pesquisar op 2 ");
                System.out.println("Atualizar op 3 ");
                System.out.println("Adicionar op 4 ");
                System.out.println("ler todos op 5");
                System.out.println("Buscar padrão op 6");
                System.out.println(FileOp.getIntCabecalho());
                int op;

                // limitar as opções 1 a 4
                do {
                    System.out.print("Digite a opção desejada: ");
                    op = MyScanner.sc.nextInt();
                } while (op != 1 && op != 2 && op != 3 && op != 4 && op != 5 && op != 6);

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
                } else if (op == 6) {
                    String alg2;
                    MyScanner.sc.nextLine();
                    do {
                        System.out.println("Digite o algoitimo desejado: ");
                        System.out.println("KMP op 1 ");
                        alg2 = MyScanner.sc.nextLine();
                    } while (!(alg2.equals("1")));
                    System.out.println("Digite o padrão desejado: ");
                    String pattern = MyScanner.sc.nextLine();
                    int numberOfOcorrences = 0;
                    if (alg2.equals("1")) {
                        numberOfOcorrences = KMP.findPattern(pattern);
                    }
                    System.out.println("Numero de ocorrencias do padrão:");
                    System.out.println(numberOfOcorrences);
                }
                System.out.println();
                do {
                    System.out.println("Deseja fazer alguma outra operação?");
                    choice = MyScanner.sc.nextLine();
                    choice = choice.toLowerCase();
                } while (!(Pattern.matches("^(?:0|n(?:o)?|n(?:ao)?|n(?:ão)?)$", choice) || Pattern
                        .matches("^(?:1|t(?:rue)?|y(?:es)?|ok(?:ay)?|s(?:im)?)$", choice)));
            } while (Pattern.matches("^(?:1|t(?:rue)?|y(?:es)?|ok(?:ay)?|s(?:im)?)$", choice));
            System.out.println("Fechando...");
            fileOp.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        MyScanner.sc.close();
    }

}