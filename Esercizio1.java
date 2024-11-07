/**
 * @author Napoletano Gabriele
 * Matricola: 0001071067
 * 
 * gabriele.napoletano@studio.unibo.it
 * 
 * Usa: parole.txt occorrenze.txt
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Esercizio1 {

    /**
     * Il costruttore della classe prende in input il file con gli oggetti 
     * ParolaOccorrenze e il file con le parole da cercare.
     * Richiama il metodo per trovare le parole, passando in input i due file.
     * 
     * @param inputf
     * @param inputp
     */
    public Esercizio1(String inputf, String inputp){
        trovaParole(inputf, inputp);
    }

    /**
     * Si definisce la classe ParolaOccorrenze, che ha come attributi la parola e la
     * sua occorrenza
     */
    public class ParolaOccorrenze{

        String parola;
        int occorrenze;

        /** 
         * Metodo costruttore di ParolaOccorrenzze
         * 
         * @param parola
         * @param numero_occorrenze
         */
        public ParolaOccorrenze(String parola, int n_occorrenze){
            this.parola = parola;
            this.occorrenze = n_occorrenze;
        }

        public String getParola() {
            return parola;
        }

        public int getOccorrenze() {
            return occorrenze;
        }

        public void setOccorrenze(int occorrenze) {
            this.occorrenze = occorrenze;
        }
    }

    /**
     * Per permettere un costo temporale medio costante dei metodi che seguono, si implementa
     * una HashTable, grazie al quale si può creare un'associazione chiave-valore (in questo
     * caso parola-occorrenza).
     * Tramite un metodo hash, che prende in input la parola, si genera un codice che definisce
     * l'indice corrispondente nella tabella Hash di dimensione K, all'interno del quale si inserisce 
     * l'occorrenza di quella determinata parola. 
     * Non avendo una funzione hash iniettiva, si è esposti a collissioni (in questo caso: parole
     * diverse che generano lo stesso hash, quindi stesso indice).
     * Di conseguenza per poterle gestire, è neccessario implementare le liste di trabocco, attraverso
     * delle LinkedList inserite in ogni indice della tabella.
     * Le chiavi con lo stesso valore hash quindi vengono memorizzate in una lista monodirezionale.
     * In questo modo, nonostante si possano generare chiavi uguali per diverse parole, si è in grado 
     * di risalire comunque alle occorrenze di una determinata parola. 
     */
    public class HashTable {

        private final int K = 211; // Numero primo distante da potenze del 2 e del 10
        private LinkedList[] tabellaHash;
        private int size;

        public HashTable(){
            tabellaHash = new LinkedList[K];
            size = 0;
        }

        /**
         * Si definisce una classe Nodo, che rappresenta l'oggetto ParolaOccorrenze all'interno
         * della LinkedList posizionata in un determinato indice della tabella Hash.
         * Inoltre, si aggiunge un attributo next di tipo Node inizializzato a null, per creare 
         * il collegamento con l'eventuale elemento successivo nella lista.
        */
        public class Node {

            ParolaOccorrenze data;
            Node next;
        
            public Node(ParolaOccorrenze data) {
                this.data = data;
                this.next = null;
            }
        }

        /**
         * Si implementa una LinkedList per permettere di poter salvare più nodi in
         * un solo indice della tabella.
         */
        public class LinkedList{

            private Node nodo;
    
            /**
             * Metodo per aggiungere un istanza della classe Node ad una lista. Aggiorna il 
             * numero delle occorenze se la parola esiste già. 
             * Cambio il collegamento del nodo col nuovo Nodo.
             * 
             * @param parola 
             * @param value (numero_occorenze)
             */
            public void add(String parola, int value) {
                Node nodoCorrente = nodo;
                while (nodoCorrente != null) {
                    if (nodoCorrente.data.parola.equals(parola)) {
                        nodoCorrente.data.occorrenze += value; 
                        return;
                    }
                    nodoCorrente = nodoCorrente.next; // Creo il collegamento al nodo successivo
                }
                ParolaOccorrenze nuovoDato = new ParolaOccorrenze(parola, value);
                Node nuovoNodo = new Node(nuovoDato);
                nuovoNodo.next = nodo;
                nodo = nuovoNodo;
            }
    

            /**
             * Metodo che ritorna il numero delle occorrenze di una determinata parola. Simile al
             * metodo get della LinkedList, ma modificato per poter ottenere solo il numero delle
             * occorrenze della parola passata in input.
             * Finchè non trova un nodo null, cerca nella lista passando per tutti i nodi, 
             * altrimenti ritorna 0.
             * 
             * @param parola
             * @return numero_occorrenze
             */
            public int getOccorrenze(String parola) {
                Node nodoCorrente = nodo;
                while (nodoCorrente != null) {
                    if (nodoCorrente.data.parola.equals(parola)) {
                        return nodoCorrente.data.occorrenze;
                    }
                    nodoCorrente = nodoCorrente.next; 
                }
                return 0;
            }

            /**
             * Metodo per prelevare dalla lista il nodo corretto. Mentre controlla che il nodo non sia 
             * null, scorre la lista fino a che non trova la corrispondenza della parola.
             * Iteriamo la lista con l'attributo next che collega un nodo con l'altro.
             * 
             * @param parola
             * @return
             */
            public Node getNode(String parola) {
                Node nodoCorrente = nodo;
                while (nodoCorrente != null) {
                    if (nodoCorrente.data.parola.equals(parola)) {
                        return nodoCorrente;
                    }
                    nodoCorrente = nodoCorrente.next;
                }
                return null;
            }

        }

        /**
         * Metodo hash che permette di creare l'indice della parola. Si trasforma la parola carattere 
         * per carattere in codice ASCII e poi, per avere un valore nell'intervallo tra 0 e K 
         * (dimensione della tabella Hash), usiamo l'operazione modulo.
         * 
         * @param parola
         * @return indice_tabellaHash
         */
        public int hash(String parola) {
            int hash = 0;
            for (int i = 0; i < parola.length(); i++) {
                hash += (int) parola.charAt(i) * (i + 1);  
            }
            return hash % K;
        }

        /**
         * Metodo per aggiungere le occorrenze di una parola. Si valuta la parola con tutti i 
         * caratteri in minuscolo. 
         * Dopo aver generato l'hash della parola, si verifica che la lista in quell'indice sia
         * inizializzata e, infine, aggiungiamo la parola.
         * 
         * @param parolaInput
         * @param occorrenzeInput
         */
        public void aggiungiOccorrenze(String parolaInput, int occorrenzeInput) {
            parolaInput = parolaInput.toLowerCase(); 
            int i = hash(parolaInput);
            if(tabellaHash[i] == null){
                tabellaHash[i] = new LinkedList();
            }
            tabellaHash[i].add(parolaInput, occorrenzeInput);         
        }

        /**
         * Metodo per ritornare il numero delle occorrenze. Si genera l'hash della parola per
         * trovare l'indice. Tramite l'indice, ci si posiziona correttamente nella tabella Hash
         * e si utilizza al metodo getOccorrenze della classe LinkedList, passando la parola in
         * input.
         * 
         * @param parolaInput
         * @return numero_occorrenze
         */
        public int occorrenzeParola(String parolaInput) {
            parolaInput = parolaInput.toLowerCase();
            int i = hash(parolaInput);
            return tabellaHash[i].getOccorrenze(parolaInput);
        }

        /**
         * Metodo per ritornare tutta la lista all'indice i della tabella Hash. Con l'hash della 
         * parola, si trova la posizione esatta nella tabella.
         * 
         * @param parolaInput
         * @return lista all'indice i
         */
        public LinkedList get(String parolaInput){
            parolaInput = parolaInput.toLowerCase();
            int i = hash(parolaInput);
            return tabellaHash[i];
        }

    }

    /**
     * Metodo principale per popolare e trovare le corrispondenze nella tabella.
     * Con il primo scanner si scorre il primo file passato in input, che permette di inserire
     * le parole e le occorrenze nella tabella.
     * Con il secondo scanner si scorre il secondo file passato in input, che cerca di trovare la
     * corrispondenza della parola nella tabella. In caso non trovi nulla, ritorna la parola con 0
     * occorrenze.
     * 
     * @param input
     * @param parole
     */
    public void trovaParole(String input, String parole){
        HashTable ht = new HashTable();
        try {
            String inputFile = input;
            String paroleFile = parole;

            // Popolo tabella Hash
            Scanner scanInput = new Scanner( new FileReader( inputFile ) );

            while(scanInput.hasNextLine()){
                String[] parts = scanInput.nextLine().split(",");
                int n_occorrenze = Integer.parseInt(parts[0]);
                String parola = parts[1];
                ht.aggiungiOccorrenze(parola, n_occorrenze);
            }
                        
            // Trovo corrispondenze
            Scanner scanParole = new Scanner( new FileReader( paroleFile ) );

            while (scanParole.hasNextLine()) {
                String parolaSearch = scanParole.nextLine();
                parolaSearch = parolaSearch.toLowerCase();
                if(ht.get(parolaSearch) == null || ht.get(parolaSearch).getNode(parolaSearch) == null){ 
                    System.out.println(parolaSearch + ", 0");
                }
                else if( parolaSearch.equalsIgnoreCase(ht.get(parolaSearch).getNode(parolaSearch).data.getParola()) ){
                    System.out.println(parolaSearch + ", " + ht.occorrenzeParola(parolaSearch));
                }
            }

            scanInput.close();
            scanParole.close();

        } catch ( IOException ex ) {
            System.err.println(ex);
            System.exit(1);
        }
    }

    /**
     * Con il metodo main, si verifica che l'utente abbia inserito i file in input
     * e si istanzia un oggetto della classe Esercizio1.
     * 
     * @param args
     */
    public static void main(String[] args) {

        if(args.length != 2){
            System.err.println("ERRORE: Inserire file in input");
            System.exit(1);
        }

        Esercizio1 es = new Esercizio1(args[0], args[1]);
        
    }
    
}
