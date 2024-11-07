/**
 * @author Napoletano Gabriele
 * Matricola: 0001071067
 * 
 * gabriele.napoletano@studio.unibo.it
 */

import java.io.*;
import java.util.*;

/**
 * O(n) -> Costo del ciclo while nel metodo shortestPaths, ogni iterazione viene 
 *         estratto un nodo dalla lista e non viene reinserito.
 * O(n²) -> Metodo findDeleteMin, ricerca del nodo con distanza minima eseguito 
 *          al più n volte per n nodi.
 * O(m) -> Costo del ciclo foreach nel metodo shortestPaths su tutti gli edge 
 *         di un nodo.
 * Costo computazionale totale: O(n²) + O(m) = O(n²)
 * 
 */

public class Esercizio4 {

    int n;      // numero di nodi nel grafo
    int m;      // numero di edge nel grafo    
    Vector< LinkedList<Edge> > adjList; // lista di adiacenza
    int source; // nodo sorgente
    ArrayList<ArrayList<Integer>> p; // lista dei predecessori (ogni nodo può avere più predecessori)
    double[] d; // array delle distanze tra nodi

    ArrayList<Integer> listaNodi; // lista dei nodi

    /**
     * Arco di un grafo pesato e non orientato
     */
    private class Edge {
        final int src;
        final int dst;
        final double w; //peso

        /**
         * Si costruisce un edge (src, dst) con un peso w
         */
        public Edge(int src, int dst, double w){
            // L'algoritmo di Dijkstra prevede che non ci siano pesi negativi
            assert(w >= 0.0); 
            this.src = src;
            this.dst = dst;
            this.w = w;
        }
    }   
    
    /**
     * Il costruttore della classe legge il file in input 
     * 
     * @param inputf
     */
    public Esercizio4(String inputf){
        readGraph(inputf);
    }

    /**
     * Legge il grafo passato in input.
     * Si inizializza ad ogni indice della lista di adiacenza una LinkedList di Edge.
     * 
     * Per tutti gli edge, si popola la lista di adiacenza aggiungendo ad ogni indice 
     * nuove istanze della classe Edge.
     * Essendo un grafo non orientato, si inserisce il collegamento sia in una direzione
     * che nell'altra.
     *
     * @param inputf
     */
    private void readGraph(String inputf){
        Locale.setDefault(Locale.US);
        try {
            Scanner f = new Scanner(new FileReader(inputf));
            n = f.nextInt(); // numero di nodi
            m = f.nextInt(); // numero di edge
            
            adjList = new Vector< LinkedList<Edge> >();
            
            for (int i=0; i<n; i++) {
                adjList.add(i, new LinkedList<Edge>() ); 
            }
            
            for (int i=0; i<m; i++) {
                final int src = f.nextInt();
                final int dst = f.nextInt();
                final double weight = f.nextDouble();
                assert( weight >= 0.0 ); // Condizione di Dijkstra
                adjList.get(src).add( new Edge(src, dst, weight) ); 
                adjList.get(dst).add( new Edge(dst, src, weight) ); 
            }

            f.close(); 
        } catch (IOException ex) {
            System.err.println(ex);
            System.exit(1);
        }  
    }

