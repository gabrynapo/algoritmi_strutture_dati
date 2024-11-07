/**
 * @author Napoletano Gabriele
 * Matricola: 0001071067
 * 
 * gabriele.napoletano@studio.unibo.it
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * - DEFINIZIONE DEI SOTTOPROBLEMI
 *   Riempire il più possibile un CD-ROM di capienza j, utilizzando un opportuno sottoinsieme 
 *   dei primi i oggetti, massimizzando il valore degli oggetti usati.
 *   
 * - DEFINIZIONE DELLE SOLUZIONI AI SOTTOPROBLEMI
 *   V[i, j] è il massimo valore ottenibile da un sottoinsieme dei file {1, 2, ..., i} in un
 *   CD-ROM che ha capacità massima j.
 *   i = 1,2,..numFile
 *   j = 0,1,..CAPACITY  
 * 
 * - SOLUZIONI NEL CASO BASE 
 *   1) CD-ROM di capienza zero : Non si può inserire nessun file.
 *   2) Solo un file da inserire : se j ≥ p(0) si può inserire il file, altrimenti no.
 * 
 * - SOLUZIONE NEL CASO GENERALE
 *   Il peso dell'oggetto i è ≤ della capienza j?      (p(i) ≤ j)
 *   Falso : La soluzione ottima di P(i, j) non fa uso dell'oggetto i.    (V[i, j] ← V[i-1, j])
 *   Vero : Mi conviene inserire l'oggetto i?   (V[i-1,j–p(i)]+v(i) > V[i-1, j])
 *        Falso : La soluzione ottima di P(i, j) non fa uso dell'oggetto i.   (V[i, j] ← V[i-1, j])
 *        Vero : La soluzione ottima di P(i, j) fa uso dell'oggetto i.   (V[i, j] ← V[i-1, j–p(i)]+v(i))
 * 
 * 
 *  Considerando tutto l'algoritmo implementato, si ha un costo asintotico che dipende dai costi 
 *  del metodo popolaDisco e del metodo stampaSoluzione, quindi da quante volte si va a richiamare 
 *  questi metodi nel caso peggiore.
 *  In questo caso, il metodo popolaDisco costa O(n * CAPACITY) e il metodo stampaSoluzione O(n) (si usa 
 *  un disco per ogni file).  
 *  Quindi, il costo asintotico nel caso peggiore, considerando CAPACITY come una costante, è: O(n²).   
 * 
 */

public class Esercizio3{

    ArrayList<Item> items; 

    final int CAPACITY = 650;

    /**
     * Il costruttore della classe popola la lista con il file in input
     * 
     * @param inputf
     */
    public Esercizio3( String inputf ){
        popolaLista(inputf);
    }

    /**
     * Classe che rappresenta i file passati in input.
     * Attributi: nome del file, peso (dimensioni), valore (dimensioni).
     * E' necessario usare anche l'attributo valore per poter implementare
     * al meglio il problema dello zaino.
     */
    public class Item{

        private String nomeFile;
        private int peso;
        private int valore;
    
        /**
         * Metodo costruttore della classe Item.
         * 
         * @param nomeFile
         * @param peso
         * @param valore
         */
        public Item(String nomeFile, int peso, int valore) {
            this.nomeFile = nomeFile;
            this.peso = peso;
            this.valore = valore;
        }

        public String getNomeFile() {
            return this.nomeFile;
        }

        public int getPeso() {
            return this.peso;
        }

        public int getValore(){
            return this.valore;
        }
    }

    /**
     * Metodo che legge, tramite uno scanner, il file in input.
     * Nell'input c'è il numero totale di file e, a seguire, i nomi dei file con i 
     * rispettivi pesi. Si imposta il valore uguale al peso.
     * I file vengono aggiunti alla lista.
     * 
     * @param inputf
     * @return Lista di Item
     */
    public ArrayList<Item> popolaLista( String inputf ){
        ArrayList<Item> listaFile = new ArrayList<Item>();
        Locale.setDefault(Locale.US);
        try {
            Scanner s = new Scanner( new FileReader( inputf ) );
            int numFile = s.nextInt();
            s.nextLine();
            for ( int i=0; i<numFile; i++ ) {
                String line = s.nextLine();
                String[] parts = line.split("\\s+"); 
                if (parts.length >= 2) {
                    String nomeFile = parts[0];
                    int pesoFile = Integer.parseInt(parts[1]);
                    int valoreFile = Integer.parseInt(parts[1]);
                    listaFile.add(new Item(nomeFile, pesoFile, valoreFile));
                }
            }
            s.close();
        } catch ( IOException ex ) {
            System.err.println(ex);
            System.exit(1);
        }
        return listaFile;
    }


