package TP2;

/**
 * * * * * * * * * *
 * @author BlackPro* 
 * * * * * * * * * * 
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.StopAnalyzer;

import Tp3.Khi2;
import language.steamming.EnglishStemmer;

public class NaiveBayes {
	public static double maxPro = 0;
	public static double pro = 1;
	public static String st = "";
	public static double probabilite = 1;
	public static double proTC = 1;
	public static double probClass = 0;
	public static double prob = 0;
	public static int nk = 0;
	public static int nbr = 0;
	public static int nbDocInClass = 0;
	public static int nbDocInCorpus = 0;
	public static final File Dir = new File("20_newsgroups2");
	public static final File Test = new File("Test2");
	public static final File path = new File("StopWords.txt");
	public static Map<String, Map<String, Integer>> Classes;

//	/*
//	 * @ Methode qui retourne le corpus Test
//	 */
//	public static Map<String, Map<String, Map<String, Integer>>> getTestCorpus() throws IOException {
//		Map<String, Map<String, Map<String, Integer>>> TestCorpus = Khi2.CreateCorpus(Test);
//		return TestCorpus;
//	}

	/*
	 * @ Methode qui retourne le corpus crree dans la Class Khi2
	 */
	public static Map<String, Map<String, Map<String, Integer>>> getKhi2Corpus(
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
		Map<String, Map<String, Map<String, Integer>>> NewCorpus = Khi2.CreateNewCorpus(Corpus);
		return NewCorpus;
	}

	/*
	 * @ Methode qui retourne le corpus Test crree dans la Class Khi2
	 */
	public static Map<String, Map<String, Map<String, Integer>>> getKhi2TestCorpus(
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
		Map<String, Map<String, Map<String, Integer>>> NewCorpus = Khi2.CreateNewTestCorpus(Corpus);
		return NewCorpus;
	}

	/*
	 * @ nk: Nombre d'occurence du terme dans la class
	 */

	public static Map<String, Map<String, Integer>> CorpusOccTermInClass(
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
		Map<String, Map<String, Integer>> corpusTermOCC = new HashMap<String, Map<String, Integer>>();
//		Map<String, Map<String, Map<String, Integer>>> corpus = getKhi2Corpus(Corpus);

		Corpus.forEach((Class, v) -> {
			Map<String, Integer> map = new HashMap<>();
			v.forEach((Doc, vv) -> vv.forEach((word, occ) -> {
				if (map.containsKey(word)) {
					map.put(word, map.get(word) + occ);
				} else {
					map.put(word, occ);
				}
			}));
			corpusTermOCC.put(Class, map);
		});
		return corpusTermOCC;
	}
	/*
	 * @ n: Nombre des terme dans la class
	 */

	public static int NbrTermInClass(String Class, Map<String, Map<String, Map<String, Integer>>> Corpus)
			throws IOException {
		nbr = 0;
		Map<String, Map<String, Integer>> TermOccInClass = CorpusOccTermInClass(Corpus);
		TermOccInClass.get(Class).forEach((word, occ) -> nbr += occ);
		return nbr;
	}

	/*
	 * @ m: Mot non repete dans le corpus
	 */

	public static int NbrMotNonRepete(Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
		Map<String, Map<String, Integer>> TermOccInClass = CorpusOccTermInClass(Corpus);
		Set<String> mapMotNonRepete = new HashSet<>();
		TermOccInClass.forEach((Clas, v) -> mapMotNonRepete.addAll(v.keySet()));
		return mapMotNonRepete.size();
	}

	public static Set<String> MotsNonRepete(Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
		Map<String, Map<String, Integer>> TermOccInClass = CorpusOccTermInClass(Corpus);
		Set<String> mapMotNonRepete = new HashSet<>();
		TermOccInClass.forEach((Clas, v) -> mapMotNonRepete.addAll(v.keySet()));
		return mapMotNonRepete;
	}

	/*
	 * 
	 * @ Prob(C): la probabilite de la class
	 * 
	 */
	public static double ProbClass(String Class, Map<String, Map<String, Map<String, Integer>>> Corpus)
			throws IOException {
//		Map<String, Map<String, Map<String, Integer>>> corpus = getKhi2Corpus(Corpus);
		nbDocInCorpus = 0;
		Corpus.forEach((Clas, v) -> {
			nbDocInCorpus += v.size();
		});
		int x = Corpus.get(Class).size();
		double y = (double) nbDocInCorpus;
		double z = x / y;

		return z;
	}

	/*
	 * @ Prob(Wi/C): Probablite d'un term dans une Class P(Wi/C)
	 */
	public static double ProbTermInClass(String term, String Class,
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
		Map<String, Map<String, Integer>> corpOccTermClass = CorpusOccTermInClass(Corpus);
		int n = NbrTermInClass(Class, Corpus);
		int m = NbrMotNonRepete(Corpus);
//		System.out.println(m);
		Map<String, Integer> cls = corpOccTermClass.get(Class);
		nk = cls.containsKey(term) ? cls.get(term) : 0;
		System.out.println(nk);
		System.out.println(n);
		System.out.println(m);
		System.out.println(cls);

		double prob = (nk + 1) / (double) (n + m);
		return prob;
	}

	/*
	 * @ Prob(W/C): Probablite d'une terme du Corpus
	 */
	public static Map<String, Map<String, Double>> mapProbTermInClasses(
			Map<String, Map<String, Map<String, Integer>>> Corpus) throws IOException {
		Map<String, Map<String, Double>> mapProbTerms = new HashMap<String, Map<String, Double>>();
//		Map<String, Map<String, Integer>> corpOccTermClass = CorpusOccTermInClass();
//		Map<String, Map<String, Map<String, Integer>>> corpOccTermClass2 = getKhi2Corpus(Corpus);
		Set<String> motNonRepete = MotsNonRepete(Corpus);
		Corpus.forEach((Class, v) -> {
			Map<String, Double> map = new HashMap<>();

			motNonRepete.forEach((term) -> {
				pro = 1;
				try {
					pro = ProbTermInClass(term, Class, Corpus);
					map.put(term, pro);

				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			mapProbTerms.put(Class, map);
		});
//		System.out.println("asdfljasdklfjasdklfj" + mapProbTerms);
		return mapProbTerms;
	}

	/*
	 * @ Prob(W/C): Probablite d'une terme dans tout les class sous forme d'une Map
	 */
	public static Map<String, Double> Prob;

	public static Map<String, Map<String, Double>> ClassiffierFiles(Map<String, Map<String, Map<String, Integer>>> Test,
			Map<String, Map<String, Map<String, Integer>>> Train) throws IOException {
//		Map<String, Map<String, Map<String, Integer>>> mapfileProcess = getTestCorpus();
		Map<String, Map<String, Double>> ClassProb = new HashMap<>();
		Map<String, Map<String, Double>> mapProb = mapProbTermInClasses(Train);
//		Map<String, Map<String, Map<String, Integer>>> Testkhi2Corpus = getKhi2TestCorpus(Test);
		System.out.println(mapProb);

		Test.forEach((Clas, v2) -> {
			v2.forEach((Doc, vv2) -> {
				Prob = new HashMap<>();
				mapProb.forEach((Class, v) -> {
					probabilite = 1;
					try {
						probabilite = ProbClass(Class, Train);
					} catch (IOException e) {
						e.printStackTrace();
					}

					vv2.forEach((term, occ) -> {

						proTC = 0;
						if (v.containsKey(term)) {

							proTC = v.get(term);
							probabilite *= proTC;
						}
					});

					Prob.put(Class, probabilite);
				});
				ClassProb.put(Doc, Prob);
			});

		});
		return ClassProb;
	}

	public static String docName = "";

	public static Map<String, String> filesClassName(Map<String, Map<String, Map<String, Integer>>> Test,
			Map<String, Map<String, Map<String, Integer>>> Train) throws IOException {
		Map<String, String> map2 = new HashMap<>();
		Map<String, Map<String, Double>> map = ClassiffierFiles(Test, Train);
		map.forEach((Doc, v) -> {
			docName = Doc;
			maxPro = 0;
			v.forEach((Class, prob) -> {

				if (prob > maxPro) {
					maxPro = prob;
					st = Class;
				}
			});
			map2.put(docName, st);
		});
		return map2;
	}

	/*
	 * 
	 */
	public static int WellPredict = 0;
	public static int DocNumber = 0;

	public static double CalculateAccuracy(Map<String, Map<String, Map<String, Integer>>> Test,
			Map<String, Map<String, Map<String, Integer>>> Train) throws IOException {
		Map<String, String> filesClass = filesClassName(Test, Train);

		Test.forEach((Class, v) -> {
			v.forEach((Doc, vv) -> {
				DocNumber++;
				if (filesClass.get(Doc).equals(Class)) {
					WellPredict++;
				}
			});
		});
		return (WellPredict / (double) DocNumber) * 100;
	}

	public static void main(String[] args) throws IOException {
		Map<String, Map<String, Map<String, Integer>>> TrainCorpus = Khi2.CreateCorpus(Dir);
		Map<String, Map<String, Map<String, Integer>>> TestCorpus = Khi2.CreateCorpus(Test);
		System.out.println("Notre Score est " + CalculateAccuracy(TestCorpus, TrainCorpus) + " %");

	}
}
