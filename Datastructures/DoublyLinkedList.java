package Datastructures;

import Cache.CacheKey;

import java.util.Iterator;
import java.util.function.Consumer;

public class DoublyLinkedList<K> {
    private DoublyLinkedListNode<K> head, tail;

    public DoublyLinkedListNode<K> getHead() {
        return head;
    }

    public void setHead(DoublyLinkedListNode<K> head) {
        this.head = head;
    }

    public DoublyLinkedListNode<K> getTail() {
        return tail;
    }

    public void setTail(DoublyLinkedListNode<K> tail) {
        this.tail = tail;
    }

    public void remove(DoublyLinkedListNode<K> node){
        DoublyLinkedListNode<K> prev = node.getPrev(), next = node.getNext();

        if(node == this.tail){
            this.tail = node.getNext();
        }

        if(node == this.head){
            this.head = node.getPrev();
        }

        if(prev != null){
            prev.setNext(next);
        }
        if(next != null){
            next.setPrev(prev);
        }
    }

    public boolean findAndRemove(K nodeValue){
        if(this.head == null) return false;
        DoublyLinkedListNode<K> iter = this.head;
        while(iter != null){
            if(iter.getValue().equals(nodeValue)){
                remove(iter);
                return true;
            }
            iter = iter.getNext();
        }
        return false;
    }

    public void moveToHead(DoublyLinkedListNode<K> node){
        remove(node);
        if(head == null){
            this.head = node;
        } else {
            head.setNext(node);
            node.setPrev(head);
            this.head = node;
        }
        if(tail == null){
            this.tail = node;
        }
    }

    public void addToHead(DoublyLinkedListNode<K> node){
        node.setPrev(this.head);
        if(this.head != null){
            this.head.setNext(node);
        }
        if(this.tail == null){
            this.tail = node;
        }
        this.head = node;
    }

    public Iterator<K> getIterator(){
        return new Iterator<K>() {

            private DoublyLinkedListNode<K> curr = head;

            @Override
            public boolean hasNext() {
                return curr != null && curr.getNext() != null;
            }

            @Override
            public K next() {
                K value = curr.getValue();
                curr = curr.getNext();
                return value;
            }
        };
    }
}
