package org.finos.springbot.teams.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.springbot.teams.TeamsException;
import org.javatuples.Pair;

public class MemoryStateStorage extends AbstractStateStorage {
	
	Map<String, Map<String, Object>> store = new HashMap<>();
	Map<String, List<Pair<String, String>>> tagIndex = new HashMap<>();
	

	@Override
	public void store(String file, Map<String, String> tags, Map<String, Object> data) {
		store.put(file, data);
		if ((tags != null) && (tags.size() > 0)){
			List<Pair<String, String>> pairtags = tags.entrySet().stream()
				.map(e -> new Pair<String, String>(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
			tagIndex.put(file, pairtags);
		} else {
			throw new TeamsException("Cannot persist data to "+file+" - no tags");

		}
		
	}

	@Override
	public Iterable<Map<String, Object>> retrieve(List<Filter> tags, boolean singleResultOnly) {
		List<Map<String, Object>> out = tagIndex.entrySet().stream()
			.filter(e -> matchEntry(e, tags))
			.map(e -> store.get(e.getKey()))
			.collect(Collectors.toList());
			
		if (singleResultOnly) {
			out = out.subList(0, 1);
		}
		
		return out;
	}

	private boolean matchEntry(Entry<String, List<Pair<String, String>>> e, List<Filter> tags) {
		for (Iterator<Filter> iterator = tags.iterator(); iterator.hasNext();) {
			Filter filter = (Filter) iterator.next();
			
			Pair<String, String> matched = getMatchedPair(e.getValue(), filter.key);
			if (matched == null) {
				return false;
			} else {
				if (!checkMatches(filter, matched.getValue1())) {
					return false;
				}
			}
		}
		
		return true;
	}

	private boolean checkMatches(Filter filter, String value) {
		int cmp = filter.value.compareTo(value);
		
		if ((filter.operator.contains("=")) && (cmp == 0)) {
			return true;
		}
			
		if (filter.operator.contains(">") && (cmp == -1)) {
			return true;
		}
		
		if (filter.operator.contains("<") && (cmp == 1)) {
			return true;
		}
		
		return false;
	}

	private Pair<String, String> getMatchedPair(List<Pair<String, String>> value, String key) {
		for (Pair<String, String> pair : value) {
			if (pair.getValue0().equals(key)) {
				return pair;
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<Map<String, Object>> retrieve(String file) {
		return Optional.ofNullable((Map<String, Object>) store.get(file));
	}

}
