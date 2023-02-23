import java.io.BufferedReader;
import java.io.FileReader;

class Programa {

    public static void escrever() {
        try (BufferedReader br = new BufferedReader(
                new FileReader("/mnt/d/Documentos/Escola/aeds3/Base de Dados/NetFlix.csv"))) {
            Filme filme = new Filme();
            br.readLine();
            String line = br.readLine();

            while (line != null) {
                String[] vect = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                filme.setId();
                filme.setType(vect[1]);
                filme.setTitle(vect[2]);
                filme.setDirector(vect[3]);
                filme.setCountry(vect[5]);
                filme.setReleaseYear(Integer.parseInt(vect[7]));
                filme.setDescription(vect[11]);

                System.out.println(filme);
                System.out.println();

                line = br.readLine();
            }

            br.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        escrever();
    }
}