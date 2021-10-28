package Tp4;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.StopAnalyzer;

import TP2.NaiveBayes;
import Tp3.Khi2;
import language.steamming.EnglishStemmer;

public class CrossValidation {
	public static final File path = new File("20_newsgroups3");
	public static int size = 0;
	public static int index = 0;
	public static int ii = 0;
	public static int i = 0;
	public static int sum = 0;
	public static Map<String, Map<String, Integer>> DocsTrain;
	public static Map<String, Map<String, Integer>> DocsTest;
	public static Map<String, Map<String, Integer>> DocsMap;
	public static Map<Integer, Double> mapAccuracy;
	public static Map<Integer, Map<String, Double>> mapRecall;
	public static Map<Integer, Map<String, Double>> mapPrecision;
	public static Map<Integer, Map<String, Double>> mapFMesure;
	public static int NBR = 0;

	public static void Split(Map<String, Map<String, Map<String, Integer>>> corpus, int K) throws IOException {
		Map<String, Map<String, Map<String, Integer>>> Train = new HashMap<>();
		Map<String, Map<String, Map<String, Integer>>> Test = new HashMap<>();
		mapAccuracy = new HashMap<>();
		for (i = 0; i < K; i++) {
			corpus.forEach((Class, v) -> {
				DocsMap = v;
				DocsTrain = new HashMap<>();
				DocsTest = new HashMap<>();
				size = DocsMap.size() / K;
				index = i * size;
				ii = 0;
				DocsMap.forEach((Doc, vv) -> {
					ii++;
					sum = index + size;
					if (ii >= index && ii <= sum) {
						DocsTrain.put(Doc, vv);
					} else {
						DocsTest.put(Doc, vv);
					}
				});
				Train.put(Class, DocsTrain);
				Test.put(Class, DocsTest);

			});
			Map<String, Map<String, Map<String, Integer>>> TrainKhi2Corpus = NaiveBayes.getKhi2Corpus(Train);
//			Map<String, Map<String, Map<String, Integer>>> TestKhi2Corpus = NaiveBayes.getKhi2TestCorpus(Test);
//			double accuracy = NaiveBayes.CalculateAccuracy(Test, TrainKhi2Corpus);
//			mapAccuracy.put(i, accuracy);
			Map<String, Double> mapClassRecall = calculateRecallsMap(Test, TrainKhi2Corpus);
//			mapRecall = new HashMap<>();
//			mapRecall.put(i, mapClassRecall);
//			System.out.println(toStringRecall(mapRecall));
			Map<String, Double> mapClassPrecision = calculatePrecisionsMap(Test, TrainKhi2Corpus);
//			mapPrecision = new HashMap<>();
//			mapPrecision.put(i, mapClassPrecision);
//			System.out.println(toStringPrecision(mapPrecision));
			Map<String, Double> mapClassFMesure = CalculateFMesure(mapClassPrecision, mapClassRecall);
			mapFMesure = new HashMap<>();
			mapFMesure.put(i, mapClassFMesure);
			System.out.println(toStringFMesure(mapFMesure));
			System.exit(0);

//			System.out.println(toString(mapAccuracy));

		} // Fin du bouche k =i
	}

	public static double fmesure = 0;
	public static Map<String, Double> FMesureMap;

	public static Map<String, Double> CalculateFMesure(Map<String, Double> Precision, Map<String, Double> Recall) {
		FMesureMap = new HashMap<>();
		Precision.forEach((Class, precision) -> {
			Recall.forEach((Clas, recall) -> {
				if (Class.equals(Clas)) {
					fmesure = (2 * precision * recall) / (double) (precision + recall);
					FMesureMap.put(Class, fmesure);
				}
			});
		});
		return FMesureMap;
	}

	public static double Recall = 0;
	public static Map<String, Double> mapClassRecall;

