/**
 * This software is released under the University of Illinois/Research and
 *  Academic Use License. See the LICENSE file in the root folder for details.
 * Copyright (c) 2016
 *
 * Developed by:
 * The Cognitive Computation Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.aceReader.documentReader;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.aceReader.Paragraph;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ACE_UN_Reader {
	
	static boolean isDebug = false;

	public static void main (String[] args) throws FileNotFoundException {
		String file = "/shared/shelley/yqsong/eventData/ace2005Modify/data/English/un/adj/alt.atheism_20041104.2428.sgm";
		List<String> lines = LineIO.read(file);
		String content = "";
		for (int i = 0; i < lines.size(); ++i) {
			content += lines.get(i) + " ";
		}
		String contentRemovingTags = content;
	 	while (contentRemovingTags.contains("<")) {
	 		int p = contentRemovingTags.indexOf('<');
	 		int q = contentRemovingTags.indexOf('>');
	 		contentRemovingTags = contentRemovingTags.substring(0,p)
	 				+ contentRemovingTags.substring(q+1, contentRemovingTags.length());
	 	}
		parse(content, contentRemovingTags);
	}
	
	public static List<Pair<String, Paragraph>> parse (String content, String contentRemovingTags) {
		List<Pair<String, Paragraph>> paragraphs = new ArrayList<Pair<String, Paragraph>>();
		
		Pattern pattern = null;
		Matcher matcher = null;
		
		String docID = "";
		String dateTime = "";
		String headLine = "";
		String text = "";
		
		pattern = Pattern.compile("<DOCID>(.*?)</DOCID>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			docID = (matcher.group(1)).trim();
	    }
		int index1 = content.indexOf(docID);
		Paragraph para1 = new Paragraph(index1, docID);
		Pair<String, Paragraph> pair1 = new Pair<String, Paragraph>("docID", para1);
		paragraphs.add(pair1);
		
		pattern = Pattern.compile("<DATETIME>(.*?)</DATETIME>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			dateTime = (matcher.group(1)).trim();
	    }
		int index2 = content.indexOf(dateTime);
		Paragraph para2 = new Paragraph(index2, dateTime);
		Pair<String, Paragraph> pair2 = new Pair<String, Paragraph>("dateTime", para2);
		paragraphs.add(pair2);
		
		pattern = Pattern.compile("<HEADLINE>(.*?)</HEADLINE>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			headLine = (matcher.group(1)).trim();
	    }
		int index3 = content.indexOf(headLine);
		Paragraph para3 = new Paragraph(index3, headLine);
		Pair<String, Paragraph> pair3 = new Pair<String, Paragraph>("headLine", para3);
		paragraphs.add(pair3);
		
		pattern = Pattern.compile("<POST>(.*?)</POST>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			text = (matcher.group(1)).trim();
			int index4 = content.indexOf(text);
			
			Pattern patternQuote = Pattern.compile("<SUBJECT>(.*?)</SUBJECT>");
			Matcher matcherQuote = patternQuote.matcher(text);			
			while (matcherQuote.find()) {
				String subject = (matcherQuote.group(1)).trim();
				int indexsubject = text.indexOf(subject) + index4;
				Paragraph paraSub = new Paragraph(indexsubject, subject);
				Pair<String, Paragraph> pair = new Pair<String, Paragraph>("postSubject", paraSub);
				paragraphs.add(pair);
			}
			
			patternQuote = Pattern.compile("<POSTER>(.*?)</POSTER>");
			matcherQuote = patternQuote.matcher(text);			
			while (matcherQuote.find()) {
				String quote = (matcherQuote.group(1)).trim();
				int indexQuote = text.indexOf(quote) + index4;
				Paragraph paraSub = new Paragraph(indexQuote, quote);
				Pair<String, Paragraph> pair = new Pair<String, Paragraph>("poster", paraSub);
				paragraphs.add(pair);
			}
			
			patternQuote = Pattern.compile("<POSTDATE>(.*?)</POSTDATE>");
			matcherQuote = patternQuote.matcher(text);			
			while (matcherQuote.find()) {
				String quote = (matcherQuote.group(1)).trim();
				int indexQuote = text.indexOf(quote) + index4;
				Paragraph paraSub = new Paragraph(indexQuote, quote);
				Pair<String, Paragraph> pair = new Pair<String, Paragraph>("postDate", paraSub);
				paragraphs.add(pair);
			}
			
			patternQuote = Pattern.compile("<QUOTE PREVIOUSPOST=\"(.*?)\"/>");
			matcherQuote = patternQuote.matcher(text);			
			while (matcherQuote.find()) {
				String quote = (matcherQuote.group(1)).trim();
				int indexQuote = text.indexOf(quote) + index4;
				Paragraph paraSub = new Paragraph(indexQuote, quote);
				Pair<String, Paragraph> pair = new Pair<String, Paragraph>("postQuote", paraSub);
				paragraphs.add(pair);
			}
			
 			if (text.contains("<QUOTE PREVIOUSPOST=")) {
				patternQuote = Pattern.compile("</SUBJECT>(.*?)<QUOTE PREVIOUSPOST=");
				matcherQuote = patternQuote.matcher(text);			
				while (matcherQuote.find()) {
					String newText = (matcherQuote.group(1)).trim();
					if (newText.equals("")) {
						continue;
					}
					if (newText.contains("</SUBJECT>"))
						newText = newText.substring(newText.indexOf("</SUBJECT>") + "</SUBJECT>".length()).trim();
					int indexNewText = text.indexOf(newText) + index4;
					Paragraph paraNewText = new Paragraph(indexNewText, newText);
					Pair<String, Paragraph> pair = new Pair<String, Paragraph>("text", paraNewText);
					paragraphs.add(pair);
				}
				
				patternQuote = Pattern.compile("\"/>(.*?)</POST>");
				matcherQuote = patternQuote.matcher(text + "</POST>");			
				while (matcherQuote.find()) {
					String newText = (matcherQuote.group(1)).trim();
					if (newText.equals("") || newText.contains("<QUOTE PREVIOUSPOST=")) {
						continue;
					}
					int indexNewText = text.indexOf(newText) + index4;
					Paragraph paraNewText = new Paragraph(indexNewText, newText);
					Pair<String, Paragraph> pair = new Pair<String, Paragraph>("text", paraNewText);
					paragraphs.add(pair);
				}
				
				patternQuote = Pattern.compile("\"/>(.*?)<QUOTE PREVIOUSPOST=");
				matcherQuote = patternQuote.matcher(text);			
				while (matcherQuote.find()) {
					String newText = (matcherQuote.group(1)).trim();
					if (newText.equals("")) {
						continue;
					}
					int indexNewText = text.indexOf(newText) + index4;
					Paragraph paraNewText = new Paragraph(indexNewText, newText);
					Pair<String, Paragraph> pair = new Pair<String, Paragraph>("text", paraNewText);
					paragraphs.add(pair);
				}
			} else {
				patternQuote = Pattern.compile("</SUBJECT>(.*?)</POST>");
				matcherQuote = patternQuote.matcher(text + "</POST>");			
				while (matcherQuote.find()) {
					String newText = (matcherQuote.group(1)).trim();
					int indexNewText = text.indexOf(newText) + index4;
					Paragraph paraNewText = new Paragraph(indexNewText, newText);
					Pair<String, Paragraph> pair = new Pair<String, Paragraph>("text", paraNewText);
					paragraphs.add(pair);
				}
			}
			
		}
		
		int index = 0;
		for (int i = 0; i < paragraphs.size(); ++i) {
			int offsetWithFiltering = contentRemovingTags.indexOf(paragraphs.get(i).getSecond().content, index);
			
			if (offsetWithFiltering == -1) {
				continue;
			}
			
			paragraphs.get(i).getSecond().offsetFilterTags = offsetWithFiltering;
			
			if (paragraphs.get(i).getFirst().equals("poster"))
				index += paragraphs.get(i).getSecond().content.length();
		}
		
		if (isDebug) {
			for (int i = 0; i < paragraphs.size(); ++i) {
				System.out.println(paragraphs.get(i).getFirst() + "--> " + paragraphs.get(i).getSecond().content);
				System.out.println(content.substring(paragraphs.get(i).getSecond().offset, 
						paragraphs.get(i).getSecond().offset + paragraphs.get(i).getSecond().content.length()));
				if (paragraphs.get(i).getSecond().offsetFilterTags == -1) {
					System.out.println("[No match phrase in filtered content.]");
				} else {
					System.out.println(contentRemovingTags.substring(paragraphs.get(i).getSecond().offsetFilterTags, 
							paragraphs.get(i).getSecond().offsetFilterTags + paragraphs.get(i).getSecond().content.length()));
				}
			
				System.out.println();
			}
		}
		
//		if (isDebug) {
//			for (int i = 0; i < paragraphs.size(); ++i) {
//				System.out.println(paragraphs.get(i).getFirst() + "--> " + paragraphs.get(i).getSecond().content);
//				System.out.println(content.substring(paragraphs.get(i).getSecond().offset, 
//						paragraphs.get(i).getSecond().offset + paragraphs.get(i).getSecond().content.length()));
//				System.out.println();
//			}
//		}

		return paragraphs;
	}
	
}