    /**
     * Metodo che implementa il codice del problema dello zaino usando la programmazione dinamica.
     * 
     * Si inizializzano due matrici. Una di tipo boolean che tiene traccia degli oggetti utilizzati 
     * per ottenere il valore massimo per ogni sottoproblema, e una matrice che memorizza il valore 
     * massimo ottenibile per ogni sottoproblema.
     * 
     * Le dimensioni delle matrici sono: il numero dei file per le righe, la capacità dello "zaino"
     * (nel nostro caso del CD-ROM) per le colonne.
     * 
     * Nel primo for, si setta la prima riga della matrice V col valore del primo file dalla colonna
     * j==peso del file in poi. Stessa cosa per la prima riga della matrice booleana use, soltanto
     * che si inserisce true dalla colonna j==peso del file in poi.
     * 
     * Successivamente, per completare le matrici, si inserisce il massimo valore ottenibile in una
     * determinata colonna j della matrice V, con a disposizione i file fino alla riga i e, nel caso 
     * venga inserito l'i-esimo elemento, si imposta a true la matrice use agli indici [i][j].  
     * 
     * @param items
     * @param CAPACITY
     * @return matrice dei file usati
     */
    public boolean[][] popolaDisco(ArrayList<Item> items, int CAPACITY){

        int i,j;

        double[][] V = new double[items.size()][CAPACITY+1];
        boolean[][] use = new boolean[items.size()][CAPACITY+1];

        for ( j=0; j<=CAPACITY; j++ ) {
            if ( j < items.get(0).getPeso() ) {
                    V[0][j] = 0.0;
                    use[0][j] = false;
            } else {
                    V[0][j] = items.get(0).getValore();
                    use[0][j] = true;
                }
        }

        for ( i=1; i<items.size(); i++ ) {
            for ( j=0; j<=CAPACITY; j++ ) {
                if ( j < items.get(i).getPeso() ) {
                    V[i][j] = V[i-1][j];
                    use[i][j] = false;
                } else {
                    if ( V[i-1][j-items.get(i).getPeso()] + items.get(i).getValore() > V[i-1][j] ) {
                        V[i][j] = V[i-1][j-items.get(i).getPeso()] + items.get(i).getValore();
                        use[i][j] = true;
                    } else {
                        V[i][j] = V[i-1][j];
                        use[i][j] = false;
                    }
                }
            }
        }
        return use;
    }

    /**
     * Metodo per la stampa delle soluzioni. Mentre si controlla che la i non "esca" dalla matrice,
     * se il file alla riga i è stato usato (quindi ha valore true nella matrice use) viene stampato 
     * insieme al peso.
     * Si tiene conto dello spazio libero rimanente tramite una variabile. 
     * 
     * Per stampare l'elemento successivo (se esiste) si sottrae, alla colonna attuale j, il peso del file; 
     * in questo modo si riduce la capacità residua disponibile per i prossimi elementi da considerare.
     * L'elemento stampato viene rimosso dalla lista.
     * 
     * @param items
     * @param numDisco
     * @param use
     * @return Items rimanenti
     */
    public ArrayList<Item> stampaSoluzione(ArrayList<Item> items,  int numDisco, boolean[][] use){

        int spazioLibero = CAPACITY;
        int i = items.size()-1;
        int j = CAPACITY;

        while( i >= 0 ){
            if( use[i][j] ){
                Item itemScelto = items.get(i);
                System.out.println(itemScelto.getNomeFile() + " " + itemScelto.getPeso());
                j = j - itemScelto.getPeso();
                spazioLibero = spazioLibero - itemScelto.getPeso();
                items.remove(itemScelto);
            }
            i--; 
        }
        
        System.out.println("Spazio libero: " + spazioLibero + "\n");
        return items;
        
    }

    /**
     * Metodo main per verificare che l'utente abbia inserito il file in input e si istanzia
     * un oggetto della classe Esercizio3. 
     * Si inseriscono gli elementi nella lista con il metodo popolaLista.
     * Si itera su tutta la lista, finchè non è vuota, il metodo per stampare la soluzione, tenendo
     * conto dei dischi usati.
     * 
     * @param args
     */
    public static void main(String[] args) {

        if(args.length != 1){
            System.err.println("ERRORE: Inserire file di input");
            System.exit(1);
        }

        final int CAPACITY = 650;

        Esercizio3 es = new Esercizio3(args[0]);

        ArrayList<Item> items = es.popolaLista(args[0]);

        int numDisco = 1;       

        while(!(items.isEmpty())){
            System.out.println("Disco: " + numDisco);
            es.stampaSoluzione(items, numDisco, es.popolaDisco(items, CAPACITY));
            numDisco++;
        }

    }
}