	public static Map<String, Double> calculateRecallsMap(Map<String, Map<String, Map<String, Integer>>> Test,
			Map<String, Map<String, Map<String, Integer>>> Train) {
		mapClassRecall = new HashMap<>();
		Test.forEach((Class, v) -> {

			try {
				Recall = CalculateRecall(Test, Train, Class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mapClassRecall.put(Class, Recall);
		});
		return mapClassRecall;
	}

	public static double Precision = 0;
	public static Map<String, Double> mapClassPrecition;

	public static Map<String, Double> calculatePrecisionsMap(Map<String, Map<String, Map<String, Integer>>> Test,
			Map<String, Map<String, Map<String, Integer>>> Train) {
		mapClassPrecition = new HashMap<>();
		Test.forEach((Class, v) -> {

			try {
				Precision = CalculateRecall(Test, Train, Class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mapClassPrecition.put(Class, Precision);
		});
		return mapClassPrecition;
	}

	/*
	 * @ Cette methode calcule les vrai positive et les Faux Positive d'une class
	 * afin de calculer Recall
	 */
	public static int VraiPositive = 0;
	public static int FauxPositive = 0;

	public static double CalculateRecall(Map<String, Map<String, Map<String, Integer>>> Test,
			Map<String, Map<String, Map<String, Integer>>> Train, String Clas) throws IOException {
		Map<String, String> filesClass = NaiveBayes.filesClassName(Test, Train);
		FauxPositive = 0;
		VraiPositive = 0;
		Test.forEach((Class, v) -> {
			v.forEach((Doc, vv) -> {
				if (Class.equals(Clas)) {
					if (filesClass.get(Doc).equals(Clas)) {
						VraiPositive++;
					}
				} else {
					if (filesClass.get(Doc).equals(Clas)) {
						FauxPositive++;
					}
				}

			});
		});
		double recall = VraiPositive / (double) (FauxPositive + VraiPositive);

		return recall;
	}

	public static int FauxNegative = 0;

	public static double CalculatePrecision(Map<String, Map<String, Map<String, Integer>>> Test,
			Map<String, Map<String, Map<String, Integer>>> Train, String Clas) throws IOException {
		Map<String, String> filesClass = NaiveBayes.filesClassName(Test, Train);
		FauxNegative = 0;
		VraiPositive = 0;
		Test.forEach((Class, v) -> {
			v.forEach((Doc, vv) -> {
				if (Class.equals(Clas)) {
					if (filesClass.get(Doc).equals(Clas)) {
						VraiPositive++;
					} else if (!filesClass.get(Doc).equals(Clas)) {
						FauxPositive++;
					}
				}
			});
		});
		double recall = VraiPositive / (double) (FauxNegative + VraiPositive);

		return recall;
	}

	public static String st;

	public static String toString(Map<Integer, Double> mapAccuracy) {
		st = "";
		mapAccuracy.forEach((split, accuracy) -> {
			st += "Split: " + split + " His Accuracy is :" + accuracy + "\n";
		});
		return st;
	}

	public static String toStringRecall(Map<Integer, Map<String, Double>> mapRecall) {
		st = "";
		mapRecall.forEach((split, v) -> {
			v.forEach((Class, recall) -> {
				st += "Split: " + split + " Class :" + Class + " Recall :" + recall + "\n";
			});

		});
		return st;
	}

	public static String toStringPrecision(Map<Integer, Map<String, Double>> mapPrecision) {
		st = "";
		mapRecall.forEach((split, v) -> {
			v.forEach((Class, precision) -> {
				st += "Split: " + split + " Class :" + Class + " Precision :" + precision + "\n";
			});
		});
		return st;
	}
	public static String toStringFMesure(Map<Integer, Map<String, Double>> mapFMesure) {
		st = "";
		mapFMesure.forEach((split, v) -> {
			v.forEach((Class, fmesure) -> {
				st += "Split: " + split + " Class :" + Class + " F-Mesure :" + fmesure + "\n";
			});
		});
		return st;
	}

	public static void main(String[] args) throws IOException {
		Map<String, Map<String, Map<String, Integer>>> corpus = Khi2.CreateCorpus(path);
		Split(corpus, 9);
	}

}