    /**
     * Calcolo dei cammini di costo minimo partendo da un nodo sorgente s.
     * Finchè ci sono nodi nella lista, si sceglie u, il nodo con distanza minima 
     * dalla sorgente.
     * Poi, per tutti gli edge del nodo u, si scelgono i diversi nodi che può 
     * raggiungere, rappresentati da v.
     * Ci sono 3 possibili casi: 
     * - Se il nodo v non è stato visitato (d[v] == Double.POSITIVE_INFINITY), si aggiorna 
     *   la sua distanza aggiungendo il peso dell'edge, si aggiunge u come predecessore 
     *   di v e si aggiunge v alla lista dei nodi;
     * - Se trova un percorso più corto verso v (d[u] + e.w < d[v]), si aggiorna la 
     *   distanza e si sostituiscono i predecessori di v;
     * - Se trova un percorso di uguale costo (d[u] + e.w == d[v]), si aggiunge u come
     *   predecessore di v.
     *
     * @param s
     */
    public void shortestPaths( int s ){
    
        source = s;   
        
        // vettore delle distanze
	    d = new double[n]; 

        // Inizialmente le distanze sono +∞, perchè i nodi non sono ancora stati visitati
        Arrays.fill(d, Double.POSITIVE_INFINITY); 

        // il nodo sorgente ha distanza zero da se stesso
        d[s] = 0.0; 

        p = new ArrayList<ArrayList<Integer>>();

        // Si inizializzano le liste dei predecessori per ogni nodo
        for(int i=0; i<n; i++){
            p.add(new ArrayList<Integer>());
        }

        listaNodi = new ArrayList<Integer>(n);

        // Si aggiunge il nodo sorgente alla lista dei nodi
        listaNodi.add(s);

        while (!(listaNodi.isEmpty())) {
            final int u = findDeleteMin(listaNodi, d);  
            for (Edge e : adjList.get(u)) {
		        final int v = e.dst;
                if(d[v] == Double.POSITIVE_INFINITY){
                    d[v]= d[u] + e.w;
                    p.get(v).add(u); 
                    listaNodi.add(v);
                } else if(d[u] + e.w < d[v] ){ 
                    d[v] = d[u] + e.w;
                    p.get(v).clear();
                    p.get(v).add(u);
                } else if (d[u] + e.w == d[v]) { 
                    p.get(v).add(u);
                }
            }
        }

    }

    /**
     * Metodo per scegliere il nodo con costo minimo. 
     * Si iterano tutti i nodi e si salva il nodo con la distanza minima dal
     * nodo sorgente.
     * Rimuoviamo poi il nodo scelto dalla lista.
     *
     * @param listaNodi
     * @param d distanze
     * @return minNode
     */
    public int findDeleteMin(ArrayList<Integer> listaNodi, double[]d){
        int minNode = listaNodi.get(0);
        for (int i = 0; i < listaNodi.size(); i++) {
            if (d[listaNodi.get(i)] < d[minNode]) {
                minNode = listaNodi.get(i);
            }
        }
        listaNodi.remove(Integer.valueOf(minNode));
        return minNode;
    }

    /**
     * Stampa di tutti i cammini di costo minimo andando a richiamare il metodo 
     * print_path per tutti i nodi.
     */
    public void print_paths( ){
        System.out.println();
        System.out.println("Source = " + source);
        System.out.println("   s    d         dist path");
        System.out.println("---- ---- ------------ -------------------");
        for (int dst=0; dst < n; dst++) {
            System.out.printf("%4d %4d %12.4f ", source, dst, d[dst]);
            print_path(dst, source);
            System.out.println();
        }
    }

    /**
     * Stampa del cammino/cammini di costo minimo tra un nodo sorgente e uno destinatario.
     * Ci sono 3 casi possibili: nodo sorgente è uguale al nodo destinatario, nodo 
     * destinatario ha un solo predecessore oppure nodo destinatario ha più di un predecessore.
     * Negli ultimi 2 casi, si esegue una chiamata ricorsiva per stampare tutti i predecessori.
     * Nell'ultima condizione si deve iterare su tutta la lista di predecessori di quel 
     * determinato nodo.
     */
    public void print_path(int dst, int source){
        if (dst == source){
            System.out.print(dst);
        }else if(p.get(dst).size() == 1){
            print_path(p.get(dst).get(0), source);
            System.out.print(" -> " + dst);
        } else {
            for(int i=0; i < p.get(dst).size(); i++){
                print_path(p.get(dst).get(i), source);
                System.out.print(" -> " + dst);
                System.out.print("  ||  ");
            }
        }
    }

    /**
     * Metodo per calcolare il tempo impiegato per calcolare tutti i cammini di
     * costo minimo partendo da tutti i nodi, in millisecondi.
     */
    public void tempoTrovaPercorsi(){
        long start_t = System.currentTimeMillis();
        for(int i=0; i<n; i++){
            shortestPaths(i); 
            print_paths();
        }		
        long end_t = System.currentTimeMillis();
		System.out.println("Tempo totale per trovare la soluzione: " + (end_t-start_t) + " ms");
    }

    /**
     * Metodo main istanzia un oggetto della classe Esercizio4 e richiama il metodo tempoTrovaPercorsi,
     * che trova tutti i cammini di costo minimo e calcola il tempo impiegato.
     */
    public static void main( String args[]){

        if(args.length != 1){
            System.err.println();
            System.exit(1);
        }
	
        Esercizio4 sp = new Esercizio4(args[0]);

        sp.tempoTrovaPercorsi();

    }
}