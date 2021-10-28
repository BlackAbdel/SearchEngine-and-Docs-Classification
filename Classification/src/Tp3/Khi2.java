package Tp3;
/**
 * * * * * * * * * *
 * @author BlackPro* 
 * * * * * * * * * * 
 */
import java.io.File;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.StopAnalyzer;

import language.steamming.EnglishStemmer;

public class Khi2 {
	public static double maxProb = 0;
	public static double Prob = 0;
	public static int ClassSize = 0;
	public static int nbDoc = 0;
	public static int nbDocWT = 0;
	public static int nbr = 0;
	public static final File Dir = new File("20_newsgroups2");
	public static final File Test = new File("Test2");

	public static ArrayList<String> ListOfStopWords() throws IOException {
		return new ArrayList<>(Arrays.asList(StopAnalyzer.ENGLISH_STOP_WORDS));
	}

	public static String EnStem(String word) {
		EnglishStemmer Stemmer = new EnglishStemmer();
		Stemmer.setCurrent(word);
		if (Stemmer.stem())
			return Stemmer.getCurrent();
		return null;
	}

	public static Map<String, Map<String, Map<String, Integer>>> corpus;

	public static Map<String, Map<String, Map<String, Integer>>> CreateCorpus(File folder) throws IOException {
		corpus = new HashMap<>();
		ArrayList<String> stopWordsList = ListOfStopWords();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				String className = fileEntry.getName();
				File file = fileEntry.getAbsoluteFile();
				Map<String, Map<String, Integer>> mapDocTerm = new HashMap<>();
				for (File docFile : file.listFiles()) {
					Map<String, Integer> mapTermOcc = new HashMap<>();
					String docName = docFile.getName();
					List<String> lines = Files.readAllLines(Paths.get(docFile.getAbsolutePath()),StandardCharsets.ISO_8859_1);
					String text = String.join(" ", lines);
					text = text.replaceAll("[^\\p{L}\\p{Nd} ]", "").toLowerCase();
					int nbrRep = 0;
					for (String st : text.split(" ")) {
						String stm = EnStem(st);
						if (!stopWordsList.contains(stm)) {
							if (mapTermOcc.containsKey(stm)) {
								nbrRep = mapTermOcc.get(stm);
								nbrRep++;
								mapTermOcc.put(stm, nbrRep);
							} else {
								mapTermOcc.put(stm, 1);
							}
						}
					}
					mapDocTerm.put(docName, mapTermOcc);
				}
				corpus.put(className, mapDocTerm);
			}
		}
		return corpus;
	}

	/*
	 * N:Nombre des documents dans la class
	 */
	public static int nbrDocInClass(String Class, Map<String, Map<String, Map<String, Integer>>> Corpus)
			throws IOException {
//		Map<String, Map<String, Map<String, Integer>>> Corpus =CreateCorpus(Dir);

		Corpus.forEach((Clas, v) -> {
			if (Class.equals(Clas)) {
				nbr = v.size();
			}
		});
		return nbr;
	}

	/*
	 * @ A: Nombre des documents qui contient le Terme dans la class
	 */
	public static int nbrDocInClassWithTerm(String term, String Class,
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
//		Map<String, Map<String, Map<String, Integer>>> Corpus = CreateCorpus(Dir);
		nbDoc = 0;
		Corpus.forEach((Clas, v) -> {
			v.forEach((Doc, vv) -> {
				vv.forEach((word, occ) -> {
					if (Class.equals(Clas)) {
						if (term.equals(word)) {
							nbDoc++;
						}
					}
				});

			});
		});
		return nbDoc;
	}

	/*
	 * @ B: Nombre des documents qui ne contient pas le Terme dans la class
	 */
	public static int nbrDocInClassWithoutTerm(String term, String Class,
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
		nbDoc = 0;
		nbDocWT = 0;
		ClassSize = 0;
		nbDocWT = nbrDocInClassWithTerm(term, Class, Corpus);
//		Map<String, Map<String, Map<String, Integer>>> Corpus = CreateCorpus(Dir);
		Corpus.forEach((Clas, v) -> {
			if (Clas.equals(Class)) {
				ClassSize = v.size();
			}
		});
		nbDoc = ClassSize - nbDocWT;
		return nbDoc;
	}

	/*
	 * @ C: Nombre des documets qui contient le terme en dehors de la class
	 */
	public static int nbrDocOutClassWithTerm(String term, String Class,
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
//		Map<String, Map<String, Map<String, Integer>>> Corpus = CreateCorpus(Dir);
		nbDoc = 0;
		Corpus.forEach((Clas, v) -> {
			v.forEach((Doc, vv) -> {

				if (!Class.equals(Clas)) {
					if (vv.containsKey(term)) {
						nbDoc++;
					}
				}

			});
		});
		return nbDoc;
	}

	/*
	 * @ D: Nombre des documents qui ne contient pas le terme en dehors de la class
	 */
	public static int nbrDocOutClassWithoutTerm(String term, String Class,
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
//		Map<String, Map<String, Map<String, Integer>>> Corpus = CreateCorpus(Dir);
		nbDoc = 0;
		Corpus.forEach((Clas, v) -> {
			v.forEach((Doc, vv) -> {

				if (!Class.equals(Clas)) {
					if (!vv.containsKey(term)) {
						nbDoc++;
					}
				}

			});
		});
		return nbDoc;
	}

	/*
	 * @ Probabilite khi2 de chaque terme dans la class
	 */
	public static double ProbKhiDeux(String term, String Class, Map<String, Map<String, Map<String, Integer>>> Corpus)
			throws IOException {
		int N = nbrDocInClass(Class, Corpus);
		int A = nbrDocInClassWithTerm(term, Class, Corpus);
		int B = nbrDocInClassWithoutTerm(term, Class, Corpus);
		int C = nbrDocOutClassWithTerm(term, Class, Corpus);
		int D = nbrDocOutClassWithoutTerm(term, Class, Corpus);
		double prob = 0;
//		System.out.println(Class);
//		System.out.println("N =" + N);
//		System.out.println("A =" + A);
//		System.out.println("B =" + B);
//		System.out.println("C =" + C);
//		System.out.println("D =" + D);
		prob = N * Math.pow(A * D - B * C, 2) / ((A + B) * (A + C) * (D + B) * (D + C));
		return prob;
	}

	/*
	 * khi2 d'un terme dans toute les classes
	 */
	public static Map<String, Map<String, Double>> Khi2TermInClasses(String term,
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
//		Map<String, Map<String, Map<String, Integer>>> Corpus = CreateCorpus(Dir);
		Map<String, Map<String, Double>> map = new HashMap<>();
		Map<String, Double> map2 = new HashMap<>();
		Prob = 0;
		Corpus.forEach((Clas, v) -> {
			try {
				Prob = ProbKhiDeux(term, Clas, Corpus);
			} catch (IOException e) {
				e.printStackTrace();
			}
			map2.put(Clas, Prob);
			map.put(term, map2);
		});
		return map;
	}

	/*
	 * MaxKhi2 d'un term dans toute les classe
	 */
	public static double MaxKhi2(String term, Map<String, Map<String, Map<String, Integer>>> Corpus)
			throws IOException {
		Map<String, Map<String, Double>> map = Khi2TermInClasses(term, Corpus);
		maxProb = 0;
		map.forEach((word, v) -> {
			v.forEach((Class, vv) -> {
				if (word.equals(term)) {
					if (v.get(Class) > maxProb) {
						maxProb = v.get(Class);
					}
				}
			});
		});
		return maxProb;
	}

	/*
	 * Creation du nouvelle Corpus apres khi2
	 */
	public static double khi2 = 0;
	public static Map<String, Map<String, Map<String, Integer>>> OurNewCorpus;

	public static Map<String, Map<String, Map<String, Integer>>> CreateNewCorpus(
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
//		if(OurNewCorpus!=null) return OurNewCorpus;
		OurNewCorpus = new HashMap<>();
		Corpus.forEach((Clas, v) -> {
			Map<String, Map<String, Integer>> mapDoc = new HashMap<>();
			v.forEach((Doc, vv) -> {
				Map<String, Integer> mapTerm = new HashMap<>();
				vv.forEach((term, occ) -> {
					khi2 = 0;
					try {
						khi2 = MaxKhi2(term, Corpus);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (khi2 > 1.1) {
						System.out.println("Terme  ajouter au Train :" + term);
						System.out.println("Son Khi2 :" + khi2);
						mapTerm.put(term, occ);
					} else {
						System.out.println("Terme non ajouter au Train :" + term);
						System.out.println("Son Khi2 :" + khi2);
					}
				});
				mapDoc.put(Doc, mapTerm);

			});
			OurNewCorpus.put(Clas, mapDoc);
		});

		System.out.println(OurNewCorpus);
		return OurNewCorpus;
	}
	public static Map<String,Map<String,Map<String,Integer>>> OurNewTestCorpus;
	public static Map<String, Map<String, Map<String, Integer>>> CreateNewTestCorpus(
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
//		if(OurNewTestCorpus!=null) return OurNewTestCorpus;
		OurNewTestCorpus = new HashMap<>();
		Corpus.forEach((Clas, v) -> {
			Map<String, Map<String, Integer>> mapDoc = new HashMap<>();
			v.forEach((Doc, vv) -> {
				Map<String, Integer> mapTerm = new HashMap<>();
				vv.forEach((term, occ) -> {
					khi2 = 0;
					try {
						khi2 = MaxKhi2(term, Corpus);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (khi2 > 1.0) {
						System.out.println("Terme"+term +"  Ajouter au Corpus: Test " );
						System.out.println("Son Khi2 :" + khi2);
						mapTerm.put(term, occ);
					} else {
						System.out.println("Terme"+term +" Non ajouter au Corpus: Test " );
						System.out.println("Son Khi2 :" + khi2);
					}
				});
				mapDoc.put(Doc, mapTerm);

			});
			OurNewTestCorpus.put(Clas, mapDoc);
		});
		return OurNewTestCorpus;
	}

	public static void main(String[] args) throws IOException {
		Map<String, Map<String, Map<String, Integer>>> Corpus = CreateCorpus(Dir);
		System.out.println("Le nouvelle Corpus: " + CreateNewCorpus(Corpus));

	}
}
