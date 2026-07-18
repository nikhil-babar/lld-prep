package Datastructures;

public class DoublyLinkedListNode<K> {
    private DoublyLinkedListNode<K> prev, next;
    private final K value;

    public DoublyLinkedListNode(K value) {
        this.value = value;
    }

    public DoublyLinkedListNode<K> getPrev() {
        return prev;
    }

    public void setPrev(DoublyLinkedListNode<K> prev) {
        this.prev = prev;
    }

    public DoublyLinkedListNode<K> getNext() {
        return next;
    }

    public void setNext(DoublyLinkedListNode<K> next) {
        this.next = next;
    }

    public K getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object node){
        if(node == this) return true;
        if(!(node instanceof DoublyLinkedListNode)) return false;

        DoublyLinkedListNode<K> dnode = this.getClass().cast(node);
        return dnode.getValue() == this.getValue();
    }
}
