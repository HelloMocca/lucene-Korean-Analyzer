package com.tistory.devyongsik.analyzer.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tistory.devyongsik.analyzer.DictionaryProperties;

public class DictionaryFactory {
	private Log logger = LogFactory.getLog(DictionaryFactory.class);

	private static DictionaryFactory factory = new DictionaryFactory();
	private Map<DictionaryType, List<String>> dictionaryMap = new HashMap<DictionaryType, List<String>>();
	private Map<String, List<String>> compoundDictionaryMap = new HashMap<String, List<String>>();
	
	//private final String defaultDictionaryPackage = "com/tistory/devyongsik/analyzer/dictionary/";

	//TODO 사전 중복으로 읽지 않도록..
	//TODO 사전을 Map으로.. 초성 분리..맵 고민
	
	public static DictionaryFactory getFactory() {
		return factory;
	}

	private DictionaryFactory() {
		initDictionary();
	}
	
	private void initDictionary() {
		DictionaryType[] dictionaryTypes = DictionaryType.values();
		for(DictionaryType dictionaryType : dictionaryTypes) {
			if(logger.isInfoEnabled()) {
				logger.info("["+dictionaryType.getDescription()+"] "+"create wordset from file");
			}
			
			List<String> dictionary = loadDictionary(dictionaryType);
			dictionaryMap.put(dictionaryType, dictionary);
		}
		
		List<String> dictionaryData = dictionaryMap.get(DictionaryType.COMPOUND);
		String[] extractKey = null;
		String key = null;
		String[] nouns = null;
		
		for(String data : dictionaryData) {
			extractKey = data.split(":");
			key = extractKey[0];
			nouns = extractKey[1].split(",");
			
			compoundDictionaryMap.put(key, Arrays.asList(nouns));
		}
	}
	
	public List<String> get(DictionaryType name) {
		return dictionaryMap.get(name);
	}
	
	public Map<String, List<String>> getCompoundDictionary() {
		return compoundDictionaryMap;
	}

	private List<String> loadDictionary(DictionaryType name) {

		BufferedReader in = null;
		String dictionaryFile = DictionaryProperties.getInstance().getProperty(name.getPropertiesKey());
		InputStream inputStream = DictionaryFactory.class.getClassLoader().getResourceAsStream(dictionaryFile);

		if(inputStream == null) {
			logger.info("couldn't find dictionary : " + dictionaryFile);
			
			inputStream = DictionaryFactory.class.getResourceAsStream(dictionaryFile);
			
			logger.info(dictionaryFile + " file loaded.. from classloader.");
		}

		List<String> words = new ArrayList<String>();

		try {
			String readWord = "";
			in = new BufferedReader( new InputStreamReader(inputStream ,"utf-8"));
			
			
			while( (readWord = in.readLine()) != null ) {
				words.add(readWord);
			}

			if(logger.isInfoEnabled()) {
				logger.info(name.getDescription() + " : " + words.size());
			}

			if(logger.isInfoEnabled()) {
				logger.info("create wordset from file complete");
			}

		}catch(IOException e){
			logger.error(e.toString());
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		
		return words;
	}
}
