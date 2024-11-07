/**
 * @author Napoletano Gabriele
 * Matricola: 0001071067
 * 
 * gabriele.napoletano@studio.unibo.it
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Esercizio2 {

    /** 
     * Si dichiarano, in due vettori di interi, tutte le possibili combinazioni
     * di mosse che può eseguire il cavallo sulla scacchiera.
    */ 
    private int[] mosseX = {-1, -2, -2, -1, 1, 2, 2, 1};
    private int[] mosseY = {-2, -1, 1, 2, -2, -1, 1, 2};

    /** Dimensioni della scacchiera */
    private int righe;
    private int colonne;

    /** Si dichiara la scacchiera come una matrice di char  */
    private char[][] board;

    /** 
     * Con queste due variabili di tipo int si tiene traccia della posizione di
     * partenza del cavallo sulla scacchiera.
     */
    private int rigaPartenza;
    private int colonnaPartenza;

    /**
     * Il costruttore della classe prende in input la scacchiera e richiama il
     * metodo per trovare tutte le possibili mosse del cavallo, passando in
     * input la matrice di tipo char (ricavata dal metodo creaBoard).
     * 
     * @param inputf
     */
    public Esercizio2(String inputf){
        trovaMosseCavallo(creaBoard(inputf));
    }

    /**
     * Tramite uno scanner, si legge il file passato in input che rappresenta la scacchiera.
     * Innanzitutto, si leggono i primi due interi del file che permettono di definire le
     * dimensioni della nostra matrice char[][].
     * Successivamente, tramite due for, che scorrono le righe e le colonne, si popola la 
     * matrice e si salvano in due variabili gli indici di partenza del cavallo.
     * 
     * @param inputf
     * @return board
     */
    public char[][] creaBoard(String inputf){
        try {
            Scanner scanner = new Scanner(new FileReader(inputf));
            righe = scanner.nextInt();
            colonne = scanner.nextInt();
            scanner.nextLine(); 

            board = new char[righe][colonne];

            rigaPartenza = 0;
            colonnaPartenza = 0;

            for (int i = 0; i < righe; i++) {
                String line = scanner.nextLine();
                for (int j = 0; j < colonne; j++) {
                    board[i][j] = line.charAt(j);
                    if (board[i][j] == 'C') {
                        rigaPartenza = i;
                        colonnaPartenza = j;
                    }
                }
            }
            scanner.close();
        } catch (IOException ex) {
            System.err.println(ex);
            System.exit(1); 
        }
        return board;
    }

    /** 
     * Utilizziamo una visità in profondità (DFS) per coprire l'intero grafo e trovare 
     * tutte le caselle raggiungibili dal cavallo partendo da una singola sorgente.
     * Con la matrice boolean visited, che ha le stesse dimensioni della scacchiera, 
     * teniamo traccia delle caselle coperte dal cavallo.
     * Il cavallo si sposta sulla scacchiera tramite un'iterazione sui vettori delle 
     * possibili mosse.
     * Dopo aver verificato, che la mossa sia valida (all'interno della scacchiera), 
     * che quella casella non sia già stata visitata e che non sia occupata da un 
     * altro pezzo (rappresentato con una X), si procede con la chiamata ricorsiva 
     * per tutta la lunghezza del vettore delle mosse.
     * 
     * @param board
     * @param visited
     * @param riga
     * @param colonna
     */
    public void dfs(char[][] board, boolean[][] visited, int riga, int colonna) {
        visited[riga][colonna] = true;
        for (int i = 0; i < mosseX.length; i++) {
            int nuovaRiga = riga + mosseX[i];
            int nuovaColonna = colonna + mosseY[i];

            if (mossaValida(board, nuovaRiga, nuovaColonna) && !visited[nuovaRiga][nuovaColonna] 
                && board[nuovaRiga][nuovaColonna] != 'X') {
                    dfs(board, visited, nuovaRiga, nuovaColonna);
            }
        }
    }

    /**
     * Si controlla che gli indici della casella dove si vuole spostare il cavallo, 
     * siano compresi tra 0 e le dimensioni della scacchiera.
     * 
     * @param board
     * @param riga
     * @param colonna
     * @return true/false
     */
    public boolean mossaValida(char[][] board, int riga, int colonna) {
        int numRighe = board.length;
        int numColonne = board[0].length;
        return riga >= 0 && riga < numRighe && colonna >= 0 && colonna < numColonne;
    }

    /**
     * Metodo di stampa della scacchiera. Tramite la matrice visited, nelle caselle true 
     * e non occupate da altri pezzi, stampiamo la C.
     * Altrimenti, viene stampato il simbolo che c'era nella scacchiera di partenza (X/.). 
     * 
     * @param board
     * @param visited
     */
    public void stampaBoard(char[][] board, boolean[][] visited) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (visited[i][j] && board[i][j] == '.') {
                    System.out.print("C");
                } else {
                    System.out.print(board[i][j]);
                }
            }
            System.out.println();
        }
    }

    /**
     * Metodo per vedere se il cavallo ha raggiunto tutte le caselle libere.
     * Se in una casella si trova un punto che non è stato visitato dal cavallo,
     * vuol dire che il cavallo non ha coperto tutte le caselle libere e quindi si può 
     * interrompere immediatamente il controllo, perchè ci basta sapere che non ha raggiunto 
     * anche una sola casella.
     *  
     * @param board
     * @param visited
     * @return raggiungeTutteCaselle
     */
    public boolean checkRaggiungeTutteCaselle(char[][] board, boolean[][] visited){
        boolean raggiungeTutteCaselle = true;
        for (int i = 0; i < righe; i++) {
            for (int j = 0; j < colonne; j++) {
                if (board[i][j] == '.' && !(visited[i][j])) {
                    raggiungeTutteCaselle = false;
                    break;
                }
            }
        }
        return raggiungeTutteCaselle;
    }

    /**
     * Questo metodo ingloba tutti i metodi precedenti per arrivare alla stampa finale
     * della scacchiera visitata e il valore booleano.
     * 
     * @param board
     */
    public void trovaMosseCavallo(char[][] board){

        boolean[][] visited = new boolean[righe][colonne];

        dfs(board, visited, rigaPartenza, colonnaPartenza);

        stampaBoard(board, visited);

        System.out.println(checkRaggiungeTutteCaselle(board, visited));

    }

    /**
     * Con il metodo main, si verifica che l'utente abbia inserito il file in input
     * e si istanzia un oggetto della classe Esercizio2.
     * 
     * @param args
     */
    public static void main(String[] args) {

        if(args.length != 1){
            System.err.println("ERRORE: Inserire file in input");
            System.exit(1);
        }
        
        Esercizio2 es = new Esercizio2(args[0]);

    }
}