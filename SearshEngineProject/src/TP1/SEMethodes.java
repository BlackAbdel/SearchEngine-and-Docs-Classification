package TP1;
/**
 * * * * * * * * * *
 * @author BlackPro* 
 * * * * * * * * * * 
 */
	
	import java.util.Map;

public class SEMethodes {
	public static double tf(Map<String, Integer> map, String term) {
		double result = 0;
		double sum = 0;
		for (String word : map.keySet()) {
			sum += map.get(word);
			if (term.equalsIgnoreCase(word))
				result++;
		}
		return result / sum;
	}

	public static double tfIdf(Map<String, Integer> doc, Map<String, Map<String, Integer>> corpus, String term) {
		return tf(doc, term) * idf(corpus, term);
	}

	public static double idf(Map<String, Map<String, Integer>> corpus, String term) {
		int nbDoc = 0;
		for (Map.Entry<String, Map<String, Integer>> entry : corpus.entrySet()) {
			if (entry.getValue().containsKey(term))
				nbDoc++;
		}

		return Math.log10(corpus.size() / nbDoc);
	}
}
