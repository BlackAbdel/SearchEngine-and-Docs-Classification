
package TP1;

/**
 * * * * * * * * * *
 * @author BlackPro*
 * * * * * * * * * *
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import safar.basic.morphology.stemmer.factory.StemmerFactory;
import safar.basic.morphology.stemmer.interfaces.IStemmer;
import safar.basic.morphology.stemmer.model.StemmerAnalysis;
import safar.basic.morphology.stemmer.model.WordStemmerAnalysis;

public class SearchEngine {

	static final private String pa = "E:/S3-TextMining/TAAMIR";
	public static Map<String, Map<String, Integer>> corpus = new HashMap<>();
	public static Map<String, Double> TF_IDF = new HashMap<>();
	public static Map<String, Map<String, Double>> TF_IDF_Corpus = new HashMap<>();
	public static Map<String, Double> OurCosSimilarity = new HashMap<>();
	public static Map<String, Double> reqIDF_Map = new HashMap<>();

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		String requete = sc.nextLine();
		try {
			File ff = new File(pa + "/Requet.txt");
			ff.createNewFile();
			FileWriter ffw = new FileWriter(ff);
			ffw.write(requete);
			ffw.close();
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		File file = new File(pa);
		Map<String, Map<String, Integer>> OurIndex = CreateIndex(file);
		Map<String, Integer> req = OurIndex.get("Requet.txt");
		Map<String, Map<String, Double>> tfidf_corpus = Create_TFIDF_CORPUS(OurIndex, req);
		reqIDF_Map = tfidf_corpus.get("Requet.txt");
		OurCosSimilarity = CalculateCos(tfidf_corpus, reqIDF_Map);
		System.out.println(CalculateMax(OurCosSimilarity));

	}/* End of Function Main */

	public static String CalculateMax(Map<String, Double> mapCosSim) {
		Double max = 0.0;
		String nameDoc = "";
		for (Map.Entry<String, Double> entry : mapCosSim.entrySet()) {
			if (entry.getValue() > max) {
				max = entry.getValue();
				nameDoc = entry.getKey();
			}
		}
		return nameDoc;
	}

	public static Double Normalisation(Map<String, Double> doc, Map<String, Double> req) {
		double val = 0;
		double val2 = 0;
		double temp = 0;
		double temp2 = 0;
		for (Map.Entry<String, Double> element : doc.entrySet()) {
			temp += Math.pow(element.getValue(), 2);
			
		}
		for (Map.Entry<String, Double> element : req.entrySet()) {
			temp2 += Math.pow(element.getValue(), 2);
		}
		val = Math.sqrt(temp);
		val2 = Math.sqrt(temp2);
		return val * val2;
	}

	public static Map<String, Double> CalculateCos(Map<String, Map<String, Double>> corpus, Map<String, Double> req) {
		Map<String, Double> CosSim = new HashMap<>();
		for (Map.Entry<String, Map<String, Double>> entry : corpus.entrySet()) {
			String nomDoc = entry.getKey();
			Map<String, Double> map = entry.getValue();
			double cos = 0;
			double cosSim = 0;
			double norm = 0;
			norm = Normalisation(map, req);
			for (String st : req.keySet()) {
				Double d = req.get(st);
				if (!nomDoc.equalsIgnoreCase("Requet.txt")) {
					if (entry.getValue().containsKey(st)) {
						cos += entry.getValue().get(st) * d;
					}
				}
			}
			cosSim = cos / norm;
			CosSim.put(nomDoc, cosSim);
		}

		return CosSim;
	}

	public static double produitScalaire(Map<String, Double> map1, Map<String, Double> map2) {
		double prodScalaire = 0;
		for (Map.Entry<String, Double> e : map1.entrySet()) {
			if (map2.containsKey(e.getKey()))
				prodScalaire += e.getValue() * map2.get(e.getKey());
		}
		return prodScalaire;
	}

	public static Map<String, Map<String, Double>> Create_TFIDF_CORPUS(Map<String, Map<String, Integer>> corpus,
			Map<String, Integer> req) {
		double tfidf = 0.0;
		for (Map.Entry<String, Map<String, Integer>> entry : corpus.entrySet()) {
			TF_IDF = new HashMap<>();
			for (String term : req.keySet()) {
				if (entry.getValue().containsKey(term)) {
					tfidf = SEMethodes.tfIdf(entry.getValue(), corpus, term);
					TF_IDF.put(term, tfidf);
					TF_IDF_Corpus.put(entry.getKey(), TF_IDF);
				}
			}
		}
		return TF_IDF_Corpus;
	}

	public static Map<String, Map<String, Integer>> CreateIndex(File file) throws IOException {
		for (File f : file.listFiles()) {

			List<String> lines = Files.readAllLines(Paths.get(f.getAbsolutePath()),StandardCharsets.ISO_8859_1);
			String text = String.join(" ", lines);
			text = text.replaceAll("[^\\p{L}\\p{Nd} ]", "");

			Map<String, Integer> map = new HashMap<String, Integer>();

			IStemmer stemmer = StemmerFactory.getKhojaImplementation();

			List<WordStemmerAnalysis> analysis = stemmer.stem(f);

			for (WordStemmerAnalysis WStAnalysis : analysis) {
				List<StemmerAnalysis> listOfStems = WStAnalysis.getListStemmerAnalysis();
				for (StemmerAnalysis stem : listOfStems) {
					String stm = stem.getMorpheme();
					int nbrRep = 0;
					if (stem.getType() != "STOPWORD" && stm != null) {
						if (map.containsKey(stm)) {
							nbrRep = map.get(stm);
							nbrRep++;
							map.put(stm, nbrRep);
						} else {
							map.put(stm, 1);
						}
					}
				}
			}
			corpus.put(f.getName(), map);
		}
		return corpus;
	}
}
