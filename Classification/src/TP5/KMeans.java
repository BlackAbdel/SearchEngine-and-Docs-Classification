package TP5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import Tp3.Khi2;

public class KMeans {
	private int k = 20;
	Map<Document, Integer> Docs = new HashMap<>();
	
	public Map<Integer, Set<Document>> getClusters() {
		Map<Integer, Set<Document>> clusters = new HashMap<>();
		for (int i = 0; i < k; i++) clusters.put(i, new HashSet<>());
		Docs.forEach((d, c) -> clusters.get(c).add(d));
		
		return clusters;
	}
	
	private int changes;


	public Kmeans(Collection<Document> data, int k) {
		List<Document> docs = new ArrayList<>(data);
		docs.forEach(d -> Docs.put(d, -1));
		this.k = k;
		
		Random rand = new Random();
		
		for (int i = 0; i < k; i++) {
			int index = rand.nextInt(docs.size());
			if(Docs.get(docs.get(index)) > -1) {
				i--; continue;
			}
			Docs.put(docs.get(index), i);
		}
		changes = 1;
		while(changes > 0) {
			changes = 0;
			Map<Integer, Document> cGrav = getCentroids();
			new HashMap<>(Docs).forEach((d, c) -> {
				PriorityQueue<Pair> ds = new PriorityQueue<>();
				cGrav.forEach((cls, mean) ->{
					ds.add(new Pair(cls, dist(d, mean)));
				});
				int newClass = Integer.parseInt(ds.poll().key);
				if(Docs.get(d) != newClass) changes++;
				Docs.put(d, newClass);
			});
			
		}
		System.out.println("Done!");
		
	}
	
	private static int d;
	private static double dist(Document doc, Document mean) {
		d = 0;
		Set<String> words = new HashSet<>(doc.wordSet);
		words.addAll(mean.mean.keySet());
		
		words.forEach(w -> {
			double oc1 = doc.words.containsKey(w) ? doc.words.get(w) : 0,
				oc2 = mean.mean.containsKey(w) ? mean.mean.get(w) : 0;
			
			d += Math.pow(oc1 - oc2, 2);
		});
		return Math.sqrt(d);
	}
	
	private Map<Integer, Document> getCentroids() {
		Map<Integer, Document> centroids = new HashMap<>();
		Map<Integer, Integer> sizes = new HashMap<>();
		
		
		Docs.forEach((d, c) -> {
			sizes.put(c, sizes.containsKey(c) ? sizes.get(c) + 1 : 1);
			d.words.forEach((w, occ) -> {
				if(c == -1) return;
				if(!centroids.containsKey(c)) centroids.put(c, new Document());
				
				Document g = centroids.get(c);
				g.mean.put(w, g.mean.containsKey(w) ? g.mean.get(w) + occ : occ);
			});
		});
		
		centroids.forEach((c, d) -> d.mean.forEach((w, occ) -> d.mean.put(w, occ / (double) sizes.get(c))));
		
		return centroids;
	}
	
}