package chat.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class AutoDelMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = -8552559946198286084L;
	
	private List<Object> elemsTimed=new ArrayList<>();
	private List<BiConsumer<K, V>> removeListeners=new ArrayList<>();
	private int maxEntries;
	
	public AutoDelMap(int maxEntries) {
		this.maxEntries=maxEntries;
	}
	
	@Override
	public V get(Object key) {
		elemsTimed.remove(key);
		elemsTimed.add(key);
		return super.get(key);
	}

	@Override
	public V put(K key, V value) {
		if (!super.containsKey(key)) {
			elemsTimed.add(key);
			if (maxEntries<=super.size()) {
//				super.remove(elemsTimed.get(0));
//				elemsTimed.remove(0);
				remove(elemsTimed.get(0));
			}
		}
		return super.put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		V value=super.remove(key);
		if (value==null) {
			return null;
		}
		elemsTimed.remove(key);
		for (BiConsumer<K, V> listener : removeListeners) {
			listener.accept((K) key, value);
		}
		return value;
	}
	public void setOnRemove(BiConsumer<K, V> onRemove) {
		removeListeners.add(onRemove);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		
		m.forEach((k,v)->{
			put(k, v);
		});
	}
}
