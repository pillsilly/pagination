package frank.collection.pagination.bean;

import java.util.ArrayList;
import java.util.List;

public class PageTag {

	/** ��һҳ **/
	public Tag next = new Tag();
	/** ��һҳ **/
	public Tag prev = new Tag();
	/** ��һҳ **/
	public Tag first = new Tag();
	/** ���һҳ **/
	public Tag last = new Tag();
	/** ҳ�� **/
	public List<Tag> list = new ArrayList<Tag>(5);
	/** ��ǰҳ  **/
	public Tag current= new Tag();
	
	
	public Tag getNext() {
		return next;
	}

	public void setNext(Tag next) {
		this.next = next;
	}

	public Tag getPrev() {
		return prev;
	}

	public void setPrev(Tag prev) {
		this.prev = prev;
	}

	public Tag getFirst() {
		return first;
	}

	public void setFirst(Tag first) {
		this.first = first;
	}

	public Tag getLast() {
		return last;
	}

	public void setLast(Tag last) {
		this.last = last;
	}

	public List<Tag> getList() {
		return list;
	}

	public void setList(List<Tag> list) {
		this.list = list;
	}

	public Tag getCurrent() {
		return current;
	}

	public void setCurrent(Tag current) {
		this.current = current;
	}

}
