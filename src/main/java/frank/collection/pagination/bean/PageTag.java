package frank.collection.pagination.bean;

import java.util.ArrayList;
import java.util.List;

public class PageTag {

	/** 下一页 **/
	public Tag next = new Tag();
	/** 上一页 **/
	public Tag prev = new Tag();
	/** 第一页 **/
	public Tag first = new Tag();
	/** 最后一页 **/
	public Tag last = new Tag();
	/** 页码 **/
	public List<Tag> list = new ArrayList<Tag>(5);
	/** 当前页  **/
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
