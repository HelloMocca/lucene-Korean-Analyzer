package com.tistory.devyongsik.analyzer;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

/**
 * @author need4spd, need4spd@cplanet.co.kr, 2011. 8. 31.
 *
 */
public class KoreanAnalyzer extends ReusableAnalyzerBase {

	@Override
	protected TokenStreamComponents createComponents(final String fieldName,
			final Reader reader) {
		
		List<Engine> nounExtractEngines = new ArrayList<Engine>();
		nounExtractEngines.add(new KoreanStemmingEngine());
		nounExtractEngines.add(new KoreanCompoundNounEngine());
		nounExtractEngines.add(new KoreanBaseNounEngine());
		nounExtractEngines.add(new KoreanLongestNounEngine());
		nounExtractEngines.add(new KoreanSynonymEngine());
		nounExtractEngines.add(new KoreanMorphEngine());
		
		Tokenizer tokenizer = new KoreanCharacterTokenizer(reader);
		TokenStream tok = new KoreanNounFilter(tokenizer, nounExtractEngines);
		tok = new KoreanStopFilter(tok);

		return new TokenStreamComponents(tokenizer, tok);
	}

}