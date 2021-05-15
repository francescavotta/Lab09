package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	
	private Map<Integer, Country> idMap;
	private BordersDAO dao;
	private SimpleGraph<Country, DefaultEdge> grafo;
	private List<Country> soluzioneRicorsione;

	public Model() {
		
		dao = new BordersDAO();
		idMap = new HashMap<Integer, Country>();
		dao.loadAllCountries(idMap);
	}
	
	public void creaGrafo(int anno) {
		grafo = new SimpleGraph<Country, DefaultEdge>(DefaultEdge.class);
		//vertici
		List <Country> vertici = dao.getVertici(anno, idMap);
		Graphs.addAllVertices(grafo, vertici);
		
		//archi
		List <Border> archi = dao.getCountryPairs(anno, idMap);
		
		for(Border b: archi) {
			//controllo che c1 e c2 del border siano nel grafo come vertici
			Country c1 = b.getC1();
			Country c2 = b.getC2();
			
			if(grafo.containsVertex(c1) && grafo.containsVertex(c2)) {
				DefaultEdge e = grafo.getEdge(c1, c2);
				
				if(e==null) {
					Graphs.addEdgeWithVertices(grafo, c1, c2);
				}
			}
		}
		System.out.println("Grafo creato");
		System.out.println("#Vertici: " + grafo.vertexSet().size());
		System.out.print("#Archi: " + grafo.edgeSet().size());
	}
	
	public Graph<Country, DefaultEdge> getGrafo(int anno) {
		this.creaGrafo(anno);
		return grafo;
	}
	
	public int getConnected(Graph<Country, DefaultEdge> grafo) {
		 ConnectivityInspector<Country, DefaultEdge> inspector = new ConnectivityInspector<Country, DefaultEdge>(grafo);
		   List<Set<Country>> lista = inspector.connectedSets();
		return lista.size();
	}
	
	public Set<Country> connessiInspector(Country c){
		ConnectivityInspector<Country, DefaultEdge> inspector = new ConnectivityInspector<Country, DefaultEdge>(grafo); 
		Set<Country> result = inspector.connectedSetOf(c);
		return result;
	}
	
	public List<Country> connessiDepth(Country partenza){
		DepthFirstIterator<Country, DefaultEdge> bfv = new DepthFirstIterator<>(this.grafo, partenza);
		
		List<Country> result = new ArrayList<>();
		
		while(bfv.hasNext()) {
			Country f = bfv.next();
			result.add(f);
		}
		
		return result;
	}

	public List<Country> connessiBreadth(Country partenza){
		BreadthFirstIterator<Country, DefaultEdge> bfv = 
				new BreadthFirstIterator<Country, DefaultEdge>(this.grafo, partenza) ;
		List<Country> result = new ArrayList<>() ;
		// fai lavorare l'iteratore per trovare tutti i vertici
		while(bfv.hasNext()) {
			Country temp = bfv.next() ;
			result.add(temp);
		}
		return result ;
	}
	
	public List<Country> connessiRicorsione(Country partenza){
		this.soluzioneRicorsione = new ArrayList<Country>();
		List<Country> parziale = new ArrayList<>();
		parziale.add(partenza);
		
		cerca(parziale);
		return parziale;	
	}

	private void cerca(List<Country> parziale) {
		//caso terminale:se non ho più vicini da esplorare
		/*if(Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1)).isEmpty()) {
			this.soluzioneRicorsione = new ArrayList<Country>(parziale);
			return;
		}*/
		
		for(Country vicino: Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				cerca(parziale);
			}
		}
		
	}
	
	public List<Country> connessiIterativo(Country partenza){
		List <Country> visitati = new ArrayList<>();
		List <Country> daVisitare = new LinkedList<>();
		daVisitare.add(partenza);
		
		Iterator<Country> i = daVisitare.iterator();
		
		while(i.hasNext()) {
			Country temp = i.next();
			List<Country> vicini = Graphs.neighborListOf(grafo, temp);
			for(Country v: vicini) {
				if(!daVisitare.contains(v) && !visitati.contains(v)) {
					daVisitare.add(v);
				}
			}
			visitati.add(temp);
			daVisitare.remove(temp);
			i= daVisitare.iterator();
		}
		return visitati;
	}
	
	
}
