package frank.collection.pagination.bean;

public class Page {
	/**
	 * ҳ��
	 */
	public static final String PAGENO = "pageno";
	/**
	 * ÿҳ����
	 */
	public static final String PAGEROWS = "pagerows";
	/**
	 * ��ҳ��
	 */
	public static final String PAGENOALL = "pagenoall";
	/**
	 * �ܼ�¼��
	 */
	public static final String ROWSALL = "rowsall";
	 
	private Long pageno = 1L;
	private Long pagerows = 10L;
	private Long pagenoall = 1L;
	private Long rowsall = 0L;
	
	//�Ƿ񻺴�count,��Ҫ��rowsall���趨��0
	private boolean isCountCache = false; 
	
	public boolean isCountCache() {
		return isCountCache;
	}

	public void setCountCache(boolean isCountCache) {
		this.isCountCache = isCountCache;
	}

	public Page() {

	}

	public Page(Long pageno, Long pagerows) {
		this.pageno = pageno;
		this.pagerows = pagerows;
	}

	public Page(Long pageno, Long pagerows, Long pagenoall, Long rowsall) {
		this.pageno = pageno;
		this.pagerows = pagerows;
		this.pagenoall = pagenoall;
		this.rowsall = rowsall;
	}

	public Long getPageno() {
		return pageno;
	}

	public void setPageno(Long pageno) {
		this.pageno = pageno;
	}

	public Long getPagerows() {
		return pagerows;
	}

	public void setPagerows(Long pagerows) {
		this.pagerows = pagerows;
	}

	public Long getPagenoall() {
		return pagenoall;
	}

	public void setPagenoall(Long pagenoall) {
		this.pagenoall = pagenoall;
	}

	public Long getRowsall() {
		return rowsall;
	}

	public void setRowsall(Long rowsall) {
		this.rowsall = rowsall;
	}

}
