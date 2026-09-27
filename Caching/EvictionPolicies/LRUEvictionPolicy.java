package Caching.EvictionPolicies;

import Caching.Cache.CacheKey;
import Caching.Datastructures.DoublyLinkedList;
import Caching.Datastructures.DoublyLinkedListNode;
import Caching.Interfaces.IEvictionPolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LRUEvictionPolicy implements IEvictionPolicy {

    private Map<CacheKey, DoublyLinkedListNode<CacheKey>> nodeMap;
    private final DoublyLinkedList<CacheKey> list;

    public LRUEvictionPolicy(){
        list = new DoublyLinkedList<>();
        nodeMap = new HashMap<CacheKey, DoublyLinkedListNode<CacheKey>>();
    }

    @Override
    public void keyAccessed(CacheKey key) {
        if(!nodeMap.containsKey(key)){
            nodeMap.put(key, new DoublyLinkedListNode<CacheKey>(key));
        }
        DoublyLinkedListNode<CacheKey> node = nodeMap.get(key);
        list.moveToHead(node);
    }

    @Override
    public void evict() throws Exception {
        DoublyLinkedListNode<CacheKey> tail = list.getTail();
        if(tail == null) throw new Exception();
        CacheKey key = tail.getValue();
        list.remove(tail);
        nodeMap.remove(key);
    }

    @Override
    public Optional<CacheKey> evictionCandidate() {
        if(list.getTail() == null){
            return Optional.empty();
        }
        return Optional.of(list.getTail().getValue());
    }
}